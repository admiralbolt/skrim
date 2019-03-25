package avi.mod.skrim.items.food;

import java.util.List;

import javax.annotation.Nullable;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.blocks.food.CustomCakeBlock;
import avi.mod.skrim.blocks.ModBlocks;
import avi.mod.skrim.items.ItemBase;
import avi.mod.skrim.items.ModItems;
import avi.mod.skrim.skills.cooking.SkillCooking;
import avi.mod.skrim.tileentity.CakeTileEntity;
import avi.mod.skrim.utils.Obfuscation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CustomCake extends ItemBlockSpecial implements ItemBase {

	private String name;

	public CustomCake(Block block, String name) {
		super(block);
		this.name = name;
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		this.setCreativeTab(Skrim.creativeTab);
	}

	// Override this
	public Block getBlock() {
		return null;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY,
			float hitZ) {
		ItemStack stack = playerIn.getHeldItem(hand);
		IBlockState iblockstate = worldIn.getBlockState(pos);
		Block block = iblockstate.getBlock();
		Block placeBlock = this.getBlock();

		if (block == Blocks.SNOW_LAYER && ((Integer) iblockstate.getValue(BlockSnow.LAYERS)).intValue() < 1) {
			facing = EnumFacing.UP;
		} else if (!block.isReplaceable(worldIn, pos)) {
			pos = pos.offset(facing);
		}

		if (playerIn.canPlayerEdit(pos, facing, stack) && !stack.isEmpty() && Obfuscation.canBlockBePlaced(worldIn, placeBlock, pos, false, facing, playerIn)) {
			IBlockState iblockstate1 = placeBlock.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, 0, playerIn);

			if (!worldIn.setBlockState(pos, iblockstate1, 11)) {
				return EnumActionResult.FAIL;
			} else {
				iblockstate1 = worldIn.getBlockState(pos);

				if (iblockstate1.getBlock() == placeBlock) {
					ItemBlock.setTileEntityNBT(worldIn, playerIn, pos, stack);
					iblockstate1.getBlock().onBlockPlacedBy(worldIn, pos, iblockstate1, playerIn, stack);
				}

				TileEntity te = worldIn.getTileEntity(pos);
				if (te != null && te instanceof CakeTileEntity) {
					CakeTileEntity cakeEntity = (CakeTileEntity) te;
					if (stack.hasTagCompound()) {
						NBTTagCompound compound = stack.getTagCompound();
						if (compound.hasKey("level")) {
							cakeEntity.setLevel(compound.getInteger("level"));
						}
					}
				}

				SoundType soundtype = iblockstate1.getBlock().getSoundType(iblockstate1, worldIn, pos, playerIn);
				worldIn.playSound(playerIn, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F,
						soundtype.getPitch() * 0.8F);
				Obfuscation.setStackSize(stack, Obfuscation.getStackSize(stack) - 1);
				return EnumActionResult.SUCCESS;
			}
		} else {
			return EnumActionResult.FAIL;
		}
	}

	public int getBaseFood() {
		return 2;
	}

	public float getBaseSaturation() {
		return 0.1F;
	}

	public static int getExtraFood(CustomCake cake, int level) {
		int baseHeal = cake.getBaseFood();
		double extraFood = SkillCooking.extraFood(level);
		return (int) (baseHeal * extraFood);
	}

	public static float getExtraSaturation(CustomCake cake, int level) {
		int baseHeal = cake.getBaseFood();
		int additionalHeal = getExtraFood(cake, level);
		float satMod = cake.getBaseSaturation();
		double extraSaturation = SkillCooking.extraSaturation(level);
		// Apply the old sat mod to the new healing, and the new sat mod to everything
		return satMod * additionalHeal + (int) (extraSaturation * (baseHeal + additionalHeal));
	}

	public static int getTotalFood(CustomCake cake, int level) {
		return getExtraFood(cake, level) + cake.getBaseFood();
	}

	public static float getTotalSaturation(CustomCake cake, int level) {
		return getExtraSaturation(cake, level) + cake.getBaseSaturation() * cake.getBaseFood();
	}

	public static int getTotalFood(CustomCakeBlock block, int level) {
		CustomCake cake = getItemFromBlock(block);
		return getTotalFood(cake, level);
	}

	public static float getTotalSaturation(CustomCakeBlock block, int level) {
		CustomCake cake = getItemFromBlock(block);
		return getTotalSaturation(cake, level);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (stack.hasTagCompound()) {
			NBTTagCompound compound = stack.getTagCompound();
			if (compound.hasKey("level")) {
				int level = compound.getInteger("level");
				CustomCake cake = (CustomCake) stack.getItem();
				tooltip.add("Cooking Level: §a" + level + "§r");
				tooltip.add("Food restored: §a" + getTotalFood(cake, level) + "§r, saturation restored: §a"
						+ String.format("%.1f", getTotalSaturation(cake, level)) + "§r.");
				if (level >= 25) {
					tooltip.add("§4Overfull§r");
					if (level >= 50) {
						tooltip.add("§4Panacea§r");
						if (level >= 75) {
							tooltip.add("§4Super Food§r");
						}
					}
				}
			}
		}
	}

	public static CustomCake getItemFromBlock(CustomCakeBlock block) {
		if (block == ModBlocks.SKRIM_CAKE) {
			return ModItems.SKRIM_CAKE;
		} else if (block == ModBlocks.ANGEL_CAKE) {
			return ModItems.ANGEL_CAKE;
		} else {
			return null;
		}
	}

	@Override
	public String getTexturePath() {
		return "blocks";
	}
}
