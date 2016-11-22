package avi.mod.skrim.blocks;

import java.util.List;
import java.util.Random;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.items.ItemModelProvider;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.EnumPlantType;

public abstract class CustomPlant extends BlockBush implements ItemModelProvider, IGrowable {

	private String name;
	private int maxAge;

	public CustomPlant(String name, int maxAge) {
		this.name = name;
		this.maxAge = maxAge;

		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		setCreativeTab(Skrim.creativeTab);
		
		this.setDefaultState(this.blockState.getBaseState().withProperty(this.getAgeProperty(), Integer.valueOf(0)));
        this.setTickRandomly(true);
	}

	protected abstract PropertyInteger getAgeProperty();

	@Override
	public void registerItemModel(Item item) {
		// ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Skrim.modId + ":" + name));
		Skrim.instance.proxy.registerItemRenderer(item, 0, name);
	}

	@Override
	public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
		return true;
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		return true;
	}

	@Override
	public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		int i = this.getAge(state) + this.getBonemealAgeIncrease(worldIn);
		int j = this.getMaxAge();

		if (i > j) {
			i = j;
		}

		System.out.println("Setting age to: " + i + ", max: " + j);
		worldIn.setBlockState(pos, this.withAge(i), 2);
		if (i == j) {
			this.finishedGrowing(worldIn, rand, pos, state);
		}
	}

	protected int getBonemealAgeIncrease(World worldIn) {
		return MathHelper.getRandomIntegerInRange(worldIn.rand, 2, 5);
	}

	public void finishedGrowing(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		System.out.println("calling finished Growing()... pos: " + pos + ", state: " + state);
	}

	public int getAge(IBlockState state) {
		return state.getValue(this.getAgeProperty()).intValue();
	}

	public int getMaxAge() {
		return this.maxAge;
	}

	public IBlockState withAge(int age) {
		return this.getDefaultState().withProperty(this.getAgeProperty(), Integer.valueOf(age));
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		IBlockState soil = worldIn.getBlockState(pos.down());
		return super.canPlaceBlockAt(worldIn, pos)
				&& soil.getBlock().canSustainPlant(soil, worldIn, pos.down(), net.minecraft.util.EnumFacing.UP, this);
	}

	@Override
	protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (!this.canBlockStay(worldIn, pos, state)) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
		}
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		List<ItemStack> ret = new java.util.ArrayList<ItemStack>();
		Random rand = world instanceof World ? ((World) world).rand : RANDOM;

		int count = quantityDropped(state, fortune, rand);
		for (int i = 0; i < count; i++) {
			Item item = this.getItemDropped(state, rand, fortune);
			if (item != null) {
				ret.add(new ItemStack(item, 1, this.damageDropped(state)));
			}
		}
		return ret;
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);

		if (worldIn.getLightFromNeighbors(pos.up()) >= 9) {
			int i = this.getAge(state);

			if (i < this.getMaxAge()) {
				float f = this.getGrowthChance(worldIn, pos);

				if (rand.nextInt((int) (25.0F / f) + 1) == 0) {
					worldIn.setBlockState(pos, this.withAge(i + 1), 2);
				}
			} else {
				this.finishedGrowing(worldIn, rand, pos, state);
			}
		}
	}

	public float getGrowthChance(World worldIn, BlockPos pos) {
		float f = 1.0F;
		BlockPos blockpos = pos.down();

		for (int i = -1; i <= 1; ++i) {
			for (int j = -1; j <= 1; ++j) {
				float f1 = 0.0F;
				IBlockState iblockstate = worldIn.getBlockState(blockpos.add(i, 0, j));

				if (iblockstate.getBlock().canSustainPlant(iblockstate, worldIn, blockpos.add(i, 0, j),
						net.minecraft.util.EnumFacing.UP, (net.minecraftforge.common.IPlantable) this)) {
					f1 = 1.0F;

					if (iblockstate.getBlock().isFertile(worldIn, blockpos.add(i, 0, j))) {
						f1 = 3.0F;
					}
				}

				if (i != 0 || j != 0) {
					f1 /= 4.0F;
				}

				f += f1;
			}
		}

		BlockPos blockpos1 = pos.north();
		BlockPos blockpos2 = pos.south();
		BlockPos blockpos3 = pos.west();
		BlockPos blockpos4 = pos.east();
		boolean flag = this == worldIn.getBlockState(blockpos3).getBlock()
				|| this == worldIn.getBlockState(blockpos4).getBlock();
		boolean flag1 = this == worldIn.getBlockState(blockpos1).getBlock()
				|| this == worldIn.getBlockState(blockpos2).getBlock();

		if (flag && flag1) {
			f /= 2.0F;
		} else {
			boolean flag2 = this == worldIn.getBlockState(blockpos3.north()).getBlock()
					|| this == worldIn.getBlockState(blockpos4.north()).getBlock()
					|| this == worldIn.getBlockState(blockpos4.south()).getBlock()
					|| this == worldIn.getBlockState(blockpos3.south()).getBlock();

			if (flag2) {
				f /= 2.0F;
			}
		}

		return f;
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	public IBlockState getStateFromMeta(int meta) {
		return this.withAge(meta);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	public int getMetaFromState(IBlockState state) {
		return this.getAge(state);
	}

	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { this.getAgeProperty() });
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
		return EnumPlantType.Plains;
	}

}
