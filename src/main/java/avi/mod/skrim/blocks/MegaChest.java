package avi.mod.skrim.blocks;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.items.ItemModelProvider;
import avi.mod.skrim.tileentity.TileEntityMegaChest;
import net.minecraft.block.BlockChest;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class MegaChest extends BlockChest implements ItemModelProvider {
	
	private String name = "mega_chest";
	
	public MegaChest() {
		this(BlockChest.Type.BASIC);
	}

	protected MegaChest(BlockChest.Type chestTypeIn) {
		super(chestTypeIn);
		this.setUnlocalizedName(this.name);
		this.setRegistryName(this.name);
	}

	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityMegaChest();
	}

	@Override
	public void registerItemModel(Item itemBlock) {
		Skrim.proxy.registerItemRenderer(itemBlock, 0, this.name);
	}
}