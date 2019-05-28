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
    if (!SkrimPotionHelper.isSkrimPotion(input)) return ItemStack.EMPTY;


    NBTTagCompound compound = input.getTagCompound();
    if (compound == null) return ItemStack.EMPTY;

    NBTTagList list = compound.getTagList("CustomPotionEffects", 9);
    List<PotionEffect> effects = PotionUtils.getEffectsFromStack(input);
    if (effects.size() == 0) return ItemStack.EMPTY;

    for (PotionEffect effect : effects) {
      PotionEffect newEffect = new PotionEffect(effect);
      Obfuscation.POTION_EFFECT_DURATION.hackValueTo(newEffect, effect.getDuration() * 2);
      Obfuscation.POTION_EFFECT_AMPLIFIER.hackValueTo(newEffect, effect.getAmplifier() + 2);
      list.appendTag(newEffect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
    }
    compound.setTag("CustomPotionEffects", list);

    ItemStack newPotion = SkrimPotionHelper.convertPotion(input);
    newPotion.setTagCompound(compound);
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
