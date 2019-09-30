package avi.mod.skrim.crafting;

import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.skills.brewing.SkrimPotionUtils;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class RecipeSkrimTippedArrow extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

  /**
   * We want either a skrim lingering potion OR a normal lingering potion in the center, and arrow in all other slots of the crafting
   * inventory.
   */
  public boolean matches(InventoryCrafting inv, @Nonnull World worldIn) {
    if (inv.getWidth() != 3 || inv.getHeight() != 3) return false;

    ItemStack potion = inv.getStackInRowAndColumn(1, 1);
    if (potion.isEmpty()) return false;

    Item potionItem = potion.getItem();
    if (potionItem != Items.LINGERING_POTION && potionItem != SkrimItems.LINGERING_SKRIM_POTION) return false;

    for (int i = 0; i < inv.getWidth(); ++i) {
      for (int j = 0; j < inv.getHeight(); ++j) {
        if (i == 1 && j == 1) continue;
        ItemStack itemstack = inv.getStackInRowAndColumn(i, j);

        if (itemstack.isEmpty() || itemstack.getItem() != Items.ARROW) {
          return false;
        }
      }
    }

    return true;
  }

  @Nonnull
  public ItemStack getCraftingResult(InventoryCrafting inv) {
    // We want to convert the potion to a skrim potion first to avoid having to handle vanilla vs. skrim potions.
    ItemStack potion = SkrimPotionUtils.convertPotion(inv.getStackInRowAndColumn(1, 1));
    ItemStack arrow = new ItemStack(SkrimItems.SKRIM_TIPPED_ARROW, 8);

    PotionUtils.addPotionToItemStack(arrow, PotionUtils.getPotionFromItem(potion));
    PotionUtils.appendEffects(arrow, PotionUtils.getFullEffectsFromItem(potion));
    return arrow;
  }

  @Nonnull
  public ItemStack getRecipeOutput() {
    return ItemStack.EMPTY;
  }

  @Nonnull
  public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
    return NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
  }

  public boolean isDynamic() {
    return true;
  }

  /**
   * Used to determine if this recipe can fit in a grid of the given width/height
   */
  public boolean canFit(int width, int height) {
    return width >= 2 && height >= 2;
  }
}
