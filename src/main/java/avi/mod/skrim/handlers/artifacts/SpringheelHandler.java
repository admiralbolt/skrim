package avi.mod.skrim.handlers.artifacts;

import avi.mod.skrim.items.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SpringheelHandler {
	
	@SubscribeEvent
	public void onJump(LivingEvent.LivingJumpEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			ItemStack stack = player.inventory.armorItemInSlot(0);
			if (stack != null) {
				Item boots = stack.getItem();
				if (boots == ModItems.bootsOfSpringheelJak) {
					player.motionY *= 3;
					player.setVelocity(player.motionX, player.motionY, player.motionZ);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onFallEvent(LivingFallEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			ItemStack stack = player.inventory.armorItemInSlot(0);
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
