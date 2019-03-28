package avi.mod.skrim.blocks.plants;

import avi.mod.skrim.blocks.BlockBase;
import avi.mod.skrim.blocks.ModBlocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.IShearable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public class WeirwoodLeaf extends BlockBase implements IShearable {

  public WeirwoodLeaf() {
    super(Material.LEAVES, "weirwood_leaf");
  }

  @Override
  public int quantityDropped(IBlockState state, int fortune, Random random) {
    return 0;
  }

  @Override
  public boolean isShearable(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos) {
    return true;
  }

  @Nonnull
  @Override
  public List<ItemStack> onSheared(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
    return NonNullList.withSize(1, new ItemStack(ModBlocks.WEIRWOOD_LEAF, 1));
  }
}
