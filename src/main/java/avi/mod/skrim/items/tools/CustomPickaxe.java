package avi.mod.skrim.items.tools;

import avi.mod.skrim.items.ItemBase;
import net.minecraft.item.ItemPickaxe;

public class CustomPickaxe extends ItemPickaxe implements ItemBase {

  private String name;

  public CustomPickaxe(String name, ToolMaterial material) {
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
