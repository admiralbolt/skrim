package avi.mod.skrim.client.renderer.tileentity;

import avi.mod.skrim.blocks.ModBlocks;
import avi.mod.skrim.tileentity.MegaChestTileEntity;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;

public class SkrimTileEntityItemRenderer extends TileEntityItemStackRenderer {
	
	private MegaChestTileEntity te = new MegaChestTileEntity();
	
	@Override
	public void renderByItem(ItemStack stack) {
		Block block = Block.getBlockFromItem(stack.getItem());
		if (block == ModBlocks.MEGA_CHEST) {
			TileEntityRendererDispatcher.instance.render(this.te, 0.0D, 0.0D, 0.0D, 0.0F);
		} else {
			super.renderByItem(stack);
		}
	}

}
