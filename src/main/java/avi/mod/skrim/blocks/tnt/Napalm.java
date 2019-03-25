package avi.mod.skrim.blocks.tnt;

import net.minecraft.block.BlockTNT;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class Napalm extends BlockTNT {

  public String name;

  public Napalm() {
    super();
    this.name = "napalm";
    this.setRegistryName(name);
    this.setUnlocalizedName(name);
  }

  @Override
  public void onBlockDestroyedByExplosion(World worldIn, BlockPos pos, Explosion explosionIn) {
    if (worldIn.isRemote) return;
    CustomTNTPrimed entitytntprimed = new CustomTNTPrimed("napalm", worldIn, (double) ((float) pos.getX() + 0.5F),
				(double) pos.getY(), (double) ((float) pos.getZ() + 0.5F), explosionIn.getExplosivePlacedBy());
    entitytntprimed.setFuse((short) (worldIn.rand.nextInt(entitytntprimed.getFuse() / 4) + entitytntprimed.getFuse() / 8));
    worldIn.spawnEntity(entitytntprimed);
  }

  @Override
  public void explode(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase igniter) {
    if (worldIn.isRemote || !state.getValue(EXPLODE)) return;
    CustomTNTPrimed entitytntprimed = new CustomTNTPrimed("napalm", worldIn, (double) ((float) pos.getX() + 0.5F),
				(double) pos.getY(), (double) ((float) pos.getZ() + 0.5F), igniter);
    worldIn.spawnEntity(entitytntprimed);
    worldIn.playSound(null, entitytntprimed.posX, entitytntprimed.posY, entitytntprimed.posZ,
				SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);

  }

}
