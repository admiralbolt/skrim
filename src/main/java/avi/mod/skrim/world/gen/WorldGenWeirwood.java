package avi.mod.skrim.world.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import avi.mod.skrim.blocks.SkrimBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenWeirwood extends WorldGenerator {

	private int minHeight;
	private int maxHeight;

	private static final IBlockState TRUNK = SkrimBlocks.WEIRWOOD_WOOD.getDefaultState();
	private static final IBlockState LEAF = SkrimBlocks.WEIRWOOD_LEAF.getDefaultState();
	private static final int MAX_RADIUS = 4;

	public WorldGenWeirwood() {
		this(6, 10);
	}

	public WorldGenWeirwood(int minHeight, int maxHeight) {
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
	}

	public int getMinHeight() {
		return minHeight;
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public boolean isReplaceable(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		return state.getBlock().isAir(state, world, pos);
	}

	public List<BlockPos> getLeafPositions(World worldIn, Random rand, BlockPos position, int trunkY) {
		List<BlockPos> leafPositions = new ArrayList<BlockPos>();
		int maxLeaf = rand.nextInt(2) + 2;
		for (int y = trunkY + 1 - maxLeaf; y <= trunkY + maxLeaf; y++) {
			List<BlockPos> trunkPositions = this.getLeafCross(position, MAX_RADIUS - Math.abs(trunkY - y), y);
			leafPositions.addAll(trunkPositions);
		}
		return leafPositions;
	}

	public List<BlockPos> getLeafCross(BlockPos position, int radius, int height) {
		List<BlockPos> crossPositions = new ArrayList<BlockPos>();
		int startX = position.getX();
		int startY = position.getY();
		int startZ = position.getZ();
		for (int i = -radius; i <= radius; i++) {
			for (int j = -radius; j <= radius; j++) {
				if (i == 0 && j == 0) {
					if (startY != height) {
						crossPositions.add(new BlockPos(startX + i, height, startZ + j));
					}
				} else if (Math.abs(i) + Math.abs(j) <= radius) {
					crossPositions.add(new BlockPos(startX + i, height, startZ + j));
				}

			}
		}
		return crossPositions;
	}

	public boolean generate(World worldIn, Random rand, BlockPos position) {
		int height = rand.nextInt(this.maxHeight - this.minHeight) + this.minHeight;
		int startX = position.getX();
		int startY = position.getY();
		int startZ = position.getZ();
		int trunkY = startY + height;

		// Check trunk
		for (int y = startY; y <= trunkY; y++) {
			if (!this.isReplaceable(worldIn, new BlockPos(startX, y, startZ))) {
				return false;
			}
		}
		// Check leaves
		List<BlockPos> leafPositions = this.getLeafPositions(worldIn, rand, position, trunkY);
		for (BlockPos pos : leafPositions) {
			if (!this.isReplaceable(worldIn, pos)) {
				return false;
			}
		}
		// Generate that motherfucker
		for (int y = startY; y<= trunkY; y++) {
			this.placeLogAt(worldIn, new BlockPos(startX, y ,startZ));
		}
		for (BlockPos pos : leafPositions) {
			this.placeLeafAt(worldIn, pos);
		}

		return true;
	}

	private boolean isAir(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		return state.getBlock().isAir(state, world, pos);
	}

	private void placeLogAt(World worldIn, BlockPos pos) {
		this.setBlockAndNotifyAdequately(worldIn, pos, TRUNK);
	}

	private void placeLeafAt(World worldIn, BlockPos pos) {
		IBlockState state = worldIn.getBlockState(pos);

		if (state.getBlock().isAir(state, worldIn, pos) || state.getBlock().isLeaves(state, worldIn, pos)) {
			this.setBlockAndNotifyAdequately(worldIn, pos, LEAF);
		}
	}

}
