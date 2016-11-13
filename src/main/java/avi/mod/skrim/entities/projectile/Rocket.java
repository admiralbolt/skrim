package avi.mod.skrim.entities.projectile;

import avi.mod.skrim.blocks.tnt.CustomTNTPrimed;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
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
    Entity entity = raytraceResultIn.entityHit;
    BlockPos pos = (entity == null) ? raytraceResultIn.getBlockPos() : entity.getPosition();
    if (pos != null) {
    	if (this.explosionType != null) {
	    	if (this.explosionType.equals("normal_tnt")) {
	    		this.worldObj.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 4.0F, false);
	    	} else {
	    		Explosion explosion = CustomTNTPrimed.createExplosion(this.explosionType, this.worldObj, this.shootingEntity, pos.getX(), pos.getY(), pos.getZ());
	    		explosion.doExplosionA();
					explosion.doExplosionB(true);
	    	}
    	}
    	this.worldObj.removeEntity(this);
    }
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
		String explosionType = ByteBufUtils.readUTF8String(additionalData);
		this.explosionType = explosionType;
	}

}
