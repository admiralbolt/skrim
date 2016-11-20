package avi.mod.skrim.blocks.plants;

import java.util.Random;

import avi.mod.skrim.blocks.CustomPlant;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBirchTree;

public class MagicBean extends CustomPlant {

	public MagicBean() {
		super("magic_bean", 100);
	}
	
	@Override
	public void finishedGrowing(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		WorldGenAbstractTree generator = new WorldGenBirchTree(true, true);
		generator.generate(worldIn, rand, pos);
	}

}
