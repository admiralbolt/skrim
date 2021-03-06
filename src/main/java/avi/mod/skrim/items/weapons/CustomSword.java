package avi.mod.skrim.items.weapons;

import avi.mod.skrim.items.ItemBase;
import net.minecraft.item.ItemSword;

public class CustomSword extends ItemSword implements ItemBase {

  private String name;

  public CustomSword(String name, ToolMaterial material) {
    super(material);
    this.name = name;
    this.setUnlocalizedName(name);
    this.setRegistryName(name);
  }

  @Override
  public String getTexturePath() {
    return "weapons";
  }

}
