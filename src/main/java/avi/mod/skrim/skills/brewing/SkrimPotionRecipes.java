package avi.mod.skrim.skills.brewing;

import com.google.common.collect.ImmutableMap;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;

import java.util.Map;

public class SkrimPotionRecipes {

  // Define a mapping from ingredients to the effect they provide.
  public static Map<ItemStack, Potion> INGREDIENT_EFFECTS = ImmutableMap.<ItemStack, Potion>builder()
      .put(new ItemStack(Items.RABBIT_FOOT), MobEffects.JUMP_BOOST)
      .put(new ItemStack(Items.SUGAR), MobEffects.SPEED)
      .put(new ItemStack(Items.BLAZE_POWDER), MobEffects.STRENGTH)
      .put(new ItemStack(Items.SPECKLED_MELON), MobEffects.INSTANT_HEALTH)
      .put(new ItemStack(Items.SPIDER_EYE), MobEffects.POISON)
      .put(new ItemStack(Items.GHAST_TEAR), MobEffects.REGENERATION)
      .put(new ItemStack(Items.MAGMA_CREAM), MobEffects.FIRE_RESISTANCE)
      .put(new ItemStack(Items.FISH, 1, ItemFishFood.FishType.PUFFERFISH.getMetadata()), MobEffects.WATER_BREATHING)
      .put(new ItemStack(Items.GOLDEN_CARROT), MobEffects.NIGHT_VISION)
      .put(new ItemStack(Items.FERMENTED_SPIDER_EYE), MobEffects.WEAKNESS)
      .build();

  // Fermented spider eyes can be used to change potions to a different type.
  public static Map<Potion, Potion> FERMENTED_EYE_MODIFIER = ImmutableMap.<Potion, Potion>builder()
      .put(MobEffects.JUMP_BOOST, MobEffects.SLOWNESS)
      .put(MobEffects.SPEED, MobEffects.SLOWNESS)
      .put(MobEffects.INSTANT_HEALTH, MobEffects.INSTANT_DAMAGE)
      .put(MobEffects.POISON, MobEffects.WITHER)
      .put(MobEffects.NIGHT_VISION, MobEffects.INVISIBILITY)
      .build();




}
