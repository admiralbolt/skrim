package avi.mod.skrim.skills.digging;

import java.util.Map;
import java.util.concurrent.Callable;

import com.sun.istack.internal.Nullable;
import avi.mod.skrim.Skrim;
import avi.mod.skrim.skills.SkillProvider;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DiggingProvider {

    @CapabilityInject(ISkillDigging.class)
    public static final Capability<ISkillDigging> DIGGING = null;
    public static final EnumFacing DEFAULT_FACING = null;
    public static final ResourceLocation ID = new ResourceLocation(Skrim.modId, "SkillDigging");

    public static void register() {
      CapabilityManager.INSTANCE.register(ISkillDigging.class, SkillDigging.skillStorage, SkillDigging.class);
      MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    public static class EventHandler {

      @SubscribeEvent
      public void attachCapabilities(AttachCapabilitiesEvent.Entity event) {
        Entity player = event.getEntity();
        if (player instanceof EntityPlayer) {
          if (!player.hasCapability(DIGGING, EnumFacing.NORTH)) {
            event.addCapability(ID, SkillDiggingProvider.instance);
          }
        }
      }

    }

    /**
     * I honestly have no idea if this is bad code or not,
     * but since i don't realy want to fuck with capabilities anymore,
     * they all get to pass around the same singleton.
     * It seems like both EntityPlayerSP and EntityPlayerMP need to be
     * registered with the capabilities, but doing so with separate instances
     * creates issues with duplicate events firing.
     */
    public static class SkillDiggingProvider extends SkillProvider<ISkillDigging> {

      public static SkillDiggingProvider instance = new SkillDiggingProvider();
      private Entity player;

      public SkillDiggingProvider() {
        this(new SkillDigging());
      }

      public SkillDiggingProvider(ISkillDigging skill) {
        super(skill, DIGGING);
      }

    }

}
