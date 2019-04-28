package avi.mod.skrim.world.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import avi.mod.skrim.blocks.SkrimBlocks;
import avi.mod.skrim.world.loot.CustomLootTables;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockVine;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenBeanstalk extends WorldGenerator {

	private int minHeight;
	private int maxHeight;
	private int minLeafDelta;
	private int maxLeafDelta;
	private int minLeafLength;
	private int maxLeafLength;

	public WorldGenBeanstalk() {
		this(30, 50, 5, 7, 5, 8);
	}

	public WorldGenBeanstalk(int minHeight, int maxHeight, int minLeafDelta, int maxLeafDelta, int minLeafLength, int maxLeafLength) {
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
		this.minLeafDelta = minLeafDelta;
		this.maxLeafDelta = maxLeafDelta;
		this.minLeafLength = minLeafLength;
		this.maxLeafLength = maxLeafLength;
	}

	public int getMinHeight() {
		return minHeight;
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public int getMinLeafDelta() {
		return minLeafDelta;
	}

	public int getMaxLeafDelta() {
		return maxLeafDelta;
	}

	public int getMinLeafLength() {
		return minLeafLength;
	}

	public int getMaxLeafLength() {
		return maxLeafLength;
	}

	private boolean isReplaceable(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		return state.getBlock().isAir(state, world, pos);
	}

	public boolean generate(World worldIn, Random rand, BlockPos position) {
		int height = rand.nextInt(this.maxHeight - this.minHeight) + this.minHeight;
		/**
		 * Generate leaves ahead of time that way we can check block positions
		 * accordingly. Leaves will go: +x, +y, -x, -y
		 */
		List<BeanstalkLeaf> leaves = new ArrayList<>();
		BeanstalkLeaf leaf = new BeanstalkLeaf(rand, this.minLeafLength, this.maxLeafLength, this.minLeafDelta, this.maxLeafDelta, position.getY() + 2, null);
		while (leaf.leafHeight < height + position.getY()) {
			leaves.add(leaf);
			leaf = new BeanstalkLeaf(rand, this.minLeafLength, this.maxLeafLength, this.minLeafDelta, this.maxLeafDelta, leaf.leafHeight, leaf.cardinality);
		}

		int x = position.getX();
		int z = position.getZ();

		BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

		if (position.getY() >= 1 && position.getY() + height <= 255) {
			// Check the stalk itself
			for (int y = position.getY(); y <= position.getY() + height; y++) {
				if (!this.isReplaceable(worldIn, blockpos$mutableblockpos.setPos(x, y, z))) {
					return false;
				}
				if (!this.isReplaceable(worldIn, blockpos$mutableblockpos.north())) {
					return false;
				}
				if (!this.isReplaceable(worldIn, blockpos$mutableblockpos.east())) {
					return false;
				}
				if (!this.isReplaceable(worldIn, blockpos$mutableblockpos.south())) {
					return false;
				}
				if (!this.isReplaceable(worldIn, blockpos$mutableblockpos.west())) {
					return false;
				}
			}
			// Check the leaves
			for (BeanstalkLeaf checkLeaf : leaves) {
				for (int i = 1; i <= checkLeaf.leafLength; i++) {
					if (!this.isReplaceable(worldIn, blockpos$mutableblockpos.setPos(x + i * checkLeaf.cardinality.getXMult(), checkLeaf.leafHeight,
							z + i * checkLeaf.cardinality.getZMult()))) {
						return false;
					}
					if (i == 1) {
						BeanstalkLeaf.LeafEnum prevFacing = BeanstalkLeaf.LeafEnum.prev(checkLeaf.cardinality);
						if (!this.isReplaceable(worldIn,
								blockpos$mutableblockpos.setPos(x + checkLeaf.cardinality.getXMult() + prevFacing.getXMult(), checkLeaf.leafHeight, z + checkLeaf.cardinality.getZMult() + prevFacing.getZMult()))) {
							return false;
						}
					} else if (i == checkLeaf.leafLength) {
						if (!this.isReplaceable(worldIn, blockpos$mutableblockpos.setPos(x + i * checkLeaf.cardinality.getXMult(), checkLeaf.leafHeight + 1,
								z + i * checkLeaf.cardinality.getZMult()))) {
							return false;
						}
					}
				}
			}

			// We can generate! Do the thing!
			IBlockState beanState = SkrimBlocks.BEANSTALK_BLOCK.getDefaultState();

			// Generate the stalk
			for (int y = position.getY(); y <= position.getY() + height; y++) {
				BlockPos stalkPos = new BlockPos(x, y, z);
				worldIn.setBlockState(new BlockPos(x, y, z), beanState);
				this.addVine(worldIn, stalkPos.west(), BlockVine.EAST);
				this.addVine(worldIn, stalkPos.east(), BlockVine.WEST);
				this.addVine(worldIn, stalkPos.north(), BlockVine.SOUTH);
				this.addVine(worldIn, stalkPos.south(), BlockVine.NORTH);
			}
			// Check the leaves
			for (BeanstalkLeaf checkLeaf : leaves) {
				for (int i = 1; i <= checkLeaf.leafLength; i++) {
					int leafX = x + i * checkLeaf.cardinality.getXMult();
					int leafZ = z + i * checkLeaf.cardinality.getZMult();
					worldIn.setBlockState(new BlockPos(leafX, checkLeaf.leafHeight, leafZ), beanState);

					if (i == 1) {
						BeanstalkLeaf.LeafEnum prevFacing = BeanstalkLeaf.LeafEnum.prev(checkLeaf.cardinality);
						int newLeafX = leafX + prevFacing.getXMult();
						int newLeafZ = leafZ + prevFacing.getZMult();
						worldIn.setBlockState(new BlockPos(newLeafX, checkLeaf.leafHeight, newLeafZ), beanState);
					} else if (i == checkLeaf.leafLength) {
						BlockPos chestPos = new BlockPos(leafX, checkLeaf.leafHeight + 1, leafZ);
						worldIn.setBlockState(chestPos, Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, checkLeaf.cardinality.getChestFacing()),
								2);
						TileEntity chestEntity = worldIn.getTileEntity(chestPos);
						if (chestEntity instanceof TileEntityChest) {
							((TileEntityChest) chestEntity).setLootTable(CustomLootTables.CHESTS_BEANSTALK, rand.nextLong());
						}
					}
				}
			}
			return true;
		} else {
			return false;
		}
	}

	private void addVine(World worldIn, BlockPos pos, PropertyBool prop) {
		IBlockState iblockstate = Blocks.VINE.getDefaultState().withProperty(prop, true);
		this.setBlockAndNotifyAdequately(worldIn, pos, iblockstate);
		int i = 4;

		for (pos = pos.down(); isAir(worldIn, pos) && i > 0; --i) {
			this.setBlockAndNotifyAdequately(worldIn, pos, iblockstate);
			pos = pos.down();
		}
	}

	private boolean isAir(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		return state.getBlock().isAir(state, world, pos);
	}

	public static class BeanstalkLeaf {

		private int leafLength;
		private int leafHeight;
		private LeafEnum cardinality;

		public BeanstalkLeaf(Random rand, int minLeafLength, int maxLeafLength, int minLeafDelta, int maxLeafDelta, int lastLeafHeight, LeafEnum cardinality) {
			this.leafLength = rand.nextInt(maxLeafLength - minLeafLength) + minLeafLength;
			this.leafHeight = rand.nextInt(maxLeafDelta - minLeafDelta) + minLeafDelta + lastLeafHeight;
			this.cardinality = LeafEnum.next(cardinality);
		}

		public enum LeafEnum {
			POSITIVE_X, POSITIVE_Z, NEGATIVE_X, NEGATIVE_Z;

			public EnumFacing getChestFacing() {
				if (this == POSITIVE_X) {
					return EnumFacing.WEST;
				} else if (this == NEGATIVE_X) {
					return EnumFacing.EAST;
				} else if (this == POSITIVE_Z) {
					return EnumFacing.NORTH;
				} else if (this == NEGATIVE_Z) {
					return EnumFacing.SOUTH;
				} else {
					return null;
				}
			}

			public int getXMult() {
				if (this == POSITIVE_X) {
					return 1;
				} else if (this == NEGATIVE_X) {
					return -1;
				} else {
					return 0;
				}
			}

			public int getZMult() {
				if (this == POSITIVE_Z) {
					return 1;
				} else if (this == NEGATIVE_Z) {
					return -1;
				} else {
					return 0;
				}
			}

			public static LeafEnum prev(LeafEnum en) {
				if (en == POSITIVE_X) {
					return NEGATIVE_Z;
				} else if (en == NEGATIVE_Z) {
					return NEGATIVE_X;
				} else if (en == NEGATIVE_X) {
					return POSITIVE_Z;
				} else {
					return POSITIVE_X;
				}
			}

			public static LeafEnum next(LeafEnum en) {
				if (en == POSITIVE_X) {
					return POSITIVE_Z;
				} else if (en == POSITIVE_Z) {
					return NEGATIVE_X;
				} else if (en == NEGATIVE_X) {
					return NEGATIVE_Z;
				} else {
					return POSITIVE_X;
				}
			}

			public static LeafEnum fromInt(int input) {
				if (input == 0) {
					return POSITIVE_X;
				} else if (input == 1) {
					return POSITIVE_Z;
				} else if (input == 2) {
					return NEGATIVE_X;
				} else if (input == 3) {
					return NEGATIVE_Z;
				} else {
					return null;
				}
			}

			public static int toInt(LeafEnum en) {
				if (en == POSITIVE_X) {
					return 0;
				} else if (en == POSITIVE_Z) {
					return 1;
				} else if (en == NEGATIVE_X) {
					return 2;
				} else if (en == NEGATIVE_Z) {
					return 3;
				} else {
					return -1;
				}
			}
		}
	}

}
