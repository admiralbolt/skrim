package avi.mod.skrim.blocks.misc;

import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.tileentity.SkrimBrewingStandEntity;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * Wholesale copied from BlockBrewingStand.
 */
public class SkrimBrewingStand extends BlockContainer {

  public static final String NAME = "skrim_brewing_stand";

  public static final PropertyBool[] HAS_BOTTLE = new PropertyBool[]{PropertyBool.create("skrim_has_bottle_0"), PropertyBool.create(
      "skrim_has_bottle_1"), PropertyBool.create("skrim_has_bottle_2")};
  protected static final AxisAlignedBB BASE_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D);
  protected static final AxisAlignedBB STICK_AABB = new AxisAlignedBB(0.4375D, 0.0D, 0.4375D, 0.5625D, 0.875D, 0.5625D);

  public SkrimBrewingStand() {
    super(Material.IRON);
    this.setHardness(1.5f);
    this.setDefaultState(this.blockState.getBaseState().withProperty(HAS_BOTTLE[0], false).withProperty(HAS_BOTTLE[1],
        false).withProperty(HAS_BOTTLE[2], true));
    this.setRegistryName(NAME);
    this.setUnlocalizedName(NAME);
  }

  /**
   * Gets the localized name of this block. Used for the statistics page.
   */
  public String getLocalizedName() {
    return I18n.translateToLocal("item.brewingStand.name");
  }

  /**
   * Used to determine ambient occlusion and culling when rebuilding chunks for render
   */
  public boolean isOpaqueCube(IBlockState state) {
    return false;
  }

  /**
   * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
   * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
   */
  public EnumBlockRenderType getRenderType(IBlockState state) {
    return EnumBlockRenderType.MODEL;
  }

  /**
   * Returns a new instance of a block's tile entity class. Called on placing the block.
   */
  public TileEntity createNewTileEntity(World worldIn, int meta) {
    return new SkrimBrewingStandEntity();
  }

  public boolean isFullCube(IBlockState state) {
    return false;
  }

  public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
                                    List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
    addCollisionBoxToList(pos, entityBox, collidingBoxes, STICK_AABB);
    addCollisionBoxToList(pos, entityBox, collidingBoxes, BASE_AABB);
  }

  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
    return BASE_AABB;
  }

  /**
   * Called when the block is right clicked by a player.
   */
  public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing
      , float hitX, float hitY, float hitZ) {
    if (worldIn.isRemote) {
      return true;
    } else {
      TileEntity tileentity = worldIn.getTileEntity(pos);

      if (tileentity instanceof SkrimBrewingStandEntity) {
        ((SkrimBrewingStandEntity) tileentity).setPlayer(playerIn);
        playerIn.displayGUIChest((SkrimBrewingStandEntity) tileentity);
        playerIn.addStat(StatList.BREWINGSTAND_INTERACTION);
      }

      return true;
    }
  }

  /**
   * Called by ItemBlocks after a block is set in the world, to allow post-place logic
   */
  public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
    TileEntity tileentity = worldIn.getTileEntity(pos);

    if (tileentity instanceof SkrimBrewingStandEntity) {
      ((SkrimBrewingStandEntity) tileentity).setName(placer.getName() + "'s Skrim Stand");
    }
  }

  @SideOnly(Side.CLIENT)
  public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
    double d0 = (double) ((float) pos.getX() + 0.4F + rand.nextFloat() * 0.2F);
    double d1 = (double) ((float) pos.getY() + 0.7F + rand.nextFloat() * 0.3F);
    double d2 = (double) ((float) pos.getZ() + 0.4F + rand.nextFloat() * 0.2F);
    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D);
  }

  /**
   * Called serverside after this block is replaced with another in Chunk, but before the Tile Entity is updated
   */
  public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
    TileEntity tileentity = worldIn.getTileEntity(pos);

    if (tileentity instanceof SkrimBrewingStandEntity) {
      InventoryHelper.dropInventoryItems(worldIn, pos, (SkrimBrewingStandEntity) tileentity);
    }

    super.breakBlock(worldIn, pos, state);
  }

  /**
   * Get the Item that this Block should drop when harvested.
   */
  public Item getItemDropped(IBlockState state, Random rand, int fortune) {
    return SkrimItems.BREWING_STAND;
  }

  public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
    return new ItemStack(SkrimItems.BREWING_STAND);
  }

  public boolean hasComparatorInputOverride(IBlockState state) {
    return true;
  }

  public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
    return Container.calcRedstone(worldIn.getTileEntity(pos));
  }

  @SideOnly(Side.CLIENT)
  public BlockRenderLayer getBlockLayer() {
    return BlockRenderLayer.CUTOUT;
  }

  /**
   * Convert the given metadata into a BlockState for this Block
   */
  public IBlockState getStateFromMeta(int meta) {
    IBlockState iblockstate = this.getDefaultState();

    for (int i = 0; i < 3; ++i) {
      iblockstate = iblockstate.withProperty(HAS_BOTTLE[i], Boolean.valueOf((meta & 1 << i) > 0));
    }

    return iblockstate;
  }

  /**
   * Convert the BlockState into the correct metadata value
   */
  public int getMetaFromState(IBlockState state) {
    int i = 0;

    for (int j = 0; j < 3; ++j) {
      if (((Boolean) state.getValue(HAS_BOTTLE[j])).booleanValue()) {
        i |= 1 << j;
      }
    }

    return i;
  }

  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, HAS_BOTTLE[0], HAS_BOTTLE[1], HAS_BOTTLE[2]);
  }


  /**
   * Get the geometry of the queried face at the given position and state. This is used to decide whether things like
   * buttons are allowed to be placed on the face, or how glass panes connect to the face, among other things.
   * <p>
   * Common values are {@code SOLID}, which is the default, and {@code UNDEFINED}, which represents something that
   * does not fit the other descriptions and will generally cause other things not to connect to the face.
   *
   * @return an approximation of the form of the given face
   */
  public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    return BlockFaceShape.UNDEFINED;
  }
}
