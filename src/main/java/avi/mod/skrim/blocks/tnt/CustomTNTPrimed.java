package avi.mod.skrim.blocks.tnt;

import avi.mod.skrim.blocks.SkrimBlocks;
import avi.mod.skrim.network.ExplosionPacket;
import avi.mod.skrim.network.SkrimPacketHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTNT;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * This is pretty much a direct clone of EntityTNTPrimed.java used for custom explosions.
 * <p>
 * It's a direct clone primarily because the explode() method needs
 */
public class CustomTNTPrimed extends Entity {

  private static final DataParameter<Integer> FUSE = EntityDataManager.createKey(CustomTNTPrimed.class, DataSerializers.VARINT);
  private static final DataParameter<String> EXPLOSION_TYPE = EntityDataManager.createKey(CustomTNTPrimed.class, DataSerializers.STRING);

  private EntityLivingBase tntPlacedBy;
  private int fuse;
  private String explosionType;
  private Float explosionSize = null;

  public CustomTNTPrimed(World worldIn) {
    super(worldIn);
    this.fuse = 80;
    this.preventEntitySpawning = true;
    this.isImmuneToFire = true;
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

  public CustomTNTPrimed(String explosionType, Float explosionSize, World worldIn, double x, double y, double z, EntityLivingBase igniter) {
    this(explosionType, worldIn, x, y, z, igniter);
    this.explosionSize = explosionSize;
  }

  protected void entityInit() {
    this.dataManager.register(FUSE, 80);
    this.dataManager.register(EXPLOSION_TYPE, this.explosionType);
  }

  /**
   * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to prevent them from
   * trampling crops
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

    if (!this.hasNoGravity()) {
      this.motionY -= 0.03999999910593033D;
    }

    this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
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

      if (!this.world.isRemote) {
        this.explode();
      }
    } else {
      this.handleWaterMovement();
      this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D, new int[0]);
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
  @Nullable
  public EntityLivingBase getTntPlacedBy() {
    return this.tntPlacedBy;
  }

  public float getEyeHeight() {
    return 0.0F;
  }

  public void setFuse(int fuseIn) {
    this.dataManager.set(FUSE, fuseIn);
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

  public int getFuseDataManager() {
    return this.dataManager.get(FUSE);
  }

  public String getExplosionDataManager() {
    return this.dataManager.get(EXPLOSION_TYPE);
  }

  public int getFuse() {
    return this.fuse;
  }

  public String getExplosionType() {
    return this.explosionType;
  }

  public void explode() {
    CustomExplosion explosion = createExplosion(this.explosionType, this.explosionSize, this.world, this, this.posX,
				this.posY + (double) (this.height / 16.0F), this.posZ);
    for (EntityPlayer entityplayer : this.world.playerEntities) {
      if (entityplayer.getDistanceSq(this.posX, this.posY, this.posZ) < 4096.0D) {
        SkrimPacketHandler.INSTANCE.sendTo(new ExplosionPacket(this.getExplosionType(), explosion.getExplosionSize(), this.getEntityId(),
						this.posX, this.posY + (double) (this.height / 16.0F), this.posZ), (EntityPlayerMP) entityplayer);
      }
    }
    if (net.minecraftforge.event.ForgeEventFactory.onExplosionStart(this.world, explosion)) return;
    explosion.doExplosionA();
    explosion.doExplosionB(true);
  }

  public static CustomExplosion createExplosion(String explosionType, Float explosionSize, World worldIn, Entity entityIn, double x,
																								double y, double z) {
    if (explosionType.equals("dynamite")) {
      return new DynamiteExplosion(worldIn, entityIn, x, y, z, explosionSize);
    } else if (explosionType.equals("biobomb")) {
      return new BioBombExplosion(worldIn, entityIn, x, y, z, explosionSize);
    } else if (explosionType.equals("napalm")) {
      return new NapalmExplosion(worldIn, entityIn, x, y, z, explosionSize);
    } else if (explosionType.equals("fat_boy")) {
      return new FatBoyExplosion(worldIn, entityIn, x, y, z, explosionSize);
    } else {
      return new CustomExplosion(worldIn, entityIn, x, y, z, explosionSize, false, true);
    }
  }

  public static String getExplosionType(ItemStack stack) {
    Item targetItem = stack.getItem();
    Item dynamite = new ItemStack(SkrimBlocks.DYNAMITE).getItem();
    Item biobomb = new ItemStack(SkrimBlocks.BIOBOMB).getItem();
    Item napalm = new ItemStack(SkrimBlocks.NAPALM).getItem();
    Item fatBoy = new ItemStack(SkrimBlocks.FAT_BOY).getItem();
    if (targetItem == dynamite) {
      return "dynamite";
    } else if (targetItem == biobomb) {
      return "biobomb";
    } else if (targetItem == napalm) {
      return "napalm";
    } else if (targetItem == fatBoy) {
      return "fat_boy";
    } else {
      return "normal_tnt";
    }
  }

  public static Block getBlock(CustomTNTPrimed entity) {
    String explosionType = entity.getExplosionType();
    if (explosionType.equals("dynamite")) {
      return SkrimBlocks.DYNAMITE;
    } else if (explosionType.equals("biobomb")) {
      return SkrimBlocks.BIOBOMB;
    } else if (explosionType.equals("napalm")) {
      return SkrimBlocks.NAPALM;
    } else if (explosionType.equals("fat_boy")) {
      return SkrimBlocks.FAT_BOY;
    } else {
      return new BlockTNT();
    }
  }

}
