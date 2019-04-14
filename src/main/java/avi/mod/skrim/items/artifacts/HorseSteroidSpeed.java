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

public class HorseSteroidSpeed extends HorseSteroid {

  private static final String SPEED_ATTRIBUTE = "5F0F7063-0905-4D86-B020-A13DEDCF4EE9";
  private static final double ATTR_MOD = 1.2;

  public HorseSteroidSpeed() {
    super("speed");
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    tooltip.add("§4\"Inject your horse for a speed boost.\"§r");
    tooltip.add("§e\"Sir, do you know how fast you were going?\"§r");
  }

  @Override
  public IAttribute getTargetAttribute(EntityHorse horse) {
    return SharedMonsterAttributes.MOVEMENT_SPEED;
  }

  @Override
  public AttributeModifier getAttributeModifier(EntityHorse horse) {
    return new AttributeModifier(UUID.fromString(SPEED_ATTRIBUTE), "skrim-steroid-speed",  ATTR_MOD, 0);
  }

}
