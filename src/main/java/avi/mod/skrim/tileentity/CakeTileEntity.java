package avi.mod.skrim.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;

public class CakeTileEntity extends TileEntity {

	private int level;

	public int getLevel() {
		return this.level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void readFromNBT(NBTTagCompound compound) {
		this.level = compound.getInteger("level");
	}

	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setInteger("level", this.level);
		return compound;
	}

}
