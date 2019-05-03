package avi.mod.skrim.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class ServerProxy implements IProxy {

  public static Configuration config;


  @Override
  public void preInit() {

  }

  @Override
  public void init() {
  }

  @Override
  public void postInit() {

  }

  @Override
  public void doClientRightClick() {
    throw new WrongSideException("Tried to perform client right click on wrong side.");
  }

  @Nullable
  @Override
  public EntityPlayer getClientPlayer() {
    throw new WrongSideException("Tried to get the client player on the dedicated server");
  }

  @Nullable
  @Override
  public World getClientWorld() {
    throw new WrongSideException("Tried to get the client world on the dedicated server");
  }

  @Override
  public IThreadListener getThreadListener(MessageContext context) {
    if (context.side.isServer()) return context.getServerHandler().player.getServer();

    throw new WrongSideException("Tried to get the IThreadListener from a client-side MessageContext on the dedicated" +
        " server");
  }

  @Override
  public EntityPlayer getPlayer(MessageContext context) {
    if (context.side.isServer()) return context.getServerHandler().player;
    throw new WrongSideException("Tried to get the player from a client-side MessageContext on the dedicated server");
  }
}
