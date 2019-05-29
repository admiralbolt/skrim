package avi.mod.skrim.skills.brewing;

import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.utils.Obfuscation;
import com.google.common.collect.ImmutableMap;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;

import java.util.List;
import java.util.Map;

/**
 * This is a helper class to apply modifications to a potion. Effectively, these are lambda functions that are run
 * against the input potion and adjust it in some way.
 */
public class PotionModifier {

  // Fermented spider eyes can be used to change potions to a different type.
  private static Map<Potion, Potion> FERMENTED_EYE_MODIFIER = ImmutableMap.<Potion, Potion>builder()
      .put(MobEffects.JUMP_BOOST, MobEffects.SLOWNESS)
      .put(MobEffects.SPEED, MobEffects.SLOWNESS)
      .put(MobEffects.INSTANT_HEALTH, MobEffects.INSTANT_DAMAGE)
      .put(MobEffects.POISON, MobEffects.WITHER)
      .put(MobEffects.NIGHT_VISION, MobEffects.INVISIBILITY)
      .put(MobEffects.STRENGTH, MobEffects.WEAKNESS)
      .build();

  @FunctionalInterface
  interface ModifierFunction<ItemStack, SkillBrewing> {
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
    // Check against totalModifiers - 1 since increased_strength will count as 2 modifiers.
    if (SkrimPotionUtils.timesModified(input) >= brewing.totalModifiers() - 1) return ItemStack.EMPTY;

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
    // A modifier so nice you have to increment it twice.
    SkrimPotionUtils.incrementModified(newPotion);
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
      Obfuscation.POTION_EFFECT_DURATION.hackValueTo(newEffect, (int) (effect.getDuration() * 1.3));
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

  public static PotionModifier CORRUPT_EFFECT = new PotionModifier((ItemStack input, SkillBrewing brewing) -> {
    List<PotionEffect> effects = PotionUtils.getEffectsFromStack(input);
    if (effects.size() == 0) return ItemStack.EMPTY;

    ItemStack newPotion = SkrimPotionUtils.convertPotion(input);
    NBTTagCompound compound = newPotion.getTagCompound();
    if (compound == null) return ItemStack.EMPTY;

    NBTTagList list = new NBTTagList();
    // We only want to return a potion *if* we can modify the effect types.
    boolean anyEffectsChanged = false;
    for (PotionEffect effect : effects) {
      anyEffectsChanged = anyEffectsChanged || FERMENTED_EYE_MODIFIER.containsKey(effect.getPotion());
      PotionEffect newEffect = new PotionEffect(FERMENTED_EYE_MODIFIER.getOrDefault(effect.getPotion(),
          effect.getPotion()), effect.getDuration(), effect.getAmplifier(), effect.getIsAmbient(), effect.doesShowParticles());
      list.appendTag(newEffect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
    }
    // No effect types changed means that fermented eye will do nothing.
    if (!anyEffectsChanged) return ItemStack.EMPTY;

    compound.setTag("CustomPotionEffects", list);
    return newPotion;
  });

  private ModifierFunction<ItemStack, SkillBrewing> conversion;

  private PotionModifier(ModifierFunction<ItemStack, SkillBrewing> conversion) {
    this.conversion = conversion;
  }

  public ItemStack apply(ItemStack input, SkillBrewing brewing) {
    return this.conversion.apply(input, brewing);
  }
}
