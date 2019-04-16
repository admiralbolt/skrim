package avi.mod.skrim.skills;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SkillProvider<T> implements ICapabilitySerializable<NBTTagCompound> {

  private final T skill;
  private final Capability<T> cap;

  public SkillProvider(Capability<T> cap, @Nullable EnumFacing facing) {
    this(cap, facing, cap.getDefaultInstance());
  }

  public SkillProvider(Capability<T> cap, @Nullable EnumFacing facing, T skill) {
    this.cap = cap;
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
  public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
    return capability == this.cap;
  }

  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
    if (capability == this.cap) {
      return this.cap.cast(skill);
    }
    return null;
  }

  public final Capability<T> getCapability() {
    return cap;
  }

  public final T getSkill() {
    return skill;
  }

}
