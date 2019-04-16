package avi.mod.skrim.items;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.init.SkrimSoundEvents;
import avi.mod.skrim.items.armor.ArtifactArmor;
import avi.mod.skrim.items.armor.CustomArmor;
import avi.mod.skrim.items.armor.LeafArmor;
import avi.mod.skrim.items.armor.Overalls;
import avi.mod.skrim.items.artifacts.*;
import avi.mod.skrim.items.food.AngelCake;
import avi.mod.skrim.items.food.CustomFood;
import avi.mod.skrim.items.food.SkrimCake;
import avi.mod.skrim.items.items.ArtifactItem;
import avi.mod.skrim.items.items.CustomRecord;
import avi.mod.skrim.items.items.WeirwoodTotem;
import avi.mod.skrim.items.tools.*;
import avi.mod.skrim.items.weapons.ArtifactSword;
import avi.mod.skrim.items.weapons.CustomSword;
import avi.mod.skrim.items.weapons.GreatBow;
import avi.mod.skrim.items.weapons.RocketLauncher;
import net.minecraft.block.BlockPlanks;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@GameRegistry.ObjectHolder(Skrim.MOD_ID)
public class SkrimItems {

  public static EnumRarity ARTIFACT_RARITY = EnumHelper.addRarity("artifact", TextFormatting.GOLD, "Artifact");

  /**
   * Default ArmorMaterials
   * <p>
   * Durability: leather -> 5 chain/iron -> 15 gold -> 7 diamond -> 33
   * <p>
   * Reductions:
   * leather -> {1, 3, 2, 1} Total 7
   * chain -> {2, 5, 4, 1} Total 12
   * iron -> {2, 6, 5, 2} Total 15
   * gold -> {2, 5, 3, 1} Total 11
   * diamond -> {3, 8, 6, 3} Total 20
   * <p>
   * Enchantability: leather -> 15 chain -> 12 iron -> 9 gold -> 25 diamond -> 10
   */
  public static ItemArmor.ArmorMaterial OBSIDIAN_MATERIAL = EnumHelper.addArmorMaterial("obsidian", "skrim" +
      ":obsidian_armor", 40, new int[]{4, 9, 7, 4}, 20, null, 1.0F);

  /**
   * Default ToolMaterials HarvestLevel: wood: 0 stone: 1 iron: 2 diamond: 3 Durability: wood: 59 stone: 131 iron:
   * 250 gold: 32 diamond: 1561 Mining Speed: wood: 2.0F stone: 4.0F iron: 6.0F gold: 12.0F diamond: 8.0F Damage vs.
   * Entity wood: 0.0F stone: 1.0F iron: 2.0F gold: 0.0F diamond: 3.0F Enchantability: wood: 15 stone: 5 iron: 14
   * gold: 22 diamond: 10
   */
  public static ToolMaterial ARTIFACT_DEFAULT = EnumHelper.addToolMaterial("artifact_default", 3, 4500, 6.0F, 4.0F, 0);
  public static ToolMaterial OBSIDIAN_TOOL = EnumHelper.addToolMaterial("obsidian", 3, 2000, 9.0F, 4.0F, 20);


  // Artifacts!
  public static ArtifactSword CANES_SWORD = new CanesSword();
  public static ArtifactItem FIRE_STAFF = new FireStaff();

  public static ArtifactArmor POWER_SUIT_CHESTPLATE = new PowerSuitChestplate();

  public static ArtifactArmor SPRINGHEEL_BOOTS = new SpringheelBoots();
  public static ArtifactArmor BLINDING_BOOTS = new BlindingBoots();

  public static ArtifactArmor GRUESOME_MASK = new GruesomeMask();
  public static ArtifactArmor FOX_MASK = new FoxMask();

  public static ArtifactItem SHEEP_FINDER = new SheepFinder3000();
  public static ArtifactItem ETHER_MEDALLION = new EtherMedallion();
  public static ArtifactItem ICARIAN_SCROLL = new IcarianScroll();
  public static ArtifactItem HORSE_STEROID_SPEED = new HorseSteroidSpeed();
  public static ArtifactItem HORSE_STEROID_HEALTH = new HorseSteroidHealth();
  public static ArtifactItem HORSE_STEROID_JUMP = new HorseSteroidJump();

  public static Item[] ARTIFACTS = {
      CANES_SWORD,
      POWER_SUIT_CHESTPLATE,
      SPRINGHEEL_BOOTS,
      BLINDING_BOOTS,
      GRUESOME_MASK,
      FOX_MASK,
      SHEEP_FINDER,
      ICARIAN_SCROLL,
      HORSE_STEROID_SPEED,
      HORSE_STEROID_HEALTH,
      HORSE_STEROID_JUMP,
      ETHER_MEDALLION,
      FIRE_STAFF
  };

