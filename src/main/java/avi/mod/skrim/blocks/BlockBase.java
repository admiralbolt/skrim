package avi.mod.skrim.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import avi.mod.skrim.Skrim;
import avi.mod.skrim.items.ItemModelProvider;

public class BlockBase extends Block {

	public String name;

	public BlockBase(Material materialIn, String name) {
		super(materialIn);
		this.name = name;
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		setCreativeTab(Skrim.creativeTab);
	}

//	@Override
//	public void registerItemModel(Item itemBlock) {
//		Skrim.proxy.registerItemRenderer(itemBlock, 0, name);
//	}


}
