package avi.mod.skrim.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SpawnEntityPacket implements IMessage {

	public SpawnEntityPacket() {

	}

	private String entityName;
	private boolean isSkrim;
	private double x;
	private double y;
	private double z;

	public SpawnEntityPacket(String entityName, boolean isSkrim, double x, double y, double z) {
		this.entityName = entityName;
		this.isSkrim = isSkrim;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		// Pay attention to order! Must be read in the same order!
		ByteBufUtils.writeUTF8String(buf, entityName);
		buf.writeBoolean(isSkrim);
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.entityName = ByteBufUtils.readUTF8String(buf);
		this.isSkrim = buf.readBoolean();
		this.x = buf.readDouble();
		this.y = buf.readDouble();
		this.z = buf.readDouble();
	}

	public static class SpawnEntityPacketHandler implements IMessageHandler<SpawnEntityPacket, IMessage> {
		@Override
		public IMessage onMessage(final SpawnEntityPacket message, MessageContext ctx) {
			if (ctx.side.isServer()) {
				final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
				if (player != null) {
					final WorldServer world = player.getServerWorld();
					world.addScheduledTask(new Runnable() {
						@Override
						public void run() {
							Entity entity = EntityList.createEntityByIDFromName(new ResourceLocation((message.isSkrim) ? "skrim" : "minecraft", message.entityName), world);
							entity.setPosition(message.x, message.y, message.z);
							world.spawnEntityInWorld(entity);
						}
					});
				}
			}
			return null;
		}
	}

}
