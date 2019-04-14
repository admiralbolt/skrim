package avi.mod.skrim.blocks.tnt;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BioBombExplosion extends CustomExplosion {

  public static final float DEFAULT_SIZE = 8.0F;
  private static final boolean FLAMING = false;
  private static final boolean DAMAGES_TERRAIN = false;

  public BioBombExplosion(World worldIn, Entity entityIn, double x, double y, double z, @Nullable Float size) {
    super(worldIn, entityIn, x, y, z, (size == null) ? DEFAULT_SIZE : size, FLAMING, DAMAGES_TERRAIN);
  }

}
