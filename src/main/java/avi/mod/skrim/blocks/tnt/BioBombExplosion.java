package avi.mod.skrim.blocks.tnt;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class BioBombExplosion extends CustomExplosion {

  public static float DEFAULT_SIZE = 8.0F;
  private static boolean FLAMING = false;
  private static boolean SMOKING = true;

  public BioBombExplosion(World worldIn, Entity entityIn, double x, double y, double z) {
    this(worldIn, entityIn, x, y, z, DEFAULT_SIZE);
  }

  public BioBombExplosion(World worldIn, Entity entityIn, double x, double y, double z, float size) {
    super(worldIn, entityIn, x, y, z, size, FLAMING, SMOKING);
  }

  @Override
  public void doExplosionA() {
    super.doExplosionA();
    this.clearAffectedBlockPositions();
  }

  @Override
  public void doExplosionB(boolean particles) {
    super.doExplosionB(particles);
  }

}