  // La musica
  public static CustomRecord ARUARIAN_DANCE = new CustomRecord("aruarian_dance", SkrimSoundEvents.ARUARIAN_DANCE);
  public static CustomRecord BUBBERDUCKY = new CustomRecord("bubberducky", SkrimSoundEvents.BUBBERDUCKY);
  public static CustomRecord CASSANDRA = new CustomRecord("cassandra", SkrimSoundEvents.CASSANDRA);
  public static CustomRecord COLOR = new CustomRecord("lack_of_color", SkrimSoundEvents.LACK_OF_COLOR);
  public static CustomRecord DOGSONG = new CustomRecord("dogsong", SkrimSoundEvents.DOGSONG);
  public static CustomRecord GDAWG = new CustomRecord("gdawg", SkrimSoundEvents.GDAWG);
  public static CustomRecord HEYA = new CustomRecord("heya", SkrimSoundEvents.HEYA);
  public static CustomRecord MONEY = new CustomRecord("money", SkrimSoundEvents.MONEY);
  public static CustomRecord NORTH = new CustomRecord("north", SkrimSoundEvents.NORTH);
  public static CustomRecord NUMBER10 = new CustomRecord("number10", SkrimSoundEvents.NUMBER10);
  public static CustomRecord SAMURAI = new CustomRecord("samurai", SkrimSoundEvents.SAMURAI);
  public static CustomRecord TRUCK = new CustomRecord("truck", SkrimSoundEvents.TRUCK);

  public static CustomRecord[] SONGS = {
      ARUARIAN_DANCE,
      BUBBERDUCKY,
      CASSANDRA,
      COLOR,
      DOGSONG,
      GDAWG,
      HEYA,
      MONEY,
      NORTH,
      NUMBER10,
      SAMURAI,
      TRUCK
  };

  // Food!
  public static CustomFood OVERWRITE_PORKCHOP = new CustomFood("overwrite_porkchop", 8, 1.6F, true);
  public static CustomFood OVERWRITE_BAKED_POTATO = new CustomFood("overwrite_baked_potato", 5, 1.2F, false);
  public static CustomFood OVERWRITE_BEETROOT_SOUP = new CustomFood("overwrite_beetroot_soup", 6, 1.2F, false);
  public static CustomFood OVERWRITE_BREAD = new CustomFood("overwrite_bread", 5, 1.2F, false);
  public static CustomFood OVERWRITE_CHICKEN = new CustomFood("overwrite_chicken", 6, 1.2F, true);
  public static CustomFood OVERWRITE_FISH = new CustomFood("overwrite_fish", 5, 1.2F, true);
  public static CustomFood OVERWRITE_MUTTON = new CustomFood("overwrite_mutton", 6, 1.6F, true);
  public static CustomFood OVERWRITE_SALMON = new CustomFood("overwrite_salmon", 6, 1.6F, true);
  public static CustomFood OVERWRITE_COOKIE = new CustomFood("overwrite_cookie", 2, 0.2F, false);
  public static CustomFood OVERWRITE_MUSHROOM_STEW = new CustomFood("overwrite_mushroom_stew", 6, 1.2F, false);
  public static CustomFood OVERWRITE_PUMPKIN_STEW = new CustomFood("overwrite_pumpkin_pie", 8, 0.6F, false);
  public static CustomFood OVERWRITE_RABBIT_STEW = new CustomFood("overwrite_rabbit_stew", 10, 1.2F, false);
  public static CustomFood OVERWRITE_STEAK = new CustomFood("overwrite_steak", 8, 1.6F, true);
  public static CustomFood OVERWRITE_RABBIT = new CustomFood("overwrite_rabbit", 5, 1.2F, true);
  public static CustomFood CANES_CHICKEN = new CustomFood("canes_chicken", 20, 1.5F, true);
  public static SkrimCake SKRIM_CAKE = new SkrimCake();
  public static AngelCake ANGEL_CAKE = new AngelCake();

  // Obsidian
  public static CustomArmor OBSIDIAN_HELMET = new CustomArmor("obsidian_helmet", OBSIDIAN_MATERIAL, 4,
      EntityEquipmentSlot.HEAD);
  public static CustomArmor OBSIDIAN_CHEST = new CustomArmor("obsidian_chest", OBSIDIAN_MATERIAL, 3,
      EntityEquipmentSlot.CHEST);
  public static CustomArmor OBSIDIAN_PANTS = new CustomArmor("obsidian_pants", OBSIDIAN_MATERIAL, 2,
      EntityEquipmentSlot.LEGS);
  public static CustomArmor OBSIDIAN_BOOTS = new CustomArmor("obsidian_boots", OBSIDIAN_MATERIAL, 1,
      EntityEquipmentSlot.FEET);

