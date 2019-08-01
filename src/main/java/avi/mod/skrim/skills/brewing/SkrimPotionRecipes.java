package avi.mod.skrim.skills.brewing;

import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.utils.Utils;
import com.google.common.collect.ImmutableMap;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public class SkrimPotionRecipes {

  // Define a mapping from ingredients to the effect they provide and the starting duration of that effect. All durations are in ticks
  // with 1 second = 20 ticks.
  public static Map<Item, Pair<Potion, Integer>> INGREDIENT_EFFECTS = ImmutableMap.<Item, Pair<Potion, Integer>>builder()
      .put(Items.RABBIT_FOOT, Pair.of(MobEffects.JUMP_BOOST, Utils.TICKS_PER_SECOND * 180))
      .put(Items.SUGAR, Pair.of(MobEffects.SPEED, Utils.TICKS_PER_SECOND * 180))
      .put(Items.BLAZE_POWDER, Pair.of(MobEffects.STRENGTH, Utils.TICKS_PER_SECOND * 180))
      .put(Items.SPECKLED_MELON, Pair.of(MobEffects.INSTANT_HEALTH, 0))
      .put(Items.SPIDER_EYE, Pair.of(MobEffects.POISON, Utils.TICKS_PER_SECOND * 45))
      .put(Items.GHAST_TEAR, Pair.of(MobEffects.REGENERATION, Utils.TICKS_PER_SECOND * 45))
      .put(Items.MAGMA_CREAM, Pair.of(MobEffects.FIRE_RESISTANCE, Utils.TICKS_PER_SECOND * 180))
      .put(Items.GOLDEN_CARROT, Pair.of(MobEffects.NIGHT_VISION, Utils.TICKS_PER_SECOND * 180))
      .put(Items.FERMENTED_SPIDER_EYE, Pair.of(MobEffects.WEAKNESS, Utils.TICKS_PER_SECOND * 90))
      .put(Items.FISH, Pair.of(MobEffects.WATER_BREATHING, Utils.TICKS_PER_SECOND * 180))
      .build();

  // Modifiers that don't change the base potion effect.
  private static Map<Item, PotionModifier> POTION_MODIFIERS = ImmutableMap.<Item, PotionModifier>builder()
      .put(Items.GLOWSTONE_DUST, PotionModifier.INCREASED_STRENGTH)
      .put(Items.REDSTONE, PotionModifier.INCREASED_DURATION)
      .put(Items.GUNPOWDER, PotionModifier.BASE_TO_SPLASH)
      .put(Items.DRAGON_BREATH, PotionModifier.SPLASH_TO_LINGERING)
      .put(Items.FERMENTED_SPIDER_EYE, PotionModifier.CORRUPT_EFFECT)
      .build();

  public static boolean hasOutput(SkillBrewing brewing, @Nonnull ItemStack input, @Nonnull ItemStack ingredientStack) {
    return getOutput(brewing, input, ingredientStack) != ItemStack.EMPTY;
  }

  /**
   * This function is beefy but does the actual brewing itself. Take a look at https://bit.ly/2EGzirg as a reference guide. There are 4
   * different types of brewing processes:
   * 1. Conversion from water -> awkward potion. This can only happen if the input is a water bottle and the ingredient is nether wart.
   * This is a required first step for brewing any other type of potion.
   * 2. Adding effects to potions. Generally this can only happen if the input is an awkward potion. In our case, you can add additional
   * effects to potions if your brewing skill is high enough. We don't add duplicate effects to a potion though.
   * 3. Modifying effects of potions. Redstone increases the duration and glowstone dust increases the amp level.
   * 4. Modifying the type of a potion. Gunpowder converts from normal -> splash, dragon's breath converts from splash -> lingering.
   * <p>
   * 3 & 4 can be handled via the PotionModifiers. These are helper lambdas that apply a function to an input potion and return
   * an output potion. 1 & 2 are handled directly in the getOutput function itself.
   * <p>
   * The only exception to this is fermented spider eyes which act as both a modifier and an ingredient. It gets a small amount of
   * special handling to make this work.
   */
  public static ItemStack getOutput(SkillBrewing brewing, @Nonnull ItemStack input, @Nonnull ItemStack ingredientStack) {
    if (input.isEmpty() || input.getCount() != 1 || ingredientStack.isEmpty() || !SkrimPotionUtils.isPotion(input))
      return ItemStack.EMPTY;

    PotionType potionType = PotionUtils.getPotionTypeFromNBT(input.getTagCompound());
    Item ingredient = ingredientStack.getItem();

    // Water -> Awkward Potion.
    if (ingredient == Items.NETHER_WART && potionType == PotionTypes.WATER) {
      ItemStack newPotion = SkrimPotionUtils.convertPotion(input);
      PotionUtils.addPotionToItemStack(newPotion, PotionTypes.AWKWARD);
      return newPotion;
    }

    // Apply potion modifiers.
    if (POTION_MODIFIERS.containsKey(ingredient)) {
      // We only want to treat fermented spider_eye as a modifier IF the potion type isn't awkward.
      if (ingredient == Items.FERMENTED_SPIDER_EYE) {
        if (PotionUtils.getPotionTypeFromNBT(input.getTagCompound()) != PotionTypes.AWKWARD)
          return POTION_MODIFIERS.get(ingredient).apply(input, brewing);
      } else {
        return POTION_MODIFIERS.get(ingredient).apply(input, brewing);
      }
    }

    // Add effects to the potion.
    if (INGREDIENT_EFFECTS.containsKey(ingredient)) {
      // Brewing only works with pufferfish, which unfortunately is still coded as metadata until mc 1.13.
      if (ingredient == Items.FISH && ingredientStack.getMetadata() != ItemFishFood.FishType.PUFFERFISH.getMetadata())
        return ItemStack.EMPTY;

      ItemStack newPotion = SkrimPotionUtils.convertPotion(input);
      NBTTagCompound compound = newPotion.getTagCompound();
      if (compound == null) return ItemStack.EMPTY;

      Pair<Potion, Integer> effect_and_duration = INGREDIENT_EFFECTS.get(ingredient);
      Potion potion = effect_and_duration.getKey();
      Integer duration = effect_and_duration.getValue();
      List<PotionEffect> effects = PotionUtils.getEffectsFromStack(newPotion);
      // Only allow extra effects on potions if the brewing level is high.
      if (effects.size() >= 2 && !brewing.hasAbility(4)) return ItemStack.EMPTY;
      if (effects.size() == 1 && !brewing.hasAbility(1)) return ItemStack.EMPTY;

      // We only want to add the effect if the potion doesn't have it already.
      for (PotionEffect effect : effects) {
        if (effect.getPotion() == potion) return ItemStack.EMPTY;
      }

      NBTTagList list = compound.getTagList("CustomPotionEffects", 10);
      list.appendTag(new PotionEffect(potion, duration).writeCustomPotionEffectToNBT(new NBTTagCompound()));
      compound.setTag("CustomPotionEffects", list);
      compound.setString("Potion", "Skrim");

      return newPotion;
    }

    return ItemStack.EMPTY;
  }

  /**
   * This is a hack to get the brewing UI to work correctly. You're only allowed to put valid potion stacks into the slots of a brewing
   * entity. Valid stacks are determined by the registered recipes for forge. So we register a recipe that treats skrim potion as a valid
   * input. The actual output of the recipe is irrelevant since we've overwritten all of the brewing logic.
   */
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
      return ItemStack.EMPTY;
    }
  }

}
