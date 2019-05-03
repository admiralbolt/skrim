package avi.mod.skrim.init;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.utils.Utils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Houses all sound events!
 * <p>
 * Sound files are stored in assets/skrim/sounds/.
 * Mapping from resource location -> sound is in assets/skrim/sounds.json
 */
public class SkrimSoundEvents {

  private static Set<SoundEvent> SOUND_EVENTS = new HashSet<>();

  private static SoundEvent makeSoundEvent(String path) {
    SoundEvent event = new SoundEvent(new ResourceLocation(Skrim.MOD_ID, path)).setRegistryName(path);
    SOUND_EVENTS.add(event);
    return event;
  }

  // Records.
  public static SoundEvent ARUARIAN_DANCE = makeSoundEvent("aruarian_dance");
  public static SoundEvent BUBBERDUCKY = makeSoundEvent("bubberducky");
  public static SoundEvent CASSANDRA = makeSoundEvent("cassandra");
  public static SoundEvent DOGSONG = makeSoundEvent("dogsong");
  public static SoundEvent GDAWG = makeSoundEvent("gdawg");
  public static SoundEvent HEYA = makeSoundEvent("heya");
  public static SoundEvent LACK_OF_COLOR = makeSoundEvent("lack_of_color");
  public static SoundEvent MONEY = makeSoundEvent("money");
  public static SoundEvent NORTH = makeSoundEvent("north");
  public static SoundEvent NUMBER10 = makeSoundEvent("number10");
  public static SoundEvent SAMURAI = makeSoundEvent("samurai");
  public static SoundEvent TRUCK = makeSoundEvent("truck");

  // Zelda chest sounds.
  private static SoundEvent ZELDA_BIG = makeSoundEvent("zelda_big");
  private static SoundEvent ZELDA_SPOOKY = makeSoundEvent("zelda_spooky");
  private static SoundEvent ZELDA_WINDWAKER = makeSoundEvent("zelda_windwaker");
  private static SoundEvent ZELDA_CHEST_OPEN = makeSoundEvent("zelda_chest_open");

  // Skill sounds.
  public static SoundEvent ANGEL_CAKE_FLYING = makeSoundEvent("angel_cake_flying");
  public static SoundEvent CRITICAL_HIT = makeSoundEvent("critical_hit");
  public static SoundEvent HEAD_SHOT = makeSoundEvent("head_shot");
  public static SoundEvent RANDOM_TREASURE = makeSoundEvent("random_treasure");
  public static SoundEvent SNEAK_ATTACK = makeSoundEvent("sneak_attack");
  public static SoundEvent SPIN_SLASH = makeSoundEvent("spin_slash");

  // Artifact sounds.
  public static SoundEvent SHINESPARK_START = makeSoundEvent("shinespark_start");
  public static SoundEvent SHINESPARK_LOOP = makeSoundEvent("shinespark_loop");
  public static SoundEvent HOME_RUN = makeSoundEvent("home_run");
  public static SoundEvent WIND_WAKER = makeSoundEvent("wind_waker");
  public static SoundEvent SPIN_JUMP = makeSoundEvent("spin_jump");
  public static SoundEvent NAIL_ART_GREAT_SLASH = makeSoundEvent("nail_art_great_slash");
  public static SoundEvent BAN_HAMMER = makeSoundEvent("ban_hammer");

  private static List<SoundEvent> ZELDA_SOUNDS = Arrays.asList(ZELDA_BIG, ZELDA_SPOOKY, ZELDA_WINDWAKER, ZELDA_CHEST_OPEN);

  public static SoundEvent randomZeldaSound() {
    return ZELDA_SOUNDS.get(Utils.rand.nextInt(ZELDA_SOUNDS.size()));
  }

  @Mod.EventBusSubscriber(modid = Skrim.MOD_ID)
  public static class Handler {

    @SubscribeEvent
    public static void registerSoundEvents(final RegistryEvent.Register<SoundEvent> event) {
      SOUND_EVENTS.forEach(event.getRegistry()::register);
    }

  }

}
