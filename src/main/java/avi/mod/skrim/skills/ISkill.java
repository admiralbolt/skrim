package avi.mod.skrim.skills;

import java.util.List;

import avi.mod.skrim.skills.mining.SkillMining;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

public interface ISkill {

  int getNextLevelTotal();
  int getXpNeeded();
  boolean canLevelUp();
  void levelUp(EntityPlayerMP player);
  List<String> getToolTip();
  ResourceLocation getIconTexture();
  ResourceLocation getAbilityTexture(int abilityLevel);
  boolean hasAbility(int abilityLevel);
  List<String> getAbilityTooltip(int abilityLevel);
  void setXp(int xp);
  void setLevel(int level);
  int getXp();
  int getLevel();
  void addXp(EntityPlayerMP player, int xp);

}
