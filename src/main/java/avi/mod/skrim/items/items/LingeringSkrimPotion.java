package avi.mod.skrim.items.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class LingeringSkrimPotion extends SkrimPotion {

  private static final String NAME = "lingering_skrim_potion";

  public LingeringSkrimPotion() {
    super(NAME);
  }

  public String getItemStackDisplayName(ItemStack stack) {
    return "Lingering " + this.getBaseDisplayName(stack);
  }

  /**
   * Called when the equipped item is right clicked.
   */
  public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
    ItemStack itemstack = playerIn.getHeldItem(handIn);
    ItemStack itemstack1 = playerIn.capabilities.isCreativeMode ? itemstack.copy() : itemstack.splitStack(1);
    worldIn.playSound((EntityPlayer) null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_SPLASH_POTION_THROW,
        SoundCategory.PLAYERS, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

    if (!worldIn.isRemote) {
      EntityPotion entitypotion = new EntityPotion(worldIn, playerIn, itemstack1);
      entitypotion.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, -20.0F, 0.5F, 1.0F);
      worldIn.spawnEntity(entitypotion);
    }

    playerIn.addStat(StatList.getObjectUseStats(this));
    return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
  }
}
