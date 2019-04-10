package avi.mod.skrim.items.artifacts;

import avi.mod.skrim.client.audio.ShineSparkSound;
import avi.mod.skrim.items.ModItems;
import avi.mod.skrim.items.armor.ArtifactArmor;
import avi.mod.skrim.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * One of the metroid artifacts. Power suit chestplate allows players to shine spark.
 * <p>
 * Activated by sprinting for at least 5 seconds granting a massive speed boost until the player slows down.
 */
public class PowerSuitChestplate extends ArtifactArmor {

  // This is pretty fucking fast.
  private static double MAX_SPEED = 3;
  private static float YAW_BOUND = 10.0F;

  private int sprintingTicks = 0;
  public boolean spark = false;

  // Saves the current speed for when jumps activate.
  private double savedMotionX;
  private double savedMotionZ;

  // Saves the initial direction for sparking purposes.
  private Float initialDirection = null;

  public PowerSuitChestplate() {
    super("powersuit_chestplate", EntityEquipmentSlot.CHEST);
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    tooltip.add("§4Sprinting for an extended duration activates your speed booster.§r");
    tooltip.add("§e\"Time and reality swirl together like estuary waters.\"");
  }

  @SideOnly(Side.CLIENT)
  private void playShineSpark(EntityPlayer player) {
    Minecraft.getMinecraft().getSoundHandler().playSound(new ShineSparkSound(player));
  }

  public static void applyChozoTech(LivingUpdateEvent event) {
    Entity entity = event.getEntity();
    if (!(entity instanceof EntityPlayer)) return;

    EntityPlayer player = (EntityPlayer) entity;
    if (!player.world.isRemote || !Utils.isWearingArmor(player, ModItems.POWER_SUIT_CHESTPLATE)) return;

    PowerSuitChestplate chozoChest = (PowerSuitChestplate) Utils.getArmor(player, EntityEquipmentSlot.CHEST).getItem();
    if (chozoChest.spark) {
      // Store the initial motion direction.
      if (chozoChest.initialDirection == null) {
        chozoChest.initialDirection = player.rotationYaw % 360;
      }
      player.rotationYaw = chozoChest.initialDirection;

      if (player.onGround) {
        // Run faster.
        player.motionX *= 1.75;
        player.motionZ *= 1.75;

        // Scale down speed to max speed if over it.
        double speed = Math.sqrt(player.motionX * player.motionX + player.motionZ * player.motionZ);
        if (speed > MAX_SPEED) {
          double scale = speed / MAX_SPEED;
          player.motionX /= scale;
          player.motionY /= scale;
        }
        chozoChest.savedMotionX = player.motionX;
        chozoChest.savedMotionZ = player.motionZ;
      } else {
        // Maintain air speed.
        player.motionX = chozoChest.savedMotionX;
        player.motionZ = chozoChest.savedMotionZ;
      }

      // If the player slows down OR changes direction significantly.
      if (Math.sqrt(player.motionX * player.motionX + player.motionZ * player.motionZ) < 0.25) {
        chozoChest.sprintingTicks = 0;
        chozoChest.spark = false;
        chozoChest.initialDirection = null;
      }
    } else if (player.isSprinting() && player.onGround) {
      chozoChest.sprintingTicks++;
      if (chozoChest.sprintingTicks >= 100) {
        chozoChest.spark = true;
        chozoChest.playShineSpark(player);
      }
    }
  }

}
