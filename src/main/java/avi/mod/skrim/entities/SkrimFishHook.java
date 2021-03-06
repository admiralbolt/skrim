package avi.mod.skrim.entities;

import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.fishing.SkillFishing;
import avi.mod.skrim.utils.Obfuscation;
import avi.mod.skrim.utils.ReflectionUtils;
import avi.mod.skrim.utils.Utils;
import avi.mod.skrim.world.loot.CustomLootTables;
import net.minecraft.command.ICommandManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.IThrowableEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Most of the fishing logic is in the fish hook itself. This is a way of overriding the base game fish hook to correctly apply the
 * fishing skill.
 */
public class SkrimFishHook extends EntityFishHook implements IThrowableEntity {

  private boolean hasAppliedCaught = false;
  private boolean hasAppliedCatchable = false;
  private EntityPlayer angler;

  /*
   * Potentially try to overwrite the entity on spawn world event, would
   * prevent the need for having a custom fish hook in the first place
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

  public SkrimFishHook(World worldIn) {
    super(worldIn, null);
  }

  public static void overrideDefaultHook(EntityJoinWorldEvent event) {
    Entity entity = event.getEntity();
    World world = event.getWorld();
    // Don't want to infinitely spawn fish hooks.
    if (entity instanceof EntityFishHook && !(entity instanceof SkrimFishHook)) {
      EntityFishHook oldHook = (EntityFishHook) entity;
      SkrimFishHook newHook = null;
      if (world.isRemote) {
        newHook = new SkrimFishHook(event.getWorld(), oldHook.getAngler(), oldHook.posX, oldHook.posY, oldHook.posZ);
        newHook.setVelocity(oldHook.motionX, oldHook.motionY, oldHook.motionZ);
      } else {
        newHook = new SkrimFishHook(event.getWorld(), oldHook.getAngler());
      }
      newHook.setLuck((int) ReflectionUtils.getPrivateField(oldHook, Obfuscation.FISH_HOOK_LUCK.getFieldNames()));
      newHook.setLureSpeed((int) ReflectionUtils.getPrivateField(oldHook, Obfuscation.FISH_HOOK_LURE_SPEED.getFieldNames()));

      event.setCanceled(true);
      world.spawnEntity(newHook);
    }
  }


  @Override
  public void onUpdate() {
    super.onUpdate();
    if (!this.hasAppliedCaught) {
      EntityPlayer player = this.getAngler();
      if (player.hasCapability(Skills.FISHING, EnumFacing.NORTH)) {
        SkillFishing fishing = (SkillFishing) player.getCapability(Skills.FISHING, EnumFacing.NORTH);
        int ticksCaught = (int) ReflectionUtils.getSuperPrivateField(this, Obfuscation.FISH_HOOK_CAUGHT_DELAY.getFieldNames());
        if (ticksCaught > 0) {
          if (fishing.skillEnabled) {
            ReflectionUtils.hackSuperValueTo(this, (int) (ticksCaught - ticksCaught * fishing.getDelayReduction()),
                Obfuscation.FISH_HOOK_CAUGHT_DELAY.getFieldNames());
          }
          this.hasAppliedCaught = true;
        }
      }
    }
    if (!this.hasAppliedCatchable) {
      int ticksCatchable = (int) ReflectionUtils.getSuperPrivateField(this, Obfuscation.FISH_HOOK_CATCHABLE.getFieldNames());
      if (ticksCatchable > 0) {
        ReflectionUtils.hackSuperValueTo(this, 50, Obfuscation.FISH_HOOK_CATCHABLE.getFieldNames());
        this.hasAppliedCatchable = true;
      }
    }
  }

  @Override
  public int handleHookRetraction() {
    int ticksCatchable = (int) ReflectionUtils.getSuperPrivateField(this, Obfuscation.FISH_HOOK_CATCHABLE.getFieldNames());
    EntityPlayer angler = this.getAngler();
    boolean inGround = (boolean) ReflectionUtils.getSuperPrivateField(this, Obfuscation.FISH_HOOK_IN_GROUND.getFieldNames());

    if (!this.world.isRemote && angler != null) {
      int i = 0;

      if (this.caughtEntity != null) {
        this.bringInHookedEntity();
        this.world.setEntityState(this, (byte) 31);
        i = this.caughtEntity instanceof EntityItem ? 3 : 5;
      } else if (ticksCatchable > 0) {
        LootContext.Builder lootcontext$builder = new LootContext.Builder((WorldServer) this.world);

        // Please kill me
        lootcontext$builder.withLuck(((float) (int) ReflectionUtils.getSuperPrivateField(this,
            Obfuscation.FISH_HOOK_LUCK.getFieldNames())) + angler.getLuck());

        for (ItemStack itemstack : this.world.getLootTableManager().getLootTableFromLocation(LootTableList.GAMEPLAY_FISHING)
            .generateLootForPools(this.rand, lootcontext$builder.build())) {
          EntityItem entityitem = new EntityItem(this.world, this.posX, this.posY, this.posZ, itemstack);
          double d0 = angler.posX - this.posX;
          double d1 = angler.posY - this.posY;
          double d2 = angler.posZ - this.posZ;
          double d3 = (double) MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
          double d4 = 0.1D;
          entityitem.motionX = d0 * 0.1D;
          entityitem.motionY = d1 * 0.1D + (double) MathHelper.sqrt(d3) * 0.08D;
          entityitem.motionZ = d2 * 0.1D;
          this.world.spawnEntity(entityitem);
          angler.world.spawnEntity(new EntityXPOrb(angler.world, angler.posX, angler.posY + 0.5D, angler.posZ + 0.5D,
              this.rand.nextInt(6) + 1));
          if (angler.hasCapability(Skills.FISHING, EnumFacing.NORTH)) {
            SkillFishing fishing = Skills.getSkill(angler, Skills.FISHING, SkillFishing.class);
            fishing.addXp((EntityPlayerMP) angler, 2000);
            // Roll for treasure chance
            if (fishing.skillEnabled && this.rand.nextDouble() < fishing.getTreasureChance()) {
              EntityItem treasure = new EntityItem(this.world, this.posX, this.posY, this.posZ,
                  CustomLootTables.getRandomTreasure(this.world, this.angler, fishing.level));
              treasure.motionX = d0 * 0.1D;
              treasure.motionY = d1 * 0.1D + (double) MathHelper.sqrt(d3) * 0.08D;
              treasure.motionZ = d2 * 0.1D;
              this.world.spawnEntity(treasure);
              Skills.playRandomTreasureSound(angler);
            }

            // Apply abilities
            if (fishing.activeAbility(2)) {
              for (int q = 0; q < 2; q++) {
                EntityItem copy = new EntityItem(this.world, this.posX, this.posY, this.posZ, itemstack);
                copy.motionX = d0 * 0.1D;
                copy.motionY = d1 * 0.1D + (double) MathHelper.sqrt(d3) * 0.08D;
                copy.motionZ = d2 * 0.1D;
                this.world.spawnEntity(copy);
              }
              if (fishing.activeAbility(3)) {
                angler.world.spawnEntity(
                    new EntityXPOrb(angler.world, angler.posX, angler.posY + 0.5D, angler.posZ + 0.5D, this.rand.nextInt(16) + 9));
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
          if (fishing.activeAbility(1)) {
            Utils.teleport(angler, this.posX, this.posY, this.posZ, false);
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
    EntityPlayer angler = this.getAngler();
    if (angler != null) {
      double d0 = angler.posX - this.posX;
      double d1 = angler.posY - this.posY;
      double d2 = angler.posZ - this.posZ;
      int addY = 0;
      SkillFishing fishing = Skills.getSkill(angler, Skills.FISHING, SkillFishing.class);

      if (fishing.activeAbility(4)) {
        addY = 5;
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
