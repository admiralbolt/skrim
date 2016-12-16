package avi.mod.skrim.network.skillpackets;

import avi.mod.skrim.skills.RandomTreasure;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.digging.SkillDigging;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MetalDetectorPacket implements IMessage {

	public double x;
	public double y;
	public double z;

	public MetalDetectorPacket() {

	}

	public MetalDetectorPacket(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeDouble(this.x);
		buf.writeDouble(this.y);
		buf.writeDouble(this.z);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.x = buf.readDouble();
		this.y = buf.readDouble();
		this.z = buf.readDouble();
	}

	public static class MetalDetectorPacketHandler implements IMessageHandler<MetalDetectorPacket, IMessage> {

		public IMessage onMessage(final MetalDetectorPacket message, MessageContext ctx) {
			if (ctx.side.isServer()) {
				final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
				player.getServerWorld();
				if (player != null) {
					final WorldServer world = player.getServerWorld();
					world.addScheduledTask(new Runnable() {
						@Override
						public void run() {
							EntityItem entityItem = new EntityItem(world, player.posX, player.posY, player.posZ, RandomTreasure.generateMetalTreasure());
							world.spawnEntityInWorld(entityItem);
							Skills.playFortuneSound(player);
							if (player.hasCapability(Skills.DIGGING, EnumFacing.NORTH)) {
								SkillDigging digging = (SkillDigging) player.getCapability(Skills.DIGGING, EnumFacing.NORTH);
								digging.addXp(player, 200);
							}
						}
					});
				}
			}
			return null;
		}

	}

}
