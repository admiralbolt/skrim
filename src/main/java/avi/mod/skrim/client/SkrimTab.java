package avi.mod.skrim.client;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.items.ModItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class SkrimTab extends CreativeTabs {

  public SkrimTab() {
    super(Skrim.MOD_ID);
  }

  @Override
  @Nonnull
  public ItemStack getTabIconItem() {
    return new ItemStack(ModItems.POWER_SUIT_CHESTPLATE);
  }

  @Override
  public boolean hasSearchBar() {
    return true;
  }

}
