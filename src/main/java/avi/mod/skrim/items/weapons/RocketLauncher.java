package avi.mod.skrim.items.weapons;

import avi.mod.skrim.entities.projectile.Rocket;
import avi.mod.skrim.items.ItemBase;
import avi.mod.skrim.skills.demolition.SkillDemolition;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RocketLauncher extends ItemBow implements ItemBase {

  private String name = "rocket_launcher";
  private float maxChargeTime = 2.0F;
  private float maxVelocity = 2.0F;

  public RocketLauncher() {
    super();
    this.setRegistryName(name);
    this.setUnlocalizedName(name);
    this.addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter() {
      @SideOnly(Side.CLIENT)
      public float apply(@Nonnull ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
        if (entityIn == null) return 0;

        ItemStack itemstack = entityIn.getActiveItemStack();
        Item item = itemstack.getItem();
        if (item instanceof RocketLauncher) {
          RocketLauncher launcher = (RocketLauncher) item;
          return (float) (stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / launcher.maxChargeTime;
        }
        return 0;
      }
    });
  }

  @Override
  public void onPlayerStoppedUsing(@Nonnull ItemStack stack, @Nonnull World worldIn, EntityLivingBase entityLiving, int timeLeft) {
    if (entityLiving instanceof EntityPlayer) {
      EntityPlayer entityplayer = (EntityPlayer) entityLiving;
      ItemStack itemstack = this.findAmmo(entityplayer);

      int i = this.getMaxItemUseDuration(stack) - timeLeft;
      if (i < 0)
        return;

      if (itemstack != null) {
        float f = getArrowVelocityOverride(i);
        if ((double) f >= 0.1D) {
          if (!worldIn.isRemote) {
            Item item = itemstack.getItem();
            Rocket rocket = new Rocket(worldIn, entityplayer, itemstack);
            rocket.shoot(entityplayer, entityplayer.rotationPitch, entityplayer.rotationYaw, 0.0F, f * 3.0F, 1.0F);
            stack.damageItem(1, entityplayer);
            worldIn.spawnEntity(rocket);
          }

          worldIn.playSound((EntityPlayer) null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_ARROW_SHOOT,
              SoundCategory.NEUTRAL, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
          stack.setCount(itemstack.getCount() - 1);

          if (itemstack.getCount() == 0) {
            entityplayer.inventory.deleteStack(itemstack);
          }
        }
      }
    }
  }

  private float getArrowVelocityOverride(int charge) {
    float f = (float) charge / this.maxChargeTime;
    f = (f * f + f * this.maxVelocity * 2) / (1 + this.maxVelocity);
    return Math.min(f, this.maxVelocity);
  }

  @Override
  protected ItemStack findAmmo(EntityPlayer player) {
    if (SkillDemolition.isExplosive(player.getHeldItem(EnumHand.OFF_HAND))) {
      return player.getHeldItem(EnumHand.OFF_HAND);
    }
    return null;
  }

  @Override
  public int getItemEnchantability() {
    return 0;
  }

  public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
    ItemStack targetStack = playerIn.getHeldItem(hand);
    if (this.findAmmo(playerIn) != null) {
      playerIn.setActiveHand(hand);
      return new ActionResult(EnumActionResult.SUCCESS, targetStack);
    } else {
      return new ActionResult(EnumActionResult.FAIL, targetStack);
    }
  }

  @Override
  public String getTexturePath() {
    return "weapons";
  }
}
