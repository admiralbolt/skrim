package avi.mod.skrim.skills;

import java.util.ArrayList;
import java.util.List;

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
  public int nextLevelTotal;
  public List<String> tooltip = new ArrayList<String>();
  public ResourceLocation iconTexture;

  /**
   * Optionally load a skill with the set level & xp
   */
  public Skill(String name, int level, int xp) {
    this.name = name;
    this.level = level;
    this.xp = xp;
    this.setNextLevelTotal();
    /**
     * Skills get xp based on actions, so we need to register them
     * to the event bus.
     */
    MinecraftForge.EVENT_BUS.register(this);
  }

  /**
   * When a player initial gets skills set them to level 1.
   */
  public Skill(String name) {
    this(name, 1, 0);
  }

  /**
   * D&D 3.5 XP. Ahhhh fuck yeah.
   */
  public void setNextLevelTotal() {
    this.nextLevelTotal = 1000 * ((this.level * this.level + this.level) / 2);
  }

  public int getNextLevelTotal() {
    return 1000 * ((this.level * this.level + this.level) / 2);
  }

  public int getXpNeeded() {
    return this.nextLevelTotal - this.xp;
  }

  public double getPercentToNext() {
	  int prevLevelXp = (((this.level - 1) * (this.level - 1) + this.level - 1) / 2) * 1000;
	  return ((double) (this.xp - prevLevelXp)) / (this.getNextLevelTotal() - prevLevelXp);
  }

  public boolean canLevelUp() {
    return (this.xp >= this.nextLevelTotal);
  }

  public void levelUp(EntityPlayerMP player) {
    if (this.canLevelUp()) {
      this.level++;
      // this.mc.thePlayer.sendChatMessage("Level up! " + this.name + " is now level " + this.level);
      this.setNextLevelTotal();
    }
    SkrimPacketHandler.INSTANCE.sendTo(new SkillPacket(this.name, this.getLevel(player), this.getXp(player)), player);
  }

  public void overwrite(Skill skill) {
	 this.xp = skill.xp;
	 this.level = skill.level;
	 this.setNextLevelTotal();
  }

  public List<String> getToolTip() {
	  List<String> tooltip = new ArrayList<String>();
	  tooltip.add("Tooltip for " + this.name);
	  return tooltip;
  }

  public ResourceLocation getIconTexture() {
    return this.iconTexture;
  }

  public int getLevel(EntityPlayerMP player) {
    Capability<? extends ISkill> cap = Skills.skillMap.get(this.name);
    if (player.hasCapability(cap, EnumFacing.NORTH)) {
      Skill skill = (Skill) player.getCapability(cap, EnumFacing.NORTH);
      return skill.level;
    } else {
      return 1;
    }
  }

  public int getXp(EntityPlayerMP player) {
    Capability<? extends ISkill> cap = Skills.skillMap.get(this.name);
    if (player.hasCapability(cap, EnumFacing.NORTH)) {
      Skill skill = (Skill) player.getCapability(cap, EnumFacing.NORTH);
      return skill.xp;
    } else {
      return 0;
    }
  }

}
