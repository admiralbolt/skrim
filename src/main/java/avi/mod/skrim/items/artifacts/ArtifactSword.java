package avi.mod.skrim.items.artifacts;

import avi.mod.skrim.items.weapons.CustomSword;
import avi.mod.skrim.items.ModItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ArtifactSword extends CustomSword {

  protected String name;

  public ArtifactSword(String name, ToolMaterial material) {
    super(name, material);
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
    tooltip.add("§4Sweep attack ignites enemies.");
    tooltip.add("§4Deals 20x damage to chickens & fries them.§r");
    tooltip.add("§e\"Chicken chicken chicken, which combo you pickin'?\"");
  }

  @Override
  public int getItemEnchantability() {
    return 0;
  }

  @Override
  public String getTexturePath() {
    return "weapons";
  }
}
