package avi.mod.skrim.skills.brewing;

import avi.mod.skrim.items.SkrimItems;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;

import java.util.ArrayList;
import java.util.List;

public class SkrimPotionUtils {

  private static final int COLOR_BLUE_POTION = 3694022;

  private static final ImmutableSet<Item> POTION_ITEMS = ImmutableSet.of(SkrimItems.SKRIM_POTION, SkrimItems.SPLASH_SKRIM_POTION,
      SkrimItems.LINGERING_SKRIM_POTION);

  public static final ImmutableMap<Item, Item> TO_SKRIM_POTION = ImmutableMap.<Item, Item>builder()
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

    NBTTagList list = new NBTTagList();

    List<PotionEffect> effects = getEffectsFromStack(potion);
    if (effects.size() > 0) {
      for (PotionEffect effect : effects) {
        list.appendTag(effect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
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

  public static int getColor(ItemStack potion) {
    NBTTagCompound compound = potion.getTagCompound();
    if (compound == null) return COLOR_BLUE_POTION;
    if (compound.hasKey("CustomPotionColor")) return compound.getInteger("CustomPotionColor");

    String potionTypeString = compound.getString("Potion");
    PotionType type = PotionUtils.getPotionFromItem(potion);
    System.out.println("type: " + type.getNamePrefixed("") + ", string: " + potionTypeString);
    if (!potionTypeString.equals("Skrim") && (type == PotionTypes.EMPTY || type == PotionTypes.AWKWARD)) return COLOR_BLUE_POTION;

    return PotionUtils.getPotionColorFromEffectList(PotionUtils.getEffectsFromStack(potion));
  }


}
