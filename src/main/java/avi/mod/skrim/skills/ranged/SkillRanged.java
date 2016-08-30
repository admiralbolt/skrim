package avi.mod.skrim.skills.ranged;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.ResourceLocation;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillStorage;

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

}
