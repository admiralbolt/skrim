package avi.mod.skrim.blocks.flowers;

import avi.mod.skrim.blocks.flowers.FlowerBase.EnumFlowerType;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class EnchantedFlowerVariants extends ItemBlock {

	private FlowerBase flower;

	public EnchantedFlowerVariants(FlowerBase flower) {
			super(flower);
			this.setMaxDamage(0);
			this.setHasSubtypes(true);
			this.flower = flower;
			this.setRegistryName(flower.getRegistryName());
		}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		EnumFlowerType type = FlowerBase.EnumFlowerType.getType(this.flower.getBlockType(), stack.getMetadata());
		return super.getUnlocalizedName() + "." + type.toString();
	}
	
	@Override
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

}
