package avi.mod.skrim.handlers;

import com.google.common.base.Function;

import avi.mod.skrim.entities.SkrimFishHook;
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

public class SkrimEntitySpawnHandler {

	@SideOnly(Side.CLIENT)
	public static void init() {
		EntityRegistration customFishHookRegistration = EntityRegistry.instance().lookupModSpawn(SkrimFishHook.class, false);
		Function<EntitySpawnMessage, Entity> fishHookSpawnHandler = new Function<EntitySpawnMessage, Entity>() {

			@Override
			public Entity apply(EntitySpawnMessage input) {
				int anglerId = 0;
				double posX = 0;
				double posY = 0;
				double posZ = 0;
				System.out.println("calling Apply");
				try {
					System.out.println("Try start");
					anglerId = ReflectionHelper.findField(EntitySpawnMessage.class, "throwerId").getInt(input);
					posX = ReflectionHelper.findField(EntitySpawnMessage.class, "rawX").getDouble(input);
					posY = ReflectionHelper.findField(EntitySpawnMessage.class, "rawY").getDouble(input);
					posZ = ReflectionHelper.findField(EntitySpawnMessage.class, "rawZ").getDouble(input);
					System.out.println("try end");
				} catch (IllegalArgumentException e) {
					System.out.println("Illegal Arguent");
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					System.out.println("illegal access");
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("anglerId: " + anglerId + ", posX: " + posX + ", posY: " + posY + ", posZ: " + posZ);
				WorldClient world = FMLClientHandler.instance().getWorldClient();

				Entity angler = world.getEntityByID(anglerId);
				System.out.println("angler: " + angler);
				if (!(angler instanceof EntityPlayer)) {
					return null;
				}
				
				System.out.println("creating skrim fish hook");

				return new SkrimFishHook(world, (EntityPlayer) angler, posX, posY, posZ);
			}
		};

		System.out.println("set Custom Spawning...");
		customFishHookRegistration.setCustomSpawning(fishHookSpawnHandler, false);
	}

}
