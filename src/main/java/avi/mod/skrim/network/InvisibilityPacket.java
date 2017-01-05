package avi.mod.skrim.network;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class InvisibilityPacket implements IMessage {

	public int duration;
	public String uuid;

	public InvisibilityPacket() {

	}

	public InvisibilityPacket(int duration, String uuid) {
		this.duration = duration;
		this.uuid = uuid;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeDouble(this.duration);
		ByteBufUtils.writeUTF8String(buf, this.uuid);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.duration = buf.readInt();
		this.uuid = ByteBufUtils.readUTF8String(buf);
	}

	public static class InvisibilityPacketHandler implements IMessageHandler<InvisibilityPacket, IMessage> {

		public IMessage onMessage(final InvisibilityPacket message, MessageContext ctx) {
			if (ctx.side.isServer()) {
				final WorldServer world = ctx.getServerHandler().playerEntity.getServerWorld();
				final EntityPlayer player = world.getPlayerEntityByUUID(UUID.fromString(message.uuid));
				if (player != null) {
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
