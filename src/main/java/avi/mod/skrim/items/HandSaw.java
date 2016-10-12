package avi.mod.skrim.items;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

public class HandSaw extends CustomAxe {

	public HandSaw(String name, ToolMaterial material) {
		super(name, material);
	}

	public HandSaw(ToolMaterial material) {
		super(material);
	}

	@Override
	public float getStrVsBlock(ItemStack stack, IBlockState state) {
		// TODO Auto-generated method stub
		return (!this.checkStrVsBlock(stack, state)) ? super.getStrVsBlock(stack, state) : 10.0F;
	}

}
