package avi.mod.skrim.items.artifacts;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.init.SkrimSoundEvents;
import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.items.armor.ArtifactArmor;
import avi.mod.skrim.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PowerSuitBoots extends ArtifactArmor {

  private static final long TICKS_BETWEEN_JUMPS = 10;
  private static Map<UUID, Long> lastJump = new HashMap<>();

  public PowerSuitBoots() {
    super("powersuit_boots", ArtifactArmor.POWERSUIT_MATERIAL, EntityEquipmentSlot.FEET);
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    tooltip.add("§4Moar double jumps.§r");
    tooltip.add("§4Prevents all fall damage.§r");
    tooltip.add("§e\"Our pride was a veil over our eyes.\"");
  }

  @Mod.EventBusSubscriber(modid = Skrim.MOD_ID)
  public static class PowerSuitBootsHandler {

    @SubscribeEvent
    public static void spaceJump(InputEvent.KeyInputEvent event) {
      EntityPlayer player = Minecraft.getMinecraft().player;
      if (!Utils.isWearingArmor(player, SkrimItems.POWER_SUIT_BOOTS)) return;
      if (player.onGround || player.isInWater()) return;

      KeyBinding jumpKey = Minecraft.getMinecraft().gameSettings.keyBindJump;
      if (!jumpKey.isPressed()) return;
      
      long ticksSinceJump = player.world.getWorldTime() - lastJump.getOrDefault(player.getUniqueID(), 0L);
      if (ticksSinceJump < TICKS_BETWEEN_JUMPS) return;

      player.playSound(SkrimSoundEvents.SPIN_JUMP, 1.0f, 1.0f);
      player.jump();
      lastJump.put(player.getUniqueID(), player.world.getWorldTime());
    }


    @SubscribeEvent
    public static void preventFallDamage(LivingFallEvent event) {
      Entity entity = event.getEntity();
      if (!(entity instanceof EntityPlayer)) return;

      EntityPlayer player = (EntityPlayer) entity;
      if (!Utils.isWearingArmor(player, SkrimItems.POWER_SUIT_BOOTS)) return;

      event.setDistance(0);
      event.setCanceled(true);
    }
  }


}
