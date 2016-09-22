package avi.mod.skrim.items;

import io.netty.buffer.ByteBuf;

import java.util.List;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import avi.mod.skrim.skills.RandomTreasure;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.fishing.SkillFishing;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.ICommandManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CustomFishHook extends EntityFishHook implements IEntityAdditionalSpawnData {

	private static final DataParameter<Integer> DATA_HOOKED_ENTITY = EntityDataManager.<Integer>createKey(EntityFishHook.class, DataSerializers.VARINT);
	private BlockPos field_189740_d;
	public int shake;
	public EntityPlayer angler;
	public Entity bobber;
	private int xTile;
	private int yTile;
	private int zTile;
	private Block inTile;
	private boolean inGround;
	private int ticksInGround;
	private int ticksInAir;
	private int baseCatchTime = 300;
	private int ticksCatchable;
	private int fishPosRotationIncrements;
	private double fishX;
	private double fishY;
	private double fishZ;
	private double fishYaw;
	private double fishPitch;
	@SideOnly(Side.CLIENT)
	private double velocityX;
	@SideOnly(Side.CLIENT)
	private double velocityY;
	@SideOnly(Side.CLIENT)
	private double velocityZ;

	private boolean canTeleport;

	private boolean isAdmin = false;
	private int ticksCaughtDelay;
	private int ticksCatchableDelay;
	private float fishApproachAngle;

	public CustomFishHook(World par1World) {
		super(par1World);
		this.xTile = -1;
		this.yTile = -1;
		this.zTile = -1;
		this.inGround = false;
		this.shake = 0;
		this.ticksInAir = 0;
		this.ticksCatchable = 0;
		this.bobber = null;
		this.field_189740_d = new BlockPos(-1, -1, -1);
		this.setSize(0.25F, 0.25F);
		this.ignoreFrustumCheck = true;
		this.canTeleport = false;
	}

	@SideOnly(Side.CLIENT)
	public CustomFishHook(World par1World, double par2, double par4, double par6, EntityPlayer par8EntityPlayer) {
		super(par1World, par2, par4, par6, par8EntityPlayer);
		this.setPosition(par2, par4, par6);
		this.ignoreFrustumCheck = true;
		this.angler = par8EntityPlayer;
		par8EntityPlayer.fishEntity = this;
		this.xTile = -1;
		this.yTile = -1;
		this.zTile = -1;
		this.inGround = false;
		this.shake = 0;
		this.ticksInAir = 0;
		this.ticksCatchable = 0;
		this.bobber = null;
		this.setSize(0.25F, 0.25F);
		this.ignoreFrustumCheck = true;
		this.field_189740_d = new BlockPos(par2, par4, par6);
		this.canTeleport = false;
	}

	public CustomFishHook(World par1World, EntityPlayer par2EntityPlayer) {
		super(par1World, par2EntityPlayer);
		this.xTile = -1;
		this.yTile = -1;
		this.zTile = -1;
		this.inGround = false;
		this.shake = 0;
		this.ticksInAir = 0;
		this.ticksCatchable = 0;
		this.bobber = null;
		this.ignoreFrustumCheck = true;
		this.angler = par2EntityPlayer;
		this.angler.fishEntity = this;
		this.setSize(0.25F, 0.25F);
		this.setLocationAndAngles(par2EntityPlayer.posX, par2EntityPlayer.posY + par2EntityPlayer.getEyeHeight(), par2EntityPlayer.posZ, par2EntityPlayer.rotationYaw, par2EntityPlayer.rotationPitch);
		this.posX -= MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
		this.posY -= 0.10000000149011612D;
		this.posZ -= MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
		this.setPosition(this.posX, this.posY, this.posZ);
		this.field_189740_d = new BlockPos(this.posX, this.posY, this.posZ);
		float f = 0.4F;
		this.motionX = -MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * f;
		this.motionZ = MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * f;
		this.motionY = -MathHelper.sin(this.rotationPitch / 180.0F * (float) Math.PI) * f;

		float velocity = 1.7F;

		this.calculateVelocity(this.motionX, this.motionY, this.motionZ, velocity, 1.0F);
		this.canTeleport = false;
	}

	public void setBaseCatchTime(int amount) {
		this.baseCatchTime = amount;
	}

	@Override
	protected void entityInit() {
		this.getDataManager().register(DATA_HOOKED_ENTITY, Integer.valueOf(0));
	}

	public void calculateVelocity(double par1, double par3, double par5, float par7, float par8) {
		float f2 = MathHelper.sqrt_double(par1 * par1 + par3 * par3 + par5 * par5);
		par1 /= f2;
		par3 /= f2;
		par5 /= f2;
		par1 += this.rand.nextGaussian() * 0.007499999832361937D * par8;
		par3 += this.rand.nextGaussian() * 0.007499999832361937D * par8;
		par5 += this.rand.nextGaussian() * 0.007499999832361937D * par8;
		par1 *= par7;
		par3 *= par7;
		par5 *= par7;
		this.motionX = par1;
		this.motionY = par3;
		this.motionZ = par5;
		float f3 = MathHelper.sqrt_double(par1 * par1 + par5 * par5);
		this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(par1, par5) * 180.0D / Math.PI);
		this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(par3, f3) * 180.0D / Math.PI);
		this.ticksInGround = 0;
	}

	public boolean isFishingRod(ItemStack stack) {
		Item item = stack.getItem();
		return (item instanceof CustomFishingRod);
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void onUpdate() {

		if (this.worldObj.isRemote) {
			int i = ((Integer) this.getDataManager().get(DATA_HOOKED_ENTITY)).intValue();
			if (i > 0 && this.caughtEntity == null) {
				this.caughtEntity = this.worldObj.getEntityByID(i - 1);
				this.inGround = false;
			}
		} else {
			if (this.angler != null) {
				ItemStack itemstack = this.angler.getHeldItemMainhand();

				if (this.angler.isDead || !this.angler.isEntityAlive() || itemstack == null || itemstack.getItem() != ModItems.fishingRod || this.getDistanceSqToEntity(this.angler) > 1024.0D) {
					this.setDead();
					this.angler.fishEntity = null;
					return;
				}
			} else {
				this.setDead();
				return;
			}
		}

		if (this.caughtEntity != null) {
			if (!this.caughtEntity.isDead) {
				this.posX = this.caughtEntity.posX;
				double d17 = (double) this.caughtEntity.height;
				this.posY = this.caughtEntity.getEntityBoundingBox().minY + d17 * 0.8D;
				this.posZ = this.caughtEntity.posZ;
				return;
			}

			this.caughtEntity = null;
		}

		if (this.fishPosRotationIncrements > 0) {
			double d3 = this.posX + (this.fishX - this.posX) / (double) this.fishPosRotationIncrements;
			double d4 = this.posY + (this.fishY - this.posY) / (double) this.fishPosRotationIncrements;
			double d6 = this.posZ + (this.fishZ - this.posZ) / (double) this.fishPosRotationIncrements;
			double d8 = MathHelper.wrapDegrees(this.fishYaw - (double) this.rotationYaw);
			this.rotationYaw = (float) ((double) this.rotationYaw + d8 / (double) this.fishPosRotationIncrements);
			this.rotationPitch = (float) ((double) this.rotationPitch + (this.fishPitch - (double) this.rotationPitch) / (double) this.fishPosRotationIncrements);
			--this.fishPosRotationIncrements;
			this.setPosition(d3, d4, d6);
			this.setRotation(this.rotationYaw, this.rotationPitch);
		} else {
			if (this.inGround) {
				if (!this.inWater) {
					this.canTeleport = true;
				}
				if (this.worldObj.getBlockState(this.field_189740_d).getBlock() == this.inTile) {
					this.motionX = 0;
					this.motionY = 0;
					this.motionZ = 0;
					++this.ticksInGround;

          if (this.ticksInGround == 1200) {
          	this.setDead();
          }

          return;
        }

        this.inGround = false;
        this.motionX *= (double)(this.rand.nextFloat() * 0.2F);
        this.motionY *= (double)(this.rand.nextFloat() * 0.2F);
        this.motionZ *= (double)(this.rand.nextFloat() * 0.2F);
        this.ticksInGround = 0;
        this.ticksInAir = 0;
    } else {
			this.ticksInAir++;
		}

			if (!this.worldObj.isRemote) {
				Vec3d vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
				Vec3d vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
				RayTraceResult raytraceresult = this.worldObj.rayTraceBlocks(vec3d1, vec3d);
				vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
				vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

				if (raytraceresult != null) {
					vec3d = new Vec3d(raytraceresult.hitVec.xCoord, raytraceresult.hitVec.yCoord, raytraceresult.hitVec.zCoord);
				}

				Entity entity = null;
				List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expandXyz(1.0D));
				double d0 = 0.0D;

				for (int j = 0; j < list.size(); ++j) {
					Entity entity1 = (Entity) list.get(j);

					if (this.func_189739_a(entity1) && (entity1 != this.angler || this.ticksInAir >= 5)) {
						AxisAlignedBB axisalignedbb1 = entity1.getEntityBoundingBox().expandXyz(0.30000001192092896D);
						RayTraceResult raytraceresult1 = axisalignedbb1.calculateIntercept(vec3d1, vec3d);

						if (raytraceresult1 != null) {
							double d1 = vec3d1.squareDistanceTo(raytraceresult1.hitVec);

							if (d1 < d0 || d0 == 0.0D) {
								entity = entity1;
								d0 = d1;
							}
						}
					}
				}

				if (entity != null) {
					raytraceresult = new RayTraceResult(entity);
				}

				if (raytraceresult != null) {
					if (raytraceresult.entityHit != null) {
						this.caughtEntity = raytraceresult.entityHit;
						this.getDataManager().set(DATA_HOOKED_ENTITY, Integer.valueOf(this.caughtEntity.getEntityId() + 1));
					} else {
						this.inGround = true;
					}
				}
			}

			if (!this.inGround) {
				this.moveEntity(this.motionX, this.motionY, this.motionZ);
				float f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
				this.rotationYaw = (float) (MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));

				for (this.rotationPitch = (float) (MathHelper.atan2(this.motionY, (double) f2) * (180D / Math.PI)); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
					;
				}

				while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
					this.prevRotationPitch += 360.0F;
				}

				while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
					this.prevRotationYaw -= 360.0F;
				}

				while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
					this.prevRotationYaw += 360.0F;
				}

				this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
				this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
				float f3 = 0.92F;

				if (this.onGround || this.isCollidedHorizontally) {
					f3 = 0.5F;
				}

				int k = 5;
				double d5 = 0.0D;

				for (int l = 0; l < 5; ++l) {
					AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
					double d9 = axisalignedbb.maxY - axisalignedbb.minY;
					double d10 = axisalignedbb.minY + d9 * (double) l / 5.0D;
					double d11 = axisalignedbb.minY + d9 * (double) (l + 1) / 5.0D;
					AxisAlignedBB axisalignedbb2 = new AxisAlignedBB(axisalignedbb.minX, d10, axisalignedbb.minZ, axisalignedbb.maxX, d11, axisalignedbb.maxZ);

					if (this.worldObj.isAABBInMaterial(axisalignedbb2, Material.WATER)) {
						d5 += 0.2D;
					}
				}

				if (!this.worldObj.isRemote && d5 > 0.0D) {
					WorldServer worldserver = (WorldServer) this.worldObj;
					int i1 = 1;
					BlockPos blockpos = (new BlockPos(this)).up();

					if (this.rand.nextFloat() < 0.25F && this.worldObj.isRainingAt(blockpos)) {
						i1 = 2;
					}

					if (this.rand.nextFloat() < 0.5F && !this.worldObj.canSeeSky(blockpos)) {
						--i1;
					}

					if (this.ticksCatchable > 0) {
						--this.ticksCatchable;

						if (this.ticksCatchable <= 0) {
							this.ticksCaughtDelay = 0;
							this.ticksCatchableDelay = 0;
						}
					} else if (this.ticksCatchableDelay > 0) {
						this.ticksCatchableDelay -= i1;

						if (this.ticksCatchableDelay <= 0) {
							this.motionY -= 0.20000000298023224D;
							this.playSound(SoundEvents.ENTITY_BOBBER_SPLASH, 0.25F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
							float f6 = (float) MathHelper.floor_double(this.getEntityBoundingBox().minY);
							worldserver.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX, (double) (f6 + 1.0F), this.posZ, (int) (1.0F + this.width * 20.0F), (double) this.width, 0.0D, (double) this.width, 0.20000000298023224D, new int[0]);
							worldserver.spawnParticle(EnumParticleTypes.WATER_WAKE, this.posX, (double) (f6 + 1.0F), this.posZ, (int) (1.0F + this.width * 20.0F), (double) this.width, 0.0D, (double) this.width, 0.20000000298023224D, new int[0]);
							this.ticksCatchable = MathHelper.getRandomIntegerInRange(this.rand, 10, 30);
						} else {
							this.fishApproachAngle = (float) ((double) this.fishApproachAngle + this.rand.nextGaussian() * 4.0D);
							float f5 = this.fishApproachAngle * 0.017453292F;
							float f8 = MathHelper.sin(f5);
							float f10 = MathHelper.cos(f5);
							double d13 = this.posX + (double) (f8 * (float) this.ticksCatchableDelay * 0.1F);
							double d15 = (double) ((float) MathHelper.floor_double(this.getEntityBoundingBox().minY) + 1.0F);
							double d16 = this.posZ + (double) (f10 * (float) this.ticksCatchableDelay * 0.1F);
							Block block1 = worldserver.getBlockState(new BlockPos((int) d13, (int) d15 - 1, (int) d16)).getBlock();

							if (block1 == Blocks.WATER || block1 == Blocks.FLOWING_WATER) {
								if (this.rand.nextFloat() < 0.15F) {
									worldserver.spawnParticle(EnumParticleTypes.WATER_BUBBLE, d13, d15 - 0.10000000149011612D, d16, 1, (double) f8, 0.1D, (double) f10, 0.0D, new int[0]);
								}

								float f = f8 * 0.04F;
								float f1 = f10 * 0.04F;
								worldserver.spawnParticle(EnumParticleTypes.WATER_WAKE, d13, d15, d16, 0, (double) f1, 0.01D, (double) (-f), 1.0D, new int[0]);
								worldserver.spawnParticle(EnumParticleTypes.WATER_WAKE, d13, d15, d16, 0, (double) (-f1), 0.01D, (double) f, 1.0D, new int[0]);
							}
						}
					} else if (this.ticksCaughtDelay > 0) {
						this.ticksCaughtDelay -= i1;
						float f4 = 0.15F;

						if (this.ticksCaughtDelay < 20) {
							f4 = (float) ((double) f4 + (double) (20 - this.ticksCaughtDelay) * 0.05D);
						} else if (this.ticksCaughtDelay < 40) {
							f4 = (float) ((double) f4 + (double) (40 - this.ticksCaughtDelay) * 0.02D);
						} else if (this.ticksCaughtDelay < 60) {
							f4 = (float) ((double) f4 + (double) (60 - this.ticksCaughtDelay) * 0.01D);
						}

						if (this.rand.nextFloat() < f4) {
							float f7 = MathHelper.randomFloatClamp(this.rand, 0.0F, 360.0F) * 0.017453292F;
							float f9 = MathHelper.randomFloatClamp(this.rand, 25.0F, 60.0F);
							double d12 = this.posX + (double) (MathHelper.sin(f7) * f9 * 0.1F);
							double d14 = (double) ((float) MathHelper.floor_double(this.getEntityBoundingBox().minY) + 1.0F);
							double d2 = this.posZ + (double) (MathHelper.cos(f7) * f9 * 0.1F);
							Block block = worldserver.getBlockState(new BlockPos((int) d12, (int) d14 - 1, (int) d2)).getBlock();

							if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
								worldserver.spawnParticle(EnumParticleTypes.WATER_SPLASH, d12, d14, d2, 2 + this.rand.nextInt(2), 0.10000000149011612D, 0.0D, 0.10000000149011612D, 0.0D, new int[0]);
							}
						}

						if (this.ticksCaughtDelay <= 0) {
							this.fishApproachAngle = MathHelper.randomFloatClamp(this.rand, 0.0F, 360.0F);
							this.ticksCatchableDelay = MathHelper.getRandomIntegerInRange(this.rand, 20, 80);
						}
					} else {
						this.ticksCaughtDelay = MathHelper.getRandomIntegerInRange(this.rand, 100, 900);
						if (this.angler.hasCapability(Skills.FISHING, EnumFacing.NORTH)) {
							SkillFishing fishing = (SkillFishing) this.angler.getCapability(Skills.FISHING, EnumFacing.NORTH);
							this.ticksCaughtDelay -= fishing.getDelayReduction() * this.ticksCaughtDelay;
						}
						this.ticksCaughtDelay -= EnchantmentHelper.getLureModifier(this.angler) * 20 * 5;
						this.ticksCaughtDelay = Math.max(0, this.ticksCaughtDelay);
					}

					if (this.ticksCatchable > 0) {
						this.motionY -= (double) (this.rand.nextFloat() * this.rand.nextFloat() * this.rand.nextFloat()) * 0.2D;
					}
				}

				double d7 = d5 * 2.0D - 1.0D;
				this.motionY += 0.03999999910593033D * d7;

				if (d5 > 0.0D) {
					f3 = (float) ((double) f3 * 0.9D);
					this.motionY *= 0.8D;
				}

				this.motionX *= (double) f3;
				this.motionY *= (double) f3;
				this.motionZ *= (double) f3;
				this.setPosition(this.posX, this.posY, this.posZ);
			}
		}

	}

	@Override
	protected void bringInHookedEntity() {
		double d0 = this.angler.posX - this.posX;
		double d1 = this.angler.posY - this.posY;
		double d2 = this.angler.posZ - this.posZ;
		double d3 = (double) MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
		double d4 = 0.1D;
		this.caughtEntity.motionX += d0 * 0.1D;
		double addY = d1 * 0.1D + (double) MathHelper.sqrt_double(d3) * 0.08D;
		if (this.angler.hasCapability(Skills.FISHING, EnumFacing.NORTH)) {
			SkillFishing fishing = (SkillFishing) this.angler.getCapability(Skills.FISHING, EnumFacing.NORTH);
			if (fishing.hasAbility(4)) {
				addY = 5;
			}
		}
		this.caughtEntity.motionY += addY;
		this.caughtEntity.motionZ += d2 * 0.1D;
	}

	@Override
	public int handleHookRetraction() {
		if (this.worldObj.isRemote) {
			return 0;
		} else {
			int i = 0;

			if (this.caughtEntity != null) {
				this.bringInHookedEntity();
				this.worldObj.setEntityState(this, (byte) 31);
				i = this.caughtEntity instanceof EntityItem ? 3 : 5;
			} else if (this.ticksCatchable > 0) {
				LootContext.Builder lootcontext$builder = new LootContext.Builder((WorldServer) this.worldObj);
				lootcontext$builder.withLuck((float) EnchantmentHelper.getLuckOfSeaModifier(this.angler) + this.angler.getLuck());

				for (ItemStack itemstack : this.worldObj.getLootTableManager().getLootTableFromLocation(LootTableList.GAMEPLAY_FISHING).generateLootForPools(this.rand, lootcontext$builder.build())) {
					EntityItem entityitem = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, itemstack);
					double d0 = this.angler.posX - this.posX;
					double d1 = this.angler.posY - this.posY;
					double d2 = this.angler.posZ - this.posZ;
					double d3 = (double) MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
					double d4 = 0.1D;
					entityitem.motionX = d0 * 0.1D;
					entityitem.motionY = d1 * 0.1D + (double) MathHelper.sqrt_double(d3) * 0.08D;
					entityitem.motionZ = d2 * 0.1D;
					this.worldObj.spawnEntityInWorld(entityitem);
					this.angler.worldObj.spawnEntityInWorld(new EntityXPOrb(this.angler.worldObj, this.angler.posX, this.angler.posY + 0.5D, this.angler.posZ + 0.5D, this.rand.nextInt(6) + 1));
					if (this.angler.hasCapability(Skills.FISHING, EnumFacing.NORTH)) {
						SkillFishing fishing = (SkillFishing) this.angler.getCapability(Skills.FISHING, EnumFacing.NORTH);
						if (fishing.hasAbility(2)) {
							for (int q = 0; q < 2; q++) {
								EntityItem copy = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, itemstack);
								copy.motionX = d0 * 0.1D;
								copy.motionY = d1 * 0.1D + (double) MathHelper.sqrt_double(d3) * 0.08D;
								copy.motionZ = d2 * 0.1D;
								this.worldObj.spawnEntityInWorld(copy);
							}
						}
					}
				}
				if (this.angler.hasCapability(Skills.FISHING, EnumFacing.NORTH)) {
					SkillFishing fishing = (SkillFishing) this.angler.getCapability(Skills.FISHING, EnumFacing.NORTH);
					fishing.addXp((EntityPlayerMP) this.angler, 50);
					if (this.rand.nextDouble() < fishing.getTreasureChance()) {
						EntityItem treasure = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, RandomTreasure.generate());
						double d0 = this.angler.posX - this.posX;
						double d1 = this.angler.posY - this.posY;
						double d2 = this.angler.posZ - this.posZ;
						double d3 = (double) MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
						treasure.motionX = d0 * 0.1D;
						treasure.motionY = d1 * 0.1D + (double) MathHelper.sqrt_double(d3) * 0.08D;
						treasure.motionZ = d2 * 0.1D;
						this.worldObj.spawnEntityInWorld(treasure);
						fishing.addXp((EntityPlayerMP) this.angler, 25);
					}
					if (fishing.hasAbility(3)) {
						this.angler.worldObj.spawnEntityInWorld(new EntityXPOrb(this.angler.worldObj, this.angler.posX, this.angler.posY + 0.5D, this.angler.posZ + 0.5D, this.rand.nextInt(16) + 9));
					}
				}

				i = 1;
			}

			Block block = this.angler.worldObj.getBlockState(new BlockPos((int) this.posX, (int) MathHelper.floor_double(this.getEntityBoundingBox().minY), (int) this.posZ)).getBlock();

			if (block != Blocks.WATER && block != Blocks.FLOWING_WATER && this.canTeleport) {
				if (this.angler.hasCapability(Skills.FISHING, EnumFacing.NORTH)) {
					SkillFishing fishing = (SkillFishing) this.angler.getCapability(Skills.FISHING, EnumFacing.NORTH);
					if (fishing.hasAbility(1)) {
						MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
						ICommandManager cm = server.getCommandManager();
						BlockPos pos = this.getPosition();
						cm.executeCommand(server, "/tp " + this.angler.getName() + " " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
					}
					i = 0;
				} else {
					i = 2;
				}
			}

			this.setDead();
			this.angler.fishEntity = null;
			return i;
		}
	}

	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeInt(this.angler != null ? this.angler.getEntityId() : 0);
	}

	public void readSpawnData(ByteBuf additionalData) {
		this.angler = (EntityPlayer) this.worldObj.getEntityByID(additionalData.readInt());
	}

}
