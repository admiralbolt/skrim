package avi.mod.skrim.network;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.network.AdvancementPacket.AchievementPacketHandler;
import avi.mod.skrim.network.ExplosionPacket.ExplosionPacketHandler;
import avi.mod.skrim.network.FallDistancePacket.FallDistancePacketHandler;
import avi.mod.skrim.network.InvisibilityPacket.InvisibilityPacketHandler;
import avi.mod.skrim.network.LevelUpPacket.LevelUpPacketHandler;
import avi.mod.skrim.network.SkillEnablePacket.SkillEnablePacketHandler;
import avi.mod.skrim.network.SkillPacket.SkillPacketHandler;
import avi.mod.skrim.network.SortChestPacket.SortChestPacketHandler;
import avi.mod.skrim.network.SpawnEntityPacket.SpawnEntityPacketHandler;
import avi.mod.skrim.network.SpawnParticlePacket.SpawnParticlePacketHandler;
import avi.mod.skrim.network.ToggleAbilityPacket.ToggleAbilityPacketHandler;
import avi.mod.skrim.network.skillpackets.*;
import avi.mod.skrim.network.skillpackets.AngelFlyingSoundPacket.AngelFlyingSoundPacketHandler;
import avi.mod.skrim.network.skillpackets.ApplyBonemealPacket.ApplyBonemealPacketHandler;
import avi.mod.skrim.network.skillpackets.CriticalAscensionPacket.CriticalAscensionPacketHandler;
import avi.mod.skrim.network.skillpackets.DrillPacket.DrillPacketHandler;
import avi.mod.skrim.network.skillpackets.MetalDetectorPacket.MetalDetectorPacketHandler;
import avi.mod.skrim.network.skillpackets.OffHandAttackPacket.OffHandAttackPacketHandler;
import avi.mod.skrim.network.skillpackets.WhirlingChopPacket.WhirlingChopPacketHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class SkrimPacketHandler {

  public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Skrim.MOD_ID);
  public static int PACKET_ID = 0;

  public static void registerPackets() {
    // Use PACKET_ID++ to guarentee unique ids for packets.
    // REMEMBER: The side that receives the packet is here.
    INSTANCE.registerMessage(SkillPacketHandler.class, SkillPacket.class, PACKET_ID++, Side.CLIENT);
    INSTANCE.registerMessage(LevelUpPacketHandler.class, LevelUpPacket.class, PACKET_ID++, Side.CLIENT);
    INSTANCE.registerMessage(FallDistancePacketHandler.class, FallDistancePacket.class, PACKET_ID++, Side.SERVER);
    INSTANCE.registerMessage(SpawnParticlePacketHandler.class, SpawnParticlePacket.class, PACKET_ID++, Side.CLIENT);
    INSTANCE.registerMessage(OffHandAttackPacketHandler.class, OffHandAttackPacket.class, PACKET_ID++, Side.SERVER);
    INSTANCE.registerMessage(DrillPacketHandler.class, DrillPacket.class, PACKET_ID++, Side.SERVER);
    INSTANCE.registerMessage(ExplosionPacketHandler.class, ExplosionPacket.class, PACKET_ID++, Side.CLIENT);
    INSTANCE.registerMessage(ApplyBonemealPacketHandler.class, ApplyBonemealPacket.class, PACKET_ID++, Side.SERVER);
    INSTANCE.registerMessage(MetalDetectorPacketHandler.class, MetalDetectorPacket.class, PACKET_ID++, Side.SERVER);
    INSTANCE.registerMessage(WhirlingChopPacketHandler.class, WhirlingChopPacket.class, PACKET_ID++, Side.SERVER);
    INSTANCE.registerMessage(AchievementPacketHandler.class, AdvancementPacket.class, PACKET_ID++, Side.SERVER);
    INSTANCE.registerMessage(InvisibilityPacketHandler.class, InvisibilityPacket.class, PACKET_ID++, Side.SERVER);
    INSTANCE.registerMessage(CriticalAscensionPacketHandler.class, CriticalAscensionPacket.class, PACKET_ID++, Side.CLIENT);
    INSTANCE.registerMessage(SpawnEntityPacketHandler.class, SpawnEntityPacket.class, PACKET_ID++, Side.SERVER);
    INSTANCE.registerMessage(SortChestPacketHandler.class, SortChestPacket.class, PACKET_ID++, Side.SERVER);
    INSTANCE.registerMessage(AngelFlyingSoundPacketHandler.class, AngelFlyingSoundPacket.class, PACKET_ID++, Side.CLIENT);
    INSTANCE.registerMessage(ToggleAbilityPacketHandler.class, ToggleAbilityPacket.class, PACKET_ID++, Side.SERVER);
    INSTANCE.registerMessage(SkillEnablePacketHandler.class, SkillEnablePacket.class, PACKET_ID++, Side.CLIENT);
  }

}
