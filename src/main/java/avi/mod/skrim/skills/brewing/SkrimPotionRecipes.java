package avi.mod.skrim.skills.brewing;

import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.skills.Skills;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
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

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;

public class SkrimPotionRecipes {

  private static Set<Item> ALL_POTION_ITEMS = ImmutableSet.of(Items.POTIONITEM, Items.SPLASH_POTION,
      Items.LINGERING_POTION, SkrimItems.SKRIM_POTION, SkrimItems.SPLASH_SKRIM_POTION,
      SkrimItems.LINGERING_SKRIM_POTION);

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

  public static Map<ItemStack, PotionModifier> POTION_MODIFIERS = ImmutableMap.<ItemStack, PotionModifier>builder()
      .put(new ItemStack(Items.GLOWSTONE_DUST), PotionModifier.INCREASED_STRENGTH)
      .put(new ItemStack(Items.REDSTONE), PotionModifier.INCREASED_DURATION)
      .put(new ItemStack(Items.GUNPOWDER), PotionModifier.BASE_TO_SPLASH)
      .put(new ItemStack(Items.DRAGON_BREATH), PotionModifier.SPLASH_TO_LINGERING)
      .build();

  public static boolean hasOutput(EntityPlayer player, @Nonnull ItemStack input, @Nonnull ItemStack ingredientStack) {
    return getOutput(player, input, ingredientStack) != ItemStack.EMPTY;
  }

  public static ItemStack getOutput(EntityPlayer player, @Nonnull ItemStack input, @Nonnull ItemStack ingredientStack) {
    if (input.isEmpty() || input.getCount() != 1 || ingredientStack.isEmpty() || !ALL_POTION_ITEMS.contains(input.getItem()))
      return ItemStack.EMPTY;


    PotionType potionType = PotionUtils.getPotionTypeFromNBT(input.getTagCompound());
    SkillBrewing brewing = Skills.getSkill(player, Skills.BREWING, SkillBrewing.class);
    Item ingredient = ingredientStack.getItem();

    // Water -> Awkward Potion.
    if (ingredient == Items.NETHER_WART && potionType == PotionTypes.WATER) return SkrimPotionHelper.convertPotion(input);

    // Conversions
    if (POTION_MODIFIERS.containsKey(ingredientStack)) {
      return POTION_MODIFIERS.get(ingredientStack).apply(input, brewing);
    }

    if (potionType == PotionTypes.AWKWARD)


      return ItemStack.EMPTY;
  }

}
