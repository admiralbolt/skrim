package avi.mod.skrim.items.items;

import avi.mod.skrim.items.ItemBase;
import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.utils.Obfuscation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SkrimPotion extends ItemPotion implements ItemBase {

  private static final String NAME = "skrim_potion";

  public SkrimPotion() {
    this.setRegistryName(NAME);
    this.setUnlocalizedName(NAME);
  }

  public static ItemStack convertVanillaPotion(ItemStack potion) {
    List<PotionEffect> effects = PotionUtils.getEffectsFromStack(potion);
    NBTTagCompound compound = new NBTTagCompound();
    NBTTagList list = compound.getTagList("CustomPotionEffects", 9);
    for (PotionEffect effect : effects) {
      System.out.println("effect: " + effect + ", effect.name: " + effect.getEffectName() + ", effect.duration: " + effect.getDuration() + ", amp: " + effect.getAmplifier());
      PotionEffect newEffect = new PotionEffect(effect);
      Obfuscation.POTION_EFFECT_DURATION.hackValueTo(newEffect, effect.getDuration() * 2);
      Obfuscation.POTION_EFFECT_AMPLIFIER.hackValueTo(newEffect, effect.getAmplifier() + 2);
      list.appendTag(newEffect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
    }
    compound.setTag("CustomPotionEffects", list);
    NBTTagCompound name = new NBTTagCompound();
    name.setString("Name", "Skrim Potion");
    compound.setTag("display", name);

    ItemStack newPotion = new ItemStack(SkrimItems.SKRIM_POTION);
    newPotion.setTagCompound(compound);
    return newPotion;
  }


  @Nonnull
  public ItemStack onItemUseFinish(@Nonnull ItemStack stack, World worldIn, @Nonnull EntityLivingBase entityLiving) {
    return super.onItemUseFinish(stack, worldIn, entityLiving);
  }

  @Nonnull
  public String getItemStackDisplayName(@Nonnull ItemStack stack) {
    return I18n.translateToLocal(PotionUtils.getPotionFromItem(stack).getNamePrefixed("potion.effect."));
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
      if (tintIndex > 0) return -1;

      List<PotionEffect> effects = new ArrayList<>();
      PotionUtils.addCustomPotionEffectToList(stack.getTagCompound(), effects);
      if (effects.size() == 0) return -1;

      return PotionUtils.getPotionColorFromEffectList(effects);
    }
  }

}
