package avi.mod.skrim.skills.ranged;

import java.util.ArrayList;
import java.util.List;

import avi.mod.skrim.init.SkrimSoundEvents;
import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.network.SkrimPacketHandler;
import avi.mod.skrim.network.skillpackets.CriticalAscensionPacket;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillAbility;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

public class SkillRanged extends Skill implements ISkillRanged {

	private static int MAX_STACKS = 999;

	public static SkillStorage<ISkillRanged> skillStorage = new SkillStorage<ISkillRanged>();

	public static float headshotBufferLow = 0.05F;
	public static float headshotBufferHigh = 2.2F;
	public static int glowDuration = 120;

	public static SkillAbility SNEAK_ATTACK = new SkillAbility("ranged", "Sneak Attack", 25, "Surprise Motherfucker.",
			"Deal 25% extra damage to enemies while crouching & undetected.");

	public static SkillAbility FAERIE_FIRE = new SkillAbility("ranged", "Faerie Fire", 50, "Light that shit up.",
			"Your arrows automatically light up the enemy.");

	public static SkillAbility GREAT_BOW = new SkillAbility("ranged", "Great Bow", 75, "gr8 bow m8, I r8 8/8.",
			"Gain the ability to craft a powerful great bow.");

	public static SkillAbility CRITICAL_ASCENSION = new SkillAbility("ranged", "Critical Ascension", 100, "Boom, Headshot.",
			"Getting a head shot grants a stack of accuracy.", "Missing a head shot removes 2 stacks of accuracy.", "Dying removes all stacks of accuracy.",
			"Each stack of accuracy grants §a+0.5%" + SkillAbility.DESC_COLOR + " ranged damage AND headshot damage.");

	private int accuracyStacks = 0;

	public SkillRanged() {
		this(1, 0);
	}

	public SkillRanged(int level, int currentXp) {
		super("Ranged", level, currentXp);
		this.addAbilities(SNEAK_ATTACK, FAERIE_FIRE, GREAT_BOW, CRITICAL_ASCENSION);
	}

	private double getExtraDamage() {
		return this.level * 0.0075 + this.accuracyStacks * 0.005;
	}

	private double getHeadshotDamage() {
		return this.level * 0.0075 + this.accuracyStacks * 0.005;
	}

	public int getStacks() {
		return this.accuracyStacks;
	}

	public void setStacks(int stacks) {
		this.accuracyStacks = stacks;
		this.verifyStacks();
	}

	public void addStacks(int add) {
		this.accuracyStacks += add;
		this.verifyStacks();
	}

	private void verifyStacks() {
		if (this.accuracyStacks < 0) {
			this.accuracyStacks = 0;
		} else if (this.accuracyStacks > MAX_STACKS) {
			this.accuracyStacks = MAX_STACKS;
		}
	}

	@Override
	public List<String> getToolTip() {
		List<String> tooltip = new ArrayList<>();
		tooltip.add("Ranged attacks deal §a" + Utils.formatPercentTwo(this.getExtraDamage()) + "%§r extra damage.");
		tooltip.add("Headshot deal §a" + Utils.formatPercentTwo(this.getHeadshotDamage()) + "%§r extra damage.");
		tooltip.add("§eYou'll know you've gotten a headshot when you hear horses.§r");
		return tooltip;
	}

	public static void applyRanged(LivingHurtEvent event) {
		DamageSource source = event.getSource();
		Entity entity = source.getTrueSource();
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.RANGED, EnumFacing.NORTH)) {
				if (source.isProjectile()) {
					SkillRanged ranged = (SkillRanged) player.getCapability(Skills.RANGED, EnumFacing.NORTH);
					Utils.logSkillEvent(event, ranged, "Base Damage: " + event.getAmount());
					event.setAmount(event.getAmount() + (float) (ranged.getExtraDamage() * event.getAmount()));
					Entity arrow = source.getImmediateSource();
					EntityLivingBase targetEntity = event.getEntityLiving();
					int addXp = 0;
					if (canHeadshot(targetEntity) && (targetEntity.posY + targetEntity.getEyeHeight() - headshotBufferLow < arrow.posY)
							&& (targetEntity.posY + targetEntity.getEyeHeight() + headshotBufferHigh > arrow.posY)) {
						player.world.playSound((EntityPlayer) null, player.getPosition(), SkrimSoundEvents.HEAD_SHOT, player.getSoundCategory(), 0.1F,
								0.6F);
						event.setAmount(event.getAmount() + (float) (ranged.getHeadshotDamage() * event.getAmount()));
						addXp = 50;
						if (ranged.hasAbility(4)) {
							if (Skills.entityKillXp(event.getEntity()) > 0) {
								ranged.addStacks(1);
							}
						}
					} else if (canHeadshot(targetEntity) && Skills.entityKillXp(event.getEntity()) > 0 && ranged.hasAbility(4)) {
						ranged.addStacks(-2);
					}
					if (ranged.hasAbility(1)) {
						if (player.isSneaking() && targetEntity.getAttackingEntity() != player) {
							event.setAmount((float) (event.getAmount() * 1.25));
							player.world.playSound((EntityPlayer) null, player.getPosition(), SkrimSoundEvents.SNEAK_ATTACK, player.getSoundCategory(), 0.2F,
									1.0F);
							addXp += 50;
						}
						if (ranged.hasAbility(2)) {
							targetEntity.addPotionEffect(new PotionEffect(MobEffects.GLOWING, glowDuration, 0, true, false));
						}
						if (ranged.hasAbility(4)) {
							SkrimPacketHandler.INSTANCE.sendTo(new CriticalAscensionPacket(ranged.getStacks()), (EntityPlayerMP) player);
						}
					}
					Utils.logSkillEvent(event, ranged, "Modified Damage: " + event.getAmount());
					addXp += event.getAmount() * 10;
					ranged.addXp((EntityPlayerMP) player, addXp);
				}
			}
		}
	}

	public static void handleKill(LivingDeathEvent event) {
		DamageSource source = event.getSource();
		Entity entity = source.getTrueSource();
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if (player != null && player.hasCapability(Skills.RANGED, EnumFacing.NORTH)) {
				if (source.isProjectile()) {
					SkillRanged ranged = (SkillRanged) player.getCapability(Skills.RANGED, EnumFacing.NORTH);
					int killXp = Skills.entityKillXp(event.getEntity());
					if (killXp > 0) {
						ranged.addXp((EntityPlayerMP) player, killXp);
					}
				}
			}
		}
	}

	public static boolean canHeadshot(Entity entity) {
		return !(entity instanceof EntitySlime || entity instanceof EntityMagmaCube || entity instanceof EntitySilverfish);
	}

	public static void verifyItems(ItemCraftedEvent event) {
		Item targetItem = event.crafting.getItem();
		if (targetItem != null && targetItem == SkrimItems.GREAT_BOW) {
			if (!Skills.canCraft(event.player, Skills.RANGED, 75)) {
				Skills.replaceWithComponents(event);
			} else if (!event.player.world.isRemote && event.player.hasCapability(Skills.RANGED, EnumFacing.NORTH)) {
				SkillRanged ranged = (SkillRanged) event.player.getCapability(Skills.RANGED, EnumFacing.NORTH);
				ranged.addXp((EntityPlayerMP) event.player, 1000);
			}
		}
	}

}
