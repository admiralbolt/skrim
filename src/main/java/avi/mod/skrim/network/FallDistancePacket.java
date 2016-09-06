package avi.mod.skrim.network;

import avi.mod.skrim.skills.ISkill;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.mining.ISkillMining;
import avi.mod.skrim.skills.mining.SkillMining;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class FallDistancePacket implements IMessage {
	
	public float distance;
	
	public FallDistancePacket(float distance) {
		this.distance = distance;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeFloat(this.distance);
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.distance = buf.readFloat();
	}
	
	public static class FallDistancePacketHandler implements IMessageHandler<FallDistancePacket, IMessage> {
		
		public IMessage onMessage(final FallDistancePacket message, MessageContext ctx) {
			System.out.println("message receieved.");
    	if (ctx.side.isServer()) {
        final EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (player != null) {
      		System.out.println("setting fall distance.");
      		IThreadListener mainThread = Minecraft.getMinecraft();
      		mainThread.addScheduledTask(new Runnable() {
      			@Override
      			public void run() {
  	      		player.fallDistance = message.distance;
      			}
      		});
        }
    	}
    	return null;
		}
		
	}

}
