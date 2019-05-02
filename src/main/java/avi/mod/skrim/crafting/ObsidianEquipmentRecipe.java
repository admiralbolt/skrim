package avi.mod.skrim.crafting;

import com.google.gson.JsonObject;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapedOreRecipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class ObsidianEquipmentRecipe extends ShapedOreRecipe {

  public ObsidianEquipmentRecipe(@Nullable final ResourceLocation group, final ItemStack result, final CraftingHelper.ShapedPrimer primer) {
    super(group, result, primer);
  }

  @Override
  @Nonnull
  public ItemStack getCraftingResult(@Nonnull final InventoryCrafting inv) {
    final ItemStack output = super.getCraftingResult(inv);
    
    if (output.isEmpty()) return output;

    ItemStack original = inv.getStackInSlot(4);
    output.setTagCompound(original.getTagCompound());

    double damagePercent = original.getItemDamage() / (double) original.getMaxDamage();
    output.setItemDamage((int) (output.getMaxDamage() * damagePercent));

    return output;
  }

  @Override
  @Nonnull
  public String getGroup() {
    return group == null ? "" : group.toString();
  }

  public static class Factory implements IRecipeFactory {

    @Override
    public IRecipe parse(final JsonContext context, final JsonObject json) {
      final String group = JsonUtils.getString(json, "group", "");
      final CraftingHelper.ShapedPrimer primer = RecipeUtil.parseShaped(context, json);
      final ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);

      return new ObsidianEquipmentRecipe(group.isEmpty() ? null : new ResourceLocation(group), result, primer);
    }
  }

}
