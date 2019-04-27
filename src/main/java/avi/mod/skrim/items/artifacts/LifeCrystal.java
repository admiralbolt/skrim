package avi.mod.skrim.items.artifacts;

import avi.mod.skrim.capabilities.maxhealth.CapabilityMaxHealth;
import avi.mod.skrim.capabilities.maxhealth.IMaxHealth;
import avi.mod.skrim.items.items.ArtifactItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class LifeCrystal extends ArtifactItem {


  public LifeCrystal() {
    super("life_crystal");
    this.setMaxDamage(1);
  }

  public int getMaxItemUseDuration(ItemStack stack) {
    return 32;
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    tooltip.add("§4Permanently increase your max health by 1 heart.§r");
    tooltip.add("§e\"To get more hearts, you must eat more hearts.\"§r");
  }

  @Nonnull
  public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
    if (entityLiving instanceof EntityPlayer) {
      IMaxHealth maxHealth = CapabilityMaxHealth.getMaxHealth(entityLiving);
      if (maxHealth != null) {
        maxHealth.addBonusMaxHealth(2.0f);
      }
    }
    stack.shrink(1);
    return stack;
  }

  public EnumAction getItemUseAction(ItemStack stack) {
    return EnumAction.EAT;
  }

  public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
    ItemStack itemstack = playerIn.getHeldItem(handIn);

    playerIn.setActiveHand(handIn);
    return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
  }

}
