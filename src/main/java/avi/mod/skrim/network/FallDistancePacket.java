package avi.mod.skrim.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * This is workaround to make the spider climb ability work. We have to override the fall distance because the speed at which you're
 * traveling does not affect fall damage. The only thing that matters is the max height since you've touched the ground.
 */
public class FallDistancePacket implements IMessage {

  public float distance;

  public FallDistancePacket() {
  }

  public FallDistancePacket(float distance) {
    this.distance = distance;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeFloat(this.distance);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    this.distance = buf.readFloat();
  }

  public static class FallDistancePacketHandler implements IMessageHandler<FallDistancePacket, IMessage> {

    public IMessage onMessage(final FallDistancePacket message, MessageContext ctx) {
      if (ctx.side.isClient()) return null;

      final EntityPlayerMP player = ctx.getServerHandler().player;
      if (player == null) return null;
      IThreadListener mainThread = player.getServerWorld();
      mainThread.addScheduledTask(() -> {
        player.fallDistance = message.distance;
      });
      return null;
    }
  }

}
