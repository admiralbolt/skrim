package avi.mod.skrim.items.artifacts;

import java.util.List;

import avi.mod.skrim.client.audio.ShineSparkSound;
import avi.mod.skrim.items.ModItems;
import avi.mod.skrim.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

public class PowerSuitChestplate extends ArtifactArmor {
	
	public int sprintingTicks = 0;
	public boolean spark = false;

	public PowerSuitChestplate() {
		super("powersuit_chestplate", EntityEquipmentSlot.CHEST);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean par4) {
		tooltip.add("§4Oh fuck yeah.§r");
		tooltip.add("§e\"Time and reality swirl together like estuary waters.\"");
	}

	public static void applyChozoTech(LivingUpdateEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if (player.world.isRemote && Utils.isWearingArmor(player, ModItems.POWER_SUIT_CHESTPLATE)) {
				ItemStack armorStack = Utils.getArmor(player, EntityEquipmentSlot.CHEST);
				Item armor = armorStack.getItem();
				if (armor instanceof PowerSuitChestplate) {
					PowerSuitChestplate chozoChest = (PowerSuitChestplate) armor;
					// Shine spark
					// System.out.println("player.isSprinting(): " + player.isSprinting() + ", onGround: " + player.onGround + ", isSpark: " + chozoChest.spark);
					if (player.isSprinting() && chozoChest.sprintingTicks >= 100) {
						if (!chozoChest.spark) {
							chozoChest.spark = true;
							Minecraft.getMinecraft().getSoundHandler().playSound(new ShineSparkSound(player));
							System.out.println("creating new ShineSparkSound()");
						}
						if (player.onGround) {
							player.motionX *= 1.75;
							player.motionZ *= 1.75;
						} else {
							player.motionX *= 1.75;
							player.motionZ *= 1.75;
							chozoChest.sprintingTicks = 0;
						}
					} else if (player.isSprinting() && player.onGround) {
						chozoChest.sprintingTicks++;
					} else {
						chozoChest.sprintingTicks = 0;
						chozoChest.spark = false;
					}
				}
			}
		}
	}

}
