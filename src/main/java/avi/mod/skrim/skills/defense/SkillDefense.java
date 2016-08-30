package avi.mod.skrim.skills.defense;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.ResourceLocation;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillStorage;

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

}
