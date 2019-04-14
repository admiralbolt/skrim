package avi.mod.skrim.items.artifacts;

import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.items.armor.ArtifactArmor;
import avi.mod.skrim.utils.Utils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;

import javax.annotation.Nullable;
import java.util.List;

/**
 * It's a reference to an item from oblivion:
 * https://elderscrolls.fandom.com/wiki/Boots_of_Springheel_Jak_(Item)
 */
public class SpringheelBoots extends ArtifactArmor {

  public SpringheelBoots() {
    super("boots_of_springheel_jak", EntityEquipmentSlot.FEET);
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    tooltip.add("§4Massively increases jump height.§r");
    tooltip.add("§4Prevents all fall damage.§r");
    tooltip.add("§e\"Falco mode engaged.\"");
  }

  public static class SpringheelHandler {

    public static void jumpHigh(LivingEvent.LivingJumpEvent event) {
      Entity entity = event.getEntity();
      if (!(entity instanceof EntityPlayer)) return;

      EntityPlayer player = (EntityPlayer) entity;
      if (!player.world.isRemote) return;
      if (!Utils.isWearingArmor(player, SkrimItems.SPRINGHEEL_BOOTS)) return;

      player.motionY *= 3;
      player.setVelocity(player.motionX, player.motionY, player.motionZ);
    }

    public static void preventFallDamage(LivingFallEvent event) {
      Entity entity = event.getEntity();
      if (!(entity instanceof EntityPlayer)) return;
      EntityPlayer player = (EntityPlayer) entity;
      if (!Utils.isWearingArmor(player, SkrimItems.SPRINGHEEL_BOOTS)) return;
      event.setDistance(0);
      event.setCanceled(true);
    }

  }

}
