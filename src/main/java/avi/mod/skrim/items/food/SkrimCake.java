package avi.mod.skrim.items.food;

import avi.mod.skrim.blocks.SkrimBlocks;
import net.minecraft.block.Block;

public class SkrimCake extends CustomCake {

  public SkrimCake() {
    super(SkrimBlocks.SKRIM_CAKE, "skrim_cake");
  }

  @Override
  public Block getBlock() {
    return SkrimBlocks.SKRIM_CAKE;
  }

}