  public static CustomSword OBSIDIAN_SWORD = new CustomSword("obsidian_sword", OBSIDIAN_TOOL);
  public static CustomHoe OBSIDIAN_HOE = new CustomHoe("obsidian_hoe", OBSIDIAN_TOOL);
  public static CustomSpade OBSIDIAN_SHOVEL = new CustomSpade("obsidian_spade", OBSIDIAN_TOOL);
  public static CustomAxe OBSIDIAN_AXE = new CustomAxe("obsidian_axe", OBSIDIAN_TOOL, 8F, -3F);
  public static CustomPickaxe OBSIDIAN_PICKAXE = new CustomPickaxe("obsidian_pickaxe", OBSIDIAN_TOOL);


  public static Item WEIRWOOD_TOTEM = new WeirwoodTotem();
  public static Item ROCKET_LAUNCHER = new RocketLauncher();
  public static Item GREAT_BOW = new GreatBow();
  public static Item OVERALLS = new Overalls();
  public static Item HAND_SAW = new HandSaw();


  // Leafy bois
  public static LeafArmor OAK_LEAF_BOOTS = new LeafArmor(BlockPlanks.EnumType.OAK, "leaf_boots", 1,
      EntityEquipmentSlot.FEET);
  public static LeafArmor OAK_LEAF_PANTS = new LeafArmor(BlockPlanks.EnumType.OAK, "leaf_pants", 1,
      EntityEquipmentSlot.LEGS);
  public static LeafArmor OAK_LEAF_ARMOR = new LeafArmor(BlockPlanks.EnumType.OAK, "leaf_armor", 1,
      EntityEquipmentSlot.CHEST);
  public static LeafArmor OAK_LEAF_HELMET = new LeafArmor(BlockPlanks.EnumType.OAK, "leaf_helmet", 1,
      EntityEquipmentSlot.HEAD);

  public static LeafArmor SPRUCE_LEAF_BOOTS = new LeafArmor(BlockPlanks.EnumType.SPRUCE, "leaf_boots", 1,
      EntityEquipmentSlot.FEET);
  public static LeafArmor SPRUCE_LEAF_PANTS = new LeafArmor(BlockPlanks.EnumType.SPRUCE, "leaf_pants", 1,
      EntityEquipmentSlot.LEGS);
  public static LeafArmor SPRUCE_LEAF_ARMOR = new LeafArmor(BlockPlanks.EnumType.SPRUCE, "leaf_armor", 1,
      EntityEquipmentSlot.CHEST);
  public static LeafArmor SPRUCE_LEAF_HELMET = new LeafArmor(BlockPlanks.EnumType.SPRUCE, "leaf_helmet", 1,
      EntityEquipmentSlot.HEAD);

  public static LeafArmor BIRCH_LEAF_BOOTS = new LeafArmor(BlockPlanks.EnumType.BIRCH, "leaf_boots", 1,
      EntityEquipmentSlot.FEET);
  public static LeafArmor BIRCH_LEAF_PANTS = new LeafArmor(BlockPlanks.EnumType.BIRCH, "leaf_pants", 1,
      EntityEquipmentSlot.LEGS);
  public static LeafArmor BIRCH_LEAF_ARMOR = new LeafArmor(BlockPlanks.EnumType.BIRCH, "leaf_armor", 1,
      EntityEquipmentSlot.CHEST);
  public static LeafArmor BIRCH_LEAF_HELMET = new LeafArmor(BlockPlanks.EnumType.BIRCH, "leaf_helmet", 1,
      EntityEquipmentSlot.HEAD);

  public static LeafArmor JUNGLE_LEAF_BOOTS = new LeafArmor(BlockPlanks.EnumType.JUNGLE, "leaf_boots", 1,
      EntityEquipmentSlot.FEET);
  public static LeafArmor JUNGLE_LEAF_PANTS = new LeafArmor(BlockPlanks.EnumType.JUNGLE, "leaf_pants", 1,
      EntityEquipmentSlot.LEGS);
  public static LeafArmor JUNGLE_LEAF_ARMOR = new LeafArmor(BlockPlanks.EnumType.JUNGLE, "leaf_armor", 1,
      EntityEquipmentSlot.CHEST);
  public static LeafArmor JUNGLE_LEAF_HELMET = new LeafArmor(BlockPlanks.EnumType.JUNGLE, "leaf_helmet", 1,
      EntityEquipmentSlot.HEAD);

