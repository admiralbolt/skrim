package avi.mod.skrim.items.artifacts;

import java.util.List;
import java.util.UUID;

import avi.mod.skrim.utils.Obfuscation;
import avi.mod.skrim.utils.Reflection;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class HorseSteroidJump extends HorseSteroid {
	
	public HorseSteroidJump() {
		super("jump");
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean par4) {
		tooltip.add("§4\"Inject your horse for a jump boost.\"§r");
		tooltip.add("§e\"Hey diddle diddle, the cat and the fiddle, the horse jumped over the moon.\"§r");
	}

	@Override
	public IAttribute getTargetAttribute(EntityHorse horse) {
		Object horsey = Reflection.getSuperPrivateField(horse, Obfuscation.HORSE_JUMP_ATTRIBUTE.getFieldNames());
		if (horsey instanceof IAttribute) {
			return (IAttribute) horsey;
		} else {
			return null;
		}
	}

	@Override
	public AttributeModifier getAttributeModifier(EntityHorse horse) {
		return new AttributeModifier(UUID.fromString("5F0F7063-0905-4D86-B020-A13DEDCF4EE9"), "skrim-steroid-jump", (double) 2, 0);
	}

}
