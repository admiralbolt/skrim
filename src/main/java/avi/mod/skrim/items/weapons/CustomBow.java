package avi.mod.skrim.items.weapons;

import avi.mod.skrim.items.ItemBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
        if (entityIn == null) {
          return 0.0F;
        } else {
          ItemStack itemstack = entityIn.getActiveItemStack();
          Item item = itemstack.getItem();
          if (item instanceof CustomBow) {
            CustomBow bow = (CustomBow) item;
            return (float) (stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / bow.maxChargeTime;
          }
          return 0.0F;
        }
      }
    });
  }


  public float getArrowVelocityOverride(int charge) {
    float f = (float) charge / this.maxChargeTime;
    f = (f * f + f * this.maxVelocity * 2) / (1 + this.maxVelocity);
    return Math.min(f, this.maxVelocity);
  }

  @Override
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

      return null;
    }
  }

  @Override
  public String getTexturePath() {
    return "weapons";
  }
}
