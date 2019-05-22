package avi.mod.skrim.init;

import avi.mod.skrim.Skrim;
import net.minecraft.init.Items;
import net.minecraft.item.crafting.IRecipe;
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
    registry.remove(Items.BOW.getRegistryName());
  }
}
