package avi.mod.skrim.items.artifacts;

import avi.mod.skrim.utils.Obfuscation;
import avi.mod.skrim.utils.ReflectionUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class HorseSteroidJump extends HorseSteroid {

  private static final String JUMP_ATTRIBUTE = "5F0F7063-0905-4D86-B020-A13DEDCF4EE9";
  private static final double ATTR_MOD = 2;

  public HorseSteroidJump() {
    super("jump");
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    tooltip.add("§4\"Inject your horse for a jump boost.\"§r");
    tooltip.add("§e\"Hey diddle diddle, the cat and the fiddle, the horse jumped over the moon.\"§r");
  }

  @Override
  public IAttribute getTargetAttribute(EntityHorse horse) {
    Object horsey = ReflectionUtils.getSuperPrivateField(horse, Obfuscation.HORSE_JUMP_ATTRIBUTE.getFieldNames());
    if (horsey instanceof IAttribute) {
      return (IAttribute) horsey;
    } else {
      return null;
    }
  }

  @Override
  public AttributeModifier getAttributeModifier(EntityHorse horse) {
    return new AttributeModifier(UUID.fromString(JUMP_ATTRIBUTE), "skrim-steroid-jump", ATTR_MOD, 0);
  }

}
