package avi.mod.skrim.items.artifacts;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class HorseSteroidHealth extends HorseSteroid {
	
	public HorseSteroidHealth() {
		super("health");
	}
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add("§4\"Inject your horse for a health boost.\"§r");
		tooltip.add("§e\"Skrim® modding industries does not support performance enhancing drugs for humans.\"§r");
	}

	@Override
	public IAttribute getTargetAttribute(EntityHorse horse) {
		return SharedMonsterAttributes.MAX_HEALTH;
	}

	@Override
	public AttributeModifier getAttributeModifier(EntityHorse horse) {
		return new AttributeModifier(UUID.fromString("5B1414DE-7621-4C51-8F11-9E216057F157"), "skrim-steroid-health", (double) 100, 0);
	}

}
