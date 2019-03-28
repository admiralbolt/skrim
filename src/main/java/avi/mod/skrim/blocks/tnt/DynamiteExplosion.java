package avi.mod.skrim.blocks.tnt;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class DynamiteExplosion extends CustomExplosion {

  private static float DEFAULT_SIZE = 5.0F;
  private static boolean FLAMING = false;
  private static boolean SMOKING = true;

  public DynamiteExplosion(World worldIn, Entity entityIn, double x, double y, double z) {
    super(worldIn, entityIn, x, y, z, DEFAULT_SIZE, FLAMING, SMOKING);
    this.dropChance = 1.0F;
  }

}
