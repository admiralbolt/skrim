package avi.mod.skrim.items.artifacts;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.blocks.ArtifactItemBlock;
import avi.mod.skrim.blocks.SkrimBlocks;
import avi.mod.skrim.blocks.tnt.CustomTNTPrimed;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * God it's beautiful.
 */
public class FatBoy extends BlockTNT {

  public String name;

  public FatBoy() {
    super();
    this.name = "fat_boy";
    this.setUnlocalizedName(name);
    this.setRegistryName(name);
  }

  @Override
  public void onBlockDestroyedByExplosion(World worldIn, @Nonnull BlockPos pos, @Nonnull Explosion explosionIn) {
    if (worldIn.isRemote) return;
    CustomTNTPrimed tntPrimed = new CustomTNTPrimed("fat_boy", worldIn, pos.getX() + 0.5,
        pos.getY(), pos.getZ() + 0.5, explosionIn.getExplosivePlacedBy());
    tntPrimed.setFuse(worldIn.rand.nextInt(tntPrimed.getFuse() / 4) + tntPrimed.getFuse() / 8);
    worldIn.spawnEntity(tntPrimed);
  }

  @Override
  public void explode(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state,
                      @Nonnull EntityLivingBase igniter) {
    if (worldIn.isRemote || !state.getValue(EXPLODE)) return;
    CustomTNTPrimed tntPrimed = new CustomTNTPrimed("fat_boy", worldIn, pos.getX() + 0.5,
        pos.getY(), pos.getZ() + 0.5, igniter);
    worldIn.spawnEntity(tntPrimed);
    worldIn.playSound(null, tntPrimed.posX, tntPrimed.posY, tntPrimed.posZ,
        SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
  }



  public static class ItemBlock extends ArtifactItemBlock {

    public ItemBlock () {
      super(SkrimBlocks.FAT_BOY);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      tooltip.add("§4A portable mini-nuke.§r");
      tooltip.add("§4Ignores explosion resistance of blocks.§r");
      tooltip.add("§e\"A blast so big it can take down a whole server.\"");
    }
  }


}
