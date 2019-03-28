package avi.mod.skrim.client.audio;

import avi.mod.skrim.init.SkrimSoundEvents;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.cooking.SkillCooking;
import avi.mod.skrim.utils.MathUtils;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * The sound that plays after eating angel cake.
 */
@SideOnly(Side.CLIENT)
public class AngelCakeFlyingSound extends MovingSound {

  private static float MAX_PITCH = 1.0F;
  private static float MIN_PITCH = 0.0F;
  private static float MAX_VOLUME = 0.3F;
  private EntityPlayer player;

  public AngelCakeFlyingSound(EntityPlayer player) {
    super(SkrimSoundEvents.ANGEL_CAKE_FLYING, SoundCategory.PLAYERS);
    this.player = player;
    this.repeat = true;
    this.repeatDelay = 0;
    this.volume = MAX_VOLUME;
    this.pitch = MAX_PITCH;
  }

  @Override
  public void update() {
    if (this.player.hasCapability(Skills.COOKING, EnumFacing.NORTH)) {
      SkillCooking cooking = (SkillCooking) this.player.getCapability(Skills.COOKING, EnumFacing.NORTH);
      if (!cooking.hasAngel) {
        this.repeat = false;
        this.donePlaying = true;
        return;
      }

      this.xPosF = (float) this.player.posX;
      this.yPosF = (float) this.player.posY;
      this.zPosF = (float) this.player.posZ;

      // Once time starts to run out, linearly decrease the pitch and volume.
      if (cooking.currentTicks > 100) return;
      this.pitch = (float) MathUtils.linearRescale(cooking.currentTicks, 0, 100, (double) MIN_PITCH,
          (double) MAX_PITCH);
      this.volume = (float) MathUtils.linearRescale(cooking.currentTicks, 0, 100, 0.0, (double) MAX_VOLUME);
    }
  }


}
