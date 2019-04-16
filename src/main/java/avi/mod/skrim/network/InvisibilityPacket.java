package avi.mod.skrim.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

/**
 * Turns a player invisible.
 */
public class InvisibilityPacket implements IMessage {

  public int duration;
  public String uuid;

  public InvisibilityPacket() {
  }

  public InvisibilityPacket(int duration, String uuid) {
    this.duration = duration;
    this.uuid = uuid;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(this.duration);
    ByteBufUtils.writeUTF8String(buf, this.uuid);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    this.duration = buf.readInt();
    this.uuid = ByteBufUtils.readUTF8String(buf);
  }

  public static class InvisibilityPacketHandler implements IMessageHandler<InvisibilityPacket, IMessage> {

    public IMessage onMessage(final InvisibilityPacket message, MessageContext ctx) {
      if (ctx.side.isClient()) return null;
      final WorldServer world = ctx.getServerHandler().player.getServerWorld();
      final EntityPlayer player = world.getPlayerEntityByUUID(UUID.fromString(message.uuid));
      if (player == null) return null;
      world.addScheduledTask(() -> {
        player.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, message.duration, 0, true, true));
      });
      return null;
    }
  }

}