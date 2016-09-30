package avi.mod.skrim.network.skillpackets;

import avi.mod.skrim.utils.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ApplyBonemealPacket implements IMessage {

	public int x;
	public int y;
	public int z;

	public ApplyBonemealPacket() {

	}

	public ApplyBonemealPacket(int x, int y, int z) {
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

	public static class ApplyBonemealPacketHandler implements IMessageHandler<ApplyBonemealPacket, IMessage> {

		public IMessage onMessage(final ApplyBonemealPacket message, MessageContext ctx) {
			if (ctx.side.isServer()) {
				final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
				if (player != null) {
					final WorldServer world = player.getServerWorld();
					world.addScheduledTask(new Runnable() {
						@Override
						public void run() {
							ItemStack mainStack = player.getHeldItemMainhand();
							InventoryPlayer inventory = player.inventory;
							ItemStack stack = inventory.armorInventory[2];
							BlockPos targetPos = new BlockPos(message.x, message.y, message.z);
							IBlockState targetState = world.getBlockState(targetPos);
							Block targetBlock = targetState.getBlock();
							((IGrowable) targetBlock).grow(player.worldObj, Utils.rand, targetPos, targetState);
							stack.damageItem(2, player);
							mainStack.damageItem(1, player);
						}
					});
				}
			}
			return null;
		}

	}

}
