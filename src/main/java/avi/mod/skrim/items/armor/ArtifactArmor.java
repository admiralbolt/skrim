package avi.mod.skrim.items.armor;

import avi.mod.skrim.items.SkrimItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Artifact Armor!
 */
public class ArtifactArmor extends CustomArmor {

  public static ArmorMaterial POWERSUIT_MATERIAL = getAndCreateMaterial("powersuit_armor");
  public static ArmorMaterial DWARVEN_MATERIAL = getAndCreateMaterial("dwarven_armor");

  protected String name;

  public ArtifactArmor(String name, EntityEquipmentSlot armorType) {
    this(name, getAndCreateMaterial(name), armorType);
  }

  public ArtifactArmor(String name, ItemArmor.ArmorMaterial material, EntityEquipmentSlot armorType) {
    super(name, material, 1, armorType);
  }

  /**
   * A separate material is created for each artifact armor piece to render a separate model for each armor piece.
   * Model rendering is done based on the armor material NAME, so a separate material is created.
   */
  private static ArmorMaterial getAndCreateMaterial(String name) {
    return EnumHelper.addArmorMaterial(name, "skrim:" + name, 50, new int[]{3, 8, 6, 3}, 0, null, 0.0F);
  }

  @Override
  public boolean hasEffect(ItemStack stack) {
    return true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  @Nonnull
  public EnumRarity getRarity(ItemStack stack) {
    return SkrimItems.ARTIFACT_RARITY;
  }

  /**
   * This should be overriden in the actual artifact class itself.
   */
  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    tooltip.add("Custom tooltip");
  }

  /**
   * Artifacts can't be enchanted, they're already pretty cool.
   */
  @Override
  public int getItemEnchantability() {
    return 0;
  }

}
