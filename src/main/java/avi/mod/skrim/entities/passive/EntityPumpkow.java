package avi.mod.skrim.entities.passive;

import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.skills.cooking.SkillCooking;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

import javax.annotation.Nullable;

public class EntityPumpkow extends EntityCow implements net.minecraftforge.common.IShearable {

  public static final String NAME = "pumpkow";

  public EntityPumpkow(World worldIn) {
    super(worldIn);
    this.setSize(0.9F, 1.4F);
    this.spawnableBlock = Blocks.MYCELIUM;
  }

  public static void registerFixesPumpkow(DataFixer fixer) {
    EntityLiving.registerFixesMob(fixer, EntityPumpkow.class);
  }

  public boolean processInteract(EntityPlayer player, EnumHand hand) {
    ItemStack itemstack = player.getHeldItem(hand);

    if (itemstack.isEmpty() && this.getGrowingAge() >= 0 && !player.capabilities.isCreativeMode) {
      player.setHeldItem(hand, SkillCooking.getReplaceFood(player, new ItemStack(Items.PUMPKIN_PIE)));
      return true;
    }

    return super.processInteract(player, hand);
  }

  public EntityPumpkow createChild(EntityAgeable ageable) {
    return new EntityPumpkow(this.world);
  }

  @Override
  public boolean isShearable(ItemStack item, net.minecraft.world.IBlockAccess world, net.minecraft.util.math.BlockPos pos) {
    return getGrowingAge() >= 0;
  }

  @Override
  public java.util.List<ItemStack> onSheared(ItemStack item, net.minecraft.world.IBlockAccess world, net.minecraft.util.math.BlockPos pos
      , int fortune) {
    this.setDead();
    ((net.minecraft.world.WorldServer) this.world).spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, false, this.posX,
        this.posY + (double) (this.height / 2.0F), this.posZ, 1, 0.0D, 0.0D, 0.0D, 0.0D);

    EntityCow entitycow = new EntityCow(this.world);
    entitycow.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
    entitycow.setHealth(this.getHealth());
    entitycow.renderYawOffset = this.renderYawOffset;

    if (this.hasCustomName()) {
      entitycow.setCustomNameTag(this.getCustomNameTag());
    }

    this.world.spawnEntity(entitycow);

    java.util.List<ItemStack> ret = new java.util.ArrayList<>();
    ret.add(new ItemStack(SkrimItems.PUMPKIN_STONE));
    this.playSound(SoundEvents.ENTITY_MOOSHROOM_SHEAR, 1.0F, 1.0F);

    return ret;
  }

  @Nullable
  protected ResourceLocation getLootTable() {
    return LootTableList.ENTITIES_COW;
  }
}
