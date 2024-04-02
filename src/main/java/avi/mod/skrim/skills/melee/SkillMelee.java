package avi.mod.skrim.skills.melee;

import avi.mod.skrim.SkrimGlobalConfig;
import avi.mod.skrim.init.SkrimSoundEvents;
import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillAbility;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

import java.util.ArrayList;
import java.util.List;

public class SkillMelee extends Skill implements ISkillMelee {

  public static SkillStorage<ISkillMelee> skillStorage = new SkillStorage<ISkillMelee>();

  private static SkillAbility VAMPIRISM = new SkillAbility("melee", "Vampirism", 25, "What I need is your blood. What" +
      " I don't need is your permission.",
      "Killing an enemy restores §a1" + SkillAbility.DESC_COLOR + " heart.");
  private static SkillAbility SPIN_SLASH = new SkillAbility("melee", "Spin Slash", 50, "Spin to win.",
          "Critical hits apply 1/4 damage in a large radius.");
  private static SkillAbility HAMSTRING = new SkillAbility("melee", "Hamstring", 75,
      "Not the hammies!",
      "Critical hits apply a slow based on damage dealt.");
  private static SkillAbility GRAND_SMITE = new SkillAbility("melee", "Grand Smite", 100, "DESTRUUUUCTIOOONNN",
      "Critical hits call down lightning.");

  private int ticksSinceLastLeft = 0;

  public SkillMelee() {
    this(1, 0);
  }

  public SkillMelee(int level, int currentXp) {
    super("Melee", level, currentXp);
    this.addAbilities(VAMPIRISM, SPIN_SLASH, HAMSTRING, GRAND_SMITE);
  }

  @Override
  public List<String> getToolTip() {
    List<String> tooltip = new ArrayList<>();
    if (this.skillEnabled) {
      tooltip.add("Melee attacks deal §a" + Utils.formatPercentTwo(this.getExtraDamage()) + "%§r extra damage.");
      tooltip.add("Melee attacks have a §a" + Utils.formatPercent(this.getCritChance()) + "%§r chance to critically " +
          "strike.");
    } else {
      tooltip.add(Skill.COLOR_DISABLED + "Melee attacks deal " + Utils.formatPercentTwo(this.getExtraDamage()) + "% extra damage.");
      tooltip.add(Skill.COLOR_DISABLED + "Melee attacks have a §a" + Utils.formatPercent(this.getCritChance()) + "% chance to critically " +
          "strike.");
    }
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
    // Should probably add this check, but I won't.
    // if (source.isProjectile()) return;

    Entity entity = source.getTrueSource();
    System.out.println(entity);
    System.out.println(source.damageType);
    if (!(entity instanceof EntityPlayer) || !source.damageType.equals("player")) return;

    EntityPlayer player = (EntityPlayer) entity;
    if (player.world.isRemote) return;

    SkillMelee melee = Skills.getSkill(player, Skills.MELEE, SkillMelee.class);

    if (melee.skillEnabled) {
      event.setAmount(event.getAmount() + (float) (melee.getExtraDamage() * event.getAmount()));
    }
    if (!SkrimGlobalConfig.ALWAYS_CRIT.value && Math.random() >= melee.getCritChance()) {
      melee.addXp((EntityPlayerMP) player, (int) event.getAmount() * 10);
      return;
    }

    if (!melee.skillEnabled) return;

    // Handle critical strike.
    EntityLivingBase targetEntity = event.getEntityLiving();
    player.world.playSound(null, player.getPosition(), SkrimSoundEvents.CRITICAL_HIT, SoundCategory.PLAYERS, 1.0f, 1.0f);
    event.setAmount(event.getAmount() * 2);
    // Hai there its me pure nail.
    if (player.getHeldItemMainhand().getItem() == SkrimItems.PURE_NAIL) {
      event.setAmount(event.getAmount() * 2);
    }
    melee.addXp((EntityPlayerMP) player, (int) event.getAmount() * 10);

    PotionEffect newEffect = new PotionEffect(MobEffects.SLOWNESS, 200 + (int) (2 * event.getAmount()), Math.max(1, Math.round(event.getAmount() / 10)));

    // Spin slash.
    if (melee.activeAbility(2)) {
      player.world.playSound(null, player.posX, player.posY, player.posZ,
          SkrimSoundEvents.SPIN_SLASH,
          player.getSoundCategory(), 1.0F, 1.0F);
      for (EntityLivingBase entitylivingbase : player.world.getEntitiesWithinAABB(EntityLivingBase.class,
          targetEntity.getEntityBoundingBox().expand(3D, 0.5D, 3D).expand(-3D, -0.5D, -3D))) {
        if (entitylivingbase != player && entitylivingbase != targetEntity && !player.isOnSameTeam(entitylivingbase)) {
          entitylivingbase.knockBack(player, 0.4F, (double) MathHelper.sin(player.rotationYaw * 0.017453292F),
              (double) (-MathHelper.cos(player.rotationYaw * 0.017453292F)));
          // Want to avoid an infinite player damage loop and want the damage to be affected by armor so we'll use cactus. :D
          entitylivingbase.attackEntityFrom(new EntityDamageSource("cactus", player), event.getAmount() / 4);
          if (melee.activeAbility(3)) {
            Utils.addOrCombineEffect(entitylivingbase, newEffect);
          }
        }
      }

      if (melee.activeAbility(3)) {
        Utils.addOrCombineEffect(targetEntity, newEffect);
      }
    }

    // Grand motherfucking smite.
    if (melee.activeAbility(4)) {
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

    if (melee.activeAbility(1)) {
      player.heal(2);
    }
  }

}
