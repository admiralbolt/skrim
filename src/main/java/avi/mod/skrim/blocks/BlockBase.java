package avi.mod.skrim.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import avi.mod.skrim.Skrim;

public class BlockBase extends Block {

	public String name;

	public BlockBase(Material materialIn, String name) {
		super(materialIn);
		this.name = name;
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		setCreativeTab(Skrim.CREATIVE_TAB);
	}

//	@Override
//	public void registerItemModel(Item itemBlock) {
//		Skrim.proxy.registerItemRenderer(itemBlock, 0, NAME);
//	}


}
