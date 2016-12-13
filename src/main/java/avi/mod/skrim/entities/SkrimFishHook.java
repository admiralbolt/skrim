package avi.mod.skrim.entities;

import avi.mod.skrim.skills.RandomTreasure;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.fishing.SkillFishing;
import avi.mod.skrim.utils.Obfuscation;
import avi.mod.skrim.utils.Reflection;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.ICommandManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.IThrowableEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SkrimFishHook extends EntityFishHook implements IThrowableEntity {

	private boolean hasAppliedCaught = false;
	private boolean hasAppliedCatchable = false;
	private EntityPlayer angler;
	
	/*
	 * Potentially try to overwrite the entity on spawn world event, would prevent the need for having a custom fish hook in the first place
	 */

	@SideOnly(Side.CLIENT)
  	public SkrimFishHook(World worldIn, EntityPlayer fishingPlayer, double x, double y, double z) {
  		super(worldIn, fishingPlayer, x, y, z);
  		this.angler = fishingPlayer;
  	}

  	public SkrimFishHook(World worldIn, EntityPlayer fishingPlayer) {
  		super(worldIn, fishingPlayer);
  		this.angler = fishingPlayer;
  	}

  	@Override
  	public void onUpdate() {
  		super.onUpdate();
  		super.onUpdate();
  		if (!this.hasAppliedCaught) {
  			EntityPlayer player = this.func_190619_l();
  			if (player != null && player.hasCapability(Skills.FISHING, EnumFacing.NORTH)) {
  				SkillFishing fishing = (SkillFishing) player.getCapability(Skills.FISHING, EnumFacing.NORTH);
  				int ticksCaught = (int) Reflection.getSuperPrivateField(this, Obfuscation.FISH_HOOK_CAUGHT_DELAY.getFieldNames());
  				if (ticksCaught > 0) {
  					System.out.println("ticksCaughtSuper: " + Reflection.getSuperPrivateField(this, Obfuscation.FISH_HOOK_CAUGHT_DELAY.getFieldNames()));
  					Reflection.hackSuperValueTo(this, (int) (ticksCaught - ticksCaught * fishing.getDelayReduction()), Obfuscation.FISH_HOOK_CAUGHT_DELAY.getFieldNames());
  					this.hasAppliedCaught = true;
  					System.out.println("ticksCaughtSuper: " + Reflection.getSuperPrivateField(this, Obfuscation.FISH_HOOK_CAUGHT_DELAY.getFieldNames()));
  				}
  			}
  		}
  		if (!this.hasAppliedCatchable) {
  			int ticksCatchable = (int) Reflection.getSuperPrivateField(this, Obfuscation.FISH_HOOK_CATCHABLE.getFieldNames());
  			if (ticksCatchable > 0) {
  				System.out.println("ticksCatchable: " + Reflection.getSuperPrivateField(this, Obfuscation.FISH_HOOK_CATCHABLE.getFieldNames()));
  				Reflection.hackSuperValueTo(this, 50, Obfuscation.FISH_HOOK_CATCHABLE.getFieldNames());
  				this.hasAppliedCatchable = true;
  				System.out.println("ticksCatchable: " + Reflection.getSuperPrivateField(this, Obfuscation.FISH_HOOK_CATCHABLE.getFieldNames()));
  			}
  		}
  	}

	@Override
	public int handleHookRetraction() {
		int ticksCatchable = (int) Reflection.getSuperPrivateField(this, Obfuscation.FISH_HOOK_CATCHABLE.getFieldNames());
		EntityPlayer angler = this.func_190619_l();
		boolean inGround = (boolean) Reflection.getSuperPrivateField(this, Obfuscation.FISH_HOOK_IN_GROUND.getFieldNames());

		if (!this.worldObj.isRemote && angler != null) {
			int i = 0;

			if (this.caughtEntity != null) {
				this.bringInHookedEntity();
				this.worldObj.setEntityState(this, (byte) 31);
				i = this.caughtEntity instanceof EntityItem ? 3 : 5;
			} else if (ticksCatchable > 0) {
				LootContext.Builder lootcontext$builder = new LootContext.Builder((WorldServer) this.worldObj);
				lootcontext$builder.withLuck((float) EnchantmentHelper.getLuckOfSeaModifier(angler) + angler.getLuck());

				for (ItemStack itemstack : this.worldObj.getLootTableManager().getLootTableFromLocation(LootTableList.GAMEPLAY_FISHING)
						.generateLootForPools(this.rand, lootcontext$builder.build())) {
					EntityItem entityitem = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, itemstack);
					double d0 = angler.posX - this.posX;
					double d1 = angler.posY - this.posY;
					double d2 = angler.posZ - this.posZ;
					double d3 = (double) MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
					double d4 = 0.1D;
					entityitem.motionX = d0 * 0.1D;
					entityitem.motionY = d1 * 0.1D + (double) MathHelper.sqrt_double(d3) * 0.08D;
					entityitem.motionZ = d2 * 0.1D;
					this.worldObj.spawnEntityInWorld(entityitem);
					angler.worldObj.spawnEntityInWorld(
							new EntityXPOrb(angler.worldObj, angler.posX, angler.posY + 0.5D, angler.posZ + 0.5D, this.rand.nextInt(6) + 1));
					if (angler.hasCapability(Skills.FISHING, EnumFacing.NORTH)) {
						SkillFishing fishing = (SkillFishing) angler.getCapability(Skills.FISHING, EnumFacing.NORTH);
						fishing.addXp((EntityPlayerMP) angler, 1000);
						// Roll for treasure chance
						if (this.rand.nextDouble() < fishing.getTreasureChance()) {
							EntityItem treasure = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, RandomTreasure.generateStandardTreasure());
							treasure.motionX = d0 * 0.1D;
							treasure.motionY = d1 * 0.1D + (double) MathHelper.sqrt_double(d3) * 0.08D;
							treasure.motionZ = d2 * 0.1D;
							this.worldObj.spawnEntityInWorld(treasure);
							Skills.playFortuneSound(angler);
							fishing.addXp((EntityPlayerMP) angler, 200);
						}

						// Apply abilities
						if (fishing.hasAbility(2)) {
							for (int q = 0; q < 2; q++) {
								EntityItem copy = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, itemstack);
								copy.motionX = d0 * 0.1D;
								copy.motionY = d1 * 0.1D + (double) MathHelper.sqrt_double(d3) * 0.08D;
								copy.motionZ = d2 * 0.1D;
								this.worldObj.spawnEntityInWorld(copy);
							}
							if (fishing.hasAbility(3)) {
								angler.worldObj.spawnEntityInWorld(
										new EntityXPOrb(angler.worldObj, angler.posX, angler.posY + 0.5D, angler.posZ + 0.5D, this.rand.nextInt(16) + 9));
							}
						}

					}
				}

				i = 1;
			}

			if (inGround) {
				i = 2;
				if (angler.hasCapability(Skills.FISHING, EnumFacing.NORTH)) {
					SkillFishing fishing = (SkillFishing) angler.getCapability(Skills.FISHING, EnumFacing.NORTH);
					if (fishing.hasAbility(1)) {
						MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
						ICommandManager cm = server.getCommandManager();
						BlockPos pos = this.getPosition();
						cm.executeCommand(server, "/tp " + angler.getName() + " " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
					}
				}
			}

			this.setDead();
			return i;
		} else {
			return 0;
		}
	}

	@Override
	protected void bringInHookedEntity() {
		EntityPlayer angler = this.func_190619_l();
		if (angler != null) {
			double d0 = angler.posX - this.posX;
			double d1 = angler.posY - this.posY;
			double d2 = angler.posZ - this.posZ;
			double d3 = 0.1D;
			int addY = 0;
			if (angler.hasCapability(Skills.FISHING, EnumFacing.NORTH)) {
				SkillFishing fishing = (SkillFishing) angler.getCapability(Skills.FISHING, EnumFacing.NORTH);
				if (fishing.hasAbility(4)) {
					addY = 5;
				}
			}
			this.caughtEntity.motionX += d0 * 0.1D;
			this.caughtEntity.motionY += d1 * 0.1D + addY;
			this.caughtEntity.motionZ += d2 * 0.1D;
		}
	}

	@Override
	public Entity getThrower() {
		return this.angler;
	}

	@Override
	public void setThrower(Entity entity) {
		this.angler = (EntityPlayer) entity;
	}

}