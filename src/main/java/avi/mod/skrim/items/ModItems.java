package avi.mod.skrim.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.items.artifacts.ArtifactArmor;
import avi.mod.skrim.items.artifacts.ArtifactSword;
import avi.mod.skrim.items.artifacts.BlindingBoots;
import avi.mod.skrim.items.artifacts.CanesSword;
import avi.mod.skrim.items.artifacts.SpringheelBoots;
import net.minecraft.block.BlockBush;
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
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModItems {

	public static ItemBase tux;

	public static CustomFood overwritePorkchop;
	public static CustomFood overwriteChicken;
	public static CustomFood overwriteMutton;
	public static CustomFood overwriteRabbit;
	public static CustomFood overwriteBakedPotato;
	public static CustomFood overwriteFish;
	public static CustomFood overwriteSalmon;
	public static CustomFood overwriteCake;
	public static CustomFood overwriteBeetrootSoup;
	public static CustomFood overwriteMushroomStew;
	public static CustomFood overwriteCookie;
	public static CustomFood overwriteBread;
	public static CustomFood overwritePumpkinPie;
	public static CustomFood overwriteRabbitStew;
	public static CustomFood overwriteSteak;
	public static CustomFood canesChicken;

	public static CustomFishingRod fishingRod;

	public static EnumRarity ARTIFACT_RARITY = EnumHelper.addRarity("artifact", TextFormatting.GOLD, "Artifact");

	/**
	 * Default ArmorMaterials Durability: leather -> 5 chain/iron -> 15 gold -> 7 diamond -> 33 Reductions: leather -> {1, 3, 2, 1} Total 7 chain -> {2, 5, 4, 1} Total 12 iron -> {2, 6, 5, 2} Total 15 gold -> {2, 5, 3, 1} Total 11 diamond -> {3, 8, 6, 3} Total 20 Enchantability: leather -> 15 chain -> 12 iron -> 9 gold -> 25 diamond -> 10 Toughness
	 */
	public static ArmorMaterial ARTIFACT_DARK = EnumHelper.addArmorMaterial("artifact_dark", "skrim:artifact_dark", 50, new int[] { 3, 8, 6, 3 }, 30, null, 0.0F);
	public static ArmorMaterial OBSIDIAN_ARMOR = EnumHelper.addArmorMaterial("obsidian", "skrim:obsidian_armor", 165, new int[] { 4, 10, 8, 4 }, 20, null, 3.0F);
	public static ArmorMaterial OVERALLS = EnumHelper.addArmorMaterial("overalls", "skrim:overalls", 10, new int[] { 1, 3, 2, 1 }, 15, null, 0.0F);

	/**
	 * Default ToolMaterials HarvestLevel: wood: 0 stone: 1 iron: 2 diamond: 3 Durability: wood: 59 stone: 131 iron: 250 gold: 32 diamond: 1561 Mining Speed: wood: 2.0F stone: 4.0F iron: 6.0F gold: 12.0F diamond: 8.0F Damage vs. Entity wood: 0.0F stone: 1.0F iron: 2.0F gold: 0.0F diamond: 3.0F Enchantability: wood: 15 stone: 5 iron: 14 gold: 22 diamond: 10
	 */

	public static ToolMaterial ARTIFACT_DEFAULT = EnumHelper.addToolMaterial("artifact_default", 3, 4500, 6.0F, 4.0F, 0);
	public static ToolMaterial OBSIDIAN_TOOL = EnumHelper.addToolMaterial("obsidian", 3, 7500, 9.0F, 4.0F, 20);

	public static ArtifactSword CANES_SWORD;
	public static ArtifactArmor SPRINGHEEL_BOOTS;
	public static ArtifactArmor BLINDING_BOOTS;

	/**
	 * Custom items created from skills
	 */

	public static CustomSword obsidianSword;
	public static CustomSpade obsidianShovel;
	public static CustomPickaxe obsidianPickaxe;
	public static CustomHoe obsidianHoe;
	public static CustomAxe obsidianAxe;

	public static CustomArmor obsidianBoots;
	public static CustomArmor obsidianPants;
	public static CustomArmor obsidianChest;
	public static CustomArmor obsidianHelmet;

	public static HandSaw handSaw;
	public static CustomArmor overalls;
	public static CustomBow GREAT_BOW;

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
		tux = register(new ItemBase("tux").setCreativeTab(Skrim.creativeTab));
		// Food!
		overwritePorkchop = register(new CustomFood("overwrite_porkchop", 8, 1.6F, true).setCreativeTab(Skrim.creativeTab));
		overwriteBakedPotato = register(new CustomFood("overwrite_baked_potato", 5, 1.2F, false).setCreativeTab(Skrim.creativeTab));
		overwriteBeetrootSoup = register(new CustomFood("overwrite_beetroot_soup", 6, 1.2F, false).setCreativeTab(Skrim.creativeTab));
		overwriteBread = register(new CustomFood("overwrite_bread", 5, 1.2F, false).setCreativeTab(Skrim.creativeTab));
		overwriteChicken = register(new CustomFood("overwrite_chicken", 6, 1.2F, true).setCreativeTab(Skrim.creativeTab));
		overwriteFish = register(new CustomFood("overwrite_fish", 5, 1.2F, true).setCreativeTab(Skrim.creativeTab));
		overwriteMutton = register(new CustomFood("overwrite_mutton", 6, 1.6F, true).setCreativeTab(Skrim.creativeTab));
		overwriteSalmon = register(new CustomFood("overwrite_salmon", 6, 1.6F, true).setCreativeTab(Skrim.creativeTab));
		overwriteCookie = register(new CustomFood("overwrite_cookie", 2, 0.2F, false).setCreativeTab(Skrim.creativeTab));
		overwriteMushroomStew = register(new CustomFood("overwrite_mushroom_stew", 6, 1.2F, false).setCreativeTab(Skrim.creativeTab));
		overwritePumpkinPie = register(new CustomFood("overwrite_pumpkin_pie", 8, 0.6F, false).setCreativeTab(Skrim.creativeTab));
		overwriteRabbitStew = register(new CustomFood("overwrite_rabbit_stew", 10, 1.2F, false).setCreativeTab(Skrim.creativeTab));
		overwriteSteak = register(new CustomFood("overwrite_steak", 8, 1.6F, true).setCreativeTab(Skrim.creativeTab));
		overwriteRabbit = register(new CustomFood("overwrite_rabbit", 5, 1.2F, true).setCreativeTab(Skrim.creativeTab));
		canesChicken = register(new CustomFood("canes_chicken", 20, 1.5F, true).setCreativeTab(Skrim.creativeTab));

		// Overwrite that fishing rod!
		fishingRod = register(new CustomFishingRod("fishing_rod"));

		// Obsidian tools & armor
		obsidianSword = register(new CustomSword("obsidian_sword", OBSIDIAN_TOOL));
		obsidianHoe = register(new CustomHoe("obsidian_hoe", OBSIDIAN_TOOL));
		obsidianShovel = register(new CustomSpade("obsidian_spade", OBSIDIAN_TOOL));
		obsidianAxe = register(new CustomAxe("obsidian_axe", OBSIDIAN_TOOL));
		obsidianPickaxe = register(new CustomPickaxe("obsidian_pickaxe", OBSIDIAN_TOOL));
		obsidianBoots = register(new CustomArmor("obsidian_boots", OBSIDIAN_ARMOR, 1, EntityEquipmentSlot.FEET));
		obsidianPants = register(new CustomArmor("obsidian_pants", OBSIDIAN_ARMOR, 2, EntityEquipmentSlot.LEGS));
		obsidianChest = register(new CustomArmor("obsidian_chest", OBSIDIAN_ARMOR, 3, EntityEquipmentSlot.CHEST));
		obsidianHelmet = register(new CustomArmor("obsidian_helmet", OBSIDIAN_ARMOR, 4, EntityEquipmentSlot.HEAD));

		// More stoof
		handSaw = register(new HandSaw("hand_saw", ToolMaterial.IRON));
		overalls = register(new CustomArmor("overalls", OVERALLS, 3, EntityEquipmentSlot.CHEST));
		GREAT_BOW = register(new GreatBow("great_bow"));

		registerArtifacts();
		registerSongs();

		/**
		 * Register crafting recipes!
		 */
		registerCraftingRecipes();
	}

	public static List<Item> artifacts = new ArrayList<Item>();

	public static void registerArtifacts() {
		// Artifact Armors
		SPRINGHEEL_BOOTS = register(new SpringheelBoots());
		artifacts.add(SPRINGHEEL_BOOTS);

		BLINDING_BOOTS = register(new BlindingBoots());
		artifacts.add(BLINDING_BOOTS);

		// Artifact Swords
		CANES_SWORD = register(new CanesSword());
		artifacts.add(CANES_SWORD);
	}

	public static Map<String, CustomRecord> songs = new HashMap<String, CustomRecord>();

	public static void registerSongs() {
		ARUARIAN_DANCE = register(new CustomRecord("aruarian_dance", registerRecordEvent("aruarian_dance")));
		BUBBERDUCKY = register(new CustomRecord("bubberducky", registerRecordEvent("bubberducky")));
		CASSANDRA = register(new CustomRecord("cassandra", registerRecordEvent("cassandra")));
		COLOR = register(new CustomRecord("color", registerRecordEvent("color")));
		DOGSONG = register(new CustomRecord("dogsong", registerRecordEvent("dogsong")));
		GDAWG = register(new CustomRecord("gdawg", registerRecordEvent("gdawg")));
		HEYA = register(new CustomRecord("heya", registerRecordEvent("heya")));
		MONEY = register(new CustomRecord("money", registerRecordEvent("money")));
		NORTH = register(new CustomRecord("north", registerRecordEvent("north")));
		NUMBER10 = register(new CustomRecord("number10", registerRecordEvent("number10")));
		SAMURAI = register(new CustomRecord("samurai", registerRecordEvent("samurai")));
		TRUCK = register(new CustomRecord("truck", registerRecordEvent("truck")));

		songs.put("aruarian_dance", ARUARIAN_DANCE);
		songs.put("bubberducky", BUBBERDUCKY);
		songs.put("cassandra", CASSANDRA);
		songs.put("color", COLOR);
		songs.put("dogsong", DOGSONG);
		songs.put("gdawg", GDAWG);
		songs.put("heya", HEYA);
		songs.put("money", MONEY);
		songs.put("north", NORTH);
		songs.put("number10", NUMBER10);
		songs.put("samurai", SAMURAI);
		songs.put("truck", TRUCK);
	}

	public static SoundEvent registerRecordEvent(String recordName) {
		ResourceLocation location = new ResourceLocation(Skrim.modId, recordName);
		SoundEvent event = new SoundEvent(location);
		GameRegistry.register(event, location);
		return event;
	}

	public static void registerCraftingRecipes() {
		registerRabbitStew();
		registerObsidian();
		registerHandSaw();
		registerOveralls();
		registerGreatBow();
	}

	/**
	 * We have to be explicit with our recipes. Which means since we're injecting our own food.... Sigh.
	 */
	public static void registerRabbitStew() {
		Item[] rabbits = { (Item) overwriteRabbit, Items.COOKED_RABBIT };
		BlockBush[] mushrooms = { Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM };
		Item[] potatoes = { overwriteBakedPotato, Items.BAKED_POTATO };
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
		GameRegistry.addRecipe(new ItemStack(handSaw), " IW", "IIW", 'I', new ItemStack(Items.IRON_INGOT), 'W', new ItemStack(Items.STICK));
		GameRegistry.addRecipe(new ItemStack(handSaw), "   ", " IW", "IIW", 'I', new ItemStack(Items.IRON_INGOT), 'W', new ItemStack(Items.STICK));
	}

	public static void registerOveralls() {
		GameRegistry.addRecipe(new ItemStack(overalls), "A A", "ALA", "LLL", 'A', new ItemStack(Items.DYE, 1, 4), 'L', new ItemStack(Items.LEATHER));
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

	private static void addWeaponRecipes(ItemStack recipeItemStack, Item sword) {
		if (sword != null) {
			GameRegistry.addRecipe(new ItemStack(sword), " A ", " A ", " S ", 'A', recipeItemStack, 'S', Items.STICK);
		}
	}

	public static void registerObsidian() {
		addArmorRecipes(new ItemStack(Blocks.OBSIDIAN), obsidianHelmet, obsidianChest, obsidianPants, obsidianBoots);
		addToolRecipes(new ItemStack(Blocks.OBSIDIAN), obsidianAxe, obsidianHoe, obsidianPickaxe, obsidianShovel);
		addWeaponRecipes(new ItemStack(Blocks.OBSIDIAN), obsidianSword);
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
