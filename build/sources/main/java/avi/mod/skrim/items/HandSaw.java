package avi.mod.skrim.items;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

public class HandSaw extends CustomAxe {

	public HandSaw(String name, ToolMaterial material) {
		super(name, material);
	}

	public HandSaw() {
		super("hand_saw", ToolMaterial.IRON);
	}

}
