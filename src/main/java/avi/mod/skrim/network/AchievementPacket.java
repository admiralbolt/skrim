package avi.mod.skrim.network;

import avi.mod.skrim.skills.ISkill;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.mining.ISkillMining;
import avi.mod.skrim.skills.mining.SkillMining;
import avi.mod.skrim.stats.SkrimAchievements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class AchievementPacket implements IMessage {

	public String statId;
	public int statLevel;

	public AchievementPacket() {

	}

	public AchievementPacket(String statId, int statLevel) {
		this.statId = statId;
		this.statLevel = statLevel;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, this.statId);
		buf.writeInt(this.statLevel);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.statId = ByteBufUtils.readUTF8String(buf);
		this.statLevel = buf.readInt();
	}

	public static class AchievementPacketHandler implements IMessageHandler<AchievementPacket, IMessage> {

		public IMessage onMessage(final AchievementPacket message, MessageContext ctx) {
			if (ctx.side.isServer()) {
				final EntityPlayerMP player = ctx.getServerHandler().player;
				if (player != null) {
					IThreadListener mainThread = player.getServerWorld();
					mainThread.addScheduledTask(new Runnable() {
						@Override
						public void run() {
							player.addStat(SkrimAchievements.getAchievmentById(message.statId), message.statLevel);
						}
					});
				}
			}
			return null;
		}

	}

}
