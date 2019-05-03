package avi.mod.skrim.entities.items;

import avi.mod.skrim.client.audio.WindWakerSeaSound;
import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.utils.Obfuscation;
import avi.mod.skrim.utils.ReflectionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.item.Item;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class EntityKingOfRedLions extends EntityBoat {

  private static final double SPEED_BOOST = 0.2;

  public EntityKingOfRedLions(World worldIn) {
    super(worldIn);
  }

  public EntityKingOfRedLions(World worldIn, double x, double y, double z) {
    super(worldIn, x, y, z);
  }

  @Override
  @Nonnull
  public Item getItemBoat() {
    return SkrimItems.KING_OF_RED_LIONS;
  }

  @Override
  public void onUpdate() {
    super.onUpdate();

    if (!this.isBeingRidden()) return;

    boolean isForwardDown = (boolean) ReflectionUtils.getSuperPrivateField(this, Obfuscation.BOAT_FORWARD_DOWN.getFieldNames());
    boolean isLeftDown = (boolean) ReflectionUtils.getSuperPrivateField(this, Obfuscation.BOAT_LEFT_DOWN.getFieldNames());
    boolean isRightDown = (boolean) ReflectionUtils.getSuperPrivateField(this, Obfuscation.BOAT_RIGHT_DOWN.getFieldNames());


    if (isForwardDown && (isLeftDown == isRightDown)) {
      this.motionX += MathHelper.sin(-this.rotationYaw * 0.017453292F) * SPEED_BOOST;
      this.motionZ += MathHelper.cos(this.rotationYaw * 0.017453292F) * SPEED_BOOST;
    }

  }

  @SideOnly(Side.CLIENT)
  @Override
  protected void addPassenger(Entity passenger) {
    super.addPassenger(passenger);

    if (passenger.world.isRemote) {
      System.out.println("playing shit.");
      Minecraft.getMinecraft().getSoundHandler().playSound(new WindWakerSeaSound(this));
    }

  }

}
