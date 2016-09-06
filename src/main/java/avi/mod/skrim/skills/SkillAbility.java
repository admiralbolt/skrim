package avi.mod.skrim.skills;

import java.util.ArrayList;
import java.util.List;

public class SkillAbility {

  public static SkillAbility defaultSkill = new SkillAbility("placeholder name", 100, "flavor text? no.", "description lines asdf asdf ");

  private static String notUnlockedColor = "§7";
  private static String nameColor = "§6";
  private static String descColor = "§4";
  private static String flavorColor = "§e";
  private static String reset = "§r";

  public String name;
  public int level;
  public List<String> description = new ArrayList<String>();
  public String flavor;

  public SkillAbility(String name, int level, String flavor, String... descLines) {
    this.name = name;
    this.level = level;
    for (String line : descLines) {
    	this.description.add(line);
    }
    this.flavor = "\"" + flavor + "\"";
  }

  public static List<String> getAbilityTooltip(SkillAbility ability, boolean hasAbility) {
    List<String> tooltip = new ArrayList<String>();
    if (hasAbility) {
      tooltip.add(nameColor + ability.name + reset);
      for (String descLine : ability.description) {
        tooltip.add(descColor + descLine + reset);
      }
      tooltip.add(flavorColor + ability.flavor + reset);
    } else {
      tooltip.add(notUnlockedColor + ability.name + reset);
    }
    return tooltip;
  }

}
