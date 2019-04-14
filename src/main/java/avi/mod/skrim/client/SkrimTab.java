package avi.mod.skrim.client;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.items.SkrimItems;
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
    return new ItemStack(SkrimItems.POWER_SUIT_CHESTPLATE);
  }

  @Override
  public boolean hasSearchBar() {
    return true;
  }

}
