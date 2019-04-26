package avi.mod.skrim.skills.melee;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.init.SkrimSoundEvents;
import avi.mod.skrim.network.SkrimPacketHandler;
import avi.mod.skrim.network.skillpackets.OffHandAttackPacket;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillAbility;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.utils.Obfuscation;
import avi.mod.skrim.utils.Utils;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SkillMelee extends Skill implements ISkillMelee {

  public static SkillStorage<ISkillMelee> skillStorage = new SkillStorage<ISkillMelee>();
  private static SkillAbility VAMPIRISM = new SkillAbility("melee", "Vampirism", 25, "What I need is your blood. What" +
      " I don't need is your permission.",
      "Killing an enemy restores §a1" + SkillAbility.DESC_COLOR + " heart.");
  private static SkillAbility SPIN_SLASH = new SkillAbility("melee", "Spin Slash", 50, "Spin to win.", "Critting an " +
      "enemy deals massive AOE damage.");
  private static SkillAbility DUAL_WIELDING = new SkillAbility("melee", "Dual Wielding", 75, "Two swords are better " +
      "than one.",
      "Allows you to swing your off hand if you have two swords equipped.", "Each hand uses a separate exhaustion " +
      "meter for damage & swipe attacks.");
  private static SkillAbility GRAND_SMITE = new SkillAbility("melee", "Grand Smite", 100, "DESTRUUUUCTIOOONNN",
      "Criitting an enemy call down lightning.");
  private int ticksSinceLastLeft = 0;

  public SkillMelee() {
    this(1, 0);
  }

  public SkillMelee(int level, int currentXp) {
    super("Melee", level, currentXp);
    this.addAbilities(VAMPIRISM, SPIN_SLASH, DUAL_WIELDING, GRAND_SMITE);
  }

  @Override
  public List<String> getToolTip() {
    List<String> tooltip = new ArrayList<>();
    tooltip.add("Melee attacks deal §a" + Utils.formatPercentTwo(this.getExtraDamage()) + "%§r extra damage.");
    tooltip.add("Melee attacks have a §a" + Utils.formatPercent(this.getCritChance()) + "%§r chance to critically " +
        "strike.");
    return tooltip;
  }

  private double getExtraDamage() {
    return this.level * 0.01;
  }

  private double getCritChance() {
    return this.level * 0.005;
  }

  public static void applyMelee(LivingHurtEvent event) {
    DamageSource source = event.getSource();
    Entity entity = source.getTrueSource();
    if (!(entity instanceof EntityPlayer) || !source.damageType.equals("player")) return;

    EntityPlayer player = (EntityPlayer) entity;
    if (player.world.isRemote) return;

    SkillMelee melee = Skills.getSkill(player, Skills.MELEE, SkillMelee.class);
    event.setAmount(event.getAmount() + (float) (melee.getExtraDamage() * event.getAmount()));
    if (!Skrim.ALWAYS_CRIT && Math.random() >= melee.getCritChance()) {
      melee.addXp((EntityPlayerMP) player, (int) event.getAmount() * 10);
      return;
    }

    // Handle critical strike.
    EntityLivingBase targetEntity = event.getEntityLiving();
    player.world.playSound(null, player.posX, player.posY, player.posZ, SkrimSoundEvents.CRITICAL_HIT,
        player.getSoundCategory(), 1.0F, 1.0F);
    event.setAmount(event.getAmount() * 2);
    melee.addXp((EntityPlayerMP) player, (int) event.getAmount() * 10);

    // Spin slash / grand smite only work with swords.
    if (!(player.getHeldItemMainhand().getItem() instanceof ItemSword)) return;

    // Spin slash.
    if (melee.hasAbility(2)) {
      player.world.playSound(null, player.posX, player.posY, player.posZ,
          SkrimSoundEvents.SPIN_SLASH,
          player.getSoundCategory(), 1.0F, 1.0F);
      for (EntityLivingBase entitylivingbase : player.world.getEntitiesWithinAABB(EntityLivingBase.class,
          targetEntity.getEntityBoundingBox().expand(2.5D, 0.25D, 2.5D))) {
        if (entitylivingbase != player && entitylivingbase != targetEntity && !player.isOnSameTeam(entitylivingbase)
            && player.getDistanceSq(entitylivingbase) < 20.0D) {
          entitylivingbase.knockBack(player, 0.4F, (double) MathHelper.sin(player.rotationYaw * 0.017453292F),
              (double) (-MathHelper.cos(player.rotationYaw * 0.017453292F)));
          // Want to avoid an infinite player damage loop and want the damage to be affected by armor so we'll use cactus. :D
          entitylivingbase.attackEntityFrom(new EntityDamageSource("cactus", player), event.getAmount() / 4);
        }
      }
    }

    // Grand motherfucking smite.
    if (melee.hasAbility(4)) {
      EntityLightningBolt smite = new EntityLightningBolt(player.world, targetEntity.posX, targetEntity.posY,
          targetEntity.posZ,
          true);
      player.world.addWeatherEffect(smite);
      targetEntity.attackEntityFrom(new EntityDamageSource("lightningBolt", player), 20.0F);
    }
  }

  public static void tickLeft(PlayerTickEvent event) {
    SkillMelee melee = Skills.getSkill(event.player, Skills.MELEE, SkillMelee.class);
    melee.ticksSinceLastLeft++;
  }

  public static void handleKill(LivingDeathEvent event) {
    DamageSource source = event.getSource();
    Entity entity = source.getTrueSource();
    if (!(entity instanceof EntityPlayer) || !source.damageType.equals("player")) return;

    EntityPlayer player = (EntityPlayer) entity;
    if (player.world.isRemote) return;

    SkillMelee melee = Skills.getSkill(player, Skills.MELEE, SkillMelee.class);
    melee.addXp((EntityPlayerMP) player, Skills.entityKillXp(event.getEntity()));

    if (melee.hasAbility(1)) {
      player.heal(2);
    }
  }

  public static void handleDual(PlayerInteractEvent.RightClickItem event) {
    EntityPlayer player = event.getEntityPlayer();
    SkillMelee melee = Skills.getSkill(player, Skills.MELEE, SkillMelee.class);
    if (!melee.hasAbility(3)) return;

    if (!(player.getHeldItemMainhand().getItem() instanceof ItemSword) || (!(player.getHeldItemOffhand().getItem() instanceof ItemSword)))
      return;

    player.swingArm(EnumHand.OFF_HAND);
    if (!player.world.isRemote) return;

    Entity targetEntity = getMouseOver(player, 1.0F);
    if (targetEntity == null) return;

    SkrimPacketHandler.INSTANCE.sendToServer(new OffHandAttackPacket(player.getEntityId(),
        targetEntity.getEntityId()));
  }

  private float getCooldownPeriod(EntityPlayer player) {
    return (float) (1.0D / player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getAttributeValue() * 20.0D);
  }

  /**
   * Returns the percentage of attack power available based on the cooldown
   * (zero to one).
   */
  private float getCooledAttackStrength(EntityPlayer player, float adjustTicks) {
    return MathHelper.clamp(((float) this.ticksSinceLastLeft + adjustTicks) / this.getCooldownPeriod(player), 0.0F,
        1.0F);
  }

  // Everything below here is disgusting and shouldn't ever be touched again. Please God let me not have to touch it
  // again.

  /**
   * This function is basically a copy of the
   * attackTargetEntityWithCurrentItem function in EntityPlayer.java. So when
   * minecraft updates and this function breaks look their to fix it. We copy
   * the whole function to make attacking with the off-hand work.
   */
  public static void attackTargetEntityWithOffhandItem(EntityPlayer player, Entity targetEntity) {
    if (!net.minecraftforge.common.ForgeHooks.onPlayerAttackTarget(player, targetEntity)) {
      return;
    }
    if (targetEntity.canBeAttackedWithItem()) {
      if (!targetEntity.hitByEntity(player)) {
        ItemStack stack = player.getHeldItemOffhand();
        ItemSword sword = (ItemSword) stack.getItem();
        SkillMelee melee = Skills.getSkill(player, Skills.MELEE, SkillMelee.class);

        float f = (float) player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        float f1;

        if (targetEntity instanceof EntityLivingBase) {
          f1 = EnchantmentHelper.getModifierForCreature(player.getHeldItemOffhand(),
              ((EntityLivingBase) targetEntity).getCreatureAttribute());
        } else {
          f1 = EnchantmentHelper.getModifierForCreature(player.getHeldItemOffhand(), EnumCreatureAttribute.UNDEFINED);
        }

        float f2 = melee.getCooledAttackStrength(player, 0.5F);
        f = f * (0.2F + f2 * f2 * 0.8F);
        f1 = f1 * f2;
        melee.ticksSinceLastLeft = 0;

        if (f > 0.0F || f1 > 0.0F) {
          boolean flag = f2 > 0.9F;
          boolean flag1 = false;
          int i = 0;
          i = i + EnchantmentHelper.getKnockbackModifier(player);

          if (player.isSprinting() && flag) {
            player.world.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK,
                player.getSoundCategory(), 1.0F, 1.0F);
            ++i;
            flag1 = true;
          }

          boolean flag2 =
              flag && player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder() && !player.isInWater()
                  && !player.isPotionActive(MobEffects.BLINDNESS) && !player.isRiding() && targetEntity instanceof EntityLivingBase;
          flag2 = flag2 && !player.isSprinting();

          if (flag2) {
            f *= 1.5F;
          }

          f = f + f1;
          boolean flag3 = false;
          double d0 = (double) (player.distanceWalkedModified - player.prevDistanceWalkedModified);

          if (flag && !flag2 && !flag1 && player.onGround && d0 < (double) player.getAIMoveSpeed()) {
            ItemStack itemstack = player.getHeldItem(EnumHand.OFF_HAND);

            if (itemstack != null && itemstack.getItem() instanceof ItemSword) {
              flag3 = true;
            }
          }

          float f4 = 0.0F;
          boolean flag4 = false;
          int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_ASPECT, stack);

          if (targetEntity instanceof EntityLivingBase) {
            f4 = ((EntityLivingBase) targetEntity).getHealth();

            if (j > 0 && !targetEntity.isBurning()) {
              flag4 = true;
              targetEntity.setFire(1);
            }
          }

          double d1 = targetEntity.motionX;
          double d2 = targetEntity.motionY;
          double d3 = targetEntity.motionZ;
          boolean flag5 = targetEntity.attackEntityFrom(DamageSource.causePlayerDamage(player), f);
          if (flag5) {
            if (i > 0) {
              if (targetEntity instanceof EntityLivingBase) {
                ((EntityLivingBase) targetEntity).knockBack(player, (float) i * 0.5F,
                    (double) MathHelper.sin(player.rotationYaw * 0.017453292F),
                    (double) (-MathHelper.cos(player.rotationYaw * 0.017453292F)));
              } else {
                targetEntity.addVelocity((double) (-MathHelper.sin(player.rotationYaw * 0.017453292F) * (float) i * 0.5F), 0.1D,
                    (double) (MathHelper.cos(player.rotationYaw * 0.017453292F) * (float) i * 0.5F));
              }

              player.motionX *= 0.6D;
              player.motionZ *= 0.6D;
              player.setSprinting(false);
            }

            if (flag3) {
              for (EntityLivingBase entitylivingbase : player.world.getEntitiesWithinAABB(EntityLivingBase.class,
                  targetEntity.getEntityBoundingBox().expand(1.0D, 0.25D, 1.0D))) {
                if (entitylivingbase != player && entitylivingbase != targetEntity && !player.isOnSameTeam(entitylivingbase)
                    && player.getDistanceSq(entitylivingbase) < 9.0D) {
                  entitylivingbase.knockBack(player, 0.4F, (double) MathHelper.sin(player.rotationYaw * 0.017453292F),
                      (double) (-MathHelper.cos(player.rotationYaw * 0.017453292F)));
                  entitylivingbase.attackEntityFrom(DamageSource.causePlayerDamage(player), 1.0F);
                }
              }

              player.world.playSound(null, player.posX, player.posY, player.posZ,
                  SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP,
                  player.getSoundCategory(), 1.0F, 1.0F);
              player.spawnSweepParticles();
            }

            if (targetEntity instanceof EntityPlayerMP && targetEntity.velocityChanged) {
              ((EntityPlayerMP) targetEntity).connection.sendPacket(new SPacketEntityVelocity(targetEntity));
              targetEntity.velocityChanged = false;
              targetEntity.motionX = d1;
              targetEntity.motionY = d2;
              targetEntity.motionZ = d3;
            }

            if (flag2) {
              player.world.playSound(null, player.posX, player.posY, player.posZ,
                  SoundEvents.ENTITY_PLAYER_ATTACK_CRIT,
                  player.getSoundCategory(), 1.0F, 1.0F);
              player.onCriticalHit(targetEntity);
            }

            if (!flag2 && !flag3) {
              if (flag) {
                player.world.playSound(null, player.posX, player.posY, player.posZ,
                    SoundEvents.ENTITY_PLAYER_ATTACK_STRONG,
                    player.getSoundCategory(), 1.0F, 1.0F);
              } else {
                player.world.playSound(null, player.posX, player.posY, player.posZ,
                    SoundEvents.ENTITY_PLAYER_ATTACK_WEAK,
                    player.getSoundCategory(), 1.0F, 1.0F);
              }
            }

            if (f1 > 0.0F) {
              player.onEnchantmentCritical(targetEntity);
            }

            if (!player.world.isRemote && targetEntity instanceof EntityPlayer) {
              EntityPlayer entityplayer = (EntityPlayer) targetEntity;
              ItemStack itemstack2 = player.getHeldItemOffhand();
              ItemStack itemstack3 = entityplayer.isHandActive() ? entityplayer.getActiveItemStack() : null;
            }

            player.setLastAttackedEntity(targetEntity);

            if (targetEntity instanceof EntityLivingBase) {
              EnchantmentHelper.applyThornEnchantments((EntityLivingBase) targetEntity, player);
            }

            EnchantmentHelper.applyArthropodEnchantments(player, targetEntity);
            ItemStack itemstack1 = player.getHeldItemOffhand();
            Entity entity = targetEntity;

            if (targetEntity instanceof MultiPartEntityPart) {
              IEntityMultiPart ientitymultipart = ((MultiPartEntityPart) targetEntity).parent;

              if (ientitymultipart instanceof EntityLivingBase) {
                entity = (EntityLivingBase) ientitymultipart;
              }
            }

            if (itemstack1 != null && entity instanceof EntityLivingBase) {
              itemstack1.hitEntity((EntityLivingBase) entity, player);

              if (Obfuscation.getStackSize(itemstack1) <= 0) {
                player.setHeldItem(EnumHand.OFF_HAND, null);
                net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, itemstack1, EnumHand.OFF_HAND);
              }
            }

            if (targetEntity instanceof EntityLivingBase) {
              float f5 = f4 - ((EntityLivingBase) targetEntity).getHealth();
              player.addStat(StatList.DAMAGE_DEALT, Math.round(f5 * 10.0F));

              if (j > 0) {
                targetEntity.setFire(j * 4);
              }

              if (player.world instanceof WorldServer && f5 > 2.0F) {
                int k = (int) ((double) f5 * 0.5D);
                ((WorldServer) player.world).spawnParticle(EnumParticleTypes.DAMAGE_INDICATOR, targetEntity.posX,
                    targetEntity.posY + (double) (targetEntity.height * 0.5F), targetEntity.posZ, k, 0.1D, 0.0D, 0.1D
                    , 0.2D);
              }
            }

            player.addExhaustion(0.3F);
          } else {
            player.world.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE,
                player.getSoundCategory(), 1.0F, 1.0F);

            if (flag4) {
              targetEntity.extinguish();
            }
          }
        }
      }
    }
  }

  /**
   * This is basically a copy of the function in EntityRenderer, so when
   * minecraft updates and breaks everything look there to fix this function.
   * I believe this function is copied to make the attack action work with
   * off-hand weapons.
   */
  private static Entity getMouseOver(EntityPlayer player, float partialTicks) {
    Entity pointedEntity = null;
    Minecraft mc = Minecraft.getMinecraft();
    Entity entity = mc.getRenderViewEntity();

    if (player != null) {
      if (player.world != null) {
        mc.mcProfiler.startSection("pick");
        double d0 = (double) mc.playerController.getBlockReachDistance();
        mc.objectMouseOver = entity.rayTrace(d0, partialTicks);
        Vec3d vec3d = entity.getPositionEyes(partialTicks);
        boolean flag = false;
        int i = 3;
        double d1 = d0;

        if (mc.playerController.extendedReach()) {
          d1 = 6.0D;
          d0 = d1;
        } else {
          if (d0 > 3.0D) {
            flag = true;
          }
        }

        if (mc.objectMouseOver != null) {
          d1 = mc.objectMouseOver.hitVec.distanceTo(vec3d);
        }

        Vec3d vec3d1 = entity.getLook(partialTicks);
        Vec3d vec3d2 = vec3d.addVector(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0);
        pointedEntity = null;
        Vec3d vec3d3 = null;
        float f = 1.0F;
        List<Entity> list = mc.world.getEntitiesInAABBexcluding(entity,
            entity.getEntityBoundingBox().expand(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0).grow(1.0D, 1.0D, 1.0D),
            Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>() {
              public boolean apply(@Nullable Entity p_apply_1_) {
                return p_apply_1_ != null && p_apply_1_.canBeCollidedWith();
              }
            }));
        double d2 = d1;

        for (int j = 0; j < list.size(); ++j) {
          Entity entity1 = list.get(j);
          if (entity1 != player) {
            AxisAlignedBB axisalignedbb =
                entity1.getEntityBoundingBox().grow((double) entity1.getCollisionBorderSize());
            RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(vec3d, vec3d2);

            if (axisalignedbb.contains(vec3d)) {
              if (d2 >= 0.0D) {
                pointedEntity = entity1;
                vec3d3 = raytraceresult == null ? vec3d : raytraceresult.hitVec;
                d2 = 0.0D;
              }
            } else if (raytraceresult != null) {
              double d3 = vec3d.distanceTo(raytraceresult.hitVec);

              if (d3 < d2 || d2 == 0.0D) {
                if (entity1.getLowestRidingEntity() == entity.getLowestRidingEntity() && !entity.canRiderInteract()) {
                  if (d2 == 0.0D) {
                    pointedEntity = entity1;
                    vec3d3 = raytraceresult.hitVec;
                  }
                } else {
                  pointedEntity = entity1;
                  vec3d3 = raytraceresult.hitVec;
                  d2 = d3;
                }
              }
            }
          }

          if (pointedEntity != null && flag && vec3d.distanceTo(vec3d3) > 3.0D) {
            pointedEntity = null;
            mc.objectMouseOver = new RayTraceResult(RayTraceResult.Type.MISS, vec3d3, null,
                new BlockPos(vec3d3));
          }

          if (pointedEntity != null && (d2 < d1 || mc.objectMouseOver == null)) {
            mc.objectMouseOver = new RayTraceResult(pointedEntity, vec3d3);
          }

          mc.mcProfiler.endSection();
        }
      }
    }
    if (pointedEntity == player) {
      return null;
    } else {
      return pointedEntity;
    }
  }

}
