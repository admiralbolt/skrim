package avi.mod.skrim.blocks;

import java.util.List;

import javax.annotation.Nullable;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.items.CustomCake;
import avi.mod.skrim.items.ItemModelProvider;
import avi.mod.skrim.items.ModItems;
import avi.mod.skrim.skills.cooking.SkillCooking;
import avi.mod.skrim.tileentity.CakeTileEntity;
import avi.mod.skrim.utils.Utils;
import net.minecraft.block.BlockCake;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.FoodStats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CustomCakeBlock extends BlockCake implements ItemModelProvider, ITileEntityProvider {

	private String name;
	private int level;

	public CustomCakeBlock(String name) {
		super();
		this.name = name;
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		this.eatCustomCake(worldIn, pos, state, playerIn);
		this.applyAdditionalEffects(playerIn);
		return true;
	}
	
	public void applyAdditionalEffects(EntityPlayer player) {
		
	}

	public void eatCustomCake(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		if (player.canEat(false) || state.getBlock() == ModBlocks.ANGEL_CAKE) {
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof CakeTileEntity) {
				CakeTileEntity cakeEntity = (CakeTileEntity) te;
				int level = cakeEntity.getLevel();
				
				boolean overFull = level >= 25;
				boolean panacea = level >= 50;
				boolean superFood = level >= 75;

				int additionalHeal = CustomCake.getTotalFood(this, level);
				float additionalSaturation = CustomCake.getTotalSaturation(this, level);
				
				FoodStats playerStats = player.getFoodStats();

				int newFood = playerStats.getFoodLevel() + additionalHeal;
				float newSaturation = playerStats.getSaturationLevel() + additionalSaturation;

				newFood = (newFood > 20 && !overFull) ? 20 : newFood;
				newSaturation = (newSaturation > newFood && !overFull) ? newFood : newSaturation;

				if (panacea) {
					player.removePotionEffect(MobEffects.POISON);
					player.removePotionEffect(MobEffects.HUNGER);
					player.removePotionEffect(MobEffects.NAUSEA);
				}

				if (superFood) {
					PotionEffect regenEffect = new PotionEffect(MobEffects.REGENERATION, 200, 1, false, false);
					PotionEffect speedEffect = new PotionEffect(MobEffects.SPEED, 200, 1, false, false);
					Utils.addOrCombineEffect(player, regenEffect);
					Utils.addOrCombineEffect(player, speedEffect);
				}
								
				/**
				 * A valiant attempt to keep me from over-filling.
				 * But not valiant enough.
				 * For some reason setFoodSaturationLevel is client side only.
				 * But setFoodLevel bypasses the maximum for food...
				 * BUT, addStats will reset the maximums for both.....
				 * BUUTTT, readNBT(NBTTagCompound compound) assigns directly so.....
				 * we need to set foodLevel, foodTimer, foodSaturationLevel, foodExhaustionLevel
				 */
				NBTTagCompound storeCompound = new NBTTagCompound();
				storeCompound.setInteger("foodLevel", newFood);
				storeCompound.setInteger("foodTimer", 0); // Starts at 0, counts up to 80 ticks
				storeCompound.setFloat("foodSaturationLevel", newSaturation);
				storeCompound.setFloat("foodExhaustionLevel", 0); // Food exhaustion max 40 when fully exhausted
				playerStats.readNBT(storeCompound);
				player.addStat(StatList.CAKE_SLICES_EATEN);
				
				int i = ((Integer) state.getValue(BITES)).intValue();

				if (i < 6) {
					world.setBlockState(pos, state.withProperty(BITES, Integer.valueOf(i + 1)), 3);
					TileEntity newCake = world.getTileEntity(pos);
					if (newCake instanceof CakeTileEntity) {
						((CakeTileEntity) newCake).setLevel(level);
					}
				} else {
					world.setBlockToAir(pos);
				}
			}

		}
	}

	@Override
	public void registerItemModel(Item itemBlock) {
		Skrim.proxy.registerItemRenderer(itemBlock, 0, this.name);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new CakeTileEntity();
	}
	
	public static CustomCakeBlock getBlockFromItem(Item item) {
		if (item == ModItems.SKRIM_CAKE) {
			return ModBlocks.SKRIM_CAKE;
		} else if (item == ModItems.ANGEL_CAKE) {
			return ModBlocks.ANGEL_CAKE;
		} else {
			return null;
		}
	}

}
