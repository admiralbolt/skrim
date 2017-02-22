package avi.mod.skrim.items.artifacts;

import java.util.List;
import java.util.UUID;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class HorseSteroidSpeed extends HorseSteroid {

	public HorseSteroidSpeed() {
		super("speed");
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean par4) {
		tooltip.add("§4\"Inject your horse for a speed boost.\"§r");
		tooltip.add("§e\"Sir, do you know how fast you were going?\"§r");
	}

	@Override
	public IAttribute getTargetAttribute(EntityHorse horse) {
		return SharedMonsterAttributes.MOVEMENT_SPEED;
	}

	@Override
	public AttributeModifier getAttributeModifier(EntityHorse horse) {
		return new AttributeModifier(UUID.fromString("5F0F7063-0905-4D86-B020-A13DEDCF4EE9"), "skrim-steroid-speed", (double) 1.2, 0);
	}

}
