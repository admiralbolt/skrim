package avi.mod.skrim.network;

import avi.mod.skrim.tileentity.MegaChestTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Sort a mega chest!
 */
public class SortChestPacket implements IMessage {

  public int x;
  public int y;
  public int z;

  public SortChestPacket() {
  }

  public SortChestPacket(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(this.x);
    buf.writeInt(this.y);
    buf.writeInt(this.z);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    this.x = buf.readInt();
    this.y = buf.readInt();
    this.z = buf.readInt();
  }

  public static class SortChestPacketHandler implements IMessageHandler<SortChestPacket, IMessage> {

    public IMessage onMessage(final SortChestPacket message, MessageContext ctx) {
      if (ctx.side.isClient()) return null;
      final EntityPlayerMP player = ctx.getServerHandler().player;
      if (player == null) return null;
      IThreadListener mainThread = player.getServerWorld();
      mainThread.addScheduledTask(() -> {
        TileEntity te = player.world.getTileEntity(new BlockPos(message.x, message.y, message.z));
        if (te instanceof MegaChestTileEntity) {
          ((MegaChestTileEntity) te).sort();
        }
      });
      return null;
    }
  }
}
