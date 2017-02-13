package avi.mod.skrim.world.loot;

import avi.mod.skrim.Skrim;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;

public class CustomLootTables {
	
	public static ResourceLocation CHESTS_BEANSTALK = null;
	public static ResourceLocation RANDOM_TREASURE = null;
	
	private static ResourceLocation register(String name) {
		return LootTableList.register(new ResourceLocation(Skrim.modId, name));
	}
	
	public static void registerLootTables() {
		CHESTS_BEANSTALK = register("chests/beanstalk");
		RANDOM_TREASURE = register("gameplay/random_treasure");
	}

}
