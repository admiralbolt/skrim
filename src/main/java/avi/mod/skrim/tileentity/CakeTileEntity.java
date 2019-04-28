package avi.mod.skrim.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;

public class CakeTileEntity extends TileEntity {

  private int level;

  public int getLevel() {
    return this.level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  @Override
  public void readFromNBT(NBTTagCompound compound) {
    super.readFromNBT(compound);

    this.level = compound.getInteger("level");
  }

  @Override
  @Nonnull
  public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
    super.writeToNBT(compound);

    compound.setInteger("level", this.level);
    return compound;
  }

}
