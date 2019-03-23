package avi.mod.skrim.items.items;

import avi.mod.skrim.items.ItemBase;
import avi.mod.skrim.items.ModItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ArtifactItem extends Item implements ItemBase {

  protected String name;

  public ArtifactItem(String name) {
    super();
    this.name = name;
    this.setUnlocalizedName(name);
    this.setRegistryName(name);
    this.maxStackSize = 1;
  }

  @Override
  public boolean hasEffect(ItemStack stack) {
    return true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public EnumRarity getRarity(ItemStack stack) {
    return ModItems.ARTIFACT_RARITY;
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
    return new ActionResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(hand));
  }

  @Override
  public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
    return stack;
  }

  @Override
  public int getMaxItemUseDuration(ItemStack stack) {
    return 0;
  }

  @Override
  public EnumAction getItemUseAction(ItemStack stack) {
    return EnumAction.NONE;
  }

  @Override
  public int getItemEnchantability() {
    return 0;
  }

  @Override
  public String getTexturePath() {
    return "items";
  }
}
