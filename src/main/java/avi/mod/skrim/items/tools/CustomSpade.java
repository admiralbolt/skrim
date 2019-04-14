package avi.mod.skrim.items.tools;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.items.ItemBase;
import net.minecraft.item.ItemSpade;

public class CustomSpade extends ItemSpade implements ItemBase {

  private String name;

  public CustomSpade(String name, ToolMaterial material) {
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
