package avi.mod.skrim.network;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.network.AchievementPacket.AchievementPacketHandler;
import avi.mod.skrim.network.ExplosionPacket.ExplosionPacketHandler;
import avi.mod.skrim.network.FallDistancePacket.FallDistancePacketHandler;
import avi.mod.skrim.network.LevelUpPacket.LevelUpPacketHandler;
import avi.mod.skrim.network.SkillPacket.SkillPacketHandler;
import avi.mod.skrim.network.SpawnHeartPacket.SpawnHeartPacketHandler;
import avi.mod.skrim.network.skillpackets.ApplyBonemealPacket;
import avi.mod.skrim.network.skillpackets.ApplyBonemealPacket.ApplyBonemealPacketHandler;
import avi.mod.skrim.network.skillpackets.DrillPacket;
import avi.mod.skrim.network.skillpackets.DrillPacket.DrillPacketHandler;
import avi.mod.skrim.network.skillpackets.MetalDetectorPacket;
import avi.mod.skrim.network.skillpackets.MetalDetectorPacket.MetalDetectorPacketHandler;
import avi.mod.skrim.network.skillpackets.OffHandAttackPacket;
import avi.mod.skrim.network.skillpackets.OffHandAttackPacket.OffHandAttackPacketHandler;
import avi.mod.skrim.network.skillpackets.WhirlingChopPacket;
import avi.mod.skrim.network.skillpackets.WhirlingChopPacket.WhirlingChopPacketHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class SkrimPacketHandler {

	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Skrim.modId);
	public static int id = 0;

	public static void registerPackets() {
		// Use id++ to guarentee unique ids for packets.
		// REMEMBER: The side that receives the packet is here.
		INSTANCE.registerMessage(SkillPacketHandler.class, SkillPacket.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(LevelUpPacketHandler.class, LevelUpPacket.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(FallDistancePacketHandler.class, FallDistancePacket.class, id++, Side.SERVER);
		INSTANCE.registerMessage(SpawnHeartPacketHandler.class, SpawnHeartPacket.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(OffHandAttackPacketHandler.class, OffHandAttackPacket.class, id++, Side.SERVER);
		INSTANCE.registerMessage(DrillPacketHandler.class, DrillPacket.class, id++, Side.SERVER);
		INSTANCE.registerMessage(ExplosionPacketHandler.class, ExplosionPacket.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(ApplyBonemealPacketHandler.class, ApplyBonemealPacket.class, id++, Side.SERVER);
		INSTANCE.registerMessage(MetalDetectorPacketHandler.class, MetalDetectorPacket.class, id++, Side.SERVER);
		INSTANCE.registerMessage(WhirlingChopPacketHandler.class, WhirlingChopPacket.class, id++, Side.SERVER);
		INSTANCE.registerMessage(AchievementPacketHandler.class, AchievementPacket.class, id++, Side.SERVER);
	}

}
