package avi.mod.skrim.skills;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public abstract class SkillProvider<T> implements ICapabilitySerializable<NBTTagCompound> {

	private final T skill;
	private final Capability<T> cap;

	public SkillProvider(T skill, Capability<T> cap) {
		this.skill = skill;
		this.cap = cap;
	}

  @Override
  public NBTTagCompound serializeNBT() {
    return (NBTTagCompound) this.cap.getStorage().writeNBT(this.cap, skill, EnumFacing.NORTH);
  }

  @Override
  public void deserializeNBT(NBTTagCompound nbt) {
    this.cap.getStorage().readNBT(this.cap, skill, EnumFacing.NORTH, nbt);
  }

  @Override
  public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
    if (capability == this.cap) {
      return true;
    } else {
      return false;
    }
  }

  @SuppressWarnings("hiding")
	@Override
  public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
    if (capability == this.cap) {
      return this.cap.cast(skill);
    } else {
  	  return null;
    }
  }

}
