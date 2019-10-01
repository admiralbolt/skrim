package avi.mod.skrim.skills;

import net.minecraft.entity.player.EntityPlayerMP;

import java.util.List;
import java.util.Map;

public interface ISkill {

  int getNextLevelTotal();

  boolean canLevelUp();

  void levelUp(EntityPlayerMP player);

  List<String> getToolTip();

  boolean hasAbility(int abilityLevel);

  boolean abilityEnabled(int abilityLevel);

  Map<Integer, Boolean> getAbilityEnabledMap();

  void toggleAbility(int abilityLevel);

  void toggleSkill();

  void setXp(double xp);

  void setLevel(int level);

  void setAbilityEnabledMap(Map<Integer, Boolean> enabledMap);

  void ding(EntityPlayerMP player);

  double getXp();

  int getLevel();

  void addXp(EntityPlayerMP player, int xp);

  int getIntXp();

  boolean getEnabled();

  void setEnabled(boolean enabled);

}
