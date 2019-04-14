package avi.mod.skrim.blocks.tnt;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class DynamiteExplosion extends CustomExplosion {

  private static final float DEFAULT_SIZE = 5.0F;
  private static final boolean FLAMING = false;
  private static final boolean SMOKING = true;

  public DynamiteExplosion(World worldIn, Entity entityIn, double x, double y, double z, @Nullable Float size) {
    super(worldIn, entityIn, x, y, z, (size == null) ? DEFAULT_SIZE : size, FLAMING, SMOKING);
    this.dropChance = 1.0f;
  }

}
