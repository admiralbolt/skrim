package avi.mod.skrim.network;

import avi.mod.skrim.skills.ISkill;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.Skills;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Toggle an ability on / off.
 */
public class ToggleAbilityPacket implements IMessage {

  public ToggleAbilityPacket() {
  }

  private String skillName;
  private int level;

  public ToggleAbilityPacket(String skillName, int level) {
    this.skillName = skillName;
    this.level = level;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    ByteBufUtils.writeUTF8String(buf, this.skillName);
    buf.writeInt(this.level);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    this.skillName = ByteBufUtils.readUTF8String(buf);
    this.level = buf.readInt();
  }

  public static class ToggleAbilityPacketHandler implements IMessageHandler<ToggleAbilityPacket, IMessage> {

    public IMessage onMessage(final ToggleAbilityPacket message, MessageContext ctx) {
      if (ctx.side.isClient() || message.skillName == null) return null;

      Capability<? extends ISkill> skill = Skills.SKILL_MAP.get(message.skillName.toLowerCase());
      final EntityPlayerMP player = ctx.getServerHandler().player;
      if (player == null) return null;

      Skill s = (Skill) player.getCapability(skill, EnumFacing.NORTH);
      if (s == null) return null;

      IThreadListener mainThread = player.getServerWorld();
      mainThread.addScheduledTask(() -> {
        s.toggleAbility(message.level);
      });

      return null;
    }
  }
}
