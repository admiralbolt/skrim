package avi.mod.skrim.blocks.flowers;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import avi.mod.skrim.Skrim;
import avi.mod.skrim.blocks.ModBlocks;
import avi.mod.skrim.blocks.VariantEnum;
import avi.mod.skrim.items.ItemModelProvider;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

public abstract class FlowerBase extends BlockBush implements ItemModelProvider {

	protected String name;

	@Override
	public FlowerBase setCreativeTab(CreativeTabs tab) {
		super.setCreativeTab(tab);
		return this;
	}

	@Override
	public void registerItemModel(Item item) {
		if (item instanceof ItemBlock) {
			ItemBlock itemBlock = (ItemBlock) item;
			System.out.println("trykng to register itmeBoakc: " + itemBlock);
			Block block = itemBlock.getBlock();
			if (block instanceof FlowerBase) {
				System.out.println("is instance!");
				FlowerBase flower = (FlowerBase) block;
				Skrim.proxy.registerBlockVariant(itemBlock, FlowerBase.EnumFlowerType.getTypes(flower.getBlockType()));
			}
		}
	}

	protected PropertyEnum<FlowerBase.EnumFlowerType> type;

	protected FlowerBase() {
		this.setDefaultState(this.blockState.getBaseState().withProperty(this.getTypeProperty(), this.getBlockType() == FlowerBase.EnumFlowerColor.RED ? FlowerBase.EnumFlowerType.POPPY : FlowerBase.EnumFlowerType.DANDELION));
		this.setCreativeTab(Skrim.creativeTab);
	}

	/**
	 * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It returns the metadata of the dropped item based on the old metadata of the block.
	 */
	public int damageDropped(IBlockState state) {
		return ((FlowerBase.EnumFlowerType) state.getValue(this.getTypeProperty())).getMeta();
	}

