package avi.mod.skrim.items.tools;

import avi.mod.skrim.items.ItemBase;
import net.minecraft.item.ItemHoe;

public class CustomHoe extends ItemHoe implements ItemBase {

  private String name;

  public CustomHoe(String name, ToolMaterial material) {
    super(material);
    this.name = name;
    this.setUnlocalizedName(name);
    this.setRegistryName(name);
  }

  @Override
  public String getTexturePath() {
    return "tools";
  }

}
