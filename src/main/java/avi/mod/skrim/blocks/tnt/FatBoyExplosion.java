package avi.mod.skrim.blocks.tnt;

import com.google.common.collect.Sets;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class FatBoyExplosion extends CustomExplosion {

  public static final float DEFAULT_SIZE = 25.0F;
  private static final boolean FLAMING = false;
  private static final boolean DAMAGES_TERRAIN = true;

  FatBoyExplosion(World worldIn, Entity entityIn, double x, double y, double z, @Nullable Float size) {
    super(worldIn, entityIn, x, y, z, (size == null) ? DEFAULT_SIZE : size, FLAMING, DAMAGES_TERRAIN);
    this.dropChance = 0;
  }

  @Override
  public void doExplosionA() {
    this.affectedBlockPositions.clear();

    int blastRadius = (int) this.explosionSize;

    Set<BlockPos> set = Sets.newHashSet();

    // Let's fuck some shit up.
    for (double x = this.explosionX - blastRadius; x < this.explosionX + blastRadius; x++) {
      for (double y = this.explosionY - blastRadius; y < this.explosionY + blastRadius; y++) {
        for (double z = this.explosionZ - blastRadius; z < this.explosionZ + blastRadius; z++) {
          set.add(new BlockPos(x, y, z));
        }
      }
    }

    this.affectedBlockPositions.addAll(set);
    float f3 = this.explosionSize * 2.0F;
    int k1 = MathHelper.floor(this.explosionX - (double) f3 - 1.0D);
    int l1 = MathHelper.floor(this.explosionX + (double) f3 + 1.0D);
    int i2 = MathHelper.floor(this.explosionY - (double) f3 - 1.0D);
    int i1 = MathHelper.floor(this.explosionY + (double) f3 + 1.0D);
    int j2 = MathHelper.floor(this.explosionZ - (double) f3 - 1.0D);
    int j1 = MathHelper.floor(this.explosionZ + (double) f3 + 1.0D);
    List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this.exploder, new AxisAlignedBB((double) k1, (double) i2,
        (double) j2, (double) l1, (double) i1, (double) j1));
    net.minecraftforge.event.ForgeEventFactory.onExplosionDetonate(this.worldObj, this, list, f3);
    Vec3d vec3d = new Vec3d(this.explosionX, this.explosionY, this.explosionZ);

    for (int k2 = 0; k2 < list.size(); ++k2) {
      Entity entity = (Entity) list.get(k2);

      if (entity.isImmuneToExplosions()) continue;
      double d12 = entity.getDistance(this.explosionX, this.explosionY, this.explosionZ) / (double) f3;

      if (d12 <= 1.0D) {
        double d5 = entity.posX - this.explosionX;
        double d7 = entity.posY + (double) entity.getEyeHeight() - this.explosionY;
        double d9 = entity.posZ - this.explosionZ;
        double d13 = (double) MathHelper.sqrt(d5 * d5 + d7 * d7 + d9 * d9);

        if (d13 != 0.0D) {
          d5 = d5 / d13;
          d7 = d7 / d13;
          d9 = d9 / d13;
          double d14 = (double) this.worldObj.getBlockDensity(vec3d, entity.getEntityBoundingBox());
          double d10 = (1.0D - d12);
          entity.attackEntityFrom(DamageSource.causeExplosionDamage(this),
              (float) ((int) ((d10 * d10 + d10) / 2.0D * 7.0D * (double) f3 + 1.0D)));
          double d11 = 1.0D;

          if (entity instanceof EntityLivingBase) {
            d11 = EnchantmentProtection.getBlastDamageReduction((EntityLivingBase) entity, d10);
          }

          entity.motionX += d5 * d11;
          entity.motionY += d7 * d11;
          entity.motionZ += d9 * d11;

          if (entity instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer) entity;

            if (!entityplayer.isSpectator() && (!entityplayer.isCreative() || !entityplayer.capabilities.isFlying)) {
              this.playerKnockbackMap.put(entityplayer, new Vec3d(d5 * d10, d7 * d10, d9 * d10));
            }
          }
        }
      }
    }
  }
}