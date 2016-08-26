package avi.mod.skrim.skills.ranged;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;

public class SkillRanged extends Skill implements ISkillRanged {

	public static SkillStorage<ISkillRanged> skillStorage = new SkillStorage<ISkillRanged>();

	public SkillRanged() {
		this(1, 0);
	}

	public SkillRanged(int level, int currentXp) {
		super("Ranged", level, currentXp);
		this.iconTexture = new ResourceLocation("skrim", "textures/guis/skills/ranged.png");
	}

	public double getExtraDamage() {
		return this.level * 0.01;
	}

	@Override
	public List<String> getToolTip() {
		DecimalFormat fmt = new DecimalFormat("0.0");
		List<String> tooltip = new ArrayList<String>();
		tooltip.add("Ranged attacks deal §a" + fmt.format(this.getExtraDamage() * 100) + "%§r extra damage.");
		return tooltip;
	}

	@SubscribeEvent
	public void onPlayerHurt(LivingHurtEvent event) {
		DamageSource source = event.getSource();
		Entity entity = source.getEntity();
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.RANGED, EnumFacing.NORTH)) {
				if (source.damageType == "arrow") {
					SkillRanged ranged = (SkillRanged) player.getCapability(Skills.RANGED, EnumFacing.NORTH);
					event.setAmount(event.getAmount() + (float) (this.getExtraDamage() * event.getAmount()));
					ranged.xp += (int) (event.getAmount() * 5);
					ranged.levelUp((EntityPlayerMP) player);
				}
			}
		}
	}

}
