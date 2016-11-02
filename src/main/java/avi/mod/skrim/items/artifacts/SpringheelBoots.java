package avi.mod.skrim.items.artifacts;

import java.util.List;

import avi.mod.skrim.items.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;

public class SpringheelBoots extends ArtifactArmor {

	public SpringheelBoots() {
		super("boots_of_springheel_jak", EntityEquipmentSlot.FEET);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean par4) {
		tooltip.add("§4Massively increases jump height.§r");
		tooltip.add("§4Prevents all fall damage.§r");
		tooltip.add("§e\"Falco mode engaged.\"");
	}

	/**
	 * Handlers for boots of springheel jack
	 */
	public static class SpringheelHandler {

		public static void jumpHigh(LivingEvent.LivingJumpEvent event) {
			Entity entity = event.getEntity();
			if (entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entity;
				if (player.worldObj.isRemote) {
					if (ArtifactUtils.isWearingArmor(player, ModItems.SPRINGHEEL_BOOTS)) {
						player.motionY *= 3;
						player.setVelocity(player.motionX, player.motionY, player.motionZ);
					}
				}
			}
		}

		public static void preventFallDamage(LivingFallEvent event) {
			Entity entity = event.getEntity();
			if (entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entity;
				if (ArtifactUtils.isWearingArmor(player, ModItems.SPRINGHEEL_BOOTS)) {
					event.setDistance(0);
					event.setCanceled(true);
				}
			}
		}
	}

}
