package avi.mod.skrim.items.artifacts;

import java.util.List;

import avi.mod.skrim.client.audio.ShineSparkSound;
import avi.mod.skrim.items.ModItems;
import avi.mod.skrim.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PowerSuitChestplate extends ArtifactArmor {
	
	public int sprintingTicks = 0;
	public boolean spark = false;

	public PowerSuitChestplate() {
		super("powersuit_chestplate", EntityEquipmentSlot.CHEST);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean par4) {
		tooltip.add("§4Sprinting for an extended duration activates your speed booster.§r");
		tooltip.add("§e\"Time and reality swirl together like estuary waters.\"");
	}
	
	@SideOnly(Side.CLIENT)
	public void playShineSpark(EntityPlayer player) {
		Minecraft.getMinecraft().getSoundHandler().playSound(new ShineSparkSound(player));
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
					if (player.isSprinting() && chozoChest.sprintingTicks >= 100) {
						if (!chozoChest.spark) {
							chozoChest.spark = true;
							chozoChest.playShineSpark(player);
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
