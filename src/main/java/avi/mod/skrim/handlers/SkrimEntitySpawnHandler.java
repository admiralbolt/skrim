package avi.mod.skrim.handlers;

import avi.mod.skrim.entities.SkrimFishHook;
import com.google.common.base.Function;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.internal.FMLMessage.EntitySpawnMessage;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry.EntityRegistration;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
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
        int anglerId = ReflectionHelper.findField(EntitySpawnMessage.class, "throwerId").getInt(input);
        double posX = ReflectionHelper.findField(EntitySpawnMessage.class, "rawX").getDouble(input);
        double posY = ReflectionHelper.findField(EntitySpawnMessage.class, "rawY").getDouble(input);
        double posZ = ReflectionHelper.findField(EntitySpawnMessage.class, "rawZ").getDouble(input);

        WorldClient world = FMLClientHandler.instance().getWorldClient();
        Entity angler = world.getEntityByID(anglerId);
        if (!(angler instanceof EntityPlayer)) return null;

        return new SkrimFishHook(world, (EntityPlayer) angler, posX, posY, posZ);
      } catch (IllegalArgumentException | IllegalAccessException e) {
        System.out.println("Illegal Arguent");
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      return null;
    };

    customFishHookRegistration.setCustomSpawning(fishHookSpawnHandler, false);
  }

}
