package avi.mod.skrim.items.artifacts;

import avi.mod.skrim.items.items.ArtifactItem;
import avi.mod.skrim.utils.Utils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for horse steroids.
 *
 * Steroids modify the base stats of horses and come in a variety of flavors.
 */
public abstract class HorseSteroid extends ArtifactItem {

  public HorseSteroid(String attrName) {
    super("horse_steroid_" + attrName);
    this.setMaxDamage(1);
  }

  public abstract IAttribute getTargetAttribute(EntityHorse horse);

  public abstract AttributeModifier getAttributeModifier(EntityHorse horse);

  @Override
  public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
    if (!(target instanceof EntityHorse)) return false;

    EntityHorse horse = (EntityHorse) target;
    if (playerIn.world.isRemote) return true;

    Map<IAttribute, AttributeModifier> attributeMap = new HashMap<>();
    attributeMap.put(this.getTargetAttribute(horse), this.getAttributeModifier(horse));
    Utils.applyAttributesModifiersToEntity(horse, attributeMap, 0);
    stack.damageItem(2, playerIn);
    BlockPos horsePos = horse.getPosition();
    playerIn.world.playSound(null, horsePos.getX(), horsePos.getY(), horsePos.getZ(), SoundEvents.ENTITY_HORSE_ANGRY,
        horse.getSoundCategory(), 1.0F, 1.0F);
    return true;
  }

}
