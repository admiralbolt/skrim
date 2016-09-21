package avi.mod.skrim.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import avi.mod.skrim.skills.ISkill;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.utils.Utils;

public class SpawnHeartPacket implements IMessage {

	public SpawnHeartPacket() {

	}

	private double x;
	private double y;
	private double z;
	private float height;
	private float width;

	public SpawnHeartPacket(double x, double y, double z, float height, float width) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.height = height;
		this.width = width;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		// Pay attention to order! Must be read in the same order!
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
		buf.writeFloat(height);
		buf.writeFloat(width);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.x = buf.readDouble();
		this.y = buf.readDouble();
		this.z = buf.readDouble();
		this.height = buf.readFloat();
		this.width = buf.readFloat();
	}

	public static class SpawnHeartPacketHandler implements IMessageHandler<SpawnHeartPacket, IMessage> {

		@Override
		public IMessage onMessage(final SpawnHeartPacket message, MessageContext ctx) {
			if (ctx.side.isClient()) {
				final EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
				IThreadListener mainThread = Minecraft.getMinecraft();
				mainThread.addScheduledTask(new Runnable() {
					@Override
					public void run() {
						for (int i = 0; i < 7; i++) {
							double d0 = Utils.rand.nextGaussian() * 0.03D;
							double d1 = Utils.rand.nextGaussian() * 0.03D;
							double d2 = Utils.rand.nextGaussian() * 0.03D;
							player.worldObj.spawnParticle(EnumParticleTypes.HEART, message.x + (double) (Utils.rand.nextFloat() * message.width * 2.0F) - (double) message.width, message.y + (double) (Utils.rand.nextFloat() * message.height), message.z + (double) (Utils.rand.nextFloat() * message.width * 2.0F) - (double) message.width, d0, d1, d2);
						}
					}
				});
			}
			return null;
		}
	}

}
