package avi.mod.skrim.skills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import avi.mod.skrim.network.LevelUpPacket;
import avi.mod.skrim.network.SkillPacket;
import avi.mod.skrim.network.SkrimPacketHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraft.util.ResourceLocation;

public class Skill implements ISkill {

  // private Minecraft mc;
  public String name;
  public int level;
  public int xp;
  public List<String> tooltip = new ArrayList<String>();
  public ResourceLocation iconTexture;
  public Map<Integer, SkillAbility> abilities = new HashMap<Integer, SkillAbility>();

  /**
   * Optionally load a skill with the set level & xp
   */
  public Skill(String name, int level, int xp) {
    this.name = name;
    this.level = level;
    this.xp = xp;
  }

  /**
   * When a player initial gets skills set them to level 1.
   */
  public Skill(String name) {
    this(name, 1, 0);
  }

  public void addXp(EntityPlayerMP player, int xp) {
    if (xp > 0) {
      this.xp += xp;
      this.levelUp(player);
    }
  }

  /**
   * D&D 3.5 XP. Ahhhh fuck yeah.
   */
  public int getNextLevelTotal() {
    return 1000 * ((this.level * this.level + this.level) / 2);
  }

  public int getXpNeeded() {
    return this.getNextLevelTotal() - this.xp;
  }

  public double getPercentToNext() {
	  int prevLevelXp = (((this.level - 1) * (this.level - 1) + this.level - 1) / 2) * 1000;
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
    return (this.abilities.containsKey(abilityLevel)) ? this.abilities.get(abilityLevel) : SkillAbility.defaultSkill;
  }

  public boolean hasAbility(int abilityLevel) {
    return (this.level / 25) >= abilityLevel;
  }

  public ResourceLocation getAbilityTexture(int abilityLevel) {
    return null;
  }

  public List<String> getAbilityTooltip(int abilityLevel) {
  	return null;
  }

  public void levelUp(EntityPlayerMP player) {
    if (this.canLevelUp()) {
      this.level++;
      SkrimPacketHandler.INSTANCE.sendTo(new LevelUpPacket(this.name, this.level), player);
    }
    SkrimPacketHandler.INSTANCE.sendTo(new SkillPacket(this.name, this.level, this.xp), player);
  }

  public List<String> getToolTip() {
	  List<String> tooltip = new ArrayList<String>();
	  tooltip.add("Tooltip for " + this.name);
	  return tooltip;
  }

  public ResourceLocation getIconTexture() {
    return this.iconTexture;
  }

  public void setXp(int xp) {
  	this.xp = xp;
  }

  public void setLevel(int level) {
  	this.level = level;
  }

  public int getXp() {
  	return this.xp;
  }

  public int getLevel() {
  	return this.level;
  }

}
