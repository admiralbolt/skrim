package avi.mod.skrim.items.items;

import avi.mod.skrim.items.ItemBase;
import avi.mod.skrim.skills.brewing.SkrimPotionUtils;
import avi.mod.skrim.utils.Utils;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class SkrimPotion extends ItemPotion implements ItemBase {

  private String name = "skrim_potion";

  public SkrimPotion() {
    this.setRegistryName(name);
    this.setUnlocalizedName(name);
  }

  public SkrimPotion(String name) {
    this.name = name;
    this.setRegistryName(name);
    this.setUnlocalizedName(name);
  }

  @Nonnull
  public ItemStack onItemUseFinish(@Nonnull ItemStack stack, World worldIn, @Nonnull EntityLivingBase entityLiving) {
    return super.onItemUseFinish(stack, worldIn, entityLiving);
  }

  @Nonnull
  public String getItemStackDisplayName(@Nonnull ItemStack stack) {
    return getBaseDisplayName(stack);
  }

  public String getBaseDisplayName(@Nonnull ItemStack stack) {
    StringBuilder sb = new StringBuilder();
    PotionType type = PotionUtils.getPotionTypeFromNBT(stack.getTagCompound());
    if (type == PotionTypes.WATER) return "Water Bottle";
    if (type == PotionTypes.AWKWARD) return "Awkward Potion";
    if (type == PotionTypes.MUNDANE) return "Mundane Potion";
    if (type == PotionTypes.THICK) return "Thick Potion";

    sb.append("Potion of");
    for (PotionEffect effect : PotionUtils.getEffectsFromStack(stack)) {
      sb.append(" ");
      sb.append(Utils.titleizeLowerCamel(effect.getEffectName().split("\\.")[1]));
    }
    return sb.toString();
  }

  /**
   * allows items to add custom lines of information to the mouseover description
   */
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    PotionUtils.addPotionTooltip(stack, tooltip, 1.0F);
  }

  @SideOnly(Side.CLIENT)
  public boolean hasEffect(ItemStack stack) {
    return super.hasEffect(stack) || !PotionUtils.getEffectsFromStack(stack).isEmpty();
  }

  @Override
  public String getTexturePath() {
    return "items";
  }

  @SideOnly(Side.CLIENT)
  public static class ColorHandler implements IItemColor {

    @Override
    public int colorMultiplier(@Nonnull ItemStack stack, int tintIndex) {
      return (tintIndex > 0) ? -1 : SkrimPotionUtils.getColor(stack);
    }
  }


}
