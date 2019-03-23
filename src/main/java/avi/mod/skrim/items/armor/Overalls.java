package avi.mod.skrim.items.armor;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.common.util.EnumHelper;

public class Overalls extends CustomArmor {

  private static ItemArmor.ArmorMaterial OVERALLS_ARMOR = EnumHelper.addArmorMaterial("overalls", "skrim:overalls", 10
      , new int[]{1, 3, 2, 1}, 15, null, 0.0F);


  public Overalls() {
    super("overalls", OVERALLS_ARMOR, 3, EntityEquipmentSlot.CHEST);
  }
}
