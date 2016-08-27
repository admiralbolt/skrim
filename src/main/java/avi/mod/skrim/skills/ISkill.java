package avi.mod.skrim.skills;

import java.util.List;

import avi.mod.skrim.skills.mining.SkillMining;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

public interface ISkill {

  void setNextLevelTotal();
  int getXpNeeded();
  boolean canLevelUp();
  void levelUp(EntityPlayerMP player);
  List<String> getToolTip();
  ResourceLocation getIconTexture();

}
