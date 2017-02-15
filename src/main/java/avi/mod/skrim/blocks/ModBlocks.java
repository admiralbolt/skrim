package avi.mod.skrim.blocks;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.blocks.flowers.EnchantedFlower;
import avi.mod.skrim.blocks.flowers.EnchantedFlowerRed;
import avi.mod.skrim.blocks.flowers.EnchantedFlowerVariants;
import avi.mod.skrim.blocks.flowers.EnchantedFlowerYellow;
import avi.mod.skrim.blocks.flowers.FlowerBase;
import avi.mod.skrim.blocks.flowers.FlowerBase.EnumFlowerType;
import avi.mod.skrim.blocks.flowers.GlowFlower;
import avi.mod.skrim.blocks.flowers.GlowFlowerRed;
import avi.mod.skrim.blocks.flowers.GlowFlowerVariants;
import avi.mod.skrim.blocks.flowers.GlowFlowerYellow;
import avi.mod.skrim.blocks.plants.BeanstalkBlock;
import avi.mod.skrim.blocks.plants.MagicBean;
import avi.mod.skrim.blocks.plants.WeirwoodSapling;
import avi.mod.skrim.blocks.tnt.BioBomb;
import avi.mod.skrim.blocks.tnt.CustomTNTPrimed;
import avi.mod.skrim.blocks.tnt.Dynamite;
import avi.mod.skrim.blocks.tnt.Napalm;
import avi.mod.skrim.items.ItemModelProvider;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class ModBlocks {

	public static Block CLONE_OLD_LOG;

	public static GlowFlower GLOW_FLOWER_RED;
	public static GlowFlowerVariants GLOW_FLOWER_RED_VARIANTS;
	public static GlowFlower GLOW_FLOWER_YELLOW;
	public static GlowFlowerVariants GLOW_FLOWER_YELLOW_VARIANTS;

	public static EnchantedFlower ENCHANTED_FLOWER_RED;
	public static EnchantedFlowerVariants ENCHANTED_FLOWER_RED_VARIANTS;
	public static EnchantedFlower ENCHANTED_FLOWER_YELLOW;
	public static EnchantedFlowerVariants ENCHANTED_FLOWER_YELLOW_VARIANTS;

	public static Dynamite DYNAMITE;
	public static BioBomb BIOBOMB;
	public static Napalm NAPALM;

	public static SkrimCakeBlock SKRIM_CAKE;
	public static AngelCakeBlock ANGEL_CAKE;

	public static MagicBean MAGIC_BEAN;
	public static BeanstalkBlock BEANSTALK_BLOCK;
	
	public static WeirwoodSapling WEIRWOOD_SAPLING;
	public static WeirwoodWood WEIRWOOD_WOOD;
	public static WeirwoodLeaf WEIRWOOD_LEAF;
	
	public static MegaChest MEGA_CHEST;

	public static void createBlocks() {
		GlowFlowerRed red = new GlowFlowerRed("glow_flower_red");
		GLOW_FLOWER_RED_VARIANTS = new GlowFlowerVariants(red);
		GLOW_FLOWER_RED = register(red, GLOW_FLOWER_RED_VARIANTS);
		addGlowFlowerRecipes(red, GLOW_FLOWER_RED_VARIANTS);

		GlowFlowerYellow yellow = new GlowFlowerYellow("glow_flower_yellow");
		GLOW_FLOWER_YELLOW_VARIANTS = new GlowFlowerVariants(yellow);
		GLOW_FLOWER_YELLOW = register(yellow, GLOW_FLOWER_YELLOW_VARIANTS);
		addGlowFlowerRecipes(yellow, GLOW_FLOWER_YELLOW_VARIANTS);

		EnchantedFlowerRed enchantedRed = new EnchantedFlowerRed("enchanted_flower_red");
		ENCHANTED_FLOWER_RED_VARIANTS = new EnchantedFlowerVariants(enchantedRed);
		ENCHANTED_FLOWER_RED = register(enchantedRed, ENCHANTED_FLOWER_RED_VARIANTS);
		addEnchantedFlowerRecipes(enchantedRed, ENCHANTED_FLOWER_RED_VARIANTS);

		EnchantedFlowerYellow enchantedYellow = new EnchantedFlowerYellow("enchanted_flower_yellow");
		ENCHANTED_FLOWER_YELLOW_VARIANTS = new EnchantedFlowerVariants(enchantedYellow);
		ENCHANTED_FLOWER_YELLOW = register(enchantedYellow, ENCHANTED_FLOWER_YELLOW_VARIANTS);
		addEnchantedFlowerRecipes(enchantedYellow, ENCHANTED_FLOWER_YELLOW_VARIANTS);

		DYNAMITE = register(new Dynamite("dynamite"));
		BIOBOMB = register(new BioBomb("biobomb"));
		NAPALM = register(new Napalm("napalm"));
		EntityRegistry.registerModEntity(new ResourceLocation("skrim:custom_tnt_primed"), CustomTNTPrimed.class, "CustomTNTPrimed", 17654, Skrim.instance, 20, 5, true);
		addExplosivesRecipes();

		SKRIM_CAKE = register(new SkrimCakeBlock());
		ANGEL_CAKE = register(new AngelCakeBlock());

		MAGIC_BEAN = register(new MagicBean());
		addBeanRecipe();
		BEANSTALK_BLOCK = register(new BeanstalkBlock());
		
		WEIRWOOD_SAPLING = register(new WeirwoodSapling());
		addWeirwoodRecipe();
		WEIRWOOD_WOOD = register(new WeirwoodWood());
		WEIRWOOD_LEAF = register(new WeirwoodLeaf());
		
		MEGA_CHEST = register(new MegaChest());
	}

	private static <T extends Block> T register(T block, ItemBlock itemBlock) {
		GameRegistry.register(block);
		if (itemBlock != null) {
			GameRegistry.register(itemBlock);
		}

		if (block instanceof ItemModelProvider) {
			((ItemModelProvider) block).registerItemModel(itemBlock);
		}

		return block;
	}

	private static <T extends Block> T register(T block) {
		ItemBlock itemBlock = new ItemBlock(block);
		itemBlock.setRegistryName(block.getRegistryName());
		return register(block, itemBlock);
	}

	private static void addGlowFlowerRecipes(GlowFlower flower, ItemBlock itemBlock) {
		for (EnumFlowerType type : FlowerBase.EnumFlowerType.getTypes(flower.getBlockType())) {
			ItemStack stack = new ItemStack(flower, 1, type.getMeta());
			GameRegistry.addShapelessRecipe(stack, flower.getMinecraftFlower(stack.getMetadata()),
					Items.GLOWSTONE_DUST);
		}
	}

	private static void addEnchantedFlowerRecipes(EnchantedFlower flower, ItemBlock itemBlock) {
		for (EnumFlowerType type : FlowerBase.EnumFlowerType.getTypes(flower.getBlockType())) {
			ItemStack stack = new ItemStack(flower, 1, type.getMeta());
			GameRegistry.addRecipe(stack, "ABA", "CDC", "ABA", 'A', Items.DIAMOND, 'B', Blocks.OBSIDIAN, 'C',
					Blocks.GLASS, 'D', flower.getMinecraftFlower(stack.getMetadata()));
		}
	}

	private static void addExplosivesRecipes() {
		Item[] pics = { Items.WOODEN_PICKAXE, Items.STONE_PICKAXE };
		for (Item pic : pics) {
			GameRegistry.addShapelessRecipe(new ItemStack(DYNAMITE), Blocks.TNT, pic);
		}

		GameRegistry.addRecipe(new ItemStack(NAPALM), "AAA", "BCB", "AAA", 'A', Items.BLAZE_POWDER, 'B',
				Items.LAVA_BUCKET, 'C', Blocks.TNT);
		GameRegistry.addRecipe(new ItemStack(BIOBOMB), "AAA", "ABA", "AAA", 'A', Items.ROTTEN_FLESH, 'B', Blocks.TNT);
	}
	
	private static void addBeanRecipe() {
		GameRegistry.addShapelessRecipe(new ItemStack(MAGIC_BEAN), Items.CARROT, new ItemStack(Items.DYE, 1, 3), Items.NETHER_WART, Items.POTATO, Items.BEETROOT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.WHEAT_SEEDS, Items.DIAMOND_HOE);
	}
	
	private static void addWeirwoodRecipe() {
		GameRegistry.addShapelessRecipe(new ItemStack(WEIRWOOD_SAPLING), new ItemStack(Blocks.SAPLING, 1, 0), new ItemStack(Blocks.SAPLING, 1, 1), new ItemStack(Blocks.SAPLING, 1, 2), new ItemStack(Blocks.SAPLING, 1, 3), new ItemStack(Blocks.SAPLING, 1, 4), new ItemStack(Blocks.SAPLING, 1, 5), new ItemStack(Items.ENDER_EYE));
	}

}
