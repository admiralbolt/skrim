package avi.mod.skrim.skills.defense;

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
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;

public class SkillDefense extends Skill implements ISkillDefense {

	public static SkillStorage<ISkillDefense> skillStorage = new SkillStorage<ISkillDefense>();

	public SkillDefense() {
		this(1, 0);
	}

	public SkillDefense(int level, int currentXp) {
		super("Defense", level, currentXp);
		this.iconTexture = new ResourceLocation("skrim", "textures/guis/skills/defense.png");
	}

	public double getDamageReduction() {
		return this.level * 0.0075;
	}

	public int getXp(float amount) {
		return (int) (amount * 10);
	}

	@Override
	public List<String> getToolTip() {
		DecimalFormat fmt = new DecimalFormat("0.00");
		List<String> tooltip = new ArrayList<String>();
		tooltip.add("Take §a" + fmt.format(this.getDamageReduction() * 100) + "%§r less damage from mob.");
		return tooltip;
	}

	public static void reduceDamage(LivingHurtEvent event) {
		DamageSource source = event.getSource();
		Entity entity = event.getEntity();
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.DEFENSE, EnumFacing.NORTH)) {
				if (source.damageType == "mob") {
					SkillDefense defense = (SkillDefense) player.getCapability(Skills.DEFENSE, EnumFacing.NORTH);
					defense.addXp((EntityPlayerMP) player, defense.getXp(event.getAmount()));
					event.setAmount(event.getAmount() - (float) (defense.getDamageReduction() * event.getAmount()));
				}
			}
		}
	}

}
