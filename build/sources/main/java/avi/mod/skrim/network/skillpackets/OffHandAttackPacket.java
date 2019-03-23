package avi.mod.skrim.network.skillpackets;

import avi.mod.skrim.skills.ISkill;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.melee.SkillMelee;
import avi.mod.skrim.skills.mining.ISkillMining;
import avi.mod.skrim.skills.mining.SkillMining;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class OffHandAttackPacket implements IMessage {

	public int playerId;
	public int targetId;

	public OffHandAttackPacket() {

	}

	public OffHandAttackPacket(int playerId, int targetId) {
		this.playerId = playerId;
		this.targetId = targetId;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.playerId);
		buf.writeInt(this.targetId);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.playerId = buf.readInt();
		this.targetId = buf.readInt();
	}

	public static class OffHandAttackPacketHandler implements IMessageHandler<OffHandAttackPacket, IMessage> {

		public IMessage onMessage(final OffHandAttackPacket message, MessageContext ctx) {
    	if (ctx.side.isServer()) {
    		final EntityPlayerMP player = ctx.getServerHandler().player;
    		player.getServerWorld();
        if (player != null) {
      		IThreadListener mainThread = player.getServerWorld();
      		mainThread.addScheduledTask(new Runnable() {
      			@Override
      			public void run() {
      				Entity targetEntity = player.getServerWorld().getEntityByID(message.targetId);
      				SkillMelee.attackTargetEntityWithOffhandItem(player, targetEntity);
      			}
      		});
        }
    	}
    	return null;
		}

	}

}
