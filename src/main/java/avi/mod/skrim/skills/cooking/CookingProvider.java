package avi.mod.skrim.skills.cooking;

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
import avi.mod.skrim.Skrim;
import avi.mod.skrim.skills.SkillProvider;

public class CookingProvider {

    @CapabilityInject(ISkillCooking.class)
    public static final Capability<ISkillCooking> COOKING = null;
    public static final EnumFacing DEFAULT_FACING = null;
    public static final ResourceLocation ID = new ResourceLocation(Skrim.modId, "SkillCooking");

    public static void register() {
      CapabilityManager.INSTANCE.register(ISkillCooking.class, SkillCooking.skillStorage, SkillCooking.class);
      MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    public static class EventHandler {

      @SubscribeEvent
      public void attachCapabilities(AttachCapabilitiesEvent.Entity event) {
        Entity player = event.getEntity();
        if (player instanceof EntityPlayer) {
          if (!player.hasCapability(COOKING, EnumFacing.NORTH)) {
            event.addCapability(ID, SkillCookingProvider.instance);
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
    public static class SkillCookingProvider extends SkillProvider<ISkillCooking> {

      public static SkillCookingProvider instance = new SkillCookingProvider();
      private Entity player;

      public SkillCookingProvider() {
        this(new SkillCooking());
      }

      public SkillCookingProvider(ISkillCooking skill) {
        super(skill, COOKING);
      }

    }

}
