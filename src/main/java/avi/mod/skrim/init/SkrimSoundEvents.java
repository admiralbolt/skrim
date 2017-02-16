package avi.mod.skrim.init;

import java.util.ArrayList;
import java.util.List;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.utils.Utils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class SkrimSoundEvents {
	
	public static SoundEvent ZELDA_BIG;
	public static SoundEvent ZELDA_SPOOKY;
	
	public static List<SoundEvent> ZELDA_SOUNDS;
	
	public static SoundEvent registerSoundEvent(String path, List<SoundEvent> addList) {
		ResourceLocation location = new ResourceLocation(Skrim.modId, path);
		SoundEvent event = new SoundEvent(location);
		GameRegistry.register(event, location);
		addList.add(event);
		return event;
	}
	
	public static SoundEvent registerSoundEvent(String path) {
		ResourceLocation location = new ResourceLocation(Skrim.modId, path);
		SoundEvent event = new SoundEvent(location);
		GameRegistry.register(event, location);
		return event;
	}
	
	public static void register() {
		ZELDA_SOUNDS = new ArrayList<SoundEvent>();
		ZELDA_BIG = registerSoundEvent("zelda_big", ZELDA_SOUNDS);
		ZELDA_SPOOKY = registerSoundEvent("zelda_spooky", ZELDA_SOUNDS);
	}
	
	public static SoundEvent randomZeldaSound() {
		return ZELDA_SOUNDS.get(Utils.rand.nextInt(ZELDA_SOUNDS.size()));
	}

}
