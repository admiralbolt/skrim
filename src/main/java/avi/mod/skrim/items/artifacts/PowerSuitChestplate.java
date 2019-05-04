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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * One of the metroid artifacts. Power suit chestplate allows players to shine spark.
 * <p>
 * Activated by sprinting for at least 5 seconds granting a massive speed boost until the player slows down.
 */
public class PowerSuitChestplate extends ArtifactArmor {

  // This is pretty fucking fast.
  private static final double SPARK_SPEED = 2.5;

  // We need to maintain information about shine sparks on a per-player basis in case there are multiple people wearing the powersuit
  // chestplate on the server at the same time.
  public static Map<UUID, ShineSpark> PLAYER_SPARKS = new HashMap<>();

  public PowerSuitChestplate() {
    super("powersuit_chestplate", ArtifactArmor.POWERSUIT_MATERIAL, EntityEquipmentSlot.CHEST);
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    tooltip.add("§4Sprinting for an extended duration activates your speed booster.§r");
    tooltip.add("§e\"Time and reality swirl together like estuary waters.\"");
  }

  @SideOnly(Side.CLIENT)
  private static void playShineSpark(EntityPlayer player) {
    Minecraft.getMinecraft().getSoundHandler().playSound(new ShineSparkSound(player));
  }

  public static void applyChozoTech(LivingUpdateEvent event) {
    Entity entity = event.getEntity();
    if (!(entity instanceof EntityPlayer)) return;

    EntityPlayer player = (EntityPlayer) entity;
    if (!player.world.isRemote || !Utils.isWearingArmor(player, SkrimItems.POWER_SUIT_CHESTPLATE)) return;

    if (!PLAYER_SPARKS.containsKey(player.getUniqueID())) {
      PLAYER_SPARKS.put(player.getUniqueID(), new ShineSpark());
    }

    ShineSpark spark = PLAYER_SPARKS.get(player.getUniqueID());

    if (spark.active) {
      if (!player.isSprinting()) {
        spark.sprintingTicks = 0;
        spark.active = false;
        spark.initialDirection = null;
        spark.savedMotionX = null;
        spark.savedMotionZ = null;
        return;
      }
      // Store the initial motion direction.
      if (spark.initialDirection == null || spark.savedMotionX == null || spark.savedMotionZ == null) {
        spark.initialDirection = player.rotationYaw;
        double speed = Math.sqrt(player.motionX * player.motionX + player.motionZ * player.motionZ);
        double scale = SPARK_SPEED / speed;
        spark.savedMotionX = scale * player.motionX;
        spark.savedMotionZ = scale * player.motionZ;
      }

      player.rotationYaw = spark.initialDirection;
      player.motionX = spark.savedMotionX;
      player.motionZ = spark.savedMotionZ;
    } else if (player.isSprinting() && player.onGround) {
      spark.sprintingTicks++;
      if (spark.sprintingTicks >= 100) {
        spark.active = true;
        playShineSpark(player);
      }
    } else {
      spark.sprintingTicks = 0;
    }
  }

  /**
   * Helper class to hold data about the shine spark.
   */
  public static class ShineSpark {

    public boolean active = false;

    private int sprintingTicks = 0;

    // Saves the current speed for when sparking starts.
    private Float initialDirection = null;
    private Double savedMotionX = null;
    private Double savedMotionZ = null;

    public ShineSpark() {
    }

  }

}
