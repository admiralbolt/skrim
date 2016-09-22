package avi.mod.skrim.skills;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.ResourceLocation;

public class SkillAbility {

  public static SkillAbility defaultSkill = new SkillAbility("placeholder name", 100, "'flavor text is kill.' 'no.'", "description lines asdf asdf ");

  private static String notUnlockedColor = "§7";
  private static String nameColor = "§6";
  public static String descColor = "§4";
  private static String flavorColor = "§e";
  private static String reset = "§r";

  public String name;
  public int level;
  public List<String> description = new ArrayList<String>();
  public String flavor;
  public ResourceLocation locked;
  public ResourceLocation unlocked;

  public SkillAbility(String name, int level, String flavor, String... descLines) {
    this.name = name;
    this.level = level;
    for (String line : descLines) {
    	this.description.add(line);
    }
    this.flavor = "\"" + flavor + "\"";
    this.locked = new ResourceLocation("skrim", "textures/guis/abilities/ability_locked.png");
    this.unlocked = new ResourceLocation("skrim", "textures/guis/abilities/ability_unlocked.png");
  }

  // fishing_1_locked.png & fishing_1_unlocked.png
  public SkillAbility(String skillName, String name, int level, String flavor, String... descLines) {
    this(name, level, flavor, descLines);
    int abilityNumber = getAbilityNumber(level);
    this.locked = new ResourceLocation("skrim", "textures/guis/abilities/" + skillName.toLowerCase() + "_" + abilityNumber + "_locked.png");
    this.unlocked = new ResourceLocation("skrim", "textures/guis/abilities/" + skillName.toLowerCase() + "_" + abilityNumber + "_unlocked.png");
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

  public static int getAbilityNumber(int level) {
    return level / 25;
  }

  public static ResourceLocation getAbilityIcon(SkillAbility ability, boolean hasAbility) {
    return (hasAbility) ? ability.unlocked : ability.locked;
  }

}
