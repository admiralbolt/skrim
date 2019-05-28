package avi.mod.skrim.entities.projectile;

import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.skills.brewing.SkrimPotionUtils;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Blocks;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Partial override of EntityPotion. The getPotion() method manually checks against Items.SPLASH_POTION and Items.LINGERING_POTION which
 * means our custom item stacks won't work correctly with the default EntityPotion.
 * <p>
 * Additionally, minecraft loves to make a whole bunch of methods private, including the isLingering() check. Which means I have to override
 * a whole bunch of methods to make lingering potions work. Ugh.
 */
public class SkrimEntityPotion extends EntityPotion {

  private static final DataParameter<ItemStack> ITEM = EntityDataManager.<ItemStack>createKey(EntityPotion.class,
      DataSerializers.ITEM_STACK);


  public SkrimEntityPotion(World worldIn) {
    super(worldIn);
  }

  public SkrimEntityPotion(World worldIn, EntityLivingBase throwerIn, ItemStack potionDamageIn) {
    super(worldIn, throwerIn, potionDamageIn);
  }

  public SkrimEntityPotion(World worldIn, double x, double y, double z, ItemStack potionDamageIn) {
    super(worldIn, x, y, z, potionDamageIn);
  }

  protected void entityInit() {
    super.entityInit();
    this.getDataManager().register(ITEM, ItemStack.EMPTY);
  }

  public void setItem(@Nonnull ItemStack stack) {
    super.setItem(stack);
    this.getDataManager().set(ITEM, stack);
    this.getDataManager().setDirty(ITEM);
  }

  @Override
  @Nonnull
  public ItemStack getPotion() {
    ItemStack itemstack = this.getDataManager().get(ITEM);
    Item potionItem = itemstack.getItem();

    if (potionItem != SkrimItems.SPLASH_SKRIM_POTION && potionItem != SkrimItems.LINGERING_SKRIM_POTION)
      return new ItemStack(SkrimItems.SPLASH_SKRIM_POTION);

    return itemstack;
  }

  private boolean isLingering() {
    return this.getPotion().getItem() == SkrimItems.LINGERING_SKRIM_POTION;
  }

  // WARNING WARNING WARNING WARNING WARNING
  // Everything below here is 99% copy pasted from EntityPotion.java. There are a few small edits to make the cloud coloring work as
  // intended.

  @Override
  protected void onImpact(RayTraceResult result) {
    if (!this.world.isRemote) {
      ItemStack itemstack = this.getPotion();
      PotionType potiontype = PotionUtils.getPotionFromItem(itemstack);
      List<PotionEffect> list = PotionUtils.getEffectsFromStack(itemstack);
      boolean flag = potiontype == PotionTypes.WATER && list.isEmpty();

      if (result.typeOfHit == RayTraceResult.Type.BLOCK && flag) {
        BlockPos blockpos = result.getBlockPos().offset(result.sideHit);
        this.extinguishFires(blockpos, result.sideHit);

        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
          this.extinguishFires(blockpos.offset(enumfacing), enumfacing);
        }
      }

      if (flag) {
        this.applyWater();
      } else if (!list.isEmpty()) {
        if (this.isLingering()) {
          this.makeAreaOfEffectCloud(itemstack);
        } else {
          this.applySplash(result, list);
        }
      }

      int i = potiontype.hasInstantEffect() ? 2007 : 2002;
      // Small edit here to apply SkrimPotionUtils.getColor() instead of PotionUtils.getColor();
      this.world.playEvent(i, new BlockPos(this), SkrimPotionUtils.getColor(itemstack));
      this.setDead();
    }
  }


  private void applyWater() {
    AxisAlignedBB axisalignedbb = this.getEntityBoundingBox().grow(4.0D, 2.0D, 4.0D);
    List<EntityLivingBase> list = this.world.<EntityLivingBase>getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb,
        WATER_SENSITIVE);

    if (!list.isEmpty()) {
      for (EntityLivingBase entitylivingbase : list) {
        double d0 = this.getDistanceSq(entitylivingbase);

        if (d0 < 16.0D && isWaterSensitiveEntity(entitylivingbase)) {
          entitylivingbase.attackEntityFrom(DamageSource.DROWN, 1.0F);
        }
      }
    }
  }

  private void applySplash(RayTraceResult p_190543_1_, List<PotionEffect> p_190543_2_) {
    AxisAlignedBB axisalignedbb = this.getEntityBoundingBox().grow(4.0D, 2.0D, 4.0D);
    List<EntityLivingBase> list = this.world.<EntityLivingBase>getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);

    if (!list.isEmpty()) {
      for (EntityLivingBase entitylivingbase : list) {
        if (entitylivingbase.canBeHitWithPotion()) {
          double d0 = this.getDistanceSq(entitylivingbase);

          if (d0 < 16.0D) {
            double d1 = 1.0D - Math.sqrt(d0) / 4.0D;

            if (entitylivingbase == p_190543_1_.entityHit) {
              d1 = 1.0D;
            }

            for (PotionEffect potioneffect : p_190543_2_) {
              Potion potion = potioneffect.getPotion();

              if (potion.isInstant()) {
                potion.affectEntity(this, this.getThrower(), entitylivingbase, potioneffect.getAmplifier(), d1);
              } else {
                int i = (int) (d1 * (double) potioneffect.getDuration() + 0.5D);

                if (i > 20) {
                  entitylivingbase.addPotionEffect(new PotionEffect(potion, i, potioneffect.getAmplifier(), potioneffect.getIsAmbient(),
                      potioneffect.doesShowParticles()));
                }
              }
            }
          }
        }
      }
    }
  }

  private void makeAreaOfEffectCloud(ItemStack potion) {
    EntityAreaEffectCloud entityareaeffectcloud = new EntityAreaEffectCloud(this.world, this.posX, this.posY, this.posZ);
    entityareaeffectcloud.setOwner(this.getThrower());
    entityareaeffectcloud.setRadius(3.0F);
    entityareaeffectcloud.setRadiusOnUse(-0.5F);
    entityareaeffectcloud.setWaitTime(10);
    entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float) entityareaeffectcloud.getDuration());

    for (PotionEffect potioneffect : PotionUtils.getFullEffectsFromItem(potion)) {
      entityareaeffectcloud.addEffect(new PotionEffect(potioneffect));
    }

    NBTTagCompound nbttagcompound = potion.getTagCompound();

    // Small edit here to apply SkrimPotionUtils.getColor() instead of PotionUtils.getColor();
    if (nbttagcompound != null && nbttagcompound.hasKey("CustomPotionColor", 99)) {
      entityareaeffectcloud.setColor(nbttagcompound.getInteger("CustomPotionColor"));
    } else {
      entityareaeffectcloud.setColor(SkrimPotionUtils.getColor(potion));
    }


    this.world.spawnEntity(entityareaeffectcloud);
  }


  private void extinguishFires(BlockPos pos, EnumFacing p_184542_2_) {
    if (this.world.getBlockState(pos).getBlock() == Blocks.FIRE) {
      this.world.extinguishFire((EntityPlayer) null, pos.offset(p_184542_2_), p_184542_2_.getOpposite());
    }
  }

  private static boolean isWaterSensitiveEntity(EntityLivingBase p_190544_0_) {
    return p_190544_0_ instanceof EntityEnderman || p_190544_0_ instanceof EntityBlaze;
  }

}
