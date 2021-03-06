package avi.mod.skrim.skills.botany;

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

public class BotanyProvider {

  @CapabilityInject(ISkillBotany.class)
  public static final Capability<ISkillBotany> BOTANY = null;
  public static final EnumFacing DEFAULT_FACING = null;
  public static final ResourceLocation ID = new ResourceLocation(Skrim.MOD_ID, "SkillBotany");

  public static void register() {
    CapabilityManager.INSTANCE.register(ISkillBotany.class, SkillBotany.skillStorage, SkillBotany::new);
    MinecraftForge.EVENT_BUS.register(new EventHandler());
  }

  public static class EventHandler {

    @SubscribeEvent
    public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
      Entity player = event.getObject();
      if (player instanceof EntityPlayer) {
        if (!player.hasCapability(BOTANY, EnumFacing.NORTH)) {
          event.addCapability(ID, createProvider());
        }
      }
    }

  }

  public static SkillProvider<ISkillBotany> createProvider() {
    return new SkillProvider<>(BOTANY, EnumFacing.NORTH);
  }

  public static SkillProvider<ISkillBotany> createProvider(ISkillBotany botany) {
    return new SkillProvider<>(BOTANY, EnumFacing.NORTH, botany);
  }

}
