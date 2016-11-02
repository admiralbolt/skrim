package avi.mod.skrim.items.artifacts;

import avi.mod.skrim.items.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ArtifactUtils {
	
	public static boolean isWearingArmor(EntityPlayer player, ArtifactArmor armor) {
		InventoryPlayer inventory = player.inventory;
		if (inventory != null) {
			ItemStack stack = inventory.armorInventory[armor.armorType.getIndex()];
			if (stack != null) {
				Item targetItem = stack.getItem();
				if (targetItem == armor) {
					return true;
				}
			}
		}
		return false;
	}

}