	/**
	 * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
	 */
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
		for (FlowerBase.EnumFlowerType blockflower$enumflowertype : FlowerBase.EnumFlowerType.getTypes(this.getBlockType())) {
			list.add(new ItemStack(itemIn, 1, blockflower$enumflowertype.getMeta()));
		}
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(this.getTypeProperty(), FlowerBase.EnumFlowerType.getType(this.getBlockType(), meta));
	}

	/**
	 * Get the Type of this flower (Yellow/Red)
	 */
	public abstract FlowerBase.EnumFlowerColor getBlockType();
	
	public ItemStack getMinecraftFlower(int meta) {
		if (this.getBlockType() == EnumFlowerColor.RED) {
			return new ItemStack(Blocks.RED_FLOWER, 1, meta);
		} else if (this.getBlockType() == EnumFlowerColor.YELLOW) {
			return new ItemStack(Blocks.YELLOW_FLOWER, 1, meta);
		} else {
			return null;
		}
	}

	public IProperty<FlowerBase.EnumFlowerType> getTypeProperty() {
		if (this.type == null) {
			this.type = PropertyEnum.<FlowerBase.EnumFlowerType> create("type", FlowerBase.EnumFlowerType.class, new Predicate<FlowerBase.EnumFlowerType>() {
				public boolean apply(@Nullable FlowerBase.EnumFlowerType p_apply_1_) {
					return p_apply_1_.getBlockType() == FlowerBase.this.getBlockType();
				}
			});
		}

		return this.type;
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	public int getMetaFromState(IBlockState state) {
		return ((FlowerBase.EnumFlowerType) state.getValue(this.getTypeProperty())).getMeta();
	}

	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { this.getTypeProperty() });
	}

	/**
	 * Get the OffsetType for this Block. Determines if the model is rendered slightly offset.
	 */
	@SideOnly(Side.CLIENT)
	public Block.EnumOffsetType getOffsetType() {
		return Block.EnumOffsetType.XZ;
	}

	public static enum EnumFlowerColor {
		YELLOW, RED;

		public FlowerBase getBlock() {
			return this == YELLOW ? ModBlocks.glowFlowerYellow : ModBlocks.glowFlowerRed;
		}
	}

	public static enum EnumFlowerType implements IStringSerializable, VariantEnum {
		DANDELION(FlowerBase.EnumFlowerColor.YELLOW, 0, "dandelion"), POPPY(FlowerBase.EnumFlowerColor.RED, 0, "poppy"), BLUE_ORCHID(FlowerBase.EnumFlowerColor.RED, 1, "blue_orchid", "blueOrchid"), ALLIUM(FlowerBase.EnumFlowerColor.RED, 2, "allium"), HOUSTONIA(FlowerBase.EnumFlowerColor.RED, 3, "houstonia"), RED_TULIP(FlowerBase.EnumFlowerColor.RED, 4, "red_tulip", "tulipRed"), ORANGE_TULIP(FlowerBase.EnumFlowerColor.RED, 5, "orange_tulip", "tulipOrange"), WHITE_TULIP(
				FlowerBase.EnumFlowerColor.RED, 6, "white_tulip", "tulipWhite"), PINK_TULIP(FlowerBase.EnumFlowerColor.RED, 7, "pink_tulip", "tulipPink"), OXEYE_DAISY(FlowerBase.EnumFlowerColor.RED, 8, "oxeye_daisy", "oxeyeDaisy");

		private static final FlowerBase.EnumFlowerType[][] TYPES_FOR_BLOCK = new FlowerBase.EnumFlowerType[FlowerBase.EnumFlowerColor.values().length][];
		private final FlowerBase.EnumFlowerColor blockType;
		private final int meta;
		private final String name;
		private final String unlocalizedName;

		private EnumFlowerType(FlowerBase.EnumFlowerColor blockType, int meta, String name) {
			this(blockType, meta, name, name);
		}

		private EnumFlowerType(FlowerBase.EnumFlowerColor blockType, int meta, String name, String unlocalizedName) {
			this.blockType = blockType;
			this.meta = meta;
			this.name = name;
			this.unlocalizedName = unlocalizedName;
		}

		public FlowerBase.EnumFlowerColor getBlockType() {
			return this.blockType;
		}

		public int getMeta() {
			return this.meta;
		}

		/**
		 * Get the given FlowerType from BlockType & metadata
		 */
		public static FlowerBase.EnumFlowerType getType(FlowerBase.EnumFlowerColor blockType, int meta) {
			FlowerBase.EnumFlowerType[] ablockflower$enumflowertype = TYPES_FOR_BLOCK[blockType.ordinal()];

			if (meta < 0 || meta >= ablockflower$enumflowertype.length) {
				meta = 0;
			}

			return ablockflower$enumflowertype[meta];
		}

		/**
		 * Get all FlowerTypes that are applicable for the given Flower block ("yellow", "red")
		 */
		@SideOnly(Side.CLIENT)
		public static FlowerBase.EnumFlowerType[] getTypes(FlowerBase.EnumFlowerColor flowerColor) {
			return TYPES_FOR_BLOCK[flowerColor.ordinal()];
		}

		public String toString() {
			return this.name;
		}

		public String getName() {
			return this.name;
		}

		public String getUnlocalizedName() {
			return this.unlocalizedName;
		}

		static {
			for (final FlowerBase.EnumFlowerColor blockflower$enumflowercolor : FlowerBase.EnumFlowerColor.values()) {
				Collection<FlowerBase.EnumFlowerType> collection = Collections2.<FlowerBase.EnumFlowerType> filter(Lists.newArrayList(values()), new Predicate<FlowerBase.EnumFlowerType>() {
					public boolean apply(@Nullable FlowerBase.EnumFlowerType p_apply_1_) {
						return p_apply_1_.getBlockType() == blockflower$enumflowercolor;
					}
				});
				TYPES_FOR_BLOCK[blockflower$enumflowercolor.ordinal()] = (FlowerBase.EnumFlowerType[]) collection.toArray(new FlowerBase.EnumFlowerType[collection.size()]);
			}
		}
	}

}
