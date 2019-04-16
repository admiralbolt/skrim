package avi.mod.skrim.handlers;

import avi.mod.skrim.client.gui.MegaChestGui;
import avi.mod.skrim.inventory.MegaChestContainer;
import avi.mod.skrim.tileentity.MegaChestTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

  public static final int MEGA_CHEST_GUI = 1;

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    if (ID == MEGA_CHEST_GUI) {
      return new MegaChestContainer(player.inventory, (MegaChestTileEntity) world.getTileEntity(new BlockPos(x, y, z)));
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    if (ID == MEGA_CHEST_GUI) {
      return new MegaChestGui(player.inventory, (MegaChestTileEntity) world.getTileEntity(new BlockPos(x, y, z)));
    }
    return null;
  }

}
