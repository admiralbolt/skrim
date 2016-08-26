package avi.mod.skrim.skills;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class SkillStorage<T> implements IStorage<T> {

  public NBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side) {
    NBTTagCompound compound = new NBTTagCompound();
    final Skill saveSkill = (Skill) instance;
    compound.setInteger(saveSkill.name + "-xp", saveSkill.xp);
    compound.setInteger(saveSkill.name + "-level", saveSkill.level);
    return compound;
  }

  public void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt) {
    NBTTagCompound compound = (NBTTagCompound) nbt;
    final Skill loadSkill = (Skill) instance;
    loadSkill.xp = compound.getInteger(loadSkill.name + "-xp");
    loadSkill.level = compound.getInteger(loadSkill.name + "-level");
    loadSkill.setNextLevelTotal();
  }
  
}
