package avi.mod.skrim.skills;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SkillAbility {

  private static final String NOT_UNLOCKED_COLOR = "§7";
  private static final String NAME_COLOR = "§6";
  private static final String FLAVOR_COLOR = "§e";
  private static final String RESET = "§r";
  public static final String DESC_COLOR = "§4";

  public String name;
  public int level;
  private List<String> description = new ArrayList<>();
  private String flavor;

  public SkillAbility(String skillName, String name, int level, String flavor, String... descLines) {
    this.name = name;
    this.level = level;
    this.description.addAll(Arrays.asList(descLines));
    this.flavor = "\"" + flavor + "\"";
  }

  public static List<String> getAbilityTooltip(SkillAbility ability, boolean hasAbility, boolean abilityEnabled) {
    List<String> tooltip = new ArrayList<>();
    if (ability == null) {
      tooltip.add(NOT_UNLOCKED_COLOR + "Nothin Yet" + RESET);
      return tooltip;
    }
    if (hasAbility) {
      if (abilityEnabled) {
        tooltip.add(NAME_COLOR + ability.name + RESET);
        for (String descLine : ability.description) {
          tooltip.add(DESC_COLOR + descLine + RESET);
        }
        tooltip.add(FLAVOR_COLOR + ability.flavor + RESET);
      } else {
        tooltip.add(NOT_UNLOCKED_COLOR + ability.name + RESET);
        for (String descLine : ability.description) {
          tooltip.add(NOT_UNLOCKED_COLOR + descLine + RESET);
        }
        tooltip.add(NOT_UNLOCKED_COLOR + ability.flavor + RESET);
      }
    } else {
      tooltip.add(NOT_UNLOCKED_COLOR + ability.name + RESET);
    }
    return tooltip;
  }

}
