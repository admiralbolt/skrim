package avi.mod.skrim.skills.fishing;

import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillAbility;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Most of the interesting logic of the fishing skill is actually in the fishing hook entity itself.
 * See the SkrimFishHook.java.
 */
public class SkillFishing extends Skill implements ISkillFishing {

  public static SkillStorage<ISkillFishing> skillStorage = new SkillStorage<>();

  private static SkillAbility BATMAN = new SkillAbility("fishing", "Batman", 25, "na na na na na na na na",
      "Your fishing rod can now be used as a grappling hook.");

  private static SkillAbility TRIPLE_HOOK = new SkillAbility("fishing", "Triple Hook", 50, "Triple the hooks, triple " +
      "the pleasure.",
      "You now catch §a3x" + SkillAbility.DESC_COLOR + " as many items.");

  private static SkillAbility BOUNTIFUL_CATCH = new SkillAbility("fishing", "Bountiful Catch", 75, "On that E-X-P " +
      "grind.",
      "Catching a fish provides an additional§a 9-24" + SkillAbility.DESC_COLOR + " xp.");

  private static SkillAbility FLING = new SkillAbility("fishing", "Fling", 100, "Sometimes I don't know my own " +
      "strength.",
      "Launch hooked entities into the air.");

  public SkillFishing() {
    this(1, 0);
  }

  private SkillFishing(int level, int currentXp) {
    super("Fishing", level, currentXp);
    this.addAbilities(BATMAN, TRIPLE_HOOK, BOUNTIFUL_CATCH, FLING);
  }

  @Override
  public List<String> getToolTip() {
    List<String> tooltip = new ArrayList<String>();
    tooltip.add("§a" + Utils.formatPercentTwo(this.getDelayReduction()) + "%§r reduced fishing time.");
    tooltip.add("§a" + Utils.formatPercent(this.getTreasureChance()) + "%§r chance to fish additional treasure.");
    return tooltip;
  }

  public double getTreasureChance() {
    return 0.01 * this.level;
  }

  public double getDelayReduction() {
    return 0.0075 * this.level;
  }


}
