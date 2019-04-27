package avi.mod.skrim.items.weapons;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.items.ItemBase;
import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.ranged.SkillRanged;
import avi.mod.skrim.utils.Obfuscation;
import avi.mod.skrim.utils.Utils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.*;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CustomBow extends ItemBow implements ItemBase {

  private String name;
  private float maxChargeTime;
  private float maxVelocity;

  public CustomBow(String name, float maxChargeTime, float maxVelocity) {
    super();
    this.name = name;
    this.setRegistryName(name);
    this.setUnlocalizedName(name);
    this.maxChargeTime = maxChargeTime;
    this.maxVelocity = maxVelocity;

    this.addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter() {
      @SideOnly(Side.CLIENT)
      public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
        if (entityIn == null) return 0.0F;

        ItemStack itemstack = entityIn.getActiveItemStack();
        Item item = itemstack.getItem();
        if (item instanceof CustomBow) {
          CustomBow bow = (CustomBow) item;
          return (float) (stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / bow.getChargeTime(entityIn);
        }
        return 0.0F;
      }
    });

  }

  /**
   * Wholesale copied from ItemBow.java.
   * <p>
   * The only reason for overriding this is to use dynamic charge times & higher max speed.
   */
  @Override
  public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
    if (entityLiving instanceof EntityPlayer) {
      EntityPlayer entityplayer = (EntityPlayer) entityLiving;
      boolean flag = entityplayer.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
      ItemStack itemstack = this.findAmmo(entityplayer);

      int i = this.getMaxItemUseDuration(stack) - timeLeft;
      i = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, worldIn, entityplayer, i, !itemstack.isEmpty() || flag);
      if (i < 0) return;

      if (!itemstack.isEmpty() || flag) {
        if (itemstack.isEmpty()) {
          itemstack = new ItemStack(Items.ARROW);
        }

        float f = this.getArrowVelocityOverride(i, entityLiving);

        if ((double) f >= 0.1D) {
          boolean flag1 =
              entityplayer.capabilities.isCreativeMode || (itemstack.getItem() instanceof ItemArrow && ((ItemArrow) itemstack.getItem()).isInfinite(itemstack, stack, entityplayer));

          if (!worldIn.isRemote) {
            ItemArrow itemarrow = (ItemArrow) (itemstack.getItem() instanceof ItemArrow ? itemstack.getItem() : Items.ARROW);
            EntityArrow entityarrow = itemarrow.createArrow(worldIn, itemstack, entityplayer);
            entityarrow = this.customizeArrow(entityarrow);
            entityarrow.shoot(entityplayer, entityplayer.rotationPitch, entityplayer.rotationYaw, 0.0F, f * 3.0F, 1.0F);

            if (f == this.maxVelocity) {
              entityarrow.setIsCritical(true);
            }

            int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);

            if (j > 0) {
              entityarrow.setDamage(entityarrow.getDamage() + (double) j * 0.5D + 0.5D);
            }

            int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);

            if (k > 0) {
              entityarrow.setKnockbackStrength(k);
            }

            if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0) {
              entityarrow.setFire(100);
            }

            stack.damageItem(1, entityplayer);

            if (flag1 || entityplayer.capabilities.isCreativeMode && (itemstack.getItem() == Items.SPECTRAL_ARROW || itemstack.getItem() == Items.TIPPED_ARROW)) {
              entityarrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
            }

            worldIn.spawnEntity(entityarrow);
          }

          worldIn.playSound((EntityPlayer) null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_ARROW_SHOOT,
              SoundCategory.PLAYERS, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

          if (!flag1 && !entityplayer.capabilities.isCreativeMode) {
            itemstack.shrink(1);

            if (itemstack.isEmpty()) {
              entityplayer.inventory.deleteStack(itemstack);
            }
          }

          entityplayer.addStat(StatList.getObjectUseStats(this));
        }
      }
    }
  }


  private float getArrowVelocityOverride(int charge, EntityLivingBase entityIn) {
    float f = (float) charge / this.getChargeTime(entityIn);
    f = (f * f + f * this.maxVelocity * 2) / (1 + this.maxVelocity);
    return Math.min(f, this.maxVelocity);
  }

  private float getChargeTime(EntityLivingBase shooter) {
    if (!(shooter instanceof EntityPlayer)) return this.maxChargeTime;

    EntityPlayer player = (EntityPlayer) shooter;
    SkillRanged ranged = Skills.getSkill(player, Skills.RANGED, SkillRanged.class);
    return Math.max(1f, this.maxChargeTime - this.maxChargeTime * ranged.getChargeReduction());
  }

  @Override
  @Nonnull
  protected ItemStack findAmmo(EntityPlayer player) {
    if (this.isArrow(player.getHeldItem(EnumHand.OFF_HAND))) {
      return player.getHeldItem(EnumHand.OFF_HAND);
    } else if (this.isArrow(player.getHeldItem(EnumHand.MAIN_HAND))) {
      return player.getHeldItem(EnumHand.MAIN_HAND);
    } else {
      for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
        ItemStack itemstack = player.inventory.getStackInSlot(i);

        if (this.isArrow(itemstack)) {
          return itemstack;
        }
      }

      return ItemStack.EMPTY;
    }
  }

  @Override
  public String getTexturePath() {
    return "weapons";
  }

  @Mod.EventBusSubscriber(modid = Skrim.MOD_ID)
  public static class CustomBowHandler {

    /**
     * Alright so here's more info than you wanted to know about how item pickup works. There are two events that can be caught:
     * 1. EntityItemPickupEvent -- Happens when the entity item is touched before added to inventory.
     * 2. PlayerEvent.ItemPickupEvent -- Happens when an item is actually added to the inventory.
     * <p>
     * In a normal world I would subscribe to the entity item event and modify the item before it gets picked up. In this world however,
     * there's a timing issue. The itemstack that's passed to the inventory is actually initialized before the event is called. No matter
     * how much you modify the item in the event, the actual item passed to the inventory will remain the same.
     * <p>
     * Instead, we do a hack: Subscribe to the ItemPickupEvent, remove the minecraft bow and add the fake bow.
     */
    @SubscribeEvent
    public static void pickupBow(PlayerEvent.ItemPickupEvent event) {
      System.out.println("pickup fired");

      ItemStack stack = event.getStack();
      if (stack.getItem() == Items.BOW) {
        System.out.println("it's a bow!");
        Utils.removeFromInventory(event.player.inventory, Items.BOW, stack.getCount());
        event.player.addItemStackToInventory(new ItemStack(SkrimItems.OVERWRITE_BOW, stack.getCount(), stack.getMetadata()));
      }
    }

    @SubscribeEvent
    public static void craftBow(PlayerEvent.ItemCraftedEvent event) {
      if (event.crafting.getItem() != Items.BOW) return;

      ItemStack newStack = new ItemStack(SkrimItems.OVERWRITE_BOW, event.crafting.getCount(), event.crafting.getMetadata());
      if (event.player.inventory.getItemStack().getItem() == Items.AIR) {
        // Player shift-clicked. We'll need to add the skrim bow to their inventory directly and remove the wrong version.
        event.player.inventory.addItemStackToInventory(newStack);
        Utils.removeFromInventory(event.player.inventory, event.crafting.getItem(), event.crafting.getCount());
      } else {
        Obfuscation.CURRENT_ITEM.hackValueTo(event.player.inventory, newStack);
      }
    }


  }

}
