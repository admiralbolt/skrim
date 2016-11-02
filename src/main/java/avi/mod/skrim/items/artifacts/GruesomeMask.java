package avi.mod.skrim.items.artifacts;

import java.util.List;

import avi.mod.skrim.items.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class GruesomeMask extends ArtifactArmor {
	
	public GruesomeMask() {
		super("gruesome_mask", EntityEquipmentSlot.HEAD);
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean par4) {
		tooltip.add("§4Deal double damage while worn.§r");
		tooltip.add("§4Recieve double damage while worn.§r");
		tooltip.add("§e\"Blood begets blood.\"");
	}
	
	/**
	 * Handlers for boots of springheel jack
	 */
	public static class GruesomeHandler {
		
		public static void doubleAllDamage(LivingHurtEvent event) {
			DamageSource source = event.getSource();
			Entity dealing = source.getEntity();
			Entity receiving = event.getEntity();
			// In case we want to balance this in the future,
			// leave each one explicit.
			if (receiving instanceof EntityPlayer) {
				EntityPlayer playerReceiving = (EntityPlayer) receiving;
				if (ArtifactUtils.isWearingArmor(playerReceiving, ModItems.GRUESOME_MASK)) {
					event.setAmount(event.getAmount() * 2);
				}
			}
			if (dealing instanceof EntityPlayer) {
				EntityPlayer playerDealing = (EntityPlayer) dealing;
				if (ArtifactUtils.isWearingArmor(playerDealing, ModItems.GRUESOME_MASK)) {
					event.setAmount(event.getAmount() * 2);
				}
			}
		}
		
	}

}
