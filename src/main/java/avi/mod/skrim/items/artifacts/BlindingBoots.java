package avi.mod.skrim.items.artifacts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import avi.mod.skrim.items.ModItems;
import avi.mod.skrim.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

public class BlindingBoots extends ArtifactArmor {

	public BlindingBoots() {
		super("boots_of_blinding_speed", EntityEquipmentSlot.FEET);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean par4) {
		tooltip.add("§4Gain absurdly enhanced speed.§r");
		tooltip.add("§4Gain blindness and naueseua.§r");
		tooltip.add("§e\"Move fast and break shit.\"");
	}

	/**
	 * Handlers for boots of blinding speed
	 */
	public static class BlindingBootsHandler {

		public static List<Potion> effects = new ArrayList<Potion>();
		public static Map<Potion, Integer> effectStrength = new HashMap<Potion, Integer>();
		static {
			effects.add(MobEffects.NAUSEA);
			effectStrength.put(MobEffects.NAUSEA, 2);

			effects.add(MobEffects.BLINDNESS);
			effectStrength.put(MobEffects.BLINDNESS, 2);

			effects.add(MobEffects.SPEED);
			effectStrength.put(MobEffects.SPEED, 10);
		}


		public static void goFast(LivingUpdateEvent event) {
			Entity entity = event.getEntity();
			if (entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entity;
				if (player.worldObj.getTotalWorldTime() % 60L == 0L && !player.worldObj.isRemote) {
					if (Utils.isWearingArmor(player, ModItems.BLINDING_BOOTS)) {
						for (Potion potion : effects) {
							PotionEffect activeEffect = player.getActivePotionEffect(potion);
							PotionEffect newEffect = new PotionEffect(potion, 100, effectStrength.get(potion), true, false);
							if (activeEffect != null) {
								activeEffect.combine(newEffect);
							} else {
								player.addPotionEffect(newEffect);
							}
						}
					}
				}
			}
		}
	}

}
