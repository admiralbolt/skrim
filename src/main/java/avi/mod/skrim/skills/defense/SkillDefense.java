package avi.mod.skrim.skills.defense;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import avi.mod.skrim.client.gui.ArmorOverlay;
import avi.mod.skrim.network.LevelUpPacket;
import avi.mod.skrim.network.SkillPacket;
import avi.mod.skrim.network.SkrimPacketHandler;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillAbility;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.utils.Reflection;
import avi.mod.skrim.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class SkillDefense extends Skill implements ISkillDefense {

	public static SkillStorage<ISkillDefense> skillStorage = new SkillStorage<ISkillDefense>();

	private static double healthPercent = 0.3;
	private static int regenLength = 15 * 20;
	private static int CAPTAIN_DURATION = 80;
	private static int CAPTAIN_RANGE = 6;
	private static int STALWART_ACTIVATION_TICKS = 60;

	public int ticks = 0;
	public int stalwartTicks = 0;
	public boolean canRegen = true;
	public boolean shouldUpdateAttribute = true;
	private Set<Integer> blocked = new HashSet<Integer>();

	public SkillDefense() {
		this(1, 0);
	}

	public static SkillAbility RITE_OF_PASSAGE = new SkillAbility("Rite of Passage", 25, "It's a reference to a magic card, so you probably missed it.", "Falling below 30% health activates a period of regeneration.", "You must fully heal before regeneration will activate again.");

	public static SkillAbility CAPTAIN = new SkillAbility("Captain", 50, "Leader of the pack.  Vroom.", "Provide protection to allies in a §a" + CAPTAIN_RANGE + "§r radius.");

	public static SkillAbility GOLEMS_ASPECT = new SkillAbility("Aspect of the Golem", 75, "The new spell resistance.", "Negative status effects last for half as long.");

	public static SkillAbility STALWART_STANCE = new SkillAbility("Stalwart Stance", 100, "That tickles.", "Crouching and blocking with a shield for at least 3 seconds grants invulnerability.");
	
	public SkillDefense(int level, int currentXp) {
		super("Defense", level, currentXp);
		this.iconTexture = new ResourceLocation("skrim", "textures/guis/skills/defense.png");
		this.addAbilities(RITE_OF_PASSAGE, CAPTAIN, GOLEMS_ASPECT, STALWART_STANCE);
	}

	public double getDamageReduction() {
		return this.level * 0.005;
	}

	public int getExtraHealth() {
		return (int) (this.level / 5);
	}

	public int getDamageXp(float amount) {
		return (int) (amount * 20);
	}

	public int getBlockingXp(float amount) {
		return (int) (amount * 2 * 80);
	}

	@Override
	public void levelUp(EntityPlayerMP player) {
		if (this.canLevelUp()) {
			this.level++;
			this.shouldUpdateAttribute = true;
			SkrimPacketHandler.INSTANCE.sendTo(new LevelUpPacket(this.name, this.level), player);
		}
		SkrimPacketHandler.INSTANCE.sendTo(new SkillPacket(this.name, this.level, this.xp), player);
	}

	@Override
	public List<String> getToolTip() {
		List<String> tooltip = new ArrayList<String>();
		tooltip.add("Take §a" + Utils.formatPercent(this.getDamageReduction()) + "%§r less damage from mob and players.");
		tooltip.add("Gain an additional §a" + this.getExtraHealth() + "§r max health.");
		return tooltip;
	}

	public static void applyDefense(LivingHurtEvent event) {
		DamageSource source = event.getSource();
		Entity entity = event.getEntity();
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.DEFENSE, EnumFacing.NORTH)) {
				SkillDefense defense = (SkillDefense) player.getCapability(Skills.DEFENSE, EnumFacing.NORTH);
				if (source.damageType == "mob" || source.damageType == "player" || source.damageType.equals("arrow")) {
					EntityLivingBase baseEntity = event.getEntityLiving();
					source.getEntity().getEntityId();
					int addXp = defense.getDamageXp(event.getAmount());
					if (canBlockDamageSource(player, source)) {
						if (source.isProjectile()) {
							int sourceId = source.getEntity().getEntityId();
							addXp += (defense.blocked.contains(sourceId)) ? 25 : 300;
							defense.blocked.add(sourceId);
						} else {
							addXp += defense.getBlockingXp(event.getAmount());
						}
					}
					defense.addXp((EntityPlayerMP) player, addXp);
					event.setAmount(event.getAmount() - (float) (defense.getDamageReduction() * event.getAmount()));
				}
			}
		}
	}

	public static void update(LivingUpdateEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if (!player.worldObj.isRemote) {
				if (player != null && player.hasCapability(Skills.DEFENSE, EnumFacing.NORTH)) {
					SkillDefense defense = (SkillDefense) player.getCapability(Skills.DEFENSE, EnumFacing.NORTH);
					if (defense.hasAbility(1)) {
						if (defense.canRegen && player.getHealth() <= (float) (defense.healthPercent * player.getMaxHealth())) {
							PotionEffect activeEffect = player.getActivePotionEffect(MobEffects.REGENERATION);
							if (activeEffect != null) {
								activeEffect.combine(new PotionEffect(MobEffects.REGENERATION, regenLength));
							} else {
								player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, regenLength));
							}
							defense.canRegen = false;
						} else if (!defense.canRegen && player.getHealth() == player.getMaxHealth()) {
							defense.canRegen = true;
						}

						if (defense.hasAbility(2)) {
							if (player.worldObj.getWorldTime() % 60L == 0L) {
								BlockPos pos = player.getPosition();
								int x = pos.getX();
								int y = pos.getY();
								int z = pos.getZ();
								AxisAlignedBB bound = new AxisAlignedBB(x - CAPTAIN_RANGE, y - CAPTAIN_RANGE, z - CAPTAIN_RANGE, x + CAPTAIN_RANGE, y + CAPTAIN_RANGE, z + CAPTAIN_RANGE);

								List<EntityPlayer> players = player.worldObj.getEntitiesWithinAABB(EntityPlayer.class, bound);
								for (EntityPlayer playa : players) {
									PotionEffect activeResistance = player.getActivePotionEffect(MobEffects.RESISTANCE);
									PotionEffect addResistance = new PotionEffect(MobEffects.RESISTANCE, CAPTAIN_DURATION, 0, false, false);
									if (activeResistance != null) {
										activeResistance.combine(addResistance);
									} else {
										playa.addPotionEffect(addResistance);
									}
								}
							}
						}

						if (defense.hasAbility(3)) {
							Collection<PotionEffect> effects = player.getActivePotionEffects();
							for (PotionEffect effect : effects) {
								if (Utils.isNegativeEffect(effect)) {
									Reflection.hackValueTo(effect, effect.getDuration() - 1, "duration", "field_76460_b");
									effect.combine(effect);
								}
							}

							if (defense.hasAbility(4)) {
								if (player.worldObj.getTotalWorldTime() % 20L == 0L) {
									if (!player.worldObj.isRemote) {
										boolean stance = (player.isSneaking() && isBlocking(player));
										defense.stalwartTicks = (stance) ? defense.stalwartTicks + 20 : 0;
										player.setEntityInvulnerable(defense.stalwartTicks >= STALWART_ACTIVATION_TICKS);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public static boolean isBlocking(EntityPlayer player) {
		if (player != null) {
			ItemStack stack = player.getActiveItemStack();
			System.out.println("stack");
			if (stack != null) {
				Item item = stack.getItem();
				if (item != null) {
					if (item == Items.SHIELD) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public Entry<IAttribute, AttributeModifier> getAttributeModifier() {
		if (this.shouldUpdateAttribute) {
			this.shouldUpdateAttribute = false;
			return new AbstractMap.SimpleEntry<IAttribute, AttributeModifier>(SharedMonsterAttributes.MAX_HEALTH, new AttributeModifier(UUID.fromString("5D6F0BA2-1186-46AC-B896-C61C5CEE99CC"), "skrim-overshields", (double) this.getExtraHealth(), 0));
		}
		return null;
	}

	public static void renderArmor(RenderGameOverlayEvent.Pre event) {
		if (event.getType() == ElementType.ARMOR) {
			event.setCanceled(true);
			new ArmorOverlay(Minecraft.getMinecraft());
		}
	}

	private static boolean canBlockDamageSource(EntityPlayer player, DamageSource damageSourceIn) {
		if (!damageSourceIn.isUnblockable() && player.isActiveItemStackBlocking()) {
			Vec3d vec3d = damageSourceIn.getDamageLocation();

			if (vec3d != null) {
				Vec3d vec3d1 = player.getLook(1.0F);
				Vec3d vec3d2 = vec3d.subtractReverse(new Vec3d(player.posX, player.posY, player.posZ)).normalize();
				vec3d2 = new Vec3d(vec3d2.xCoord, 0.0D, vec3d2.zCoord);

				if (vec3d2.dotProduct(vec3d1) < 0.0D) {
					return true;
				}
			}
		}
		return false;
	}

}
