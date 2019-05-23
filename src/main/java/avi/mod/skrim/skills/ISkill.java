package avi.mod.skrim.skills;

import net.minecraft.entity.player.EntityPlayerMP;

import java.util.List;

public interface ISkill {

  int getNextLevelTotal();

  boolean canLevelUp();

  void levelUp(EntityPlayerMP player);

  List<String> getToolTip();

  boolean hasAbility(int abilityLevel);

  void setXp(double xp);

  void setLevel(int level);

  double getXp();

  int getLevel();

  void addXp(EntityPlayerMP player, int xp);

  int getIntXp();

}
