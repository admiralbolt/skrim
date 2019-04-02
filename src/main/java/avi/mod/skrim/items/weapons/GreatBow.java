package avi.mod.skrim.items.weapons;

import avi.mod.skrim.Skrim;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class GreatBow extends CustomBow {

	public GreatBow() {
		super("great_bow", 40.0F, 1.5F);
	}

	public ModelResourceLocation getModel(ItemStack stack, EntityPlayer player, int useRemaining) {
		ModelResourceLocation resource = new ModelResourceLocation(Skrim.MOD_ID + ":great_bow", "inventory");
		if (stack.getItem() == this && player.getItemInUseCount() > 0) {
			if (useRemaining >= 18) {
				resource = new ModelResourceLocation(Skrim.MOD_ID + ":custom_bow_pull2", "inventory");
			} else if (useRemaining > 13) {
				resource = new ModelResourceLocation(Skrim.MOD_ID + ":custom_bow_pull1", "inventory");
			} else if (useRemaining > 0) {
				resource = new ModelResourceLocation(Skrim.MOD_ID + ":custom_bow_pull0", "inventory");
			}
		}
		return resource;
	}

}
