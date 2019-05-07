package avi.mod.skrim.network.skillpackets;

import avi.mod.skrim.client.audio.AngelCakeFlyingSound;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Plays some music when you eat a slice of angel food cake.
 */
public class AngelFlyingSoundPacket implements IMessage {

  public AngelFlyingSoundPacket() {
  }

  @Override
  public void toBytes(ByteBuf buf) {
  }

  @Override
  public void fromBytes(ByteBuf buf) {
  }

  public static class AngelFlyingSoundPacketHandler implements IMessageHandler<AngelFlyingSoundPacket, IMessage> {

    @SideOnly(Side.CLIENT)
    @Override
    public IMessage onMessage(final AngelFlyingSoundPacket message, MessageContext ctx) {
      if (ctx.side.isServer()) return null;

      final EntityPlayer player = Minecraft.getMinecraft().player;
      if (player == null) return null;

      System.out.println("playing sound for player: " + player.getName());

      IThreadListener mainThread = Minecraft.getMinecraft();
      mainThread.addScheduledTask(() -> {
        Minecraft.getMinecraft().getSoundHandler().playSound(new AngelCakeFlyingSound(player));
      });

      return null;
    }

  }

}
