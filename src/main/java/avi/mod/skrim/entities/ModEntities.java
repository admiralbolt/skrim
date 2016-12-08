package avi.mod.skrim.entities;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.entities.monster.BioCreeper;
import avi.mod.skrim.entities.monster.NapalmCreeper;
import avi.mod.skrim.entities.projectile.Rocket;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ModEntities {

	private static int ENTITY_ID = 0;

	public static void register() {
		EntityRegistry.registerModEntity(new ResourceLocation("skrim:custom_fish_hook"), CustomFishHook.class, "CustomFishHook", ENTITY_ID++, Skrim.instance, 64, 5, true);
		EntityRegistry.registerModEntity(new ResourceLocation("skrim:napalm_creeper"), NapalmCreeper.class, "napalm_creeper", ENTITY_ID++, Skrim.instance, 48, 3, true, 0xFF3000, 0xEE9000);
		EntityRegistry.addSpawn(NapalmCreeper.class, 10, 2, 4, EnumCreatureType.MONSTER, Biomes.DESERT, Biomes.DESERT_HILLS, Biomes.MUTATED_DESERT, Biomes.MUTATED_SAVANNA, Biomes.MUTATED_SAVANNA_ROCK, Biomes.SAVANNA_PLATEAU, Biomes.SAVANNA, Biomes.HELL);
		EntityRegistry.registerModEntity(new ResourceLocation("skrim:bio_creeper"), BioCreeper.class, "bio_creeper", ENTITY_ID++, Skrim.instance, 48, 3, true, 0x00CCEE, 0x00CCBB);
		EntityRegistry.addSpawn(BioCreeper.class, 20, 2, 4, EnumCreatureType.MONSTER, Biomes.SWAMPLAND, Biomes.COLD_TAIGA, Biomes.COLD_TAIGA_HILLS, Biomes.MUTATED_SWAMPLAND, Biomes.MUTATED_TAIGA_COLD);
		EntityRegistry.registerModEntity(new ResourceLocation("skrim:rocket"), Rocket.class, "rocket", ENTITY_ID++, Skrim.instance, 48, 3, true);
	}

}
