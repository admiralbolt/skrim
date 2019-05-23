package avi.mod.skrim.skills.brewing;

import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.utils.Utils;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class SkillBrewing extends Skill implements ISkillBrewing {

  public static SkillStorage<ISkillBrewing> STORAGE = new SkillStorage<>();

  public SkillBrewing() {
    this(1, 0);
  }

  public SkillBrewing(int level, int xp) {
    super("Brewing", level, xp);
  }

  @Override
  public List<String> getToolTip() {
    return ImmutableList.of(
        "Potions have §a+" + Utils.formatPercent(this.extraDuration()) + "%§r extra duration.",
        "Potions take §a" + Utils.formatPercent(this.brewSpeed()) + "%§r less time to brew.");
  }

  public double extraDuration() {
    return this.level * 0.01;
  }

  public double brewSpeed() {
    return Math.min(this.level * 0.005, 1.00);
  }

}
