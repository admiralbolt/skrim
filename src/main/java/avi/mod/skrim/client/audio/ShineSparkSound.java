package avi.mod.skrim.client.audio;

import avi.mod.skrim.init.SkrimSoundEvents;
import avi.mod.skrim.items.artifacts.PowerSuitChestplate;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Shine spark sound for the Samus chestplate!
 */
@SideOnly(Side.CLIENT)
public class ShineSparkSound extends MovingSound {

  private static final float MAX_PITCH = 1.0F;
  private static final float MAX_VOLUME = 0.35F;

  private EntityPlayer player;

  public ShineSparkSound(EntityPlayer player) {
    super(SkrimSoundEvents.SHINESPARK_LOOP, SoundCategory.PLAYERS);
    this.player = player;
    this.repeat = true;
    this.repeatDelay = 0;
    this.volume = MAX_VOLUME;
    this.pitch = MAX_PITCH;
  }

  @Override
  public void update() {
    Item armor = this.player.inventory.armorInventory.get(EntityEquipmentSlot.CHEST.getIndex()).getItem();
    if (!(armor instanceof PowerSuitChestplate)) return;

    PowerSuitChestplate.ShineSpark spark = PowerSuitChestplate.PLAYER_SPARKS.get(this.player.getUniqueID());

    if (spark.active) {
      this.xPosF = (float) this.player.posX;
      this.yPosF = (float) this.player.posY;
      this.zPosF = (float) this.player.posZ;
      return;
    }

    this.repeat = false;
    this.donePlaying = true;
  }

}
