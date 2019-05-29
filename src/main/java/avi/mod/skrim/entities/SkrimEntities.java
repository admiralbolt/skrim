package avi.mod.skrim.entities;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.entities.items.EntityKingOfRedLions;
import avi.mod.skrim.entities.monster.BioCreeper;
import avi.mod.skrim.entities.monster.GigaChicken;
import avi.mod.skrim.entities.monster.MegaChicken;
import avi.mod.skrim.entities.monster.NapalmCreeper;
import avi.mod.skrim.entities.passive.EntityFox;
import avi.mod.skrim.entities.passive.EntityPumpkow;
import avi.mod.skrim.entities.passive.EntityWatermoolon;
import avi.mod.skrim.entities.projectile.Rocket;
import avi.mod.skrim.entities.projectile.SkrimEntityPotion;
import avi.mod.skrim.items.artifacts.DeathArrow;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.HashSet;
import java.util.Set;

@GameRegistry.ObjectHolder(Skrim.MOD_ID)
public class SkrimEntities {

  public static final EntityEntry SKRIM_FISH_HOOK = null;
  public static final EntityEntry ROCKET = null;
  public static final EntityEntry NAPALM_CREEPER = null;
  public static final EntityEntry BIO_CREEPER = null;
  public static final EntityEntry MEGA_CHICKEN = null;
  public static final EntityEntry GIGA_CHICKEN = null;
  public static final EntityEntry FOX = null;
  public static final EntityEntry DEATH_ARROW = null;
  public static final EntityEntry KING_OF_RED_LIONS = null;
  public static final EntityEntry PUMPKOW = null;

  @Mod.EventBusSubscriber(modid = Skrim.MOD_ID)
  public static class RegistrationHandler {

    private static int ENTITY_ID = 0;

    @SubscribeEvent
    public static void registerEntities(final RegistryEvent.Register<EntityEntry> event) {
      final EntityEntry[] entries = {
          createBuilder("skrim_fish_hook").entity(SkrimFishHook.class).tracker(64, 10, true).build(),
          createBuilder("rocket").entity(Rocket.class).tracker(48, 3, true).build(),
          createBuilder("skrim_potion").entity(SkrimEntityPotion.class).tracker(48, 3, true).build(),
          createBuilder("napalm_creeper").entity(NapalmCreeper.class).tracker(48, 3, true).egg(0xFF3000, 0xEE9000).build(),
          createBuilder("bio_creeper").entity(BioCreeper.class).tracker(48, 3, true).egg(0x00CCEE, 0x00CCBB).build(),
          createBuilder("mega_chicken").entity(MegaChicken.class).tracker(48, 3, true).egg(0xFFFFFF, 0xFF8888).build(),
          createBuilder("giga_chicken").entity(GigaChicken.class).tracker(48, 3, true).egg(0xFFFFFF, 0x000000).build(),
          createBuilder("fox").entity(EntityFox.class).tracker(48, 3, true).egg(0xFF9900, 0xFF4400).build(),
          createBuilder("death_arrow").entity(DeathArrow.EntityDeathArrow.class).tracker(48, 3, true).build(),
          createBuilder("king_of_red_lions").entity(EntityKingOfRedLions.class).tracker(48, 3, true).build(),
          createBuilder("pumpkow").entity(EntityPumpkow.class).tracker(48, 3, true).build(),
          createBuilder("watermoolon").entity(EntityWatermoolon.class).tracker(48, 3, true).build()
      };

      event.getRegistry().registerAll(entries);
      addSpawns();
    }

    private static void addSpawns() {
      EntityRegistry.addSpawn(NapalmCreeper.class, 15, 2, 6, EnumCreatureType.MONSTER, getBiomes(BiomeDictionary.Type.HOT));
      EntityRegistry.addSpawn(BioCreeper.class, 20, 2, 6, EnumCreatureType.MONSTER, getBiomes(BiomeDictionary.Type.COLD,
          BiomeDictionary.Type.SWAMP, BiomeDictionary.Type.CONIFEROUS));
    }

    private static Biome[] getBiomes(BiomeDictionary.Type... types) {
      Set<Biome> biomes = new HashSet<>();
      for (BiomeDictionary.Type type : types) {
        biomes.addAll(BiomeDictionary.getBiomes(type));
      }
      return biomes.toArray(new Biome[0]);
    }

    /**
     * Create an {@link EntityEntryBuilder} with the specified registry name/translation key and an automatically-assigned network ID.
     *
     * @param name The name
     * @param <E>  The entity type
     * @return The builder
     */
    private static <E extends Entity> EntityEntryBuilder<E> createBuilder(final String name) {
      final EntityEntryBuilder<E> builder = EntityEntryBuilder.create();
      final ResourceLocation registryName = new ResourceLocation(Skrim.MOD_ID, name);
      return builder.id(registryName, ENTITY_ID++).name(registryName.toString());
    }
  }

}
