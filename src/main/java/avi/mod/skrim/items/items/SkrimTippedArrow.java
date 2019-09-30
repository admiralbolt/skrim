package avi.mod.skrim.items.items;

import avi.mod.skrim.items.ItemBase;
import avi.mod.skrim.skills.brewing.SkrimPotionUtils;
import avi.mod.skrim.utils.ObfuscatedMethod;
import avi.mod.skrim.utils.Utils;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTippedArrow;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * An overridden verison of ItemTippedArrow to work with SkrimPotions or normal potions correctly.
 */
public class SkrimTippedArrow extends ItemTippedArrow implements ItemBase {

  private static final String NAME = "skrim_tipped_arrow";

  public SkrimTippedArrow() {
    this.setUnlocalizedName(NAME);
    this.setRegistryName(NAME);
  }

  @Override
  @Nonnull
  public EntityArrow createArrow(@Nonnull World worldIn, @Nonnull ItemStack stack, EntityLivingBase shooter) {
    EntityTippedArrow entitytippedarrow = new EntityTippedArrow(worldIn, shooter);
    entitytippedarrow.setPotionEffect(stack);
    ObfuscatedMethod.SET_FIXED_COLOR.invoke(entitytippedarrow, SkrimPotionUtils.getColor(stack));
    return entitytippedarrow;
  }

  @Override
  @Nonnull
  public String getItemStackDisplayName(ItemStack stack) {
    StringBuilder sb = new StringBuilder();
    PotionType type = PotionUtils.getPotionTypeFromNBT(stack.getTagCompound());
    if (type == PotionTypes.WATER) return "Tipped Arrow";
    if (type == PotionTypes.AWKWARD) return "Tipped Arrow";
    if (type == PotionTypes.MUNDANE) return "Tipped Arrow";
    if (type == PotionTypes.THICK) return "Tipped Arrow";

    sb.append("Arrow of");
    boolean first = true;
    for (PotionEffect effect : PotionUtils.getEffectsFromStack(stack)) {
      if (!first) {
        sb.append(",");
      } else {
        first = false;
      }
      sb.append(" ");
      // Converting from a name like effect.moveSpeed -> Move Speed.
      sb.append(Utils.titleizeLowerCamel(effect.getEffectName().split("\\.")[1]));
    }
    return sb.toString();
  }

  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, ITooltipFlag flagIn) {
    PotionUtils.addPotionTooltip(stack, tooltip, 1.0F);
    tooltip.add("");
    tooltip.add("Modification Level: " + SkrimPotionUtils.timesModified(stack));
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
