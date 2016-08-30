package avi.mod.skrim.skills.melee;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.ResourceLocation;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillStorage;

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

}
