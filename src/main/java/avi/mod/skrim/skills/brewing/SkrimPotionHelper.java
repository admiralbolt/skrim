package avi.mod.skrim.skills.brewing;

import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.utils.Obfuscation;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;

import java.util.List;

public class SkrimPotionHelper {

  private static final ImmutableSet<Item> POTION_ITEMS = ImmutableSet.of(SkrimItems.SKRIM_POTION, SkrimItems.SPLASH_SKRIM_POTION,
      SkrimItems.LINGERING_SKRIM_POTION);

  private static final ImmutableMap<Item, Item> TO_SKRIM_POTION = ImmutableMap.<Item, Item>builder()
      .put(Items.POTIONITEM, SkrimItems.SKRIM_POTION)
      .put(SkrimItems.SKRIM_POTION, SkrimItems.SKRIM_POTION)
      .put(Items.SPLASH_POTION, SkrimItems.SPLASH_SKRIM_POTION)
      .put(SkrimItems.SPLASH_SKRIM_POTION, SkrimItems.SPLASH_SKRIM_POTION)
      .put(Items.LINGERING_POTION, SkrimItems.LINGERING_SKRIM_POTION)
      .put(SkrimItems.LINGERING_SKRIM_POTION, SkrimItems.LINGERING_SKRIM_POTION)
      .build();

  // Converts any potion to a skrim potion. Can be used to convert vanilla potions to skrim potions or to copy existing skrim potions.
  public static ItemStack convertPotion(ItemStack potion) {
    if (!TO_SKRIM_POTION.containsKey(potion.getItem())) return ItemStack.EMPTY;

    NBTTagCompound compound = new NBTTagCompound();

    compound.setString("Potion",
        PotionUtils.getPotionTypeFromNBT(potion.getTagCompound()).getRegistryName().getResourcePath());
    NBTTagList list = compound.getTagList("CustomPotionEffects", 9);

    List<PotionEffect> effects = PotionUtils.getEffectsFromStack(potion);
    if (effects.size() > 0) {
      for (PotionEffect effect : effects) {
        PotionEffect newEffect = new PotionEffect(effect);
        Obfuscation.POTION_EFFECT_DURATION.hackValueTo(newEffect, effect.getDuration() * 2);
        Obfuscation.POTION_EFFECT_AMPLIFIER.hackValueTo(newEffect, effect.getAmplifier() + 2);
        list.appendTag(newEffect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
      }
      compound.setTag("CustomPotionEffects", list);
    }

    ItemStack newPotion = new ItemStack(TO_SKRIM_POTION.get(potion.getItem()));
    newPotion.setTagCompound(compound);
    return newPotion;
  }

  // Get the number of times a potion has been modified.
  public static int timesModified(ItemStack potion) {
    NBTTagCompound compound = potion.getTagCompound();
    if (compound == null) return 0;

    return compound.hasKey("Modified") ? compound.getInteger("Modified") : 0;
  }

  // Increment the number of times a potion has been modified.
  public static void incrementModified(ItemStack potion) {
    NBTTagCompound compound = potion.getTagCompound();
    if (compound == null) return;

    if (!compound.hasKey("Modified")) {
      compound.setInteger("Modified", 0);
    }
    compound.setInteger("Modified", compound.getInteger("Modified") + 1);
  }

  public static boolean isSkrimPotion(ItemStack potion) {
    return POTION_ITEMS.contains(potion.getItem());
  }

}
