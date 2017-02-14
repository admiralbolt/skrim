package avi.mod.skrim.blocks;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.items.ItemModelProvider;
import avi.mod.skrim.network.GuiHandler;
import avi.mod.skrim.tileentity.MegaChestTileEntity;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MegaChest extends BlockContainer implements ItemModelProvider {
	
	private String name = "mega_chest";
	
	public MegaChest() {
		this(BlockChest.Type.BASIC);
	}

	protected MegaChest(BlockChest.Type chestTypeIn) {
		super(Material.IRON);
		this.setUnlocalizedName(this.name);
		this.setRegistryName(this.name);
		this.setHardness(2.0F);
		this.setResistance(6.0F);
		this.isBlockContainer = true;
	}

	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new MegaChestTileEntity();
	}

	@Override
	public void registerItemModel(Item itemBlock) {
		Skrim.proxy.registerItemRenderer(itemBlock, 0, this.name);
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState blockstate) {
	    MegaChestTileEntity te = (MegaChestTileEntity) world.getTileEntity(pos);
	    InventoryHelper.dropInventoryItems(world, pos, te);
	    super.breakBlock(world, pos, blockstate);
	}


	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
	    if (stack.hasDisplayName()) {
	        ((MegaChestTileEntity) worldIn.getTileEntity(pos)).setCustomName(stack.getDisplayName());
	    }
	}
	
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
	    if (!world.isRemote) {
	    	System.out.println("Getting ready to open gui...");
	    	System.out.println("GuiHandler.MEGA_CHEST_GUI: " + GuiHandler.MEGA_CHEST_GUI);
	        player.openGui(Skrim.instance, GuiHandler.MEGA_CHEST_GUI, world, pos.getX(), pos.getY(), pos.getZ());
	    }
	    return true;
	}
	
}