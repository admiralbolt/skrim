package avi.mod.skrim.skills;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class SkillProvider<T> implements ICapabilitySerializable<NBTTagCompound> {

	private final T skill;
	private final EnumFacing facing;
	private final Capability<T> cap;
	
	public SkillProvider(Capability<T> cap, @Nullable EnumFacing facing) {
		this(cap, facing, cap.getDefaultInstance());
	}
	
	public SkillProvider(Capability<T> cap, @Nullable EnumFacing facing, T skill) {
		this.cap = cap;
		this.facing = facing;
		this.skill = skill;
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
  public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
    if (capability == this.cap) {
      return true;
    } else {
      return false;
    }
  }

  @SuppressWarnings("hiding")
	@Override
  public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
    if (capability == this.cap) {
      return this.cap.cast(skill);
    } else {
  	  return null;
    }
  }
  
  public final Capability<T> getCapability() {
  	return cap;
  }
  
  @Nullable
  public final EnumFacing getFacing() {
  	return facing;
  }
  
  public final T getSkill() {
  	return skill;
  }

}
