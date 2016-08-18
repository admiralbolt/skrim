package avi.mod.skrim.items;

import avi.mod.skrim.Skrim;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ModItems {

  public static ItemBase testItem;

  public static void createItems() {
    testItem = register(new ItemBase("test_item").setCreativeTab(CreativeTabs.MATERIALS));
  }

	private static <T extends Item> T register(T item) {
   GameRegistry.register(item);
		if (item instanceof ItemModelProvider) {
			((ItemModelProvider) item).registerItemModel(item);
		}
		return item;
	}

}
