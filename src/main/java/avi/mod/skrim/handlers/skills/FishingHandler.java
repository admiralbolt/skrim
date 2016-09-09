package avi.mod.skrim.handlers.skills;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.fishing.SkillFishing;
import avi.mod.skrim.items.CustomFishingRod;
import avi.mod.skrim.items.ModItems;

public class FishingHandler {

	@SubscribeEvent
	public void onItemPickup(EntityItemPickupEvent event) {
		EntityPlayer player = event.getEntityPlayer();
		if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.FISHING, EnumFacing.NORTH)) {
			SkillFishing fishing = (SkillFishing) player.getCapability(Skills.FISHING, EnumFacing.NORTH);
			EntityItem eitem = event.getItem();
			ItemStack stack = eitem.getEntityItem();
			Item item = stack.getItem();
			if (item == Items.FISHING_ROD) {
				stack.setItem(ModItems.fishingRod);
			}
		}
	}
	
	@SubscribeEvent
	public void onCraftRod(ItemCraftedEvent event) {
		EntityPlayer player = event.player;
		if (player != null && player.hasCapability(Skills.FISHING, EnumFacing.NORTH)) {
			Item item = event.crafting.getItem();
			if (item == Items.FISHING_ROD) {
				event.crafting.setItem(ModItems.fishingRod);
			}
		}
		
	}

}
