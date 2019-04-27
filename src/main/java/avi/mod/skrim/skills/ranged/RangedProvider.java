package avi.mod.skrim.skills.ranged;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.skills.SkillProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RangedProvider {

  @CapabilityInject(ISkillRanged.class)
  public static final Capability<ISkillRanged> RANGED = null;
  public static final EnumFacing DEFAULT_FACING = null;
  public static final ResourceLocation ID = new ResourceLocation(Skrim.MOD_ID, "SkillRanged");

  public static void register() {
    CapabilityManager.INSTANCE.register(ISkillRanged.class, SkillRanged.skillStorage, SkillRanged::new);
    MinecraftForge.EVENT_BUS.register(new EventHandler());
  }

  public static class EventHandler {

    @SubscribeEvent
    public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
      Entity player = event.getObject();
      if (player instanceof EntityPlayer) {
        if (!player.hasCapability(RANGED, EnumFacing.NORTH)) {
          event.addCapability(ID, createProvider());
        }
      }
    }

  }

  public static SkillProvider<ISkillRanged> createProvider() {
    return new SkillProvider<>(RANGED, EnumFacing.NORTH);
  }

  public static SkillProvider<ISkillRanged> createProvider(ISkillRanged ranged) {
    return new SkillProvider<>(RANGED, EnumFacing.NORTH, ranged);
  }

}
