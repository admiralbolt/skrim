package avi.mod.skrim.network;

import avi.mod.skrim.skills.ISkill;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.Skills;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class SkillPacket implements IMessage {

  public SkillPacket() {

  }

  private String skillName;
  private int level;
  private int xp;

  public SkillPacket(String skillName, int level, int xp) {
  	System.out.println("packet created.");
  	this.skillName = skillName;
    this.level = level;
    this.xp = xp;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    // Pay attention to order!  Must be read in the same order!
  	ByteBufUtils.writeUTF8String(buf, this.skillName);
    buf.writeInt(level);
    buf.writeInt(xp);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
  	this.skillName = ByteBufUtils.readUTF8String(buf);
    this.level = buf.readInt();
    this.xp = buf.readInt();
  }

  public static class SkillPacketHandler implements IMessageHandler<SkillPacket, IMessage> {

    @Override
    public IMessage onMessage(SkillPacket message, MessageContext ctx) {
    	if (ctx.side.isClient()) {
    		if (message.skillName != null) {
	    		Capability<? extends ISkill> cap = Skills.skillMap.get(message.skillName.toLowerCase());
	        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
	      	if (player != null && player.hasCapability(cap, EnumFacing.NORTH)) {
	      		Skill playerSkill = (Skill) player.getCapability(cap, EnumFacing.NORTH);
	      		playerSkill.level = message.level;
	      		playerSkill.xp = message.xp;
	      		System.out.println("setting skill: " + message.skillName + ", to level: " + message.level + ", and xp: " + message.xp);
	      	}
    		}
    	}
    	return null;
    }

  }

}
