package avi.mod.skrim.skills.brewing;

import avi.mod.skrim.items.SkrimItems;
import net.minecraft.item.ItemStack;

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
    ItemStack newPotion = new ItemStack(SkrimItems.SPLASH_SKRIM_POTION);
    newPotion.setTagCompound(input.getTagCompound());
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
