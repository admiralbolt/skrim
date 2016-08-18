package avi.mod.skrim.client;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import avi.mod.skrim.Skrim;
import avi.mod.skrim.items.ModItems;

public class TestTab extends CreativeTabs {

  public TestTab() {
    super(Skrim.modId);
  }

  @Override
  public Item getTabIconItem() {
    return ModItems.testItem;
  }

  @Override
  public boolean hasSearchBar() {
    return true;
  }

}
