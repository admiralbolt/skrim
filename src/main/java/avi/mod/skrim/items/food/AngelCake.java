package avi.mod.skrim.items.food;

import avi.mod.skrim.blocks.SkrimBlocks;
import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AngelCake extends CustomCake {

	public AngelCake() {
		super(SkrimBlocks.ANGEL_CAKE, "angel_cake");
	}
	
	@Override
	public Block getBlock() {
		return SkrimBlocks.ANGEL_CAKE;
	}
	
	@Override
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.EPIC;
	}

}
