package avi.mod.skrim.items.artifacts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import avi.mod.skrim.init.SkrimSoundEvents;
import avi.mod.skrim.utils.Reflection;
import avi.mod.skrim.utils.Utils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public abstract class HorseSteroid extends ArtifactItem {

	private String attrType;

	public HorseSteroid(String attrType) {
		super("horse_steroid_" + attrType);
		this.attrType = attrType;
		this.setMaxDamage(1);
	}

	public abstract IAttribute getTargetAttribute(EntityHorse horse);

	public abstract AttributeModifier getAttributeModifier(EntityHorse horse);

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
		if (target instanceof EntityHorse) {
			EntityHorse horse = (EntityHorse) target;
			if (!playerIn.world.isRemote) {
				Map<IAttribute, AttributeModifier> attributeMap = new HashMap<IAttribute, AttributeModifier>();
				attributeMap.put(this.getTargetAttribute(horse), this.getAttributeModifier(horse));
				Utils.applyAttributesModifiersToEntity(horse, attributeMap, 0);
				stack.damageItem(2, playerIn);
				BlockPos horsePos = horse.getPosition();
				playerIn.world.playSound((EntityPlayer) null, horsePos.getX(), horsePos.getY(), horsePos.getZ(), SoundEvents.ENTITY_HORSE_ANGRY,
						horse.getSoundCategory(), 1.0F, 1.0F);
			}
			return true;
		}
		return false;
	}

}
