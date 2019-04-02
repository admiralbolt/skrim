package avi.mod.skrim.blocks;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.blocks.flowers.EnchantedFlower;
import avi.mod.skrim.blocks.flowers.GlowFlower;
import avi.mod.skrim.blocks.food.AngelCakeBlock;
import avi.mod.skrim.blocks.food.SkrimCakeBlock;
import avi.mod.skrim.blocks.misc.MegaChest;
import avi.mod.skrim.blocks.plants.*;
import avi.mod.skrim.blocks.tnt.BioBomb;
import avi.mod.skrim.blocks.tnt.CustomTNTPrimed;
import avi.mod.skrim.blocks.tnt.Dynamite;
import avi.mod.skrim.blocks.tnt.Napalm;
import com.google.common.base.Preconditions;
import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@GameRegistry.ObjectHolder(Skrim.modId)
public final class ModBlocks {

  public static GlowFlower GLOW_FLOWER_POPPY = new GlowFlower("glow_flower_poppy");
  public static GlowFlower GLOW_FLOWER_DANDELION = new GlowFlower("glow_flower_dandelion");
  public static GlowFlower GLOW_FLOWER_BLUE_ORCHID = new GlowFlower("glow_flower_blue_orchid");
  public static GlowFlower GLOW_FLOWER_ALLIUM = new GlowFlower("glow_flower_allium");
  public static GlowFlower GLOW_FLOWER_AZURE_BLUET = new GlowFlower("glow_flower_azure_bluet");
  public static GlowFlower GLOW_FLOWER_RED_TULIP = new GlowFlower("glow_flower_red_tulip");
  public static GlowFlower GLOW_FLOWER_ORANGE_TULIP = new GlowFlower("glow_flower_orange_tulip");
  public static GlowFlower GLOW_FLOWER_WHITE_TULIP = new GlowFlower("glow_flower_white_tulip");
  public static GlowFlower GLOW_FLOWER_PINK_TULIP = new GlowFlower("glow_flower_pink_tulip");
  public static GlowFlower GLOW_FLOWER_OXEYE_DAISY = new GlowFlower("glow_flower_oxeye_daisy");

  public static EnchantedFlower ENCHANTED_FLOWER_POPPY = new EnchantedFlower("enchanted_flower_poppy");
  public static EnchantedFlower ENCHANTED_FLOWER_DANDELION = new EnchantedFlower("enchanted_flower_dandelion");
  public static EnchantedFlower ENCHANTED_FLOWER_BLUE_ORCHID = new EnchantedFlower("enchanted_flower_blue_orchid");
  public static EnchantedFlower ENCHANTED_FLOWER_ALLIUM = new EnchantedFlower("enchanted_flower_allium");
  public static EnchantedFlower ENCHANTED_FLOWER_AZURE_BLUET = new EnchantedFlower("enchanted_flower_azure_bluet");
  public static EnchantedFlower ENCHANTED_FLOWER_RED_TULIP = new EnchantedFlower("enchanted_flower_red_tulip");
  public static EnchantedFlower ENCHANTED_FLOWER_ORANGE_TULIP = new EnchantedFlower("enchanted_flower_orange_tulip");
  public static EnchantedFlower ENCHANTED_FLOWER_WHITE_TULIP = new EnchantedFlower("enchanted_flower_white_tulip");
  public static EnchantedFlower ENCHANTED_FLOWER_PINK_TULIP = new EnchantedFlower("enchanted_flower_pink_tulip");
  public static EnchantedFlower ENCHANTED_FLOWER_OXEYE_DAISY = new EnchantedFlower("enchanted_flower_oxeye_daisy");

  public static Dynamite DYNAMITE = new Dynamite();
  public static BioBomb BIOBOMB = new BioBomb();
  public static Napalm NAPALM = new Napalm();
  public static SkrimCakeBlock SKRIM_CAKE = new SkrimCakeBlock();
  public static AngelCakeBlock ANGEL_CAKE = new AngelCakeBlock();
  public static MagicBean MAGIC_BEAN = new MagicBean();
  public static BeanstalkBlock BEANSTALK_BLOCK = new BeanstalkBlock();
  public static WeirwoodSapling WEIRWOOD_SAPLING = new WeirwoodSapling();
  public static WeirwoodWood WEIRWOOD_WOOD = new WeirwoodWood();
  public static WeirwoodLeaf WEIRWOOD_LEAF = new WeirwoodLeaf();
  public static MegaChest MEGA_CHEST = new MegaChest();

