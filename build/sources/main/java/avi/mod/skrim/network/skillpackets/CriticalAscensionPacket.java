package avi.mod.skrim.network.skillpackets;

import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.ranged.SkillRanged;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CriticalAscensionPacket implements IMessage {

	public CriticalAscensionPacket() {

	}

	private int stacks;

	public CriticalAscensionPacket(int stacks) {
		this.stacks = stacks;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.stacks);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.stacks = buf.readInt();
	}

	public static class CriticalAscensionPacketHandler implements IMessageHandler<CriticalAscensionPacket, IMessage> {

		@Override
		public IMessage onMessage(final CriticalAscensionPacket message, MessageContext ctx) {
			if (ctx.side.isClient()) {
				EntityPlayerSP player = Minecraft.getMinecraft().player;
				if (player != null && player.hasCapability(Skills.RANGED, EnumFacing.NORTH)) {
					IThreadListener mainThread = Minecraft.getMinecraft();
					final SkillRanged ranged = (SkillRanged) player.getCapability(Skills.RANGED, EnumFacing.NORTH);
					mainThread.addScheduledTask(new Runnable() {
						@Override
						public void run() {
							ranged.setStacks(message.stacks);
						}
					});
				}
			}
			return null;
		}

	}

}
