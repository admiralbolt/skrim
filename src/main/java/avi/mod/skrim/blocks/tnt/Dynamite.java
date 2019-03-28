package avi.mod.skrim.blocks.tnt;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.items.ItemModelProvider;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class Dynamite extends BlockTNT implements ItemModelProvider {

  public String name;

  public Dynamite() {
    super();
    this.name = "dynamite";
    this.setUnlocalizedName(name);
    this.setRegistryName(name);
  }

  @Override
  public void registerItemModel(Item itemBlock) {
    Skrim.proxy.registerItemRenderer(itemBlock, 0, name);
    this.setCreativeTab(Skrim.creativeTab);
  }

  @Override
  public void onBlockDestroyedByExplosion(World worldIn, @Nonnull BlockPos pos, @Nonnull Explosion explosionIn) {
    if (worldIn.isRemote) return;
    CustomTNTPrimed tntPrimed = new CustomTNTPrimed("dynamite", worldIn, pos.getX() + 0.5,
        pos.getY(), pos.getZ() + 0.5, explosionIn.getExplosivePlacedBy());
    tntPrimed.setFuse(worldIn.rand.nextInt(tntPrimed.getFuse() / 4) + tntPrimed.getFuse() / 8);
    worldIn.spawnEntity(tntPrimed);
  }

  @Override
  public void explode(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state,
                      @Nonnull EntityLivingBase igniter) {
    if (worldIn.isRemote || !state.getValue(EXPLODE)) return;
    CustomTNTPrimed tntPrimed = new CustomTNTPrimed("dynamite", worldIn, pos.getX() + 0.5,
        pos.getY(), pos.getZ() + 0.5, igniter);
    worldIn.spawnEntity(tntPrimed);
    worldIn.playSound(null, tntPrimed.posX, tntPrimed.posY, tntPrimed.posZ,
        SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
  }

}
