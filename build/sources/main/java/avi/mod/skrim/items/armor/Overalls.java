package avi.mod.skrim.items.armor;

        import avi.mod.skrim.items.CustomArmor;
        import net.minecraft.inventory.EntityEquipmentSlot;
        import net.minecraft.item.ItemArmor;
        import net.minecraftforge.common.util.EnumHelper;

public class Overalls extends CustomArmor {

  public static ItemArmor.ArmorMaterial OVERALLS_ARMOR = EnumHelper.addArmorMaterial("overalls", "skrim:overalls", 10
          , new int[]{1, 3, 2, 1}, 15, null, 0.0F);


  public Overalls() {
    super("overalls", OVERALLS_ARMOR, 3, EntityEquipmentSlot.CHEST);
  }
}
