package avi.mod.skrim.skills.melee;

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

public class SkillMelee extends Skill implements ISkillMelee {

	public static SkillStorage<ISkillMelee> skillStorage = new SkillStorage<ISkillMelee>();

	public SkillMelee() {
		this(1, 0);
	}

	public SkillMelee(int level, int currentXp) {
		super("Melee", level, currentXp);
		this.iconTexture = new ResourceLocation("skrim", "textures/guis/skills/melee.png");
	}

	public double getExtraDamage() {
		return this.level * 0.01;
	}

	@Override
	public List<String> getToolTip() {
		DecimalFormat fmt = new DecimalFormat("0.0");
		List<String> tooltip = new ArrayList<String>();
		tooltip.add("Melee attacks deal §a" + fmt.format(this.getExtraDamage() * 100) + "%§r extra damage.");
		return tooltip;
	}

	@SubscribeEvent
	public void onPlayerHurt(LivingHurtEvent event) {
		DamageSource source = event.getSource();
		Entity entity = source.getEntity();
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if (player != null && player.hasCapability(Skills.MELEE, EnumFacing.NORTH)) {
				if (source.damageType == "player") {
					SkillMelee melee = (SkillMelee) player.getCapability(Skills.MELEE, EnumFacing.NORTH);
					event.setAmount(event.getAmount() + (float) (this.getExtraDamage() * event.getAmount()));
					melee.xp += (int) (event.getAmount() * 5);
					melee.levelUp((EntityPlayerMP) player);
				}
			}
		}
	}

}
