package avi.mod.skrim.network;

import avi.mod.skrim.utils.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SetBlockPacket implements IMessage {

	public SetBlockPacket() {

	}

	private int stateId;
	private double x;
	private double y;
	private double z;

	public SetBlockPacket(int stateId, double x, double y, double z) {
		this.stateId = stateId;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		// Pay attention to order! Must be read in the same order!
		buf.writeInt(this.stateId);
		buf.writeDouble(this.x);
		buf.writeDouble(this.y);
		buf.writeDouble(this.z);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.stateId = buf.readInt();
		this.x = buf.readDouble();
		this.y = buf.readDouble();
		this.z = buf.readDouble();
	}

	public static class SetBlockPacketHandler implements IMessageHandler<SetBlockPacket, IMessage> {

		@Override
		public IMessage onMessage(final SetBlockPacket message, MessageContext ctx) {
			if (ctx.side.isServer()) {
				final EntityPlayerMP player = ctx.getServerHandler().player;
				IThreadListener mainThread = Minecraft.getMinecraft();
				mainThread.addScheduledTask(new Runnable() {
					@Override
					public void run() {
						player.world.setBlockState(new BlockPos(message.x, message.y, message.z), Block.getStateById(message.stateId));
					}
				});
			}
			return null;
		}
	}

}
