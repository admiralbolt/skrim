package avi.mod.skrim.skills.mining;

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

public class MiningProvider {

    @CapabilityInject(ISkillMining.class)
    public static final Capability<ISkillMining> MINING = null;
    public static final EnumFacing DEFAULT_FACING = null;
    public static final ResourceLocation ID = new ResourceLocation(Skrim.modId, "SkillMining");

    public static void register() {
      CapabilityManager.INSTANCE.register(ISkillMining.class, SkillMining.skillStorage, SkillMining.class);
      MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    public static class EventHandler {

      @SubscribeEvent
      public void attachCapabilities(AttachCapabilitiesEvent.Entity event) {
        Entity player = event.getEntity();
        if (player instanceof EntityPlayer) {
          if (!player.hasCapability(MINING, EnumFacing.NORTH)) {
            event.addCapability(ID, SkillMiningProvider.instance);
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
    public static class SkillMiningProvider extends SkillProvider<ISkillMining> {

      public static SkillMiningProvider instance = new SkillMiningProvider();
      private Entity player;

      public SkillMiningProvider() {
        this(new SkillMining());
      }

      public SkillMiningProvider(ISkillMining skill) {
        super(skill, MINING);
      }

    }

}
