package avi.mod.skrim.skills.defense;

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

public class DefenseProvider {

    @CapabilityInject(ISkillDefense.class)
    public static final Capability<ISkillDefense> DEFENSE = null;
    public static final EnumFacing DEFAULT_FACING = null;
    public static final ResourceLocation ID = new ResourceLocation(Skrim.modId, "SkillDefense");

    public static void register() {
      CapabilityManager.INSTANCE.register(ISkillDefense.class, SkillDefense.skillStorage, SkillDefense.class);
      MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    public static class EventHandler {

      @SubscribeEvent
      public void attachCapabilities(AttachCapabilitiesEvent.Entity event) {
        Entity player = event.getEntity();
        if (player instanceof EntityPlayer) {
          if (!player.hasCapability(DEFENSE, EnumFacing.NORTH)) {
            event.addCapability(ID, createProvider());
          }
        }
      }

    }

    public static SkillProvider<ISkillDefense> createProvider() {
    	return new SkillProvider<ISkillDefense>(DEFENSE, EnumFacing.NORTH);
    }

    public static SkillProvider<ISkillDefense> createProvider(ISkillDefense defense) {
    	return new SkillProvider<ISkillDefense>(DEFENSE, EnumFacing.NORTH, defense);
    }

}
