package avi.mod.skrim.blocks;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

public class WeirwoodLeaf extends BlockBase {
	
	public WeirwoodLeaf() {
		super(Material.LEAVES, "weirwood_leaf");
	}
	
	@Override
    public int quantityDropped(IBlockState state, int fortune, Random random) {
		return 0;
	}

}
