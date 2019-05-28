package avi.mod.skrim.skills.brewing;

import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.items.items.SplashSkrimPotion;
import avi.mod.skrim.utils.Obfuscation;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;

import java.util.List;

/**
 * This is a helper class to apply modifications to a potion. Effectively, these are lambda functions that are run
 * against the input potion and adjust it in some way.
 */
public class PotionModifier {

  @FunctionalInterface
  interface Function<ItemStack, SkillBrewing> {
    public ItemStack apply(ItemStack input, SkillBrewing brewing);
  }

  public static PotionModifier BASE_TO_SPLASH = new PotionModifier((ItemStack input, SkillBrewing brewing) -> {
    ItemStack newPotion = new ItemStack(SkrimItems.SPLASH_SKRIM_POTION);
    newPotion.setTagCompound(input.getTagCompound());
    return newPotion;
  });

  public static PotionModifier SPLASH_TO_LINGERING = new PotionModifier((ItemStack input, SkillBrewing brewing) -> {
    if (!(input.getItem() instanceof SplashSkrimPotion)) return ItemStack.EMPTY;

    ItemStack newPotion = new ItemStack(SkrimItems.LINGERING_SKRIM_POTION);
    newPotion.setTagCompound(input.getTagCompound());
    return newPotion;
  });

  public static PotionModifier INCREASED_STRENGTH = new PotionModifier((ItemStack input, SkillBrewing brewing) -> {
    if (!SkrimPotionUtils.isSkrimPotion(input)) return ItemStack.EMPTY;
    if (input.getTagCompound() == null) return ItemStack.EMPTY;

    NBTTagCompound compound = input.getTagCompound().copy();
    List<PotionEffect> effects = PotionUtils.getEffectsFromStack(input);
    if (effects.size() == 0) return ItemStack.EMPTY;

    if (SkrimPotionUtils.timesModified(input) >= brewing.totalModifiers()) return ItemStack.EMPTY;

    NBTTagList list = new NBTTagList();
    for (PotionEffect effect : effects) {
      PotionEffect newEffect = new PotionEffect(effect);
      Obfuscation.POTION_EFFECT_AMPLIFIER.hackValueTo(newEffect, effect.getAmplifier() + 1);
      list.appendTag(newEffect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
    }
    compound.setTag("CustomPotionEffects", list);
    compound.setString("Potion", "Skrim");

    ItemStack newPotion = new ItemStack(SkrimPotionUtils.TO_SKRIM_POTION.get(input.getItem()));
    newPotion.setTagCompound(compound);
    SkrimPotionUtils.incrementModified(newPotion);
    return newPotion;
  });

  public static PotionModifier INCREASED_DURATION = new PotionModifier((ItemStack input, SkillBrewing brewing) -> {
    if (!SkrimPotionUtils.isSkrimPotion(input)) return ItemStack.EMPTY;
    if (input.getTagCompound() == null) return ItemStack.EMPTY;

    NBTTagCompound compound = input.getTagCompound().copy();
    List<PotionEffect> effects = PotionUtils.getEffectsFromStack(input);
    if (effects.size() == 0) return ItemStack.EMPTY;

    if (SkrimPotionUtils.timesModified(input) >= brewing.totalModifiers()) return ItemStack.EMPTY;

    NBTTagList list = new NBTTagList();
    for (PotionEffect effect : effects) {
      PotionEffect newEffect = new PotionEffect(effect);
      Obfuscation.POTION_EFFECT_DURATION.hackValueTo(newEffect, (int) (effect.getDuration() * 1.5));
      list.appendTag(newEffect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
    }
    compound.setTag("CustomPotionEffects", list);
    compound.setString("Potion", "Skrim");

    ItemStack newPotion = new ItemStack(SkrimPotionUtils.TO_SKRIM_POTION.get(input.getItem()));
    newPotion.setTagCompound(compound);
    SkrimPotionUtils.incrementModified(newPotion);
    return newPotion;
  });

  private Function<ItemStack, SkillBrewing> conversion;

  private PotionModifier(Function<ItemStack, SkillBrewing> conversion) {
    this.conversion = conversion;
  }

  public ItemStack apply(ItemStack input, SkillBrewing brewing) {
    return this.conversion.apply(input, brewing);
  }
}
