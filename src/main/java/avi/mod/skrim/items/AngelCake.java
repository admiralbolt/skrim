package avi.mod.skrim.items;

import java.util.List;

import avi.mod.skrim.blocks.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AngelCake extends CustomCake {

	public AngelCake() {
		super(ModBlocks.ANGEL_CAKE, "angel_cake");
	}
	
	@Override
	public Block getBlock() {
		return ModBlocks.ANGEL_CAKE;
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
