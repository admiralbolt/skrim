package avi.mod.skrim.skills;

import avi.mod.skrim.skills.mining.SkillMining;
import net.minecraft.entity.player.EntityPlayer;

public interface ISkill {

  void setNextLevelTotal();
  int getXpNeeded();
  boolean canLevelUp();
  void levelUp();
  void setBuffs(EntityPlayer player);

}
