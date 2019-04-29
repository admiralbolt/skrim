package avi.mod.skrim.items.artifacts;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.items.ItemBase;
import avi.mod.skrim.items.SkrimItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Enchiridion extends ItemEnchantedBook implements ItemBase {

  private final String name = "enchiridion";

  public Enchiridion() {
    this.setUnlocalizedName(name);
    this.setRegistryName(name);
    this.setMaxStackSize(1);
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    tooltip.add("§4Contains ALL enchantments at MAX level.§r");
    tooltip.add("§e\"Chapter IV: Didactic Padding That The Hero Should Duly Ignore.\"");
  }

  @Override
  @Nonnull
  @SideOnly(Side.CLIENT)
  public EnumRarity getRarity(ItemStack stack) {
    return SkrimItems.ARTIFACT_RARITY;
  }

  @Override
  public String getTexturePath() {
    return "items";
  }

  @Override
  public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems) {
    ItemStack book = new ItemStack(SkrimItems.ENCHIRIDION);
    for (Enchantment enchantment : Enchantment.REGISTRY) {
      if (enchantment.isCurse()) continue;

      addEnchantment(book, new EnchantmentData(enchantment, enchantment.getMaxLevel()));
    }

    subItems.add(book);
  }

  @Mod.EventBusSubscriber(modid = Skrim.MOD_ID)
  public static class EnchiridionHandler {

    @SubscribeEvent
    public static void applyEnchantments(AnvilUpdateEvent event) {
      ItemStack book = event.getRight();
      if (book.getItem() != SkrimItems.ENCHIRIDION) return;

      ItemStack input = event.getLeft();
      if (input.isEmpty()) return;

      Map<Enchantment, Integer> finalEnchantments = new HashMap<>();

      for (Enchantment enchantment : Enchantment.REGISTRY) {
        if (enchantment.isCurse() || !enchantment.canApply(input)) continue;

        finalEnchantments.put(enchantment, enchantment.getMaxLevel());
      }

      if (finalEnchantments.size() == 0) return;

      event.setCost(39);

      ItemStack output = input.copy();
      EnchantmentHelper.setEnchantments(finalEnchantments, output);
      event.setOutput(output);
    }

  }


}
