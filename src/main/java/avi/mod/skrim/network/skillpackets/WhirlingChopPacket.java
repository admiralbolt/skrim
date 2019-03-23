package avi.mod.skrim.network.skillpackets;

import avi.mod.skrim.items.tools.HandSaw;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.woodcutting.SkillWoodcutting;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class WhirlingChopPacket implements IMessage {

	public int x;
	public int y;
	public int z;

	public WhirlingChopPacket() {

	}

	public WhirlingChopPacket(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
	}

	public static class WhirlingChopPacketHandler implements IMessageHandler<WhirlingChopPacket, IMessage> {

		public IMessage onMessage(final WhirlingChopPacket message, MessageContext ctx) {
			if (ctx.side.isServer()) {
				final EntityPlayerMP player = ctx.getServerHandler().player;
				player.getServerWorld();
				if (player != null) {
					final WorldServer world = player.getServerWorld();
					world.addScheduledTask(new Runnable() {
						@Override
						public void run() {
							if (player.hasCapability(Skills.WOODCUTTING, EnumFacing.NORTH)) {
								SkillWoodcutting woodcutting = (SkillWoodcutting) player.getCapability(Skills.WOODCUTTING, EnumFacing.NORTH);
								BlockPos start = new BlockPos(message.x, message.y, message.z);
								ItemStack mainStack = player.getHeldItemMainhand();
								Item mainItem = mainStack.getItem();
								int addXp = woodcutting.hewTree(world, woodcutting, start, start, player, mainStack, (mainItem instanceof HandSaw), 3);
								woodcutting.addXp(player, addXp / 5);
							}
						}
					});
				}
			}
			return null;
		}

	}

}
