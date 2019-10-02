package avi.mod.skrim.network;

import avi.mod.skrim.utils.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class TeleportPacket implements IMessage {

  public double x;
  public double y;
  public double z;

  public TeleportPacket() {

  }

  public TeleportPacket(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeDouble(this.x);
    buf.writeDouble(this.y);
    buf.writeDouble(this.z);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    this.x = buf.readDouble();
    this.y = buf.readDouble();
    this.z = buf.readDouble();
  }

  public static class TeleportPacketHandler implements IMessageHandler<TeleportPacket, IMessage> {

    @Override
    public IMessage onMessage(TeleportPacket message, MessageContext ctx) {
      if (ctx.side.isClient()) return null;

      final EntityPlayerMP player = ctx.getServerHandler().player;
      if (player == null) return null;
      IThreadListener mainThread = player.getServerWorld();
      mainThread.addScheduledTask(() -> {
        Utils.teleport(player, message.x, message.y, message.z);
      });
      return null;
    }
  }
}
