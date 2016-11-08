package avi.mod.skrim.blocks.tnt;

import avi.mod.skrim.blocks.ModBlocks;
import avi.mod.skrim.network.ExplosionPacket;
import avi.mod.skrim.network.SkrimPacketHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class CustomTNTPrimed extends Entity {
	private static final DataParameter<Integer> FUSE = EntityDataManager.<Integer>createKey(CustomTNTPrimed.class, DataSerializers.VARINT);
	private static final DataParameter<String> EXPLOSION_TYPE = EntityDataManager.<String>createKey(CustomTNTPrimed.class, DataSerializers.STRING);
	private EntityLivingBase tntPlacedBy;
	/** How long the fuse is */
	private int fuse;
	private String explosionType;

	public CustomTNTPrimed(World worldIn) {
		super(worldIn);
		this.fuse = 80;
		this.preventEntitySpawning = true;
		this.setSize(0.98F, 0.98F);
	}

	public CustomTNTPrimed(String explosionType, World worldIn, double x, double y, double z, EntityLivingBase igniter) {
		this(worldIn);
		this.explosionType = explosionType;
		this.setPosition(x, y, z);
		float f = (float) (Math.random() * (Math.PI * 2D));
		this.motionX = (double) (-((float) Math.sin((double) f)) * 0.02F);
		this.motionY = 0.20000000298023224D;
		this.motionZ = (double) (-((float) Math.cos((double) f)) * 0.02F);
		this.setFuse(80);
		this.setExplosionType(explosionType);
		this.prevPosX = x;
		this.prevPosY = y;
		this.prevPosZ = z;
		this.tntPlacedBy = igniter;
	}

	protected void entityInit() {
		this.dataManager.register(FUSE, Integer.valueOf(80));
		this.dataManager.register(EXPLOSION_TYPE, this.explosionType);
	}

	/**
	 * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to prevent them from trampling crops
	 */
	protected boolean canTriggerWalking() {
		return false;
	}

	/**
	 * Returns true if other Entities should be prevented from moving through this Entity.
	 */
	public boolean canBeCollidedWith() {
		return !this.isDead;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		if (!this.func_189652_ae()) {
			this.motionY -= 0.03999999910593033D;
		}

		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		this.motionX *= 0.9800000190734863D;
		this.motionY *= 0.9800000190734863D;
		this.motionZ *= 0.9800000190734863D;

		if (this.onGround) {
			this.motionX *= 0.699999988079071D;
			this.motionZ *= 0.699999988079071D;
			this.motionY *= -0.5D;
		}

		--this.fuse;

		if (this.fuse <= 0) {
			this.setDead();

			if (!this.worldObj.isRemote) {
				this.explode();
			}
		} else {
			this.handleWaterMovement();
			this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D, new int[0]);
		}
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	protected void writeEntityToNBT(NBTTagCompound compound) {
		compound.setShort("Fuse", (short) this.getFuse());
		compound.setString("ExplosionType", this.getExplosionType());
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	protected void readEntityFromNBT(NBTTagCompound compound) {
		this.setFuse(compound.getShort("Fuse"));
		this.setExplosionType(compound.getString("ExplosionType"));
	}

	/**
	 * returns null or the entityliving it was placed or ignited by
	 */
	public EntityLivingBase getTntPlacedBy() {
		return this.tntPlacedBy;
	}

	public float getEyeHeight() {
		return 0.0F;
	}

	public void setFuse(int fuseIn) {
		this.dataManager.set(FUSE, Integer.valueOf(fuseIn));
		this.fuse = fuseIn;
	}

	public void setExplosionType(String explosionType) {
		this.dataManager.set(EXPLOSION_TYPE, explosionType);
	}

	public void notifyDataManagerChange(DataParameter<?> key) {
		if (FUSE.equals(key)) {
			this.fuse = this.getFuseDataManager();
		} else if (EXPLOSION_TYPE.equals(key)) {
			this.explosionType = this.getExplosionDataManager();
		}
	}

	/**
	 * Gets the fuse from the data manager
	 */
	public int getFuseDataManager() {
		return ((Integer) this.dataManager.get(FUSE)).intValue();
	}

	public String getExplosionDataManager() {
		return this.dataManager.get(EXPLOSION_TYPE).toString();
	}

	public int getFuse() {
		return this.fuse;
	}

	public String getExplosionType() {
		return this.explosionType;
	}

	public Explosion explode() {
		for (EntityPlayer entityplayer : this.worldObj.playerEntities) {
			if (entityplayer.getDistanceSq(this.posX, this.posY, this.posZ) < 4096.0D) {
				SkrimPacketHandler.INSTANCE.sendTo(new ExplosionPacket(this.getExplosionType(), this.getEntityId(), this.posX, this.posY + (double) (this.height / 16.0F), this.posZ), (EntityPlayerMP) entityplayer);
			}
		}
		Explosion explosion = createExplosion(this.explosionType, this.worldObj, this, this.posX, this.posY + (double) (this.height / 16.0F), this.posZ);
		if (net.minecraftforge.event.ForgeEventFactory.onExplosionStart(this.worldObj, explosion)) {
			return explosion;
		}
		explosion.doExplosionA();
		explosion.doExplosionB(true);
		return explosion;
	}

	public static Explosion createExplosion(String explosionType, World worldIn, Entity entityIn, double x, double y, double z) {
		if (explosionType.equals("dynamite")) {
			return new DynamiteExplosion(worldIn, entityIn, x, y, z);
		} else if (explosionType.equals("biobomb")) {
			return new BioBombExplosion(worldIn, entityIn, x, y, z);
		} else if (explosionType.equals("napalm")) {
			return new NapalmExplosion(worldIn, entityIn, x, y, z);
		}
		return null;
	}

	public static Block getBlock(CustomTNTPrimed entity) {
		String explosionType = entity.getExplosionType();
		if (explosionType.equals("dynamite")) {
			return ModBlocks.DYNAMITE;
		} else if (explosionType.equals("biobomb")) {
			return ModBlocks.BIOBOMB;
		} else if (explosionType.equals("napalm")) {
			return ModBlocks.NAPALM;
		}
		return null;
	}

}
