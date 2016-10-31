package avi.mod.skrim.skills.ranged;

import java.util.ArrayList;
import java.util.List;

import avi.mod.skrim.blocks.ModBlocks;
import avi.mod.skrim.items.ModItems;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillAbility;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.cooking.SkillCooking;
import avi.mod.skrim.skills.demolition.SkillDemolition;
import avi.mod.skrim.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

public class SkillRanged extends Skill implements ISkillRanged {

	public static SkillStorage<ISkillRanged> skillStorage = new SkillStorage<ISkillRanged>();

	public static float headshotBufferLow = -0.1F;
	public static float headshotBufferHigh = 2.2F;
	public static int glowDuration = 120;

	public static SkillAbility sneakAttack = new SkillAbility(
		"Sneak Attack",
		25,
		"Surprise Motherfucker.",
		"Deal 25% extra damage to enemies while crouching & undetected."
	);

	public static SkillAbility faerieFire = new SkillAbility(
		"Faerie Fire",
		50,
		"Light that shit up.",
		"Your arrows automatically light up the enemy."
	);
	
	public static SkillAbility greatBow = new SkillAbility(
		"Great Bow",
		75,
		"gr8 bow m8, I r8 8/8.",
		"Gain the ability to craft a powerful great bow."
	);

	public SkillRanged() {
		this(1, 0);
	}

	public SkillRanged(int level, int currentXp) {
		super("Ranged", level, currentXp);
		this.iconTexture = new ResourceLocation("skrim", "textures/guis/skills/ranged.png");
		this.addAbilities(sneakAttack, faerieFire, greatBow);
	}

	public double getExtraDamage() {
		return this.level * 0.0075;
	}

	public double getHeadshotDamage() {
		return this.level * 0.0075;
	}

	@Override
	public List<String> getToolTip() {
		List<String> tooltip = new ArrayList<String>();
		tooltip.add("Ranged attacks deal §a" + Utils.formatPercentTwo(this.getExtraDamage()) + "%§r extra damage.");
		tooltip.add("Headshot deal §a" + Utils.formatPercentTwo(this.getHeadshotDamage()) + "%§r extra damage.");
		tooltip.add("§eYou'll know you've gotten a headshot when you hear horses.§r");
		return tooltip;
	}

	public static void applyRanged(LivingHurtEvent event) {
		DamageSource source = event.getSource();
		Entity entity = source.getEntity();
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.RANGED, EnumFacing.NORTH)) {
				if (source.isProjectile()) {
					SkillRanged ranged = (SkillRanged) player.getCapability(Skills.RANGED, EnumFacing.NORTH);
					event.setAmount(event.getAmount() + (float) (ranged.getExtraDamage() * event.getAmount()));
					Entity arrow = source.getSourceOfDamage();
					EntityLivingBase targetEntity = event.getEntityLiving();
					int addXp = 0;
					if (canHeadshot(targetEntity) && (targetEntity.posY + targetEntity.getEyeHeight() - headshotBufferLow < arrow.posY) && (targetEntity.posY + targetEntity.getEyeHeight() + headshotBufferHigh > arrow.posY)) {
						player.worldObj.playSound((EntityPlayer) null, targetEntity.getPosition(), SoundEvents.ENTITY_HORSE_ANGRY, player.getSoundCategory(), 1.0F, 1.0F);
						event.setAmount(event.getAmount() + (float) (ranged.getHeadshotDamage() * event.getAmount()));
						addXp = 50;
					}
					if (ranged.hasAbility(1)) {
						if (player.isSneaking() && targetEntity.getAITarget() != player) {
							event.setAmount((float) (event.getAmount() * 1.25));
							player.worldObj.playSound((EntityPlayer) null, player.getPosition(), SoundEvents.BLOCK_NOTE_PLING, player.getSoundCategory(), 1.0F, 1.0F);
							addXp += 50;
						}
						if (ranged.hasAbility(2)) {
							targetEntity.addPotionEffect(new PotionEffect(MobEffects.GLOWING, glowDuration, 0, true, false));
						}
					}
					addXp += event.getAmount() * 10;
					ranged.addXp((EntityPlayerMP) player, addXp);
				}
			}
		}
	}
	
	public static void handleKill(LivingDeathEvent event) {
		DamageSource source = event.getSource();
		Entity entity = source.getEntity();
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
		return (entity instanceof EntityPlayer
				|| entity instanceof EntityVillager
				|| entity instanceof EntityZombie
				|| entity instanceof EntitySkeleton
				|| entity instanceof EntityCreeper
				|| entity instanceof EntityDragon
				|| entity instanceof EntitySpider
				|| entity instanceof EntityBlaze
				|| entity instanceof EntityWitch
				|| entity instanceof EntityWolf
				|| entity instanceof EntityOcelot
			);
	}
	
	public static void verifyItems(ItemCraftedEvent event) {
		Item targetItem = event.crafting.getItem();
		if (targetItem != null && targetItem == ModItems.GREAT_BOW) {
			if (!Skills.canCraft(event.player, Skills.RANGED, 75)) {
				Skills.replaceWithComponents(event);
			} else if (!event.player.worldObj.isRemote && event.player.hasCapability(Skills.RANGED, EnumFacing.NORTH)) {
				SkillRanged ranged = (SkillRanged) event.player.getCapability(Skills.RANGED, EnumFacing.NORTH);
				ranged.addXp((EntityPlayerMP) event.player, 1000);
			}
		}
	}
	
	

}
