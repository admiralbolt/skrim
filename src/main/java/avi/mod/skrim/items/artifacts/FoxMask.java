package avi.mod.skrim.items.artifacts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import avi.mod.skrim.items.ModItems;
import avi.mod.skrim.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

public class FoxMask extends ArtifactArmor {

	public FoxMask() {
		super("fox_mask", EntityEquipmentSlot.HEAD);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean par4) {
		tooltip.add("§4Increase jump height, speed, and grants night vision.§r");
		tooltip.add("§4Sneaking grants invisibility.§r");
		tooltip.add("§e\"I'll wear my guy fox mask.\"");
	}

	/**
	 * Handlers for boots of springheel jack
	 */
	public static class FoxHandler {

		public static List<Potion> effects = new ArrayList<Potion>();
		public static Map<Potion, Integer> effectStrength = new HashMap<Potion, Integer>();
		static {
			effects.add(MobEffects.JUMP_BOOST);
			effectStrength.put(MobEffects.JUMP_BOOST, 1);

			effects.add(MobEffects.NIGHT_VISION);
			effectStrength.put(MobEffects.NIGHT_VISION, 0);

			effects.add(MobEffects.SPEED);
			effectStrength.put(MobEffects.SPEED, 1);
		}

		public static void beAFox(LivingUpdateEvent event) {
			Entity entity = event.getEntity();
			if (entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entity;
				if (Utils.isWearingArmor(player, ModItems.FOX_MASK)) {
					if (player.world.getTotalWorldTime() % 60L == 0L && !player.world.isRemote) {
						for (Potion potion : effects) {
							PotionEffect newEffect = new PotionEffect(potion, 80, effectStrength.get(potion), true, false);
							Utils.addOrCombineEffect(player, newEffect);
						}
					}
				}
				if (!player.world.isRemote && player.isSneaking()) {
					PotionEffect activeEffect = player.getActivePotionEffect(MobEffects.INVISIBILITY);
					PotionEffect newEffect = new PotionEffect(MobEffects.INVISIBILITY, 3, 0, true, false);
					Utils.addOrCombineEffect(player, newEffect);
				}
			}
		}

	}

}
