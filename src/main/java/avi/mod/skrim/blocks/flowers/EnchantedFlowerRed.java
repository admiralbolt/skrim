package avi.mod.skrim.blocks.flowers;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class EnchantedFlowerRed extends EnchantedFlower {

	public EnchantedFlowerRed(String name) {
		super();
		this.name = name;
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
	}

	public FlowerBase.EnumFlowerColor getBlockType() {
		return FlowerBase.EnumFlowerColor.RED;
	}

}
