package avi.mod.skrim.blocks.plants;

import java.util.Random;

import avi.mod.skrim.world.gen.WorldGenBeanstalk;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MagicBean extends CustomPlant {

	public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 15);

	public MagicBean() {
		super("magic_bean", 15);
	}

	protected PropertyInteger getAgeProperty() {
		return AGE;
	}

	@Override
	public void finishedGrowing(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 4);
		WorldGenBeanstalk generator = new WorldGenBeanstalk();
		generator.generate(worldIn, rand, pos);
	}
	
	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		return false;
	}

}
