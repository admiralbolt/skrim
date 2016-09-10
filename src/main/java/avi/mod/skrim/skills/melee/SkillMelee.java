package avi.mod.skrim.skills.melee;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class SkillMelee extends Skill implements ISkillMelee {

	public static SkillStorage<ISkillMelee> skillStorage = new SkillStorage<ISkillMelee>();
	public int ticksSinceLastLeft = 0;

	public SkillMelee() {
		this(1, 0);
	}

	public SkillMelee(int level, int currentXp) {
		super("Melee", level, currentXp);
	}

	public double getExtraDamage() {
		return this.level * 0.01;
	}

	public double getCritChance() {
		return this.level * 0.005;
	}

	@Override
	public List<String> getToolTip() {
		DecimalFormat fmt = new DecimalFormat("0.0");
		List<String> tooltip = new ArrayList<String>();
		tooltip.add("Melee attacks deal §a" + fmt.format(this.getExtraDamage() * 100) + "%§r extra damage.");
		tooltip.add("Melee attacks have a §a" + fmt.format(this.getCritChance() * 100) + "%§r chance to critically strike.");
		return tooltip;
	}

	public static void applyMelee(LivingHurtEvent event) {
		DamageSource source = event.getSource();
		Entity entity = source.getEntity();
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if (player != null && player.hasCapability(Skills.MELEE, EnumFacing.NORTH)) {
				if (source.damageType == "player") {
					SkillMelee melee = (SkillMelee) player.getCapability(Skills.MELEE, EnumFacing.NORTH);
					event.setAmount(event.getAmount() + (float) (melee.getExtraDamage() * event.getAmount()));
					int addXp = 0;
					if (Math.random() < melee.getCritChance()) {
						EntityLivingBase targetEntity = event.getEntityLiving();
						event.setAmount(event.getAmount() * 2);
						// Spin slash
						if (melee.hasAbility(2)) {
							ItemStack stack = player.getHeldItemMainhand();
							Item item = (stack == null) ? null : stack.getItem();
							if (item != null && item instanceof ItemSword) {
								for (EntityLivingBase entitylivingbase : player.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, targetEntity.getEntityBoundingBox().expand(2.5D, 0.25D, 2.5D))) {
									if (entitylivingbase != player && entitylivingbase != targetEntity && !player.isOnSameTeam(entitylivingbase) && player.getDistanceSqToEntity(entitylivingbase) < 20.0D) {
										entitylivingbase.knockBack(player, 0.4F, (double) MathHelper.sin(player.rotationYaw * 0.017453292F), (double) (-MathHelper.cos(player.rotationYaw * 0.017453292F)));
										// Want to avoid an infinite player damage loop, use generic damage source
										entitylivingbase.attackEntityFrom(DamageSource.generic, event.getAmount() / 4);
										addXp += event.getAmount() / 4;
									}
								}
							}
						}
						// Grand motherfucking smite
						if (melee.hasAbility(4)) {
							EntityLightningBolt smite = new EntityLightningBolt(player.worldObj, targetEntity.posX, targetEntity.posY, targetEntity.posZ, true);
							targetEntity.attackEntityFrom(source.lightningBolt, 100.0F);
							player.worldObj.addWeatherEffect(smite);
							if (!player.worldObj.isRemote) {
								BlockPos blockpos = new BlockPos(targetEntity);
                if (player.worldObj.getGameRules().getBoolean("doFireTick")
              		&& player.worldObj.isAreaLoaded(blockpos, 10)
              		&& player.worldObj.getBlockState(blockpos).getMaterial() == Material.AIR
              		&& Blocks.FIRE.canPlaceBlockAt(player.worldObj, blockpos)) {
                    player.worldObj.setBlockState(blockpos, Blocks.FIRE.getDefaultState());
                }
              }
						}
						addXp += 50;
					}
					addXp += (int) event.getAmount() * 10;
					melee.addXp((EntityPlayerMP) player, addXp);
				}
			}
		}
	}

	public float getCooldownPeriod(EntityPlayer player) {
		return (float) (1.0D / player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getAttributeValue() * 20.0D);
	}

	/**
	 * Returns the percentage of attack power available based on the cooldown (zero to one).
	 */
	public float getCooledAttackStrength(EntityPlayer player, float adjustTicks) {
		return MathHelper.clamp_float(((float) this.ticksSinceLastLeft + adjustTicks) / this.getCooldownPeriod(player), 0.0F, 1.0F);
	}

	public static void tickLeft(PlayerTickEvent event) {
		if (event.player.hasCapability(Skills.MELEE, EnumFacing.NORTH)) {
			SkillMelee melee = (SkillMelee) event.player.getCapability(Skills.MELEE, EnumFacing.NORTH);
			melee.ticksSinceLastLeft++;
		}
	}

	public static void handleKill(LivingDeathEvent event) {
		DamageSource source = event.getSource();
		Entity entity = source.getEntity();
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if (player != null && player.hasCapability(Skills.MELEE, EnumFacing.NORTH)) {
				if (source.damageType == "player") {
					SkillMelee melee = (SkillMelee) player.getCapability(Skills.MELEE, EnumFacing.NORTH);
					if (melee.hasAbility(1)) {
						player.setHealth(player.getHealth() + 2);
					}
				}
			}
		}
	}

	public static void handleDual(PlayerInteractEvent.RightClickItem event) {
		EntityPlayer player = event.getEntityPlayer();
		if (player != null && player.hasCapability(Skills.MELEE, EnumFacing.NORTH)) {
			SkillMelee melee = (SkillMelee) player.getCapability(Skills.MELEE, EnumFacing.NORTH);
			if (melee.hasAbility(3)) {
				ItemStack mainStack = player.getHeldItemMainhand();
				Item mainItem = (mainStack == null) ? null : mainStack.getItem();
				ItemStack offStack = player.getHeldItemOffhand();
				Item offItem = (offStack == null) ? null : offStack.getItem();
				if (mainItem != null && mainItem instanceof ItemSword && offItem != null && offItem instanceof ItemSword) {
					player.swingArm(EnumHand.OFF_HAND);
					Entity targetEntity = getMouseOver(player, 1.0F);
					if (targetEntity != null) {
						attackTargetEntityWithOffhandItem(player, targetEntity);
					}
				}
			}
		}
	}

	public static void attackTargetEntityWithOffhandItem(EntityPlayer player, Entity targetEntity) {
		if (!net.minecraftforge.common.ForgeHooks.onPlayerAttackTarget(player, targetEntity)) {
			return;
		}
		if (targetEntity.canBeAttackedWithItem()) {
			if (!targetEntity.hitByEntity(player)) {
				ItemStack stack = player.getHeldItemOffhand();
				ItemSword sword = (ItemSword) stack.getItem();
				sword.getDamageVsEntity();
				SkillMelee melee = (SkillMelee) player.getCapability(Skills.MELEE, EnumFacing.NORTH);

				float f = sword.getDamageVsEntity();
				float f1;

				if (targetEntity instanceof EntityLivingBase) {
					f1 = EnchantmentHelper.getModifierForCreature(player.getHeldItemOffhand(), ((EntityLivingBase) targetEntity).getCreatureAttribute());
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
						player.worldObj.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, player.getSoundCategory(), 1.0F, 1.0F);
						++i;
						flag1 = true;
					}

					boolean flag2 = flag && player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder() && !player.isInWater() && !player.isPotionActive(MobEffects.BLINDNESS) && !player.isRiding() && targetEntity instanceof EntityLivingBase;
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
					int j = EnchantmentHelper.getFireAspectModifier(player);

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
								((EntityLivingBase) targetEntity).knockBack(player, (float) i * 0.5F, (double) MathHelper.sin(player.rotationYaw * 0.017453292F), (double) (-MathHelper.cos(player.rotationYaw * 0.017453292F)));
							} else {
								targetEntity.addVelocity((double) (-MathHelper.sin(player.rotationYaw * 0.017453292F) * (float) i * 0.5F), 0.1D, (double) (MathHelper.cos(player.rotationYaw * 0.017453292F) * (float) i * 0.5F));
							}

							player.motionX *= 0.6D;
							player.motionZ *= 0.6D;
							player.setSprinting(false);
						}

						if (flag3) {
							for (EntityLivingBase entitylivingbase : player.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, targetEntity.getEntityBoundingBox().expand(1.0D, 0.25D, 1.0D))) {
								if (entitylivingbase != player && entitylivingbase != targetEntity && !player.isOnSameTeam(entitylivingbase) && player.getDistanceSqToEntity(entitylivingbase) < 9.0D) {
									entitylivingbase.knockBack(player, 0.4F, (double) MathHelper.sin(player.rotationYaw * 0.017453292F), (double) (-MathHelper.cos(player.rotationYaw * 0.017453292F)));
									entitylivingbase.attackEntityFrom(DamageSource.causePlayerDamage(player), 1.0F);
								}
							}

							player.worldObj.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0F, 1.0F);
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
							player.worldObj.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, player.getSoundCategory(), 1.0F, 1.0F);
							player.onCriticalHit(targetEntity);
						}

						if (!flag2 && !flag3) {
							if (flag) {
								player.worldObj.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, player.getSoundCategory(), 1.0F, 1.0F);
							} else {
								player.worldObj.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, player.getSoundCategory(), 1.0F, 1.0F);
							}
						}

						if (f1 > 0.0F) {
							player.onEnchantmentCritical(targetEntity);
						}

						if (!player.worldObj.isRemote && targetEntity instanceof EntityPlayer) {
							EntityPlayer entityplayer = (EntityPlayer) targetEntity;
							ItemStack itemstack2 = player.getHeldItemOffhand();
							ItemStack itemstack3 = entityplayer.isHandActive() ? entityplayer.getActiveItemStack() : null;
						}

						if (f >= 18.0F) {
							player.addStat(AchievementList.OVERKILL);
						}

						player.setLastAttacker(targetEntity);

						if (targetEntity instanceof EntityLivingBase) {
							EnchantmentHelper.applyThornEnchantments((EntityLivingBase) targetEntity, player);
						}

						EnchantmentHelper.applyArthropodEnchantments(player, targetEntity);
						ItemStack itemstack1 = player.getHeldItemOffhand();
						Entity entity = targetEntity;

						if (targetEntity instanceof EntityDragonPart) {
							IEntityMultiPart ientitymultipart = ((EntityDragonPart) targetEntity).entityDragonObj;

							if (ientitymultipart instanceof EntityLivingBase) {
								entity = (EntityLivingBase) ientitymultipart;
							}
						}

						if (itemstack1 != null && entity instanceof EntityLivingBase) {
							itemstack1.hitEntity((EntityLivingBase) entity, player);

							if (itemstack1.stackSize <= 0) {
								player.setHeldItem(EnumHand.OFF_HAND, (ItemStack) null);
								net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, itemstack1, EnumHand.OFF_HAND);
							}
						}

						if (targetEntity instanceof EntityLivingBase) {
							float f5 = f4 - ((EntityLivingBase) targetEntity).getHealth();
							player.addStat(StatList.DAMAGE_DEALT, Math.round(f5 * 10.0F));

							if (j > 0) {
								targetEntity.setFire(j * 4);
							}

							if (player.worldObj instanceof WorldServer && f5 > 2.0F) {
								int k = (int) ((double) f5 * 0.5D);
								((WorldServer) player.worldObj).spawnParticle(EnumParticleTypes.DAMAGE_INDICATOR, targetEntity.posX, targetEntity.posY + (double) (targetEntity.height * 0.5F), targetEntity.posZ, k, 0.1D, 0.0D, 0.1D, 0.2D, new int[0]);
							}
						}

						player.addExhaustion(0.3F);
					} else {
						player.worldObj.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, player.getSoundCategory(), 1.0F, 1.0F);

						if (flag4) {
							targetEntity.extinguish();
						}
					}
				}
			}
		}
	}

	public static Entity getMouseOver(EntityPlayer player, float partialTicks) {
		Entity pointedEntity = null;
		Minecraft mc = Minecraft.getMinecraft();
		Entity entity = mc.getRenderViewEntity();

		if (player != null) {
			if (player.worldObj != null) {
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
				Vec3d vec3d2 = vec3d.addVector(vec3d1.xCoord * d0, vec3d1.yCoord * d0, vec3d1.zCoord * d0);
				pointedEntity = null;
				Vec3d vec3d3 = null;
				float f = 1.0F;
				List<Entity> list = player.worldObj.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec3d1.xCoord * d0, vec3d1.yCoord * d0, vec3d1.zCoord * d0).expand(1.0D, 1.0D, 1.0D), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>() {
					public boolean apply(@Nullable Entity p_apply_1_) {
						return p_apply_1_ != null && p_apply_1_.canBeCollidedWith();
					}
				}));
				double d2 = d1;

				for (int j = 0; j < list.size(); ++j) {
					Entity entity1 = (Entity) list.get(j);
					if (entity1 != player) {
						AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expandXyz((double) entity1.getCollisionBorderSize());
						RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(vec3d, vec3d2);

						if (axisalignedbb.isVecInside(vec3d)) {
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
						mc.objectMouseOver = new RayTraceResult(RayTraceResult.Type.MISS, vec3d3, (EnumFacing) null, new BlockPos(vec3d3));
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
