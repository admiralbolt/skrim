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
    compound.setDouble(saveSkill.name + "-xp", saveSkill.xp);
    compound.setInteger(saveSkill.name + "-level", saveSkill.level);
    compound.setBoolean("ability1", saveSkill.abilityEnabled(1));
    compound.setBoolean("ability2", saveSkill.abilityEnabled(2));
    compound.setBoolean("ability3", saveSkill.abilityEnabled(3));
    compound.setBoolean("ability4", saveSkill.abilityEnabled(4));
    return compound;
  }

  public void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt) {
    NBTTagCompound compound = (NBTTagCompound) nbt;
    final Skill loadSkill = (Skill) instance;
    loadSkill.xp = compound.getDouble(loadSkill.name + "-xp");
    loadSkill.level = compound.getInteger(loadSkill.name + "-level");
    loadSkill.enabledMap.put(1, compound.getBoolean("ability1"));
    loadSkill.enabledMap.put(2, compound.getBoolean("ability2"));
    loadSkill.enabledMap.put(3, compound.getBoolean("ability3"));
    loadSkill.enabledMap.put(4, compound.getBoolean("ability4"));
  }

}
