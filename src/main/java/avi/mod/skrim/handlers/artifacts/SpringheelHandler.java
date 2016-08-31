package avi.mod.skrim.handlers.artifacts;

import avi.mod.skrim.items.ModItems;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SpringheelHandler {

	@SubscribeEvent
	public void onJump(LivingEvent.LivingJumpEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if (player.worldObj.isRemote) {
				InventoryPlayer inventory = player.inventory;
				if (inventory != null) {
					ItemStack stack = inventory.armorInventory[0];
					if (stack != null) {
						Item boots = stack.getItem();
						if (boots == ModItems.bootsOfSpringheelJak) {
							player.motionY *= 3;
							player.setVelocity(player.motionX, player.motionY, player.motionZ);
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onFallEvent(LivingFallEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			InventoryPlayer inventory = player.inventory;
			if (inventory != null) {
				ItemStack stack = inventory.armorInventory[0];
				if (stack != null) {
					Item boots = stack.getItem();
					if (boots == ModItems.bootsOfSpringheelJak) {
						event.setDistance(0);
						event.setCanceled(true);
					}
				}
			}
		}
	}

}