  @Mod.EventBusSubscriber(modid = Skrim.modId)
  public static class RegistrationHandler {

    public static final Block[] NORMAL_BLOCKS = {
        MEGA_CHEST,
        // BOOM
        BIOBOMB,
        DYNAMITE,
        NAPALM,
        // Skill blocks
        SKRIM_CAKE,
        ANGEL_CAKE,
        MAGIC_BEAN,
        BEANSTALK_BLOCK,
        WEIRWOOD_LEAF,
        WEIRWOOD_SAPLING,
        WEIRWOOD_WOOD
    };

    public static final Block[] GLOW_FLOWERS = {
        GLOW_FLOWER_POPPY,
        GLOW_FLOWER_DANDELION,
        GLOW_FLOWER_BLUE_ORCHID,
        GLOW_FLOWER_ALLIUM,
        GLOW_FLOWER_AZURE_BLUET,
        GLOW_FLOWER_RED_TULIP,
        GLOW_FLOWER_ORANGE_TULIP,
        GLOW_FLOWER_WHITE_TULIP,
        GLOW_FLOWER_PINK_TULIP,
        GLOW_FLOWER_OXEYE_DAISY
    };


    public static final Block[] ENCHANTED_FLOWERS = {
        ENCHANTED_FLOWER_POPPY,
        ENCHANTED_FLOWER_DANDELION,
        ENCHANTED_FLOWER_BLUE_ORCHID,
        ENCHANTED_FLOWER_ALLIUM,
        ENCHANTED_FLOWER_AZURE_BLUET,
        ENCHANTED_FLOWER_RED_TULIP,
        ENCHANTED_FLOWER_ORANGE_TULIP,
        ENCHANTED_FLOWER_WHITE_TULIP,
        ENCHANTED_FLOWER_PINK_TULIP,
        ENCHANTED_FLOWER_OXEYE_DAISY
    };

    public static final Block[] ALL_BLOCKS =
        Stream.of(NORMAL_BLOCKS, GLOW_FLOWERS, ENCHANTED_FLOWERS).flatMap(Stream::of).toArray(Block[]::new);

    @SubscribeEvent
    public static void registerBlocks(final RegistryEvent.Register<Block> event) {
      final IForgeRegistry<Block> registry = event.getRegistry();
      EntityRegistry.registerModEntity(new ResourceLocation("skrim:custom_tnt_primed"), CustomTNTPrimed.class,
          "CustomTNTPrimed", 17654, Skrim.instance, 20,
          5, true);
      for (final Block block : ALL_BLOCKS) {
        System.out.println("registering block: " + block);
        System.out.println("unlocalizedName: " + block.getUnlocalizedName() + ", registryName: " + block.getRegistryName());
        block.setCreativeTab(Skrim.creativeTab);
        registry.register(block);
      }
    }

    @SubscribeEvent
    public static void registerItemBlocks(final RegistryEvent.Register<Item> event) {
      final IForgeRegistry<Item> registry = event.getRegistry();

      for (Block block : NORMAL_BLOCKS) {
        CustomItemBlock item = new CustomItemBlock(block);
        final ResourceLocation registryName = Preconditions.checkNotNull(block.getRegistryName(), "Block %s has null " +
            "registry name", block);
        registry.register(item.setRegistryName(registryName));
      }

      for (Block block : GLOW_FLOWERS) {
        CustomItemBlock item = new CustomItemBlock(block, false, EnumRarity.UNCOMMON);
        final ResourceLocation registryName = Preconditions.checkNotNull(block.getRegistryName(), "Block %s has null " +
            "registry name", block);
        registry.register(item.setRegistryName(registryName));
      }

      for (Block block : ENCHANTED_FLOWERS) {
        CustomItemBlock item = new CustomItemBlock(block, true, EnumRarity.RARE);
        final ResourceLocation registryName = Preconditions.checkNotNull(block.getRegistryName(), "Block %s has null " +
            "registry name", block);
        registry.register(item.setRegistryName(registryName));
      }

    }

  }

}
