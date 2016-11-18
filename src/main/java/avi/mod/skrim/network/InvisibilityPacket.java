package avi.mod.skrim.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class InvisibilityPacket implements IMessage {
	
	public int duration;
	
	public InvisibilityPacket() {
		
	}
	
	public InvisibilityPacket(int duration) {
		this.duration = duration;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeDouble(this.duration);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.duration = buf.readInt();
	}

	public static class InvisibilityPacketHandler implements IMessageHandler<InvisibilityPacket, IMessage> {

		public IMessage onMessage(final InvisibilityPacket message, MessageContext ctx) {
    	if (ctx.side.isServer()) {
    		final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        if (player != null) {
      		player.getServerWorld();
        	final WorldServer world = player.getServerWorld();
      		world.addScheduledTask(new Runnable() {
      			@Override
      			public void run() {
      				player.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 20, 0, true, true));
      			}
      		});
        }
    	}
    	return null;
		}

	}

}
