package avi.mod.skrim.items.food;

import avi.mod.skrim.blocks.ModBlocks;
import net.minecraft.block.Block;

public class SkrimCake extends CustomCake {

	public SkrimCake() {
		super(ModBlocks.SKRIM_CAKE, "skrim_cake");
	}

	@Override
	public Block getBlock() {
		return ModBlocks.SKRIM_CAKE;
	}

}
