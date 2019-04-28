package avi.mod.skrim.items.tools;

import avi.mod.skrim.items.SkrimItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ArtifactPickaxe extends CustomPickaxe {

  public ArtifactPickaxe(String name, ToolMaterial material) {
    super(name, material);
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

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
  }

  @Override
  public int getItemEnchantability() {
    return 0;
  }


}
