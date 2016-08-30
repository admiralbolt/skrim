package avi.mod.skrim.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import avi.mod.skrim.skills.ISkill;
import avi.mod.skrim.skills.Skills;

public class LevelUpPacket implements IMessage {

	 public LevelUpPacket() {

	  }

	  private String skillName;
	  private int level;

	  public LevelUpPacket(String skillName, int level) {
	  	this.skillName = skillName;
	    this.level = level;
	  }

	  @Override
	  public void toBytes(ByteBuf buf) {
	    // Pay attention to order!  Must be read in the same order!
	  	ByteBufUtils.writeUTF8String(buf, this.skillName);
	    buf.writeInt(this.level);
	  }

	  @Override
	  public void fromBytes(ByteBuf buf) {
	  	this.skillName = ByteBufUtils.readUTF8String(buf);
	    this.level = buf.readInt();
	  }

	  public static class LevelUpPacketHandler implements IMessageHandler<LevelUpPacket, IMessage> {

	    @Override
	    public IMessage onMessage(final LevelUpPacket message, MessageContext ctx) {
	    	if (ctx.side.isClient()) {
	    		if (message.skillName != null) {
		    		Capability<? extends ISkill> cap = Skills.skillMap.get(message.skillName.toLowerCase());
		        final EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		      	if (player != null && player.hasCapability(cap, EnumFacing.NORTH)) {
		      		IThreadListener mainThread = Minecraft.getMinecraft();
		      		mainThread.addScheduledTask(new Runnable() {
		      			@Override
		      			public void run() {
				      		player.sendChatMessage("Level up!  " + message.skillName + " is now level: " + message.level);
		      			}
		      		});
		      	}
	    		}
	    	}
	    	return null;
	    }

	  }

}
