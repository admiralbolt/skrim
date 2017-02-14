package avi.mod.skrim.skills;

import java.util.ArrayList;
import java.util.List;

import avi.mod.skrim.client.gui.GuiUtils;
import avi.mod.skrim.client.gui.GuiUtils.Icon;

public class SkillAbility {

	private static String notUnlockedColor = "§7";
	private static String nameColor = "§6";
	public static String descColor = "§4";
	private static String flavorColor = "§e";
	private static String reset = "§r";

	public String name;
	public int level;
	public List<String> description = new ArrayList<String>();
	public String flavor;
	public Icon locked;
	public Icon unlocked;
	
	public SkillAbility(String skillName, String name, int level, String flavor, String... descLines) {
		this.name = name;
		this.level = level;
		for (String line : descLines) {
			this.description.add(line);
		}
		this.flavor = "\"" + flavor + "\"";
		int abilityNumber = getAbilityNumber(level);
		this.locked = GuiUtils.getAbilityIcon(skillName, (level / 25), false);
		this.unlocked = GuiUtils.getAbilityIcon(skillName, (level / 25), true);
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

	public static Icon getAbilityIcon(SkillAbility ability, boolean hasAbility) {
		return (hasAbility) ? ability.unlocked : ability.locked;
	}

}
