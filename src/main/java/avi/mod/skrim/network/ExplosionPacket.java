package avi.mod.skrim.network;

import avi.mod.skrim.blocks.tnt.CustomTNTPrimed;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.Explosion;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Make boom.
 */
public class ExplosionPacket implements IMessage {

  private String explosionType;
  private float explosionSize;
  private int entityId;
  public double posX;
  public double posY;
  public double posZ;

  public ExplosionPacket() {
  }

  public ExplosionPacket(String explosionType, float explosionSize, int entityId, double posX, double posY, double posZ) {
    this.explosionType = explosionType;
    this.explosionSize = explosionSize;
    this.entityId = entityId;
    this.posX = posX;
    this.posY = posY;
    this.posZ = posZ;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    ByteBufUtils.writeUTF8String(buf, this.explosionType);
    buf.writeFloat(this.explosionSize);
    buf.writeInt(this.entityId);
    buf.writeDouble(this.posX);
    buf.writeDouble(this.posY);
    buf.writeDouble(this.posZ);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    this.explosionType = ByteBufUtils.readUTF8String(buf);
    this.explosionSize = buf.readFloat();
    this.entityId = buf.readInt();
    this.posX = buf.readDouble();
    this.posY = buf.readDouble();
    this.posZ = buf.readDouble();
  }

  public static class ExplosionPacketHandler implements IMessageHandler<ExplosionPacket, IMessage> {

    @SideOnly(Side.CLIENT)
    @Override
    public IMessage onMessage(final ExplosionPacket message, MessageContext ctx) {
      if (ctx.side.isServer()) return null;

      final IThreadListener mainThread = Minecraft.getMinecraft();
      final EntityPlayer player = Minecraft.getMinecraft().player;
      mainThread.addScheduledTask(() -> {
        Entity entity = player.world.getEntityByID(message.entityId);
        Explosion explosion = CustomTNTPrimed.createExplosion(message.explosionType, message.explosionSize, player.world, entity,
            message.posX, message.posY,
            message.posZ);
        explosion.doExplosionA();
        explosion.doExplosionB(true);
      });
      return null;
    }
  }
}