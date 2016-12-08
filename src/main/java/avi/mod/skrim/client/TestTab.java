package avi.mod.skrim.client;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import avi.mod.skrim.Skrim;
import avi.mod.skrim.items.ModItems;

public class TestTab extends CreativeTabs {

  public TestTab() {
    super(Skrim.modId);
  }

  @Override
  public ItemStack getTabIconItem() {
    return new ItemStack(ModItems.TUX);
  }

  @Override
  public boolean hasSearchBar() {
    return true;
  }

}
