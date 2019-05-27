package avi.mod.skrim.skills.brewing;

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

public class BrewingProvider {

  @CapabilityInject(ISkillBrewing.class)
  public static final Capability<ISkillBrewing> BREWING= null;
  public static final EnumFacing DEFAULT_FACING = null;
  public static final ResourceLocation ID = new ResourceLocation(Skrim.MOD_ID, "SkillBrewing");

  public static void register() {
    CapabilityManager.INSTANCE.register(ISkillBrewing.class, SkillBrewing.STORAGE,
        SkillBrewing::new);
    MinecraftForge.EVENT_BUS.register(new EventHandler());
  }

  public static SkillProvider<ISkillBrewing> createProvider() {
    return new SkillProvider<>(BREWING, EnumFacing.NORTH);
  }

  public static SkillProvider<ISkillBrewing> createProvider(ISkillBrewing blacksmithing) {
    return new SkillProvider<>(BREWING, EnumFacing.NORTH, blacksmithing);
  }

  public static class EventHandler {

    @SubscribeEvent
    public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
      Entity player = event.getObject();
      if (player instanceof EntityPlayer) {
        if (!player.hasCapability(BREWING, EnumFacing.NORTH)) {
          event.addCapability(ID, createProvider());
        }
      }
    }

  }

}
