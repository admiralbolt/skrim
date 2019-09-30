package avi.mod.skrim.init;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.crafting.RecipeSkrimTippedArrow;
import net.minecraft.init.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistryModifiable;

@Mod.EventBusSubscriber(modid = Skrim.MOD_ID)
public class SkrimRecipes {

  @SubscribeEvent
  public static void editRecipes(RegistryEvent.Register<IRecipe> event) {
    IForgeRegistryModifiable registry = (IForgeRegistryModifiable) event.getRegistry();
    // Delete the recipe for the brewing stand. We are replacing the default brewing stand with a custom copy, with a recipe defined in
    // skrim_brewing_stand_item.json.
    registry.remove(Items.BREWING_STAND.getRegistryName());

    // Delete the recipe for the bow. We are replacing the default bow with a custom copy, defined in skrim_bow.json.
    registry.remove(Items.BOW.getRegistryName());

    // Remove the default tipped arrow recipe with a custom one that functions with skrim potions OR vanilla potions. Both versions of these
    // recipes are not JSON, but are custom IRecipe definitions to handle all potions.
    registry.remove(new ResourceLocation("minecraft", "tippedarrow"));
    event.getRegistry().register(new RecipeSkrimTippedArrow().setRegistryName("skrim_tipped_arrow"));
  }
}
