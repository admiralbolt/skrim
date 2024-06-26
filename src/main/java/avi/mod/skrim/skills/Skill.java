package avi.mod.skrim.skills;

import avi.mod.skrim.advancements.SkrimAdvancements;
import avi.mod.skrim.network.LevelUpPacket;
import avi.mod.skrim.network.SkillPacket;
import avi.mod.skrim.network.SkrimPacketHandler;
import avi.mod.skrim.utils.Utils;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Skill implements ISkill {

  public static final int XP_FACTOR = 10000;
  public static final String COLOR_DISABLED = "§7";

  public String name;
  public int level;
  public double xp;
  public boolean skillEnabled = true;
  public Map<Integer, Boolean> enabledMap = new HashMap<>();
  public List<String> tooltip = new ArrayList<>();
  private Map<Integer, SkillAbility> abilities = new HashMap<>();

  /**
   * Optionally load a skill with the set level & xp
   */
  public Skill(String name, int level, int xp) {
    this.name = name;
    this.level = level;
    this.xp = xp;
  }

  /**
   * When a player initially gets skills set them to level 1.
   */
  public Skill(String name) {
    this(name, 1, 0);
  }

  public abstract List<String> getToolTip();

  public void addXp(EntityPlayerMP player, int xp) {
    if (xp <= 0) return;
    this.xp += Skills.getTotalXp(player, xp);
    this.levelUp(player);
  }

  /**
   * D&D 3.5 XP. Ahhhh fuck yeah.
   */
  public int getNextLevelTotal() {
    return XP_FACTOR * ((this.level * this.level + this.level) / 2);
  }

  public double getXpNeeded() {
    return this.getNextLevelTotal() - this.xp;
  }

  /**
   * Gets the percent of the way to the next level. Used for rendering the experience bar.
   * <p>
   * NOTE: It is possible for a player to over level themselves under the correct circumstances. This function will
   * return 1.0 in those cases to prevent rendering bugs.
   */
  public double getPercentToNext() {
    int prevLevelXp = Utils.gaussianSum(this.level - 1) * XP_FACTOR;
    return Math.min(1.0, (this.xp - prevLevelXp) / (this.getNextLevelTotal() - prevLevelXp));
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
    return this.abilities.getOrDefault(abilityLevel, null);
  }

  public boolean hasAbility(int abilityLevel) {
    return (this.level / 25) >= abilityLevel;
  }

  public boolean activeAbility(int abilityLevel) {
    return hasAbility(abilityLevel) && abilityEnabled(abilityLevel);
  }

  public boolean abilityEnabled(int abilityLevel) {
    return this.enabledMap.getOrDefault(abilityLevel, true);
  }

  public void toggleAbility(int abilityLevel) {
    if (abilityLevel <= 0 || abilityLevel > 4) return;

    this.enabledMap.put(abilityLevel, !this.abilityEnabled(abilityLevel));
  }

  public void toggleSkill() {
    this.skillEnabled = !this.skillEnabled;
  }


  public void levelUp(EntityPlayerMP player) {
    boolean didLevel = false;
    while (this.canLevelUp()) {
      this.level++;
      didLevel = true;
    }

    if (didLevel) {
      SkrimPacketHandler.INSTANCE.sendTo(new LevelUpPacket(this.name, this.level), player);
      this.ding(player);
    }

    SkrimPacketHandler.INSTANCE.sendTo(new SkillPacket(this.name, this.level, this.xp), player);
  }

  public void ding(EntityPlayerMP player) {
    if (this.level >= 25) SkrimAdvancements.DING_APPRENTICE.grant(player);
    if (this.level >= 50) SkrimAdvancements.DING_JOURNEYMAN.grant(player);
    if (this.level >= 75) SkrimAdvancements.DING_EXPERT.grant(player);
    if (this.level >= 100) SkrimAdvancements.DING_MASTER.grant(player);
  }

  public double getXp() {
    return this.xp;
  }

  public void setXp(double xp) {
    this.xp = xp;
  }

  public int getIntXp() {
    return (int) this.xp;
  }

  public int getLevel() {
    return this.level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public Map<Integer, Boolean> getAbilityEnabledMap() {
    return this.enabledMap;
  }

  public void setAbilityEnabledMap(Map<Integer, Boolean> enabledMap) {
    this.enabledMap = enabledMap;
  }

  public boolean getEnabled() {
    return this.skillEnabled;
  }

  public void setEnabled(boolean enabled) {
    this.skillEnabled = enabled;
  }

}
