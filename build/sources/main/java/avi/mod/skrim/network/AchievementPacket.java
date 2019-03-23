package avi.mod.skrim.network;

import avi.mod.skrim.skills.ISkill;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.mining.ISkillMining;
import avi.mod.skrim.skills.mining.SkillMining;
import avi.mod.skrim.stats.SkrimAchievements;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
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
							player.getAdvancements();
							/**
							 * 
							 * NEED TO LOOK AT ADVANCEMENTS MORE STILL!!!
							 * protected boolean perform(EntityPlayerMP
							 * p_193537_1_, Advancement p_193537_2_) {
							 * AdvancementProgress advancementprogress =
							 * p_193537_1_.getAdvancements().getProgress(p_193537_2_);
							 * 
							 * if (advancementprogress.isDone()) { return false;
							 * } else { for (String s :
							 * advancementprogress.getRemaningCriteria()) {
							 * p_193537_1_.getAdvancements().grantCriterion(p_193537_2_,
							 * s); }
							 * 
							 * return true; } }
							 * 
							 * 
							 */
							// player.addStat(SkrimAchievements.getAchievmentById(message.statId), message.statLevel);
						}
					});
				}
			}
			return null;
		}

	}

}
