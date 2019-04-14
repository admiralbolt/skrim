package avi.mod.skrim.items.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class CustomItem extends Item {

  protected String name;

  public CustomItem(String name) {
    super();
    this.name = name;
    this.setUnlocalizedName(name);
    this.setRegistryName(name);
    this.maxStackSize = 1;
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

  }

  @Override
  @Nonnull
  public ActionResult<ItemStack> onItemRightClick(World itemStackIn, EntityPlayer worldIn, @Nonnull EnumHand playerIn) {
    return new ActionResult<>(EnumActionResult.PASS, worldIn.getHeldItem(playerIn));
  }

  @Override
  @Nonnull
  public EnumActionResult onItemUse(EntityPlayer stack, World playerIn, BlockPos worldIn, EnumHand pos, EnumFacing hand, float facing,
                                    float hitX,
                                    float hitY) {
    return EnumActionResult.PASS;
  }

  @Override
  @Nonnull
  public ItemStack onItemUseFinish(@Nonnull ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
    return stack;
  }

  @Override
  public int getMaxItemUseDuration(ItemStack stack) {
    return 0;
  }

  @Override
  @Nonnull
  public EnumAction getItemUseAction(ItemStack stack) {
    return EnumAction.NONE;
  }

  @Override
  public int getItemEnchantability() {
    return 0;
  }

}
