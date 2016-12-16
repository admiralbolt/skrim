package avi.mod.skrim.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.items.armor.LeafArmor;
import avi.mod.skrim.items.artifacts.ArtifactArmor;
import avi.mod.skrim.items.artifacts.ArtifactItem;
import avi.mod.skrim.items.artifacts.ArtifactSword;
import avi.mod.skrim.items.artifacts.BlindingBoots;
import avi.mod.skrim.items.artifacts.CanesSword;
import avi.mod.skrim.items.artifacts.GruesomeMask;
import avi.mod.skrim.items.artifacts.IcarianScroll;
import avi.mod.skrim.items.artifacts.SheepFinder3000;
import avi.mod.skrim.items.artifacts.SpringheelBoots;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockPlanks;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModItems {

	public static ItemBase TUX;

	public static CustomFood OVERWRITE_PORKCHOP;
	public static CustomFood OVERWRITE_CHICKEN;
	public static CustomFood OVERWRITE_MUTTON;
	public static CustomFood OVERWRITE_RABBIT;
	public static CustomFood OVERWRITE_BAKED_POTATO;
	public static CustomFood OVERWRITE_FISH;
	public static CustomFood OVERWRITE_SALMON;
	public static CustomFood OVERWRITE_CAKE;
	public static CustomFood OVERWRITE_BEETROOT_SOUP;
	public static CustomFood OVERWRITE_MUSHROOM_STEW;
	public static CustomFood OVERWRITE_COOKIE;
	public static CustomFood OVERWRITE_BREAD;
	public static CustomFood OVERWRITE_PUMPKIN_STEW;
	public static CustomFood OVERWRITE_RABBIT_STEW;
	public static CustomFood OVERWRITE_STEAK;
	public static CustomFood CANES_CHICKEN;

	public static SkrimCake SKRIM_CAKE;
	public static AngelCake ANGEL_CAKE;

	public static EnumRarity ARTIFACT_RARITY = EnumHelper.addRarity("artifact", TextFormatting.GOLD, "Artifact");

	/**
	 * Default ArmorMaterials
	 *
	 * Durability: leather -> 5 chain/iron -> 15 gold -> 7 diamond -> 33
	 *
	 * Reductions: leather -> {1, 3, 2, 1} Total 7 / chain -> {2, 5, 4, 1} Total 12 iron -> {2, 6, 5, 2} Total 15 gold -> {2, 5, 3, 1} Total 11 diamond -> {3, 8, 6, 3} Total 20
	 *
	 * Enchantability: leather -> 15 chain -> 12 iron -> 9 gold -> 25 diamond -> 10
	 */


	public static ArmorMaterial OBSIDIAN_ARMOR = EnumHelper.addArmorMaterial("obsidian", "skrim:obsidian_armor", 165, new int[] { 4, 9, 7, 4 }, 20, null, 2.0F);
	public static ArmorMaterial OVERALLS_ARMOR = EnumHelper.addArmorMaterial("overalls", "skrim:overalls", 10, new int[] { 1, 3, 2, 1 }, 15, null, 0.0F);

	/**
	 * Default ToolMaterials HarvestLevel: wood: 0 stone: 1 iron: 2 diamond: 3 Durability: wood: 59 stone: 131 iron: 250 gold: 32 diamond: 1561 Mining Speed: wood: 2.0F stone: 4.0F iron: 6.0F gold: 12.0F diamond: 8.0F Damage vs. Entity wood: 0.0F stone: 1.0F iron: 2.0F gold: 0.0F diamond: 3.0F Enchantability: wood: 15 stone: 5 iron: 14 gold: 22 diamond: 10
	 */

	public static ToolMaterial ARTIFACT_DEFAULT = EnumHelper.addToolMaterial("artifact_default", 3, 4500, 6.0F, 4.0F, 0);
	public static ToolMaterial OBSIDIAN_TOOL = EnumHelper.addToolMaterial("obsidian", 3, 7500, 9.0F, 4.0F, 20);

	public static ArtifactSword CANES_SWORD;

	public static ArtifactArmor SPRINGHEEL_BOOTS;
	public static ArtifactArmor BLINDING_BOOTS;
	public static ArtifactArmor GRUESOME_MASK;

	public static ArtifactItem SHEEP_FINDER;
	public static ArtifactItem ICARIAN_SCROLL;

	/**
	 * Custom items created from skills
	 */

	public static CustomSword OBSIDIAN_SWORD;
	public static CustomSpade OBSIDIAN_SHOVEL;
	public static CustomPickaxe OBSIDIAN_PICKAXE;
	public static CustomHoe OBSIDIAN_HOE;
	public static CustomAxe OBSIDIAN_AXE;

	public static CustomArmor OBSIDIAN_BOOTS;
	public static CustomArmor OBSIDIAN_PANTS;
	public static CustomArmor OBSIDIAN_CHEST;
	public static CustomArmor OBSIDIAN_HELMET;

	public static LeafArmor OAK_LEAF_BOOTS;
	public static LeafArmor OAK_LEAF_PANTS;
	public static LeafArmor OAK_LEAF_ARMOR;
	public static LeafArmor OAK_LEAF_HELMET;


	public static LeafArmor SPRUCE_LEAF_BOOTS;
	public static LeafArmor SPRUCE_LEAF_PANTS;
	public static LeafArmor SPRUCE_LEAF_ARMOR;
	public static LeafArmor SPRUCE_LEAF_HELMET;


	public static LeafArmor BIRCH_LEAF_BOOTS;
	public static LeafArmor BIRCH_LEAF_PANTS;
	public static LeafArmor BIRCH_LEAF_ARMOR;
	public static LeafArmor BIRCH_LEAF_HELMET;


	public static LeafArmor JUNGLE_LEAF_BOOTS;
	public static LeafArmor JUNGLE_LEAF_PANTS;
	public static LeafArmor JUNGLE_LEAF_ARMOR;
	public static LeafArmor JUNGLE_LEAF_HELMET;


	public static LeafArmor ACACIA_LEAF_BOOTS;
	public static LeafArmor ACACIA_LEAF_PANTS;
	public static LeafArmor ACACIA_LEAF_ARMOR;
	public static LeafArmor ACACIA_LEAF_HELMET;


	public static LeafArmor DARK_OAK_LEAF_BOOTS;
	public static LeafArmor DARK_OAK_LEAF_PANTS;
	public static LeafArmor DARK_OAK_LEAF_ARMOR;
	public static LeafArmor DARK_OAK_LEAF_HELMET;

	public static WeirwoodTotem WEIRWOOD_TOTEM;

	public static HandSaw HAND_SAW;
	public static CustomArmor OVERALLS;
	public static CustomBow GREAT_BOW;
	public static RocketLauncher ROCKET_LAUNCHER;

	// La musica
	public static CustomRecord ARUARIAN_DANCE;
	public static CustomRecord BUBBERDUCKY;
	public static CustomRecord CASSANDRA;
	public static CustomRecord COLOR;
	public static CustomRecord DOGSONG;
	public static CustomRecord GDAWG;
	public static CustomRecord HEYA;
	public static CustomRecord MONEY;
	public static CustomRecord NORTH;
	public static CustomRecord NUMBER10;
	public static CustomRecord SAMURAI;
	public static CustomRecord TRUCK;

	public static void createItems() {
		TUX = register(new ItemBase("tux").setCreativeTab(Skrim.creativeTab));
		// Food!
		OVERWRITE_PORKCHOP = register(new CustomFood("overwrite_porkchop", 8, 1.6F, true).setCreativeTab(Skrim.creativeTab));
		OVERWRITE_BAKED_POTATO = register(new CustomFood("overwrite_baked_potato", 5, 1.2F, false).setCreativeTab(Skrim.creativeTab));
		OVERWRITE_BEETROOT_SOUP = register(new CustomFood("overwrite_beetroot_soup", 6, 1.2F, false).setCreativeTab(Skrim.creativeTab));
		OVERWRITE_BREAD = register(new CustomFood("overwrite_bread", 5, 1.2F, false).setCreativeTab(Skrim.creativeTab));
		OVERWRITE_CHICKEN = register(new CustomFood("overwrite_chicken", 6, 1.2F, true).setCreativeTab(Skrim.creativeTab));
		OVERWRITE_FISH = register(new CustomFood("overwrite_fish", 5, 1.2F, true).setCreativeTab(Skrim.creativeTab));
		OVERWRITE_MUTTON = register(new CustomFood("overwrite_mutton", 6, 1.6F, true).setCreativeTab(Skrim.creativeTab));
		OVERWRITE_SALMON = register(new CustomFood("overwrite_salmon", 6, 1.6F, true).setCreativeTab(Skrim.creativeTab));
		OVERWRITE_COOKIE = register(new CustomFood("overwrite_cookie", 2, 0.2F, false).setCreativeTab(Skrim.creativeTab));
		OVERWRITE_MUSHROOM_STEW = register(new CustomFood("overwrite_mushroom_stew", 6, 1.2F, false).setCreativeTab(Skrim.creativeTab));
		OVERWRITE_PUMPKIN_STEW = register(new CustomFood("overwrite_pumpkin_pie", 8, 0.6F, false).setCreativeTab(Skrim.creativeTab));
		OVERWRITE_RABBIT_STEW = register(new CustomFood("overwrite_rabbit_stew", 10, 1.2F, false).setCreativeTab(Skrim.creativeTab));
		OVERWRITE_STEAK = register(new CustomFood("overwrite_steak", 8, 1.6F, true).setCreativeTab(Skrim.creativeTab));
		OVERWRITE_RABBIT = register(new CustomFood("overwrite_rabbit", 5, 1.2F, true).setCreativeTab(Skrim.creativeTab));
		CANES_CHICKEN = register(new CustomFood("canes_chicken", 20, 1.5F, true).setCreativeTab(Skrim.creativeTab));
		SKRIM_CAKE = register(new SkrimCake());

		// Obsidian tools & armor
		OBSIDIAN_SWORD = register(new CustomSword("obsidian_sword", OBSIDIAN_TOOL));
		OBSIDIAN_HOE = register(new CustomHoe("obsidian_hoe", OBSIDIAN_TOOL));
		OBSIDIAN_SHOVEL = register(new CustomSpade("obsidian_spade", OBSIDIAN_TOOL));
		OBSIDIAN_AXE = register(new CustomAxe("obsidian_axe", OBSIDIAN_TOOL));
		OBSIDIAN_PICKAXE = register(new CustomPickaxe("obsidian_pickaxe", OBSIDIAN_TOOL));
		OBSIDIAN_BOOTS = register(new CustomArmor("obsidian_boots", OBSIDIAN_ARMOR, 1, EntityEquipmentSlot.FEET));
		OBSIDIAN_PANTS = register(new CustomArmor("obsidian_pants", OBSIDIAN_ARMOR, 2, EntityEquipmentSlot.LEGS));
		OBSIDIAN_CHEST = register(new CustomArmor("obsidian_chest", OBSIDIAN_ARMOR, 3, EntityEquipmentSlot.CHEST));
		OBSIDIAN_HELMET = register(new CustomArmor("obsidian_helmet", OBSIDIAN_ARMOR, 4, EntityEquipmentSlot.HEAD));

		// Skill Stuff
		HAND_SAW = register(new HandSaw("hand_saw", ToolMaterial.IRON));
		OVERALLS = register(new CustomArmor("overalls", OVERALLS_ARMOR, 3, EntityEquipmentSlot.CHEST));
		GREAT_BOW = register(new GreatBow("great_bow"));
		ANGEL_CAKE = register(new AngelCake());
		ROCKET_LAUNCHER = register(new RocketLauncher());

		OAK_LEAF_BOOTS = register(new LeafArmor(BlockPlanks.EnumType.OAK, "leaf_boots", 1, EntityEquipmentSlot.FEET));
		OAK_LEAF_PANTS = register(new LeafArmor(BlockPlanks.EnumType.OAK, "leaf_pants", 1, EntityEquipmentSlot.LEGS));
		OAK_LEAF_ARMOR = register(new LeafArmor(BlockPlanks.EnumType.OAK, "leaf_armor", 1, EntityEquipmentSlot.CHEST));
		OAK_LEAF_HELMET = register(new LeafArmor(BlockPlanks.EnumType.OAK, "leaf_helmet", 1, EntityEquipmentSlot.HEAD));
		SPRUCE_LEAF_BOOTS = register(new LeafArmor(BlockPlanks.EnumType.SPRUCE, "leaf_boots", 1, EntityEquipmentSlot.FEET));
		SPRUCE_LEAF_PANTS = register(new LeafArmor(BlockPlanks.EnumType.SPRUCE, "leaf_pants", 1, EntityEquipmentSlot.LEGS));
		SPRUCE_LEAF_ARMOR = register(new LeafArmor(BlockPlanks.EnumType.SPRUCE, "leaf_armor", 1, EntityEquipmentSlot.CHEST));
		SPRUCE_LEAF_HELMET = register(new LeafArmor(BlockPlanks.EnumType.SPRUCE, "leaf_helmet", 1, EntityEquipmentSlot.HEAD));
		BIRCH_LEAF_BOOTS = register(new LeafArmor(BlockPlanks.EnumType.BIRCH, "leaf_boots", 1, EntityEquipmentSlot.FEET));
		BIRCH_LEAF_PANTS = register(new LeafArmor(BlockPlanks.EnumType.BIRCH, "leaf_pants", 1, EntityEquipmentSlot.LEGS));
		BIRCH_LEAF_ARMOR = register(new LeafArmor(BlockPlanks.EnumType.BIRCH, "leaf_armor", 1, EntityEquipmentSlot.CHEST));
		BIRCH_LEAF_HELMET = register(new LeafArmor(BlockPlanks.EnumType.BIRCH, "leaf_helmet", 1, EntityEquipmentSlot.HEAD));
		JUNGLE_LEAF_BOOTS = register(new LeafArmor(BlockPlanks.EnumType.JUNGLE, "leaf_boots", 1, EntityEquipmentSlot.FEET));
		JUNGLE_LEAF_PANTS = register(new LeafArmor(BlockPlanks.EnumType.JUNGLE, "leaf_pants", 1, EntityEquipmentSlot.LEGS));
		JUNGLE_LEAF_ARMOR = register(new LeafArmor(BlockPlanks.EnumType.JUNGLE, "leaf_armor", 1, EntityEquipmentSlot.CHEST));
		JUNGLE_LEAF_HELMET = register(new LeafArmor(BlockPlanks.EnumType.JUNGLE, "leaf_helmet", 1, EntityEquipmentSlot.HEAD));
		ACACIA_LEAF_BOOTS = register(new LeafArmor(BlockPlanks.EnumType.ACACIA, "leaf_boots", 1, EntityEquipmentSlot.FEET));
		ACACIA_LEAF_PANTS = register(new LeafArmor(BlockPlanks.EnumType.ACACIA, "leaf_pants", 1, EntityEquipmentSlot.LEGS));
		ACACIA_LEAF_ARMOR = register(new LeafArmor(BlockPlanks.EnumType.ACACIA, "leaf_armor", 1, EntityEquipmentSlot.CHEST));
		ACACIA_LEAF_HELMET = register(new LeafArmor(BlockPlanks.EnumType.ACACIA, "leaf_helmet", 1, EntityEquipmentSlot.HEAD));
		DARK_OAK_LEAF_BOOTS = register(new LeafArmor(BlockPlanks.EnumType.DARK_OAK, "leaf_boots", 1, EntityEquipmentSlot.FEET));
		DARK_OAK_LEAF_PANTS = register(new LeafArmor(BlockPlanks.EnumType.DARK_OAK, "leaf_pants", 1, EntityEquipmentSlot.LEGS));
		DARK_OAK_LEAF_ARMOR = register(new LeafArmor(BlockPlanks.EnumType.DARK_OAK, "leaf_armor", 1, EntityEquipmentSlot.CHEST));
		DARK_OAK_LEAF_HELMET = register(new LeafArmor(BlockPlanks.EnumType.DARK_OAK, "leaf_helmet", 1, EntityEquipmentSlot.HEAD));

		WEIRWOOD_TOTEM = register(new WeirwoodTotem());

		registerArtifacts();
		registerSongs();

		/**
		 * Register crafting recipes!
		 */
		registerCraftingRecipes();
	}

	public static List<Item> ARTIFACTS = new ArrayList<Item>();

	public static void registerArtifacts() {
		// Artifact Armors
		SPRINGHEEL_BOOTS = register(new SpringheelBoots());
		ARTIFACTS.add(SPRINGHEEL_BOOTS);

		BLINDING_BOOTS = register(new BlindingBoots());
		ARTIFACTS.add(BLINDING_BOOTS);

		GRUESOME_MASK = register(new GruesomeMask());
		ARTIFACTS.add(GRUESOME_MASK);

		// Artifact Swords
		CANES_SWORD = register(new CanesSword());
		ARTIFACTS.add(CANES_SWORD);

		// Artifact Items
		SHEEP_FINDER = register(new SheepFinder3000());
		ARTIFACTS.add(SHEEP_FINDER);

		ICARIAN_SCROLL = register(new IcarianScroll());
		ARTIFACTS.add(ICARIAN_SCROLL);
	}

	public static Map<String, CustomRecord> SONGS = new HashMap<String, CustomRecord>();

	public static void registerSongs() {
		ARUARIAN_DANCE = register(new CustomRecord("aruarian_dance", registerRecordEvent("aruarian_dance")));
		BUBBERDUCKY = register(new CustomRecord("bubberducky", registerRecordEvent("bubberducky")));
		CASSANDRA = register(new CustomRecord("cassandra", registerRecordEvent("cassandra")));
		COLOR = register(new CustomRecord("lack_of_color", registerRecordEvent("lack_of_color")));
		DOGSONG = register(new CustomRecord("dogsong", registerRecordEvent("dogsong")));
		GDAWG = register(new CustomRecord("gdawg", registerRecordEvent("gdawg")));
		HEYA = register(new CustomRecord("heya", registerRecordEvent("heya")));
		MONEY = register(new CustomRecord("money", registerRecordEvent("money")));
		NORTH = register(new CustomRecord("north", registerRecordEvent("north")));
		NUMBER10 = register(new CustomRecord("number10", registerRecordEvent("number10")));
		SAMURAI = register(new CustomRecord("samurai", registerRecordEvent("samurai")));
		TRUCK = register(new CustomRecord("truck", registerRecordEvent("truck")));

		SONGS.put("aruarian_dance", ARUARIAN_DANCE);
		SONGS.put("bubberducky", BUBBERDUCKY);
		SONGS.put("cassandra", CASSANDRA);
		SONGS.put("lack_of_color", COLOR);
		SONGS.put("dogsong", DOGSONG);
		SONGS.put("gdawg", GDAWG);
		SONGS.put("heya", HEYA);
		SONGS.put("money", MONEY);
		SONGS.put("north", NORTH);
		SONGS.put("number10", NUMBER10);
		SONGS.put("samurai", SAMURAI);
		SONGS.put("truck", TRUCK);
	}

	public static SoundEvent registerRecordEvent(String recordName) {
		ResourceLocation location = new ResourceLocation(Skrim.modId, recordName);
		SoundEvent event = new SoundEvent(location);
		GameRegistry.register(event, location);
		return event;
	}

	public static void registerCraftingRecipes() {
		registerRabbitStew();
		registerAngelCake();
		registerObsidian();
		registerLeafArmor();
		registerRocketLauncher();
		registerHandSaw();
		registerOveralls();
		registerGreatBow();
		registerWeirwoodSapling();
	}

	public static void registerWeirwoodSapling() {
		GameRegistry.addRecipe(new ItemStack(WEIRWOOD_TOTEM), " a ", "bcb", "b b", 'a', new ItemStack(Items.ENDER_EYE), 'b', new ItemStack(Items.STICK), 'c', new ItemStack(Items.DIAMOND));
	}

	public static void registerRocketLauncher() {
		GameRegistry.addRecipe(new ItemStack(ROCKET_LAUNCHER), " aa", "aaa", " bb", 'a', new ItemStack(Items.IRON_INGOT), 'b', new ItemStack(Items.STICK));
	}

	public static void registerAngelCake() {
		GameRegistry.addRecipe(new ItemStack(ANGEL_CAKE), "aba", "cdc", "eee", 'a', new ItemStack(Items.FEATHER), 'b', new ItemStack(Items.MILK_BUCKET), 'c', new ItemStack(Items.SUGAR), 'd', new ItemStack(Items.EGG), 'e', new ItemStack(Items.WHEAT));
	}

	/**
	 * We have to be explicit with our recipes. Which means since we're injecting our own food.... Sigh.
	 */
	public static void registerRabbitStew() {
		Item[] rabbits = { (Item) OVERWRITE_RABBIT, Items.COOKED_RABBIT };
		BlockBush[] mushrooms = { Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM };
		Item[] potatoes = { OVERWRITE_BAKED_POTATO, Items.BAKED_POTATO };
		for (Item rabbit : rabbits) {
			for (Item potato : potatoes) {
				if (rabbit != Items.COOKED_RABBIT || potato != Items.BAKED_POTATO) {
					for (BlockBush mushroom : mushrooms) {
						GameRegistry.addRecipe(new ItemStack(Items.RABBIT_STEW), " a ", "bcd", " e ", 'a', new ItemStack(rabbit), 'b', new ItemStack(Items.CARROT), 'c', new ItemStack(potato), 'd', new ItemStack(mushroom), 'e', new ItemStack(Items.BOWL));
					}
				}
			}
		}
	}

	public static void registerHandSaw() {
		GameRegistry.addRecipe(new ItemStack(HAND_SAW), " IW", "IIW", 'I', new ItemStack(Items.IRON_INGOT), 'W', new ItemStack(Items.STICK));
		GameRegistry.addRecipe(new ItemStack(HAND_SAW), "   ", " IW", "IIW", 'I', new ItemStack(Items.IRON_INGOT), 'W', new ItemStack(Items.STICK));
	}

	public static void registerOveralls() {
		GameRegistry.addRecipe(new ItemStack(OVERALLS), "A A", "ALA", "LLL", 'A', new ItemStack(Items.DYE, 1, 4), 'L', new ItemStack(Items.LEATHER));
	}

	public static void registerGreatBow() {
		GameRegistry.addRecipe(new ItemStack(GREAT_BOW), "GWS", "W S", "GWS", 'G', new ItemStack(Items.GOLD_INGOT), 'W', new ItemStack(Items.STICK), 'S', new ItemStack(Items.STRING));
		GameRegistry.addRecipe(new ItemStack(GREAT_BOW), "SWG", "S W", "SWG", 'G', new ItemStack(Items.GOLD_INGOT), 'W', new ItemStack(Items.STICK), 'S', new ItemStack(Items.STRING));
	}

	private static void addToolRecipes(ItemStack recipeItemStack, Item axe, Item hoe, Item pickaxe, Item spade) {
		if (axe != null) {
			GameRegistry.addRecipe(new ItemStack(axe), "AA ", "AS ", " S ", 'A', recipeItemStack, 'S', Items.STICK);
			GameRegistry.addRecipe(new ItemStack(axe), " AA", " SA", " S ", 'A', recipeItemStack, 'S', Items.STICK);
		}
		if (hoe != null) {
			GameRegistry.addRecipe(new ItemStack(hoe), "AA ", " S ", " S ", 'A', recipeItemStack, 'S', Items.STICK);
			GameRegistry.addRecipe(new ItemStack(hoe), " AA", " S ", " S ", 'A', recipeItemStack, 'S', Items.STICK);
		}
		if (pickaxe != null) {
			GameRegistry.addRecipe(new ItemStack(pickaxe), "AAA", " S ", " S ", 'A', recipeItemStack, 'S', Items.STICK);
		}
		if (spade != null) {
			GameRegistry.addRecipe(new ItemStack(spade), " A ", " S ", " S ", 'A', recipeItemStack, 'S', Items.STICK);
		}
	}

	private static void addArmorRecipes(ItemStack recipeItemStack, Item helmet, Item chest, Item pants, Item boots) {
		if (helmet != null) {
			GameRegistry.addRecipe(new ItemStack(helmet), "AAA", "A A", "   ", 'A', recipeItemStack);
		}
		if (chest != null) {
			GameRegistry.addRecipe(new ItemStack(chest), "A A", "AAA", "AAA", 'A', recipeItemStack);
		}
		if (pants != null) {
			GameRegistry.addRecipe(new ItemStack(pants), "AAA", "A A", "A A", 'A', recipeItemStack);
		}
		if (boots != null) {
			GameRegistry.addRecipe(new ItemStack(boots), "   ", "A A", "A A", 'A', recipeItemStack);
		}
	}

	private static void registerLeafArmor() {
		addArmorRecipes(new ItemStack(Blocks.LEAVES, 1, BlockPlanks.EnumType.OAK.getMetadata()), OAK_LEAF_HELMET, OAK_LEAF_ARMOR, OAK_LEAF_PANTS, OAK_LEAF_BOOTS);
		addArmorRecipes(new ItemStack(Blocks.LEAVES, 1, BlockPlanks.EnumType.SPRUCE.getMetadata()), SPRUCE_LEAF_HELMET, SPRUCE_LEAF_ARMOR, SPRUCE_LEAF_PANTS, SPRUCE_LEAF_BOOTS);
		addArmorRecipes(new ItemStack(Blocks.LEAVES, 1, BlockPlanks.EnumType.BIRCH.getMetadata()), BIRCH_LEAF_HELMET, BIRCH_LEAF_ARMOR, BIRCH_LEAF_PANTS, BIRCH_LEAF_BOOTS);
		addArmorRecipes(new ItemStack(Blocks.LEAVES, 1, BlockPlanks.EnumType.JUNGLE.getMetadata()), JUNGLE_LEAF_HELMET, JUNGLE_LEAF_ARMOR, JUNGLE_LEAF_PANTS, JUNGLE_LEAF_BOOTS);
		addArmorRecipes(new ItemStack(Blocks.LEAVES2, 1, 0), ACACIA_LEAF_HELMET, ACACIA_LEAF_ARMOR, ACACIA_LEAF_PANTS, ACACIA_LEAF_BOOTS);
		addArmorRecipes(new ItemStack(Blocks.LEAVES2, 1, 1), DARK_OAK_LEAF_HELMET, DARK_OAK_LEAF_ARMOR, DARK_OAK_LEAF_PANTS, DARK_OAK_LEAF_BOOTS);
	}

	private static void addWeaponRecipes(ItemStack recipeItemStack, Item sword) {
		if (sword != null) {
			GameRegistry.addRecipe(new ItemStack(sword), " A ", " A ", " S ", 'A', recipeItemStack, 'S', Items.STICK);
		}
	}

	public static void registerObsidian() {
		addArmorRecipes(new ItemStack(Blocks.OBSIDIAN), OBSIDIAN_HELMET, OBSIDIAN_CHEST, OBSIDIAN_PANTS, OBSIDIAN_BOOTS);
		addToolRecipes(new ItemStack(Blocks.OBSIDIAN), OBSIDIAN_AXE, OBSIDIAN_HOE, OBSIDIAN_PICKAXE, OBSIDIAN_SHOVEL);
		addWeaponRecipes(new ItemStack(Blocks.OBSIDIAN), OBSIDIAN_SWORD);
	}

	private static <T extends Item> T register(T item) {
		GameRegistry.register(item);
		if (item instanceof ItemModelProvider) {
			((ItemModelProvider) item).registerItemModel(item);
		}
		item.setCreativeTab(Skrim.creativeTab);
		return item;
	}

}
