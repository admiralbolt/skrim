package avi.mod.skrim.init;

import java.util.ArrayList;
import java.util.List;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.utils.Utils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class SkrimSoundEvents {

	// Records
	public static SoundEvent ARUARIAN_DANCE;
	public static SoundEvent BUBBERDUCKY;
	public static SoundEvent CASSANDRA;
	public static SoundEvent DOGSONG;
	public static SoundEvent GDAWG;
	public static SoundEvent HEYA;
	public static SoundEvent LACK_OF_COLOR;
	public static SoundEvent MONEY;
	public static SoundEvent NORTH;
	public static SoundEvent NUMBER10;
	public static SoundEvent SAMURAI;
	public static SoundEvent TRUCK;

	public static SoundEvent ZELDA_BIG;
	public static SoundEvent ZELDA_SPOOKY;
	public static SoundEvent ZELDA_WINDWAKER;
	public static SoundEvent ZELDA_CHEST_OPEN;

	// Skill sounds
	public static SoundEvent ANGEL_CAKE_FLYING;
	public static SoundEvent CRITICAL_HIT;
	public static SoundEvent HEAD_SHOT;
	public static SoundEvent RANDOM_TREASURE;
	public static SoundEvent SNEAK_ATTACK;
	public static SoundEvent SPIN_SLASH;

	// Artifact sounds
	public static SoundEvent SHINESPARK_START;
	public static SoundEvent SHINESPARK_LOOP;

	public static List<SoundEvent> ZELDA_SOUNDS = new ArrayList<SoundEvent>();
	public static List<SoundEvent> RECORDS = new ArrayList<SoundEvent>();

	public static SoundEvent registerSoundEvent(String path, List<SoundEvent> addList) {
		SoundEvent event = registerSoundEvent(path);
		addList.add(event);
		return event;
	}

	public static SoundEvent registerSoundEvent(String path) {
		ResourceLocation location = new ResourceLocation(Skrim.modId, path);
		SoundEvent event = new SoundEvent(location);
		// Don't actually need to call // [REGISTRY] GameRegistry.register() anymore
		// // [REGISTRY] GameRegistry.register(event, location);
		return event;
	}

	public static void register() {
		ARUARIAN_DANCE = registerSoundEvent("aruarian_dance", RECORDS);
		BUBBERDUCKY = registerSoundEvent("bubberducky", RECORDS);
		CASSANDRA = registerSoundEvent("cassandra", RECORDS);
		DOGSONG = registerSoundEvent("dogsong", RECORDS);
		GDAWG = registerSoundEvent("gdawg", RECORDS);
		HEYA = registerSoundEvent("heya", RECORDS);
		LACK_OF_COLOR = registerSoundEvent("lack_of_color", RECORDS);
		MONEY = registerSoundEvent("money", RECORDS);
		NORTH = registerSoundEvent("north", RECORDS);
		NUMBER10 = registerSoundEvent("number10", RECORDS);
		SAMURAI = registerSoundEvent("samurai", RECORDS);
		TRUCK = registerSoundEvent("truck", RECORDS);

		ZELDA_BIG = registerSoundEvent("zelda_big", ZELDA_SOUNDS);
		ZELDA_SPOOKY = registerSoundEvent("zelda_spooky", ZELDA_SOUNDS);
		ZELDA_WINDWAKER = registerSoundEvent("zelda_windwaker", ZELDA_SOUNDS);
		ZELDA_CHEST_OPEN = registerSoundEvent("zelda_chest_open", ZELDA_SOUNDS);

		ANGEL_CAKE_FLYING = registerSoundEvent("angel_cake_flying");
		CRITICAL_HIT = registerSoundEvent("critical_hit");
		HEAD_SHOT = registerSoundEvent("head_shot");
		RANDOM_TREASURE = registerSoundEvent("random_treasure");
		SNEAK_ATTACK = registerSoundEvent("sneak_attack");
		SPIN_SLASH = registerSoundEvent("spin_slash");

		SHINESPARK_START = registerSoundEvent("shinespark_start");
		SHINESPARK_LOOP = registerSoundEvent("shinespark_loop");
	}

	public static SoundEvent randomZeldaSound() {
		return ZELDA_SOUNDS.get(Utils.rand.nextInt(ZELDA_SOUNDS.size()));
	}

}
