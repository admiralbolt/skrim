package avi.mod.skrim.skills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.client.gui.GuiUtils;
import avi.mod.skrim.client.gui.GuiUtils.Icon;
import avi.mod.skrim.network.LevelUpPacket;
import avi.mod.skrim.network.SkillPacket;
import avi.mod.skrim.network.SkrimPacketHandler;
import avi.mod.skrim.stats.SkrimAchievements;
import avi.mod.skrim.utils.Utils;
import net.minecraft.entity.player.EntityPlayerMP;

public class Skill implements ISkill {

	// private Minecraft mc;
	public String name;
	public int level;
	public double xp;
	public List<String> tooltip = new ArrayList<String>();
	public Map<Integer, SkillAbility> abilities = new HashMap<Integer, SkillAbility>();
	private Icon icon;

	public static int xpFactor = 10000;

	/**
	 * Optionally load a skill with the set level & xp
	 */
	public Skill(String name, int level, int xp) {
		this.name = name;
		this.level = level;
		this.xp = xp;
		this.icon = GuiUtils.getSkillIcon(this.name.toLowerCase());
	}

	/**
	 * When a player initial gets skills set them to level 1.
	 */
	public Skill(String name) {
		this(name, 1, 0);
	}

	public void addXp(EntityPlayerMP player, int xp) {
		if (xp > 0) {
			Utils.log(
					"Giving player " + player + ": " + Skills.getTotalXp(player, xp) + " " + this.name + " xp (Boost: " + Skills.getTotalXpBonus(player) + ")");
			this.xp += Skills.getTotalXp(player, xp);
			this.levelUp(player);
		}
	}

	/**
	 * D&D 3.5 XP. Ahhhh fuck yeah.
	 */
	public int getNextLevelTotal() {
		return xpFactor * ((this.level * this.level + this.level) / 2);
	}

	public double getXpNeeded() {
		return this.getNextLevelTotal() - this.xp;
	}

	public double getPercentToNext() {
		int prevLevelXp = Utils.gaussianSum(this.level - 1) * xpFactor;
		return ((double) (this.xp - prevLevelXp)) / (this.getNextLevelTotal() - prevLevelXp);
	}

	public boolean canLevelUp() {
		return (this.xp >= this.getNextLevelTotal());
	}

	public void addAbilities(SkillAbility... abilities) {
		for (int i = 0; i < abilities.length; i++) {
			this.abilities.put(i + 1, abilities[i]);
		}
	}

	public SkillAbility getAbility(int abilityLevel) {
		return (this.abilities.containsKey(abilityLevel)) ? this.abilities.get(abilityLevel) : null;
	}

	public boolean hasAbility(int abilityLevel) {
		return (this.level / 25) >= abilityLevel;
	}

	public Icon getAbilityIcon(int abilityLevel) {
		return SkillAbility.getAbilityIcon(this.getAbility(abilityLevel), this.hasAbility(abilityLevel));
	}

	public List<String> getAbilityTooltip(int abilityLevel) {
		return null;
	}

	public void levelUp(EntityPlayerMP player) {
		if (this.canLevelUp()) {
			this.level++;
			SkrimPacketHandler.INSTANCE.sendTo(new LevelUpPacket(this.name, this.level), player);
			this.ding(player);
		}
		SkrimPacketHandler.INSTANCE.sendTo(new SkillPacket(this.name, this.level, this.xp), player);
	}

	public void ding(EntityPlayerMP player) {
		// [ADVANCEMENT]
		/**
		 * if (this.level >= 25) {
		 * player.addStat(SkrimAchievements.DING_APPRENTICE, 1); if (this.level
		 * >= 50) { player.addStat(SkrimAchievements.DING_JOURNEYMAN, 1); if
		 * (this.level >= 75) { player.addStat(SkrimAchievements.DING_EXPERT,
		 * 1); if (this.level >= 100) {
		 * player.addStat(SkrimAchievements.DING_MASTER, 1); } } } }
		 */
	}

	public List<String> getToolTip() {
		List<String> tooltip = new ArrayList<String>();
		tooltip.add("Tooltip for " + this.name);
		return tooltip;
	}

	public Icon getIcon() {
		return this.icon;
	}

	public void setXp(double xp) {
		this.xp = xp;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public double getXp() {
		return this.xp;
	}

	public int getIntXp() {
		return (int) this.xp;
	}

	public int getLevel() {
		return this.level;
	}

}
