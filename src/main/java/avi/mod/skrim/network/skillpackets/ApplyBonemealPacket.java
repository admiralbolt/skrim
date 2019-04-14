package avi.mod.skrim.network.skillpackets;

import avi.mod.skrim.utils.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Applies bonemeal to a square. Used by the Overalls.
 */
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
      if (ctx.side.isClient()) return null;

      final EntityPlayerMP player = ctx.getServerHandler().player;
      if (player == null) return null;

      final WorldServer world = player.getServerWorld();
      world.addScheduledTask(() -> {
        ItemStack mainStack = player.getHeldItemMainhand();
        ItemStack overalls = player.inventory.armorInventory.get(2);
        BlockPos targetPos = new BlockPos(message.x, message.y, message.z);
        IBlockState targetState = world.getBlockState(targetPos);
        Block targetBlock = targetState.getBlock();
        ((IGrowable) targetBlock).grow(player.world, Utils.rand, targetPos, targetState);
        overalls.damageItem(2, player);
        mainStack.damageItem(1, player);
      });
      return null;
    }

  }

}
