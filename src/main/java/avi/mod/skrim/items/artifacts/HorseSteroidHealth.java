package avi.mod.skrim.items.artifacts;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class HorseSteroidHealth extends HorseSteroid {

  private static final String MAX_HEALTH_ATTRIBUTE = "5B1414DE-7621-4C51-8F11-9E216057F157";
  private static final double ATTR_MOD = 100;

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
    return new AttributeModifier(UUID.fromString(MAX_HEALTH_ATTRIBUTE), "skrim-steroid-health", ATTR_MOD, 0);
  }

}
