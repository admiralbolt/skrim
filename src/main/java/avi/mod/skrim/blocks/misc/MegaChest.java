package avi.mod.skrim.blocks.misc;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.items.ItemModelProvider;
import avi.mod.skrim.network.GuiHandler;
import avi.mod.skrim.tileentity.MegaChestTileEntity;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MegaChest extends BlockContainer implements ItemModelProvider {

	public static final PropertyDirection FACING = BlockHorizontal.FACING;

	private String name = "mega_chest";

	public MegaChest() {
		super(Material.WOOD);
		this.setDefaultState(this.getDefaultState().withProperty(FACING, EnumFacing.EAST));

		this.setUnlocalizedName(this.name);
		this.setRegistryName(this.name);
		this.setHardness(2.0F);
		setCreativeTab(Skrim.creativeTab);

		// Doesn't exist anymore, not sure if it's relevant or not.
		// this.isBlockContainer = true;
	}

	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { FACING });
	}

	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new MegaChestTileEntity();
	}

	@Override
	public void registerItemModel(Item itemBlock) {
		Skrim.proxy.registerItemRenderer(itemBlock, 0, this.name);
	}

	@SideOnly(Side.CLIENT)
	public boolean hasCustomBreakingProgress(IBlockState state) {
		return true;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState blockstate) {
		MegaChestTileEntity te = (MegaChestTileEntity) world.getTileEntity(pos);
		InventoryHelper.dropInventoryItems(world, pos, te);
		super.breakBlock(world, pos, blockstate);
	}

	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		EnumFacing enumfacing = EnumFacing.getHorizontal(MathHelper.floor((double) (placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3).getOpposite();
		System.out.println("enumfacing: " + enumfacing);
		state = state.withProperty(FACING, enumfacing);
		if (stack.hasDisplayName()) {
			((MegaChestTileEntity) worldIn.getTileEntity(pos)).setCustomName(stack.getDisplayName());
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY,
			float hitZ) {
		if (!world.isRemote) {
			player.openGui(Skrim.instance, GuiHandler.MEGA_CHEST_GUI, world, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}

	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta,
			EntityLivingBase placer) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
	}

	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing = EnumFacing.getFront(meta);

		if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
			enumfacing = EnumFacing.NORTH;
		}

		return this.getDefaultState().withProperty(FACING, enumfacing);
	}

	public int getMetaFromState(IBlockState state) {
		return ((EnumFacing) state.getValue(FACING)).getIndex();
	}

	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate((EnumFacing) state.getValue(FACING)));
	}

	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation((EnumFacing) state.getValue(FACING)));
	}

}