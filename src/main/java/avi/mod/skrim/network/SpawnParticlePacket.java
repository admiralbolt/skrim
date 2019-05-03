package avi.mod.skrim.network;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.utils.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Spawn a particle!
 */
public class SpawnParticlePacket implements IMessage {

  public SpawnParticlePacket() {

  }

  private String particle;
  private double x;
  private double y;
  private double z;
  private float height;
  private float width;

  public SpawnParticlePacket(String particle, double x, double y, double z, float height, float width) {
    this.particle = particle;
    this.x = x;
    this.y = y;
    this.z = z;
    this.height = height;
    this.width = width;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    ByteBufUtils.writeUTF8String(buf, particle);
    buf.writeDouble(x);
    buf.writeDouble(y);
    buf.writeDouble(z);
    buf.writeFloat(height);
    buf.writeFloat(width);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    this.particle = ByteBufUtils.readUTF8String(buf);
    this.x = buf.readDouble();
    this.y = buf.readDouble();
    this.z = buf.readDouble();
    this.height = buf.readFloat();
    this.width = buf.readFloat();
  }

  public static class SpawnParticlePacketHandler implements IMessageHandler<SpawnParticlePacket, IMessage> {

    @SideOnly(Side.CLIENT)
    @Override
    public IMessage onMessage(final SpawnParticlePacket message, MessageContext ctx) {
      if (ctx.side.isServer()) return null;

      Skrim.proxy.getThreadListener(ctx).addScheduledTask(() -> {
        final EntityPlayer player = Minecraft.getMinecraft().player;
        IThreadListener mainThread = Minecraft.getMinecraft();
        for (int i = 0; i < 7; i++) {
          double d0 = Utils.rand.nextGaussian() * 0.03D;
          double d1 = Utils.rand.nextGaussian() * 0.03D;
          double d2 = Utils.rand.nextGaussian() * 0.03D;
          player.world.spawnParticle(EnumParticleTypes.valueOf(message.particle),
              message.x + (double) (Utils.rand.nextFloat() * message.width * 2.0F) - (double) message.width,
              message.y + (double) (Utils.rand.nextFloat() * message.height),
              message.z + (double) (Utils.rand.nextFloat() * message.width * 2.0F) - (double) message.width, d0, d1, d2);
        }
      });

      return null;
    }
  }
}
