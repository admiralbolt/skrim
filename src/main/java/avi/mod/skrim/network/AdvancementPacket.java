package avi.mod.skrim.network;

import avi.mod.skrim.advancements.ModAdvancements;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class AdvancementPacket implements IMessage {

  public String advancementName;

  public AdvancementPacket() {

  }

  public AdvancementPacket(String statId) {
    this.advancementName = statId;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    ByteBufUtils.writeUTF8String(buf, this.advancementName);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    this.advancementName = ByteBufUtils.readUTF8String(buf);
  }

  public static class AchievementPacketHandler implements IMessageHandler<AdvancementPacket, IMessage> {

    public IMessage onMessage(final AdvancementPacket message, MessageContext ctx) {
      if (ctx.side.isServer()) {
        final EntityPlayerMP player = ctx.getServerHandler().player;
        if (player != null) {
          IThreadListener mainThread = player.getServerWorld();
          mainThread.addScheduledTask(() -> {
            System.out.println("Advancement packet recieved, executing.");
            ModAdvancements.ADVANCEMENTS_BY_NAME.get(message.advancementName).grant(player);
          });
        }
      }
      return null;
    }

  }

}
