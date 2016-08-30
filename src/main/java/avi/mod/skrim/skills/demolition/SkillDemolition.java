package avi.mod.skrim.skills.demolition;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.ResourceLocation;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillStorage;

public class SkillDemolition extends Skill implements ISkillDemolition {

	public static SkillStorage<ISkillDemolition> skillStorage = new SkillStorage<ISkillDemolition>();

	public SkillDemolition() {
		this(1, 0);
	}

	public SkillDemolition(int level, int currentXp) {
		super("Demolition", level, currentXp);
		this.iconTexture = new ResourceLocation("skrim", "textures/guis/skills/demolition.png");
	}

	public double getResistance() {
		return this.level * 0.01;
	}

	public double getExplosionChance(int extra) {
		return this.level * 0.01 - ((extra - 2) * 0.1);
	}

	public int getMaxAdditional() {
		return (int) ((this.level - 1) / 10);
	}

	@Override
	public List<String> getToolTip() {
		DecimalFormat fmt = new DecimalFormat("0.0");
		List<String> tooltip = new ArrayList<String>();
		tooltip.add("Passively gain §a" + fmt.format(this.getResistance() * 100) + "%§r explosive resistance.");
		int maxAdditional = this.getMaxAdditional();
		for (int i = 0; i <= maxAdditional; i++) {
			tooltip.add("Your TNT has a §a" + fmt.format(this.getExplosionChance(2 + i) * 100) + "%§r chance to cause an §aadditional explosion§r.");
		}
		return tooltip;
	}

}
