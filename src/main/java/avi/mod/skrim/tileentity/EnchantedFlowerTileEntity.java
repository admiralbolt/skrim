package avi.mod.skrim.tileentity;

import avi.mod.skrim.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Look at the TileEntityBeacon code to understand what the fuck is going on here.
 */
public class EnchantedFlowerTileEntity extends TileEntity implements ITickable {

  private int levels = 2;
  private Potion primaryEffect = MobEffects.SPEED;
  private String customName;

  /**
   * Like the old updateEntity(), except more generic.
   */
  public void update() {
    if (!(this.world.getTotalWorldTime() % 5L == 0L)) return;

    BlockPos pos = this.getPos();
    double x = pos.getX() + 0.25 + Utils.rand.nextDouble() / 2;
    double y = pos.getY() + 0.5 + Utils.rand.nextDouble() / 2;
    double z = pos.getZ() + 0.25 + Utils.rand.nextDouble() / 2;
    this.world.spawnParticle(EnumParticleTypes.CRIT_MAGIC, x, y, z, 0, -0.05, 0);
    if (!(this.world.getTotalWorldTime() % 80L == 0L)) return;

    this.addEffectsToPlayers();
  }

  @Override
  @SideOnly(Side.CLIENT)
  @Nonnull
  public AxisAlignedBB getRenderBoundingBox() {
    return INFINITE_EXTENT_AABB;
  }

  private void addEffectsToPlayers() {
    if (this.world.isRemote) return;

    // This poorly named variable controls the radius of the potion effect.
    double d0 = (double) (4 * 10 + 10);
    int i = 0;

    if (this.levels >= 4) {
      i = 1;
    }

    int j = (9 + this.levels * 2) * 20;
    int k = this.pos.getX();
    int l = this.pos.getY();
    int i1 = this.pos.getZ();
    AxisAlignedBB axisalignedbb = (new AxisAlignedBB((double) k, (double) l, (double) i1, (double) (k + 1),
        (double) (l + 1), (double) (i1 + 1)))
        .grow(d0).expand(0.0D, (double) this.world.getHeight(), 0.0D);
    List<EntityPlayer> list = this.world.getEntitiesWithinAABB(EntityPlayer.class, axisalignedbb);

    for (EntityPlayer entityplayer : list) {
      entityplayer.addPotionEffect(new PotionEffect(this.primaryEffect, j, i, true, true));
    }

  }

  @Nullable
  public SPacketUpdateTileEntity getUpdatePacket() {
    return new SPacketUpdateTileEntity(this.pos, 3, this.getUpdateTag());
  }

  public NBTTagCompound getUpdateTag() {
    return this.writeToNBT(new NBTTagCompound());
  }

  @SideOnly(Side.CLIENT)
  public double getMaxRenderDistanceSquared() {
    return 65536.0D;
  }

  public void readFromNBT(NBTTagCompound compound) {
    super.readFromNBT(compound);
    this.primaryEffect = Potion.getPotionById(compound.getInteger("Primary"));
    this.levels = compound.getInteger("Levels");
  }

  public NBTTagCompound writeToNBT(NBTTagCompound compound) {
    super.writeToNBT(compound);
    compound.setInteger("Primary", Potion.getIdFromPotion(this.primaryEffect));
    compound.setInteger("Levels", this.levels);
    return compound;
  }

  /**
   * Get the NAME of this object. For players this returns their username
   */
  public String getName() {
    return this.hasCustomName() ? this.customName : "container.beacon";
  }

  public void setName(String name) {
    this.customName = name;
  }

  /**
   * Returns true if this thing is named
   */
  private boolean hasCustomName() {
    return this.customName != null && !this.customName.isEmpty();
  }

}
