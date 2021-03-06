package avi.mod.skrim.skills.melee;

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

public class MeleeProvider {

  @CapabilityInject(ISkillMelee.class)
  public static final Capability<ISkillMelee> MELEE = null;
  public static final EnumFacing DEFAULT_FACING = null;
  public static final ResourceLocation ID = new ResourceLocation(Skrim.MOD_ID, "SkillMelee");

  public static void register() {
    CapabilityManager.INSTANCE.register(ISkillMelee.class, SkillMelee.skillStorage, SkillMelee::new);
    MinecraftForge.EVENT_BUS.register(new EventHandler());
  }

  public static SkillProvider<ISkillMelee> createProvider() {
    return new SkillProvider<>(MELEE, EnumFacing.NORTH);
  }

  public static SkillProvider<ISkillMelee> createProvider(ISkillMelee melee) {
    return new SkillProvider<>(MELEE, EnumFacing.NORTH, melee);
  }

  public static class EventHandler {

    @SubscribeEvent
    public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
      Entity player = event.getObject();
      if (player instanceof EntityPlayer) {
        if (!player.hasCapability(MELEE, EnumFacing.NORTH)) {
          event.addCapability(ID, createProvider());
        }
      }
    }

  }

}
