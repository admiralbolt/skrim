package avi.mod.skrim.blocks.food;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.blocks.ModBlocks;
import avi.mod.skrim.items.ItemModelProvider;
import avi.mod.skrim.items.food.CustomCake;
import avi.mod.skrim.tileentity.CakeTileEntity;
import avi.mod.skrim.utils.Utils;
import net.minecraft.block.BlockCake;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.FoodStats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class CustomCakeBlock extends BlockCake implements ITileEntityProvider {

  private String name;

  public CustomCakeBlock(String name) {
    super();
    this.name = name;
    this.setUnlocalizedName(name);
    this.setRegistryName(name);
  }

  @Override
  public boolean onBlockActivated(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state,
                                  EntityPlayer playerIn,
                                  @Nonnull EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    this.eatCustomCake(worldIn, pos, state, playerIn);
    this.applyAdditionalEffects(playerIn);
    return true;
  }

  public void applyAdditionalEffects(EntityPlayer player) {

  }

  private void eatCustomCake(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
    if (!(world.getTileEntity(pos) instanceof CakeTileEntity) && !player.canEat(false) && !(state.getBlock() == ModBlocks.ANGEL_CAKE))
      return;
    CakeTileEntity cakeEntity = (CakeTileEntity) world.getTileEntity(pos);
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

    int i = state.getValue(BITES);

    if (i >= 6) {
      world.setBlockToAir(pos);
      return;
    }

    world.setBlockState(pos, state.withProperty(BITES, i + 1), 3);
    TileEntity newCake = world.getTileEntity(pos);
    if (newCake instanceof CakeTileEntity) {
      ((CakeTileEntity) newCake).setLevel(level);
    }
  }

  @Override
  public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
    return new CakeTileEntity();
  }

}
