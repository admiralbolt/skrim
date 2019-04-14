package avi.mod.skrim.network.skillpackets;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Level 100 mining skill!
 */
public class DrillPacket implements IMessage {

  public int x;
  public int y;
  public int z;

  public DrillPacket() {
  }

  public DrillPacket(int x, int y, int z) {
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

  public static class DrillPacketHandler implements IMessageHandler<DrillPacket, IMessage> {

    public IMessage onMessage(final DrillPacket message, MessageContext ctx) {
      if (ctx.side.isClient()) return null;
      final EntityPlayerMP player = ctx.getServerHandler().player;
      final WorldServer world = player.getServerWorld();
      world.addScheduledTask(() -> {
        ItemStack mainStack = player.getHeldItemMainhand();
        BlockPos targetPos = new BlockPos(message.x, message.y, message.z);
        IBlockState targetState = world.getBlockState(targetPos);
        Block targetBlock = targetState.getBlock();
        for (int y = targetPos.getY(); y >= 1; y--) {
          if (targetBlock == Blocks.BEDROCK) break;
          if (targetBlock.canHarvestBlock(world, targetPos, player)) {
            targetBlock.harvestBlock(world, player, targetPos, targetState, null, mainStack);
          }
          world.destroyBlock(targetPos, false);
          mainStack.onBlockDestroyed(world, targetState, targetPos, player);
          targetPos = targetPos.add(0, -1, 0);
          targetState = world.getBlockState(targetPos);
          targetBlock = targetState.getBlock();
        }
      });
      return null;
    }

  }

}
