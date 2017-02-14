package avi.mod.skrim.network;

import avi.mod.skrim.client.gui.MegaChestGui;
import avi.mod.skrim.tileentity.MegaChestContainerTileEntity;
import avi.mod.skrim.tileentity.MegaChestTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	public static final int SKILL_TAB_ID = 0;
	public static final int MEGA_CHEST_GUI = 1;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		System.out.println("SERVER GUI ID == " + ID);
		if (ID == MEGA_CHEST_GUI) {
			MegaChestContainerTileEntity entity = new MegaChestContainerTileEntity(player.inventory, (MegaChestTileEntity) world.getTileEntity(new BlockPos(x, y, z)));
			System.out.println("Created entity :" + entity);
			return entity;
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		System.out.println("CLIENT GUI ID == " + ID);
		if (ID == MEGA_CHEST_GUI) {
			return new MegaChestGui(player.inventory, (MegaChestTileEntity) world.getTileEntity(new BlockPos(x, y, z)));
		}
		return null;
	}

}
