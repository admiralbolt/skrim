package avi.mod.skrim.network;

import avi.mod.skrim.blocks.tnt.CustomTNTPrimed;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.Explosion;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ExplosionPacket implements IMessage {

	public String explosionType;
	public int entityId;
	public double posX;
	public double posY;
	public double posZ;

	public ExplosionPacket() {

	}

	public ExplosionPacket(String explosionType, int entityId, double posX, double posY, double posZ) {
		this.explosionType = explosionType;
		this.entityId = entityId;
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, this.explosionType);
		buf.writeInt(this.entityId);
		buf.writeDouble(this.posX);
		buf.writeDouble(this.posY);
		buf.writeDouble(this.posZ);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.explosionType = ByteBufUtils.readUTF8String(buf);
		this.entityId = buf.readInt();
		this.posX = buf.readDouble();
		this.posY = buf.readDouble();
		this.posZ = buf.readDouble();
	}

	public static class ExplosionPacketHandler implements IMessageHandler<ExplosionPacket, IMessage> {

		@Override
		public IMessage onMessage(final ExplosionPacket message, MessageContext ctx) {
			if (ctx.side.isClient()) {
				final IThreadListener mainThread = Minecraft.getMinecraft();
				final EntityPlayerSP player = Minecraft.getMinecraft().player;
				mainThread.addScheduledTask(new Runnable() {
					@Override
					public void run() {
						Entity entity = player.world.getEntityByID(message.entityId);
						Explosion explosion = CustomTNTPrimed.createExplosion(message.explosionType, player.world, entity, message.posX, message.posY, message.posZ);
						explosion.doExplosionA();
						explosion.doExplosionB(true);
					}
				});
			}
			return null;
		}

	}

}
