package avi.mod.skrim.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import avi.mod.skrim.Skrim;

public class ItemBase extends Item implements ItemModelProvider {

  protected String name;

  public ItemBase(String name) {
    this.name = name;
    this.setUnlocalizedName(name);
    this.setRegistryName(name);
    setCreativeTab(Skrim.creativeTab);
  }

  @Override
  public void registerItemModel(Item item) {
    Skrim.proxy.registerItemRenderer(this, 0, name);
  }

  @Override
  public ItemBase setCreativeTab(CreativeTabs tab) {
    super.setCreativeTab(tab);
    return this;
  }

}
