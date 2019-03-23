package avi.mod.skrim.items;

import avi.mod.skrim.Skrim;
import net.minecraft.item.ItemHoe;

public class CustomHoe extends ItemHoe implements ItemBase {

  private String name;

  public CustomHoe(String name, ToolMaterial material) {
    super(material);
    this.name = name;
    this.setUnlocalizedName(name);
    this.setRegistryName(name);
    setCreativeTab(Skrim.creativeTab);
  }

  @Override
  public String getTexturePath() {
    return "tools";
  }
}
