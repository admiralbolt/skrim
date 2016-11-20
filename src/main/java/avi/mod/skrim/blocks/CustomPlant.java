package avi.mod.skrim.blocks;

import java.util.List;
import java.util.Random;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.items.ItemModelProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class CustomPlant extends BlockBush implements ItemModelProvider, IGrowable {

	public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 7);

	private String name;
	private int maxAge;
	private PropertyInteger age;

	public CustomPlant(String name, int maxAge) {
		this.name = name;
		this.maxAge = maxAge;
		age = PropertyInteger.create("age", 0, maxAge);

		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		setCreativeTab(Skrim.creativeTab);
	}

	@Override
	public void registerItemModel(Item item) {
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

		worldIn.setBlockState(pos, this.withAge(i), 2);
	}

	protected int getBonemealAgeIncrease(World worldIn) {
		return MathHelper.getRandomIntegerInRange(worldIn.rand, 2, 5);
	}

	public void finishedGrowing(World worldIn, Random rand, BlockPos pos, IBlockState state) {

	}
	
	public int getAge(IBlockState state) {
		return state.getValue(AGE).intValue();
	}
	
	public int getMaxAge() {
		return this.maxAge;
	}
	
	public IBlockState withAge(int age) {
    return this.getDefaultState().withProperty(AGE, Integer.valueOf(age));
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		IBlockState soil = worldIn.getBlockState(pos.down());
		return super.canPlaceBlockAt(worldIn, pos) && soil.getBlock().canSustainPlant(soil, worldIn, pos.down(), net.minecraft.util.EnumFacing.UP, this);
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

}
