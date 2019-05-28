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
        "Potions take §a" + Utils.formatPercent(this.brewSpeed()) + "%§r less time to brew.",
        "You can apply strength/duration modifications §a" + this.totalModifiers() + "§r times.");
  }

  public int totalModifiers() {
    return 1 + this.level / 10;
  }

  public double brewSpeed() {
    return Math.min(this.level * 0.005, 1.00);
  }

}
