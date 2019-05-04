package avi.mod.skrim.items.artifacts;

import avi.mod.skrim.client.audio.ShineSparkSound;
import avi.mod.skrim.items.SkrimItems;
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
  private static double SPARK_SPEED = 2.5;

  private int sprintingTicks = 0;
  public boolean spark = false;

  // Saves the current speed for when jumps activate.
  private Float initialDirection = null;
  private Double savedMotionX = null;
  private Double savedMotionZ = null;

  public PowerSuitChestplate() {
    super("powersuit_chestplate", ArtifactArmor.POWERSUIT_MATERIAL, EntityEquipmentSlot.CHEST);
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
    if (!player.world.isRemote || !Utils.isWearingArmor(player, SkrimItems.POWER_SUIT_CHESTPLATE)) return;

    PowerSuitChestplate chozoChest = (PowerSuitChestplate) Utils.getArmor(player, EntityEquipmentSlot.CHEST).getItem();
    if (chozoChest.spark) {
      if (!player.isSprinting()) {
        chozoChest.sprintingTicks = 0;
        chozoChest.spark = false;
        chozoChest.initialDirection = null;
        chozoChest.savedMotionX = null;
        chozoChest.savedMotionZ = null;
        return;
      }
      // Store the initial motion direction.
      if (chozoChest.initialDirection == null || chozoChest.savedMotionX == null || chozoChest.savedMotionZ == null) {
        chozoChest.initialDirection = player.rotationYaw;
        double speed = Math.sqrt(player.motionX * player.motionX + player.motionZ * player.motionZ);
        double scale = SPARK_SPEED / speed;
        chozoChest.savedMotionX = scale * player.motionX;
        chozoChest.savedMotionZ = scale * player.motionZ;
      }

      player.rotationYaw = chozoChest.initialDirection;
      player.motionX = chozoChest.savedMotionX;
      player.motionZ = chozoChest.savedMotionZ;
    } else if (player.isSprinting() && player.onGround) {
      chozoChest.sprintingTicks++;
      if (chozoChest.sprintingTicks >= 100) {
        chozoChest.spark = true;
        chozoChest.playShineSpark(player);
      }
    }
  }

}
