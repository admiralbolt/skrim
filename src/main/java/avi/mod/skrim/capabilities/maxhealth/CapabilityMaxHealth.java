package avi.mod.skrim.capabilities.maxhealth;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.capabilities.SimpleCapabilityProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;

public class CapabilityMaxHealth {

  @CapabilityInject(IMaxHealth.class)
  public static final Capability<IMaxHealth> MAX_HEALTH_CAPABILITY = null;

  public static final EnumFacing DEFAULT_FACING = null;
  public static final ResourceLocation ID = new ResourceLocation(Skrim.MOD_ID, "MaxHealth");

  public static void register() {
    CapabilityManager.INSTANCE.register(IMaxHealth.class, new Capability.IStorage<IMaxHealth>() {
      @Override
      public NBTBase writeNBT(Capability<IMaxHealth> capability, IMaxHealth instance, EnumFacing side) {
        return new NBTTagFloat(instance.getBonusMaxHealth());
      }

      @Override
      public void readNBT(Capability<IMaxHealth> capability, IMaxHealth instance, EnumFacing side, NBTBase nbt) {
        instance.setBonusMaxHealth(((NBTTagFloat) nbt).getFloat());
      }
    }, () -> new MaxHealth(null));
  }


  @Nullable
  public static IMaxHealth getMaxHealth(EntityLivingBase entity) {
    return entity.getCapability(MAX_HEALTH_CAPABILITY, DEFAULT_FACING);
  }

  public static ICapabilityProvider createProvider(IMaxHealth maxHealth) {
    return new SimpleCapabilityProvider<>(MAX_HEALTH_CAPABILITY, DEFAULT_FACING, maxHealth);
  }


  @Mod.EventBusSubscriber
  public static class EventHandler {

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
      if (event.getObject() instanceof EntityPlayer) {
        final MaxHealth maxHealth = new MaxHealth((EntityLivingBase) event.getObject());
        event.addCapability(ID, createProvider(maxHealth));
      }
    }

    @SubscribeEvent
    public static void playerClone(PlayerEvent.Clone event) {
      final IMaxHealth oldMaxHealth = getMaxHealth(event.getOriginal());
      final IMaxHealth newMaxHealth = getMaxHealth(event.getEntityPlayer());

      if (newMaxHealth != null && oldMaxHealth != null) {
        newMaxHealth.setBonusMaxHealth(oldMaxHealth.getBonusMaxHealth());
      }
    }
  }
}