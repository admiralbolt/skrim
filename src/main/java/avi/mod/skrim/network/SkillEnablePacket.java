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

import java.util.Map;

/**
 * Enable / disable skills & abilities.
 */
public class SkillEnablePacket implements IMessage {

  private String skillName;
  private boolean enabled;
  private boolean ability1;
  private boolean ability2;
  private boolean ability3;
  private boolean ability4;

  public SkillEnablePacket() {
  }

  public SkillEnablePacket(String skillName, boolean enabled, Map<Integer, Boolean> abilityEnabledMap) {
    this.skillName = skillName;
    this.enabled = enabled;
    this.ability1 = abilityEnabledMap.getOrDefault(1, true);
    this.ability2 = abilityEnabledMap.getOrDefault(2, true);
    this.ability3 = abilityEnabledMap.getOrDefault(3, true);
    this.ability4 = abilityEnabledMap.getOrDefault(4, true);
  }

  @Override
  public void toBytes(ByteBuf buf) {
    ByteBufUtils.writeUTF8String(buf, this.skillName);
    buf.writeBoolean(this.enabled);
    buf.writeBoolean(this.ability1);
    buf.writeBoolean(this.ability2);
    buf.writeBoolean(this.ability3);
    buf.writeBoolean(this.ability4);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    this.skillName = ByteBufUtils.readUTF8String(buf);
    this.enabled = buf.readBoolean();
    this.ability1 = buf.readBoolean();
    this.ability2 = buf.readBoolean();
    this.ability3 = buf.readBoolean();
    this.ability4 = buf.readBoolean();
  }

  public static class SkillEnablePacketHandler implements IMessageHandler<SkillEnablePacket, IMessage> {

    @SideOnly(Side.CLIENT)
    @Override
    public IMessage onMessage(final SkillEnablePacket message, MessageContext ctx) {
      if (ctx.side.isServer() || message.skillName == null) return null;

      Skrim.proxy.getThreadListener(ctx).addScheduledTask(() -> {
        Capability<? extends ISkill> skill = Skills.SKILL_MAP.get(message.skillName.toLowerCase());
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null || !player.hasCapability(skill, EnumFacing.NORTH)) return;

        final Skill playerSkill = Skills.getSkill(player, skill, Skill.class);
        playerSkill.skillEnabled = message.enabled;
        playerSkill.enabledMap.put(1, message.ability1);
        playerSkill.enabledMap.put(2, message.ability2);
        playerSkill.enabledMap.put(3, message.ability3);
        playerSkill.enabledMap.put(4, message.ability4);
      });
      return null;
    }
  }
}

