package avi.mod.skrim.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

import java.util.Objects;
import java.util.function.ToIntFunction;

/**
 * Utilities for helping register blocks.
 */
public class BlockRegistrationUtils {

  private static final StateMapperBase propertyStringMapper = new StateMapperBase() {
    @Override
    protected ModelResourceLocation getModelResourceLocation(final IBlockState state) {
      return new ModelResourceLocation("minecraft:air");
    }
  };

  /**
   * Register a single model for an {@link Item}.
   * <p>
   * Uses the registry name as the domain/path and {@code "inventory"} as the variant.
   *
   * @param item The Item
   */
  public static void registerItemModel(final Item item) {
    final ResourceLocation registryName = Objects.requireNonNull(item.getRegistryName());
    registerItemModel(item, registryName.toString());
  }

  /**
   * Register a single model for an {@link Item}.
   * <p>
   * Uses {@code modelLocation} as the domain/path and {@link "inventory"} as the variant.
   *
   * @param item          The Item
   * @param modelLocation The model location
   */
  private static void registerItemModel(final Item item, final String modelLocation) {
    final ModelResourceLocation fullModelLocation = new ModelResourceLocation(modelLocation, "inventory");
    ModelBakery.registerItemVariants(item, fullModelLocation);
    // Ensure the custom model is loaded and prevent the default model from being loaded.
    registerItemModel(item, stack -> fullModelLocation);
  }

  /**
   * Register an {@link ItemMeshDefinition} for an {@link Item}.
   *
   * @param item           The Item
   * @param meshDefinition The ItemMeshDefinition
   */
  private static void registerItemModel(final Item item, final ItemMeshDefinition meshDefinition) {
    ModelLoader.setCustomMeshDefinition(item, meshDefinition);
  }

  /**
   * Register a model for each metadata value of the {@link Block}'s {@link Item} corresponding to the values of an
   * {@link IProperty}.
   * <p>
   * For each value:
   * <li>The domain/path is the registry name</li>
   * <li>The variant is {@code baseState} with the {@link IProperty} set to the value</li>
   * <p>
   * The {@code getMeta} function is used to get the metadata of each value.
   *
   * @param baseState The base state to use for the variant
   * @param property  The property whose values should be used
   * @param getMeta   A function to get the metadata of each value
   * @param <T>       The value type
   */
  public static <T extends Comparable<T>> void registerVariantBlockItemModels(final IBlockState baseState,
                                                                               final IProperty<T> property,
                                                                               final ToIntFunction<T> getMeta) {
    property.getAllowedValues().forEach(value -> registerBlockItemModelForMeta(baseState.withProperty(property,
        value), getMeta.applyAsInt(value)));
  }

  /**
   * Register a single model for the {@link Block}'s {@link Item}.
   * <p>
   * Uses the registry name as the domain/path and the {@link IBlockState} as the variant.
   *
   * @param state The state to use as the variant
   */
  public static void registerBlockItemModel(final IBlockState state) {
    final Block block = state.getBlock();
    final Item item = Item.getItemFromBlock(block);

    if (item != Items.AIR) {
      final ResourceLocation registryName = Objects.requireNonNull(block.getRegistryName());
      final ModelResourceLocation fullModelLocation = new ModelResourceLocation(registryName,
          propertyStringMapper.getPropertyString(state.getProperties()));
      ModelBakery.registerItemVariants(item, fullModelLocation);
      // Ensure the custom model is loaded and prevent the default model from being loaded.
      registerItemModel(item, stack -> fullModelLocation);
    }
  }

  /**
   * Register a model for a metadata value of the {@link Block}'s {@link Item}.
   * <p>
   * Uses the registry name as the domain/path and the {@link IBlockState} as the variant.
   *
   * @param state    The state to use as the variant
   * @param metadata The item metadata to register the model for
   */
  private static void registerBlockItemModelForMeta(final IBlockState state, final int metadata) {
    final Item item = Item.getItemFromBlock(state.getBlock());

    if (item != Items.AIR) {
      registerItemModelForMeta(item, metadata, propertyStringMapper.getPropertyString(state.getProperties()));
    }
  }

  /**
   * Register a model for a metadata value an {@link Item}.
   * <p>
   * Uses the registry name as the domain/path and {@code variant} as the variant.
   *
   * @param item     The Item
   * @param metadata The metadata
   * @param variant  The variant
   */
  private static void registerItemModelForMeta(final Item item, final int metadata, final String variant) {
    ModelLoader.setCustomModelResourceLocation(item, metadata, new ModelResourceLocation(item.getRegistryName(),
        variant));
  }
}
