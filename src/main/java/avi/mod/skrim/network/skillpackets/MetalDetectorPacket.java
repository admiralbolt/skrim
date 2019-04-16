package avi.mod.skrim.network.skillpackets;

import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.digging.SkillDigging;
import avi.mod.skrim.world.loot.CustomLootTables;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Level 50 digging skill!
 */
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
      if (ctx.side.isClient()) return null;

      final EntityPlayerMP player = ctx.getServerHandler().player;
      final WorldServer world = player.getServerWorld();
      world.addScheduledTask(() -> {
        if (!player.hasCapability(Skills.DIGGING, EnumFacing.NORTH)) return;
        SkillDigging digging = (SkillDigging) player.getCapability(Skills.DIGGING, EnumFacing.NORTH);
        EntityItem entityItem = new EntityItem(world, player.posX, player.posY, player.posZ,
            CustomLootTables.getMetalTreasure(world, player, digging.level));
        world.spawnEntity(entityItem);
        Skills.playFortuneSound(player);
        digging.addXp(player, 200);
      });
      return null;
    }
  }

}
