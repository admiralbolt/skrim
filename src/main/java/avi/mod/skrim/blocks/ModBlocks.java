package avi.mod.skrim.blocks;

import java.util.ArrayList;
import java.util.List;

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
import avi.mod.skrim.items.ItemModelProvider;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ModBlocks {

  public static Block orePenguin;
  
  public static GlowFlower glowFlowerRed;
  public static GlowFlowerVariants glowFlowerRedVariants;
  public static GlowFlower glowFlowerYellow;
  public static GlowFlowerVariants glowFlowerYellowVariants;
  
  public static EnchantedFlower enchantedFlowerRed;
  public static EnchantedFlowerVariants enchantedFlowerRedVariants;
  public static EnchantedFlower enchantedFlowerYellow;
  public static EnchantedFlowerVariants enchantedFlowerYellowVariants;
  
  public static void createBlocks() {
    orePenguin = register(new BlockOre("orePenguin").setCreativeTab(CreativeTabs.MATERIALS));
    
    GlowFlowerRed red = new GlowFlowerRed("glow_flower_red");
    glowFlowerRedVariants = new GlowFlowerVariants(red);
    glowFlowerRed = register(red, glowFlowerRedVariants);
    addGlowFlowerRecipes(red, glowFlowerRedVariants);
    
    GlowFlowerYellow yellow = new GlowFlowerYellow("glow_flower_yellow");
    glowFlowerYellowVariants = new GlowFlowerVariants(yellow);
    glowFlowerYellow = register(yellow, glowFlowerYellowVariants);
    addGlowFlowerRecipes(yellow, glowFlowerYellowVariants);
    
    EnchantedFlowerRed enchantedRed = new EnchantedFlowerRed("enchanted_flower_red");
    enchantedFlowerRedVariants = new EnchantedFlowerVariants(enchantedRed);
    enchantedFlowerRed = register(enchantedRed, enchantedFlowerRedVariants);
    addEnchantedFlowerRecipes(enchantedRed, enchantedFlowerRedVariants);
    
    EnchantedFlowerYellow enchantedYellow = new EnchantedFlowerYellow("enchanted_flower_yellow");
    enchantedFlowerYellowVariants = new EnchantedFlowerVariants(enchantedYellow);
    enchantedFlowerYellow = register(enchantedYellow, enchantedFlowerYellowVariants);
    addEnchantedFlowerRecipes(enchantedYellow, enchantedFlowerYellowVariants);
    
    
  }

  private static <T extends Block> T register (T block, ItemBlock itemBlock) {
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
			GameRegistry.addShapelessRecipe(stack, flower.getMinecraftFlower(stack.getMetadata()), Items.GLOWSTONE_DUST);
		}
  }
  
  private static void addEnchantedFlowerRecipes(EnchantedFlower flower, ItemBlock itemBlock) {
		for (EnumFlowerType type : FlowerBase.EnumFlowerType.getTypes(flower.getBlockType())) {
			ItemStack stack = new ItemStack(flower, 1, type.getMeta());
  		GameRegistry.addRecipe(stack, "ABA", "CDC", "ABA", 'A', Items.DIAMOND, 'B', Blocks.OBSIDIAN, 'C', Blocks.GLASS, 'D', flower.getMinecraftFlower(stack.getMetadata()));
  	}
  }

}
