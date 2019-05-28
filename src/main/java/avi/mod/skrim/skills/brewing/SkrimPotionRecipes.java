package avi.mod.skrim.skills.brewing;

import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.skills.Skills;
import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;

import javax.annotation.Nonnull;
import java.util.Map;

public class SkrimPotionRecipes {

  // Define a mapping from ingredients to the effect they provide.
  public static Map<Item, Potion> INGREDIENT_EFFECTS = ImmutableMap.<Item, Potion>builder()
      .put(Items.RABBIT_FOOT, MobEffects.JUMP_BOOST)
      .put(Items.SUGAR, MobEffects.SPEED)
      .put(Items.BLAZE_POWDER, MobEffects.STRENGTH)
      .put(Items.SPECKLED_MELON, MobEffects.INSTANT_HEALTH)
      .put(Items.SPIDER_EYE, MobEffects.POISON)
      .put(Items.GHAST_TEAR, MobEffects.REGENERATION)
      .put(Items.MAGMA_CREAM, MobEffects.FIRE_RESISTANCE)
      .put(Items.GOLDEN_CARROT, MobEffects.NIGHT_VISION)
      .put(Items.FERMENTED_SPIDER_EYE, MobEffects.WEAKNESS)
      .put(Items.FISH, MobEffects.WATER_BREATHING)
      .build();

  // Fermented spider eyes can be used to change potions to a different type.
  public static Map<Potion, Potion> FERMENTED_EYE_MODIFIER = ImmutableMap.<Potion, Potion>builder()
      .put(MobEffects.JUMP_BOOST, MobEffects.SLOWNESS)
      .put(MobEffects.SPEED, MobEffects.SLOWNESS)
      .put(MobEffects.INSTANT_HEALTH, MobEffects.INSTANT_DAMAGE)
      .put(MobEffects.POISON, MobEffects.WITHER)
      .put(MobEffects.NIGHT_VISION, MobEffects.INVISIBILITY)
      .build();

  // Modifiers that don't change the base potion effect.
  private static Map<Item, PotionModifier> POTION_MODIFIERS = ImmutableMap.<Item, PotionModifier>builder()
      .put(Items.GLOWSTONE_DUST, PotionModifier.INCREASED_STRENGTH)
      .put(Items.REDSTONE, PotionModifier.INCREASED_DURATION)
      .put(Items.GUNPOWDER, PotionModifier.BASE_TO_SPLASH)
      .put(Items.DRAGON_BREATH, PotionModifier.SPLASH_TO_LINGERING)
      .build();

  public static boolean hasOutput(EntityPlayer player, @Nonnull ItemStack input, @Nonnull ItemStack ingredientStack) {
    return getOutput(player, input, ingredientStack) != ItemStack.EMPTY;
  }

  public static ItemStack getOutput(EntityPlayer player, @Nonnull ItemStack input, @Nonnull ItemStack ingredientStack) {
    if (input.isEmpty() || input.getCount() != 1 || ingredientStack.isEmpty() || !SkrimPotionUtils.isPotion(input))
      return ItemStack.EMPTY;

    PotionType potionType = PotionUtils.getPotionTypeFromNBT(input.getTagCompound());
    SkillBrewing brewing = Skills.getSkill(player, Skills.BREWING, SkillBrewing.class);
    Item ingredient = ingredientStack.getItem();

    // Water -> Awkward Potion.
    if (ingredient == Items.NETHER_WART && potionType == PotionTypes.WATER) {
      ItemStack newPotion = SkrimPotionUtils.convertPotion(input);
      PotionUtils.addPotionToItemStack(newPotion, PotionTypes.AWKWARD);
      return newPotion;
    }

    // Apply potion modifiers.
    if (POTION_MODIFIERS.containsKey(ingredient)) {
      return POTION_MODIFIERS.get(ingredient).apply(input, brewing);
    }

    // Add effects to the potion.
    if (INGREDIENT_EFFECTS.containsKey(ingredient)) {
      // Brewing only works with pufferfish, which unfortunately is still coded as metadata until mc 1.13.
      if (ingredient == Items.FISH && ingredientStack.getMetadata() != ItemFishFood.FishType.PUFFERFISH.getMetadata())
        return ItemStack.EMPTY;

    }

    return ItemStack.EMPTY;
  }

  public static void registerRecipes() {
    BrewingRecipeRegistry.addRecipe(new SkrimRecipes());
  }

  public static class SkrimRecipes implements IBrewingRecipe {

    @Override
    public boolean isInput(@Nonnull ItemStack input) {
      Item potion = input.getItem();
      return (potion == SkrimItems.SKRIM_POTION || potion == SkrimItems.SPLASH_SKRIM_POTION || potion == SkrimItems.LINGERING_SKRIM_POTION);
    }

    @Override
    public boolean isIngredient(@Nonnull ItemStack ingredient) {
      return true;
    }

    @Nonnull
    @Override
    public ItemStack getOutput(@Nonnull ItemStack input, @Nonnull ItemStack ingredient) {
      return null;
    }
  }

}
