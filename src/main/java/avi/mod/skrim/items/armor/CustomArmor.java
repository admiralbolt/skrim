package avi.mod.skrim.items.armor;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.items.ItemBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;

/**
 * Helper class for custom armors. Little base functionality besides correct names in the registry and overriding the default texture path.
 */
public class CustomArmor extends ItemArmor implements ItemBase {

  protected String name;

  public CustomArmor(String name, ArmorMaterial material, int renderIndex, EntityEquipmentSlot armorType) {
    super(material, renderIndex, armorType);
    this.name = name;
    this.setUnlocalizedName(name);
    this.setRegistryName(name);
  }

  @Override
  public String getTexturePath() {
    return "armor";
  }
}
