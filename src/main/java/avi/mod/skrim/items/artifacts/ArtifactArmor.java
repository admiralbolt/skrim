package avi.mod.skrim.items.artifacts;

import avi.mod.skrim.items.CustomArmor;
import avi.mod.skrim.items.ModItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ArtifactArmor extends CustomArmor {

  protected String name;

  public ArtifactArmor(String name, EntityEquipmentSlot armorType) {
    this(name, getAndCreateMaterial(name), armorType);
  }

  public ArtifactArmor(String name, ItemArmor.ArmorMaterial material, EntityEquipmentSlot armorType) {
    super(name, material, 1, armorType);
  }

  /**
   * A separate material is created for each artifact armor piece to render a separate model for each armor piece.
   * Model rendering is done based on the armor material name, so a separate material is created.
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
  public EnumRarity getRarity(ItemStack stack) {
    return ModItems.ARTIFACT_RARITY;
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    tooltip.add("Custom tooltip");
  }

  @Override
  public int getItemEnchantability() {
    return 0;
  }

}
