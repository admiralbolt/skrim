package avi.mod.skrim.blocks.flowers;

import avi.mod.skrim.tileentity.EnchantedFlowerTileEntity;
import net.minecraft.block.BlockBush;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EnchantedFlower extends BlockBush implements ITileEntityProvider {

  public String name;

  public EnchantedFlower(String name) {
    super();
    this.name = name;
    this.setRegistryName(name);
    this.setUnlocalizedName(name);
    this.setHardness(1.0F);
    this.setLightLevel(1.0F);
  }

  public TileEntity createNewTileEntity(World worldIn, int meta) {
    return new EnchantedFlowerTileEntity();
  }

  /**
   * Used to determine ambient occlusion and culling when rebuilding chunks for render
   */
  public boolean isOpaqueCube(IBlockState state) {
    return false;
  }

  public boolean isFullCube(IBlockState state) {
    return false;
  }

  /**
   * The type of render function called. 3 for standard block models, 2 for TESR's, 1 for liquids, -1 is no render
   */
  public EnumBlockRenderType getRenderType(IBlockState state) {
    return EnumBlockRenderType.MODEL;
  }

  /**
   * Called by ItemBlocks after a block is set in the world, to allow post-place logic
   */
  public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
															ItemStack stack) {
    super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    if (stack.hasDisplayName()) {
      TileEntity tileentity = worldIn.getTileEntity(pos);

      if (tileentity instanceof EnchantedFlowerTileEntity) {
        ((EnchantedFlowerTileEntity) tileentity).setName(stack.getDisplayName());
      }
    }
  }

  @SideOnly(Side.CLIENT)
  public BlockRenderLayer getBlockLayer() {
    return BlockRenderLayer.CUTOUT;
  }

}
