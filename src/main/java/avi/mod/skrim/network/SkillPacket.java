package avi.mod.skrim.network;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.skills.ISkill;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.Skills;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Sets a skill to a given level with a given xp amount.
 */
public class SkillPacket implements IMessage {

  private String skillName;
  private int level;
  private double xp;

  public SkillPacket() {
  }

  public SkillPacket(String skillName, int level, double xp) {
    this.skillName = skillName;
    this.level = level;
    this.xp = xp;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    ByteBufUtils.writeUTF8String(buf, this.skillName);
    buf.writeInt(this.level);
    buf.writeDouble(this.xp);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    this.skillName = ByteBufUtils.readUTF8String(buf);
    this.level = buf.readInt();
    this.xp = buf.readDouble();
  }

  public static class SkillPacketHandler implements IMessageHandler<SkillPacket, IMessage> {

    @SideOnly(Side.CLIENT)
    @Override
    public IMessage onMessage(final SkillPacket message, MessageContext ctx) {
      if (ctx.side.isServer() || message.skillName == null) return null;

      Skrim.proxy.getThreadListener(ctx).addScheduledTask(() -> {
        Capability<? extends ISkill> skill = Skills.SKILL_MAP.get(message.skillName.toLowerCase());
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null || !player.hasCapability(skill, EnumFacing.NORTH)) return;

        final Skill playerSkill = Skills.getSkill(player, skill, Skill.class);
        playerSkill.level = message.level;
        playerSkill.xp = message.xp;
      });
      return null;
    }
  }
}

