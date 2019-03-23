package avi.mod.skrim.items.artifacts;

import java.util.List;

import javax.annotation.Nullable;

import avi.mod.skrim.items.ModItems;
import avi.mod.skrim.utils.Utils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class GruesomeMask extends ArtifactArmor {
	
	public GruesomeMask() {
		super("gruesome_mask", EntityEquipmentSlot.HEAD);
	}
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
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
			Entity dealing = source.getTrueSource();
			Entity receiving = event.getEntity();
			// In case we want to balance this in the future,
			// leave each one explicit.
			if (receiving instanceof EntityPlayer) {
				EntityPlayer playerReceiving = (EntityPlayer) receiving;
				if (Utils.isWearingArmor(playerReceiving, ModItems.GRUESOME_MASK)) {
					event.setAmount(event.getAmount() * 2);
				}
			}
			if (dealing instanceof EntityPlayer) {
				EntityPlayer playerDealing = (EntityPlayer) dealing;
				if (Utils.isWearingArmor(playerDealing, ModItems.GRUESOME_MASK)) {
					event.setAmount(event.getAmount() * 2);
				}
			}
		}
		
	}

}
