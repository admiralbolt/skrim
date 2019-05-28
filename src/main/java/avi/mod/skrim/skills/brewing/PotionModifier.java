package avi.mod.skrim.skills.brewing;

import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.utils.Obfuscation;
import net.minecraft.init.Items;
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
    ItemStack apply(ItemStack input, SkillBrewing brewing);
  }

  // Convert a base potion to a splash version. This modification should only be applied to normal potions or skrim
  // potions.
  public static PotionModifier BASE_TO_SPLASH = new PotionModifier((ItemStack input, SkillBrewing brewing) -> {
    if (input.getItem() != SkrimItems.SKRIM_POTION && input.getItem() != Items.POTIONITEM) return ItemStack.EMPTY;

    ItemStack newPotion = new ItemStack(SkrimItems.SPLASH_SKRIM_POTION);
    newPotion.setTagCompound(input.getTagCompound());
    return newPotion;
  });

  // Converts a potion from splash -> lingering.
  public static PotionModifier SPLASH_TO_LINGERING = new PotionModifier((ItemStack input, SkillBrewing brewing) -> {
    if (input.getItem() != SkrimItems.SPLASH_SKRIM_POTION && input.getItem() != Items.SPLASH_POTION)
      return ItemStack.EMPTY;

    ItemStack newPotion = new ItemStack(SkrimItems.LINGERING_SKRIM_POTION);
    newPotion.setTagCompound(input.getTagCompound());
    return newPotion;
  });

  // Increase the strength of all effects on a potion by 1 level. Some effects like night vision & invisibility don't
  // get any benefits from strength modification.
  public static PotionModifier INCREASED_STRENGTH = new PotionModifier((ItemStack input, SkillBrewing brewing) -> {
    if (!SkrimPotionUtils.isPotion(input)) return ItemStack.EMPTY;
    if (input.getTagCompound() == null) return ItemStack.EMPTY;

    // If the potion doesn't have any effects, there's nothing to be done.
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
    // We set the potion type to a custom one in case the input was a vanilla potion.
    compound.setString("Potion", "Skrim");

    ItemStack newPotion = new ItemStack(SkrimPotionUtils.TO_SKRIM_POTION.get(input.getItem()));
    newPotion.setTagCompound(compound);
    SkrimPotionUtils.incrementModified(newPotion);
    return newPotion;
  });

  // Increase the duration of all effects on a potion by 50%.
  public static PotionModifier INCREASED_DURATION = new PotionModifier((ItemStack input, SkillBrewing brewing) -> {
    if (!SkrimPotionUtils.isPotion(input)) return ItemStack.EMPTY;
    if (input.getTagCompound() == null) return ItemStack.EMPTY;

    // If the potion doesn't have any effects, there's nothing to be done.
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
    // We set the potion type to a custom one in case the input was a vanilla potion.
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
