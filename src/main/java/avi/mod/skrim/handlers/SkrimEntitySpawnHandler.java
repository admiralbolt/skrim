package avi.mod.skrim.handlers;

import avi.mod.skrim.entities.SkrimFishHook;
import avi.mod.skrim.utils.ReflectionUtils;
import com.google.common.base.Function;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.internal.FMLMessage.EntitySpawnMessage;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry.EntityRegistration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Overrides the default fish hook spawned when a fishing rod is right clicked.
 * <p>
 * Most of the fishing logic is in the fishing hook entity, so by providing our custom entity instead of the default,
 * we can control more of the fishing functionality.
 */
public class SkrimEntitySpawnHandler {

  @SideOnly(Side.CLIENT)
  public static void init() {
    EntityRegistration customFishHookRegistration = EntityRegistry.instance().lookupModSpawn(SkrimFishHook.class,
        false);
    Function<EntitySpawnMessage, Entity> fishHookSpawnHandler = (EntitySpawnMessage input) -> {
      try {
        // Duplicate the "angler" aka the player who cast the line, as well as the x/y/z coordinate of the hook.
        int anglerId = (int) ReflectionUtils.getPrivateField(input, "throwerId");
        double posX = (double) ReflectionUtils.getPrivateField(input, "rawX");
        double posY = (double) ReflectionUtils.getPrivateField(input, "rawY");
        double posZ = (double) ReflectionUtils.getPrivateField(input, "rawZ");

        WorldClient world = FMLClientHandler.instance().getWorldClient();
        Entity angler = world.getEntityByID(anglerId);
        if (!(angler instanceof EntityPlayer)) return null;

        return new SkrimFishHook(world, (EntityPlayer) angler, posX, posY, posZ);
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      }

      return null;
    };

    customFishHookRegistration.setCustomSpawning(fishHookSpawnHandler, false);
  }

}
