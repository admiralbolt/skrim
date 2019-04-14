package avi.mod.skrim.entities.projectile;

import avi.mod.skrim.blocks.tnt.CustomExplosion;
import avi.mod.skrim.blocks.tnt.CustomTNTPrimed;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.demolition.SkillDemolition;
import avi.mod.skrim.utils.Obfuscation;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class Rocket extends EntityArrow implements IEntityAdditionalSpawnData {

  private String explosionType;

  public Rocket(World worldIn) {
    super(worldIn);
  }

  public Rocket(World worldIn, EntityLivingBase shooter, ItemStack ammoType) {
    super(worldIn, shooter);
    this.explosionType = CustomTNTPrimed.getExplosionType(ammoType);
    this.pickupStatus = EntityArrow.PickupStatus.DISALLOWED;
  }

  @Override
  protected ItemStack getArrowStack() {
    return null;
  }

  @Override
  protected void onHit(RayTraceResult raytraceResultIn) {
    if (this.world.isRemote) return;
    Entity entity = raytraceResultIn.entityHit;
    BlockPos pos = (entity == null) ? raytraceResultIn.getBlockPos() : entity.getPosition();
    if (this.explosionType != null) {
      if (this.explosionType.equals("normal_tnt")) {
        this.world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 4.0F, false);
      } else {
        CustomExplosion explosion = CustomTNTPrimed.createExplosion(this.explosionType, null, this.world, this.shootingEntity, pos.getX(), pos.getY(), pos.getZ());
        if (this.shootingEntity.hasCapability(Skills.DEMOLITION, EnumFacing.NORTH)) {
          SkillDemolition demolition = (SkillDemolition) this.shootingEntity.getCapability(Skills.DEMOLITION, EnumFacing.NORTH);
          if (explosion instanceof CustomExplosion) {
            CustomExplosion customBoom = (CustomExplosion) explosion;
            customBoom.setExplosionSize((float) (customBoom.getExplosionSize() * (1 + demolition.getExtraPower())));
          } else {
            Obfuscation.EXPLOSION_SIZE.hackValueTo(explosion, 4.0 * (1 + demolition.getExtraPower()));
          }
          // I may need to send an exp packet here, not 100%.
          demolition.addXp((EntityPlayerMP) this.shootingEntity, 7000);
        }
        explosion.doExplosionA();
        explosion.doExplosionB(true);
      }
    }
    this.world.removeEntity(this);

  }

  public ResourceLocation getResourceLocation() {
    return new ResourceLocation("skrim:textures/entities/rocket.png");
  }

  @Override
  public void writeSpawnData(ByteBuf buf) {
    ByteBufUtils.writeUTF8String(buf, this.explosionType);
  }

  @Override
  public void readSpawnData(ByteBuf additionalData) {
    this.explosionType = ByteBufUtils.readUTF8String(additionalData);
  }

}
