package avi.mod.skrim.network.skillpackets;

import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.ranged.SkillRanged;
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
 * Level 100 ranged skill.
 */
public class CriticalAscensionPacket implements IMessage {

  private int stacks;

  public CriticalAscensionPacket() {
  }

  public CriticalAscensionPacket(int stacks) {
    this.stacks = stacks;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(this.stacks);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    this.stacks = buf.readInt();
  }

  public static class CriticalAscensionPacketHandler implements IMessageHandler<CriticalAscensionPacket, IMessage> {

    @SideOnly(Side.CLIENT)
    @Override
    public IMessage onMessage(final CriticalAscensionPacket message, MessageContext ctx) {
      if (ctx.side.isServer()) return null;

      EntityPlayer player = Minecraft.getMinecraft().player;
      if (player == null) return null;

      IThreadListener mainThread = Minecraft.getMinecraft();
      SkillRanged ranged = Skills.getSkill(player, Skills.RANGED, SkillRanged.class);
      mainThread.addScheduledTask(() -> {
        ranged.setStacks(message.stacks);
      });
      return null;
    }

  }

}