  public static LeafArmor DARK_OAK_LEAF_BOOTS = new LeafArmor(BlockPlanks.EnumType.DARK_OAK, "leaf_boots", 1,
      EntityEquipmentSlot.FEET);
  public static LeafArmor DARK_OAK_LEAF_PANTS = new LeafArmor(BlockPlanks.EnumType.DARK_OAK, "leaf_pants", 1,
      EntityEquipmentSlot.LEGS);
  public static LeafArmor DARK_OAK_LEAF_ARMOR = new LeafArmor(BlockPlanks.EnumType.DARK_OAK, "leaf_armor", 1,
      EntityEquipmentSlot.CHEST);
  public static LeafArmor DARK_OAK_LEAF_HELMET = new LeafArmor(BlockPlanks.EnumType.DARK_OAK, "leaf_helmet", 1,
      EntityEquipmentSlot.HEAD);

  public static LeafArmor ACACIA_LEAF_BOOTS = new LeafArmor(BlockPlanks.EnumType.ACACIA, "leaf_boots", 1,
      EntityEquipmentSlot.FEET);
  public static LeafArmor ACACIA_LEAF_PANTS = new LeafArmor(BlockPlanks.EnumType.ACACIA, "leaf_pants", 1,
      EntityEquipmentSlot.LEGS);
  public static LeafArmor ACACIA_LEAF_ARMOR = new LeafArmor(BlockPlanks.EnumType.ACACIA, "leaf_armor", 1,
      EntityEquipmentSlot.CHEST);
  public static LeafArmor ACACIA_LEAF_HELMET = new LeafArmor(BlockPlanks.EnumType.ACACIA, "leaf_helmet", 1,
      EntityEquipmentSlot.HEAD);

  @Mod.EventBusSubscriber(modid = Skrim.MOD_ID)
  public static class RegistrationHandler {
    public static final Set<Item> ITEMS = new HashSet<>();

    @SubscribeEvent
    public static void registerItems(final RegistryEvent.Register<Item> event) {
      ArrayList<Item> items = new ArrayList<Item>(Arrays.asList(
          OAK_LEAF_BOOTS,
          OAK_LEAF_PANTS,
          OAK_LEAF_ARMOR,
          OAK_LEAF_HELMET,
          SPRUCE_LEAF_BOOTS,
          SPRUCE_LEAF_PANTS,
          SPRUCE_LEAF_ARMOR,
          SPRUCE_LEAF_HELMET,
          BIRCH_LEAF_BOOTS,
          BIRCH_LEAF_PANTS,
          BIRCH_LEAF_ARMOR,
          BIRCH_LEAF_HELMET,
          JUNGLE_LEAF_BOOTS,
          JUNGLE_LEAF_PANTS,
          JUNGLE_LEAF_ARMOR,
          JUNGLE_LEAF_HELMET,
          DARK_OAK_LEAF_BOOTS,
          DARK_OAK_LEAF_PANTS,
          DARK_OAK_LEAF_ARMOR,
          DARK_OAK_LEAF_HELMET,
          ACACIA_LEAF_BOOTS,
          ACACIA_LEAF_PANTS,
          ACACIA_LEAF_ARMOR,
          ACACIA_LEAF_HELMET,
          OBSIDIAN_HELMET,
          OBSIDIAN_CHEST,
          OBSIDIAN_PANTS,
          OBSIDIAN_BOOTS,
          OBSIDIAN_AXE,
          OBSIDIAN_HOE,
          OBSIDIAN_PICKAXE,
          OBSIDIAN_SHOVEL,
          OBSIDIAN_SWORD,
          WEIRWOOD_TOTEM,
          ROCKET_LAUNCHER,
          GREAT_BOW,
          OVERALLS,
          HAND_SAW,

          // Food
          OVERWRITE_PORKCHOP,
          OVERWRITE_BAKED_POTATO,
          OVERWRITE_BEETROOT_SOUP,
          OVERWRITE_BREAD,
          OVERWRITE_CHICKEN,
          OVERWRITE_FISH,
          OVERWRITE_MUTTON,
          OVERWRITE_SALMON,
          OVERWRITE_COOKIE,
          OVERWRITE_MUSHROOM_STEW,
          OVERWRITE_PUMPKIN_STEW,
          OVERWRITE_RABBIT_STEW,
          OVERWRITE_STEAK,
          OVERWRITE_RABBIT,
          CANES_CHICKEN,
          SKRIM_CAKE,
          ANGEL_CAKE
      ));

      items.addAll(new ArrayList<>(Arrays.asList(ARTIFACTS)));
      items.addAll(new ArrayList<Item>(Arrays.asList(SONGS)));

      final IForgeRegistry<Item> registry = event.getRegistry();
      for (final Item item : items) {
        registry.register(item);
        item.setCreativeTab(Skrim.CREATIVE_TAB);
        ITEMS.add(item);
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(),
            ((ItemBase) item).getTexturePath()));

      }
    }

  }

}
