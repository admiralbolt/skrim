package avi.mod.skrim.skills.ranged;

import java.util.ArrayList;
import java.util.List;

import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.utils.Utils;
import net.minecraft.entity.Entity;
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
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class SkillRanged extends Skill implements ISkillRanged {

	public static SkillStorage<ISkillRanged> skillStorage = new SkillStorage<ISkillRanged>();

	public static float headshotBufferLow = 0.0F;
	public static float headshotBufferHigh = 2.2F;

	public SkillRanged() {
		this(1, 0);
	}

	public SkillRanged(int level, int currentXp) {
		super("Ranged", level, currentXp);
		this.iconTexture = new ResourceLocation("skrim", "textures/guis/skills/ranged.png");
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
					Entity targetEntity = event.getEntity();
					int addXp = 0;
					if (canHeadshot(targetEntity) && (targetEntity.posY + targetEntity.getEyeHeight() - headshotBufferLow < arrow.posY) && (targetEntity.posY + targetEntity.getEyeHeight() + headshotBufferHigh > arrow.posY)) {
						player.worldObj.playSound((EntityPlayer) null, targetEntity.getPosition(), SoundEvents.ENTITY_HORSE_ANGRY, player.getSoundCategory(), 1.0F, 1.0F);
						event.setAmount(event.getAmount() + (float) (ranged.getHeadshotDamage() * event.getAmount()));
						addXp = 25;
					}
					addXp += event.getAmount() * 3;
					ranged.addXp((EntityPlayerMP) player, addXp);
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

}