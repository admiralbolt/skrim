package avi.mod.skrim.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import avi.mod.skrim.Skrim;
import avi.mod.skrim.network.SkillPacket.SkillPacketHandler;

public class SkrimPacketHandler {

  public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Skrim.modId);
  public static int id = 0;

  public static void registerSkillPackets() {
  	// Use id++ to guarentee unique ids for packets.
  	INSTANCE.registerMessage(SkillPacketHandler.class, SkillPacket.class, id++, Side.CLIENT);
  	System.out.println("Registered server side skill handler.");
  }

}
