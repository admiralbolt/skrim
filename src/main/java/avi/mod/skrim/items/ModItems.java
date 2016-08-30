package avi.mod.skrim.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scala.actors.threadpool.Arrays;

import avi.mod.skrim.Skrim;
import net.minecraft.block.BlockBush;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

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

  public static EnumRarity ARTIFACT_RARITY = EnumHelper.addRarity("artifact", TextFormatting.GOLD, "Artifact");

  public static ArmorMaterial ARTIFACT_DARK = EnumHelper.addArmorMaterial("artifact_dark", "skrim:artifact_dark", 50, new int[] {3, 8, 6, 3}, 30, null, 5.0F);
  public static ToolMaterial ARTIFACT_DEFAULT = EnumHelper.addToolMaterial("artifact_default", 3, 4500, 6.0F, 4.0F, 0);


  public static ArtifactArmor bootsOfSpringheelJak;

  public static ArtifactSword raisingCanesFrySword;

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

    // Artifact Armors
    bootsOfSpringheelJak = register(new ArtifactArmor("boots_of_springheel_jak", ARTIFACT_DARK, 1, EntityEquipmentSlot.FEET));

    // Artifact Swords
    raisingCanesFrySword = register(new ArtifactSword("raising_canes_fry_sword", ARTIFACT_DEFAULT));

    registerRabbitStew();
  }


  /**
   * We have to be explicit with our recipes.
   * Which means since we're injecting our own food....
   * Sigh.
   */
  public static void registerRabbitStew() {
  	Item[] rabbits = {(Item) overwriteRabbit, Items.COOKED_RABBIT};
  	BlockBush[] mushrooms = {Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM};
  	Item[] potatoes = {overwriteBakedPotato, Items.BAKED_POTATO};
  	for (Item rabbit : rabbits) {
  		for (Item potato: potatoes) {
				if (rabbit != Items.COOKED_RABBIT || potato != Items.BAKED_POTATO) {
					for (BlockBush mushroom: mushrooms) {
						GameRegistry.addRecipe(
							new ItemStack(Items.RABBIT_STEW),
							" a ",
							"bcd",
							" e ",
							'a', new ItemStack(rabbit),
							'b', new ItemStack(Items.CARROT),
							'c', new ItemStack(potato),
							'd', new ItemStack(mushroom),
							'e', new ItemStack(Items.BOWL)
						);
					}
				}
			}
  	}
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
