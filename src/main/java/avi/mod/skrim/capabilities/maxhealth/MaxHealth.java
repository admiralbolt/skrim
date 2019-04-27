package avi.mod.skrim.capabilities.maxhealth;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;

import javax.annotation.Nullable;
import java.util.UUID;

public class MaxHealth implements IMaxHealth {

  private static final UUID MODIFIER_ID = UUID.fromString("d5d0d878-b3c2-469b-ba89-ac01c0635a9c");
  private static final String MODIFIER_NAME = "Bonus Max Health";
  private static final int ATTRIBUTE_MODIFIER_OPERATION_ADD = 0;

  private final EntityLivingBase entity;
  private float bonusMaxHealth;

  public MaxHealth(@Nullable EntityLivingBase entity) {
    this.entity = entity;
  }

  @Override
  public final float getBonusMaxHealth() {
    return bonusMaxHealth;
  }

  @Override
  public final void setBonusMaxHealth(float bonusMaxHealth) {
    this.bonusMaxHealth = bonusMaxHealth;
    onBonusMaxHealthChanged();
  }

  @Override
  public final void addBonusMaxHealth(float healthToAdd) {
    setBonusMaxHealth(getBonusMaxHealth() + healthToAdd);
  }

  private void onBonusMaxHealthChanged() {
    if (entity == null) return;

    final IAttributeInstance entityMaxHealthAttribute = entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
    final AttributeModifier oldModifier = entityMaxHealthAttribute.getModifier(MODIFIER_ID);

    float oldAmount = 0;

    if (oldModifier != null) {
      oldAmount = (float) oldModifier.getAmount();
      entityMaxHealthAttribute.removeModifier(oldModifier);
    }

    entityMaxHealthAttribute.applyModifier(new AttributeModifier(MODIFIER_ID, MODIFIER_NAME, this.getBonusMaxHealth(),
        ATTRIBUTE_MODIFIER_OPERATION_ADD));

    final float amountToHeal = this.getBonusMaxHealth() - oldAmount;
    if (amountToHeal > 0) {
      entity.heal(amountToHeal);
    }
  }
}