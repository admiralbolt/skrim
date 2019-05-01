package avi.mod.skrim.init;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.utils.Utils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

import java.util.Arrays;
import java.util.List;

/**
 * Houses all sound events!
 *
 * Sound files are stored in assets/skrim/sounds/.
 * Mapping from resource location -> sound is in assets/skrim/sounds.json
 */
public class SkrimSoundEvents {

  private static SoundEvent makeSoundEvent(String path) {
    return new SoundEvent(new ResourceLocation(Skrim.MOD_ID, path));
  }

  // Records.
  public static SoundEvent ARUARIAN_DANCE = makeSoundEvent("aruarian_dance");
  public static SoundEvent BUBBERDUCKY = makeSoundEvent("bubberducky");
  public static SoundEvent CASSANDRA = makeSoundEvent("cassandra");
  public static SoundEvent DOGSONG = makeSoundEvent("dogsong");
  public static SoundEvent GDAWG = makeSoundEvent("gdawg");
  public static SoundEvent HEYA = makeSoundEvent("heya");
  public static SoundEvent LACK_OF_COLOR = makeSoundEvent("money");
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

  private static List<SoundEvent> ZELDA_SOUNDS = Arrays.asList(ZELDA_BIG, ZELDA_SPOOKY, ZELDA_WINDWAKER, ZELDA_CHEST_OPEN);

  public static SoundEvent randomZeldaSound() {
    return ZELDA_SOUNDS.get(Utils.rand.nextInt(ZELDA_SOUNDS.size()));
  }

}
