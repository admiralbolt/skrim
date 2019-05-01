package avi.mod.skrim.client.audio;

import avi.mod.skrim.init.SkrimSoundEvents;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class WindWakerSeaSound extends MovingSound {

  private EntityBoat boat;

  public WindWakerSeaSound(EntityBoat boat) {
    super(SkrimSoundEvents.WIND_WAKER, SoundCategory.MUSIC);
    this.boat = boat;
    this.repeat = true;
    this.repeatDelay = 0;
    this.volume = 1.0F;
    this.pitch = 1.0F;
  }

  @Override
  public void update() {
    this.xPosF = (float) boat.posX;
    this.yPosF = (float) boat.posY;
    this.zPosF = (float) boat.posZ;
    if (boat.isBeingRidden()) return;

    System.out.println("OOO");
    this.repeat = false;
    this.donePlaying = true;
  }
}
