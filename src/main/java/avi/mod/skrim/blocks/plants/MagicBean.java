package avi.mod.skrim.blocks.plants;

import java.util.Random;

import avi.mod.skrim.blocks.CustomPlant;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBirchTree;

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
		System.out.println("Bean Finished Growing!  GENERATE THE MOTHERFUCKER... pos: " + pos + ", state: " + state);
		worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 4);
		WorldGenAbstractTree generator = new WorldGenBirchTree(true, true);
		generator.generate(worldIn, rand, pos);
	}

}
