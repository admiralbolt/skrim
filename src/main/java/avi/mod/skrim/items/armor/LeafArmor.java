package avi.mod.skrim.items.armor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import avi.mod.skrim.items.CustomArmor;
import avi.mod.skrim.items.ModItems;
import avi.mod.skrim.network.InvisibilityPacket;
import avi.mod.skrim.network.SkrimPacketHandler;
import avi.mod.skrim.utils.Obfuscation;
import avi.mod.skrim.utils.Utils;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBirchTree;
import net.minecraft.world.gen.feature.WorldGenCanopyTree;
import net.minecraft.world.gen.feature.WorldGenSavannaTree;
import net.minecraft.world.gen.feature.WorldGenTaiga1;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class LeafArmor extends CustomArmor {

	private BlockPlanks.EnumType plankType;
	public static Map<UUID, Integer> TICKS_SINCE_MOVE = new HashMap<UUID, Integer>();
	private static final IBlockState JUNGLE_LOG = Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.JUNGLE);
	private static final IBlockState JUNGLE_LEAF = Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.JUNGLE)
			.withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));

	public static ArmorMaterial OAK_LEAF_MATERIAL = EnumHelper.addArmorMaterial("oak_leaf_armor", "skrim:oak_leaf_armor", 20, new int[] { 2, 4, 3, 2 }, 30,
			null, 0.0F);
	public static ArmorMaterial SPRUCE_LEAF_MATERIAL = EnumHelper.addArmorMaterial("spruce_leaf_armor", "skrim:spruce_leaf_armor", 20, new int[] { 2, 4, 3, 2 },
			30, null, 0.0F);
	public static ArmorMaterial BIRCH_LEAF_MATERIAL = EnumHelper.addArmorMaterial("birch_leaf_armor", "skrim:birch_leaf_armor", 20, new int[] { 2, 4, 3, 2 },
			30, null, 0.0F);
	public static ArmorMaterial JUNGLE_LEAF_MATERIAL = EnumHelper.addArmorMaterial("jungle_leaf_armor", "skrim:jungle_leaf_armor", 20, new int[] { 2, 4, 3, 2 },
			30, null, 0.0F);
	public static ArmorMaterial ACACIA_LEAF_MATERIAL = EnumHelper.addArmorMaterial("acacia_leaf_armor", "skrim:acacia_leaf_armor", 20, new int[] { 2, 4, 3, 2 },
			30, null, 0.0F);
	public static ArmorMaterial DARK_OAK_LEAF_MATERIAL = EnumHelper.addArmorMaterial("dark_oak_leaf_armor", "skrim:dark_oak_leaf_armor", 20,
			new int[] { 2, 4, 3, 2 }, 30, null, 0.0F);

	public LeafArmor(BlockPlanks.EnumType plankType, String name, int renderIndex, EntityEquipmentSlot armorType) {
		super(plankType.getName() + "_" + name, getMaterial(plankType), renderIndex, armorType);
		this.plankType = plankType;
	}

	public BlockPlanks.EnumType getPlankType() {
		return this.plankType;
	}

	public static ArmorMaterial getMaterial(BlockPlanks.EnumType plankType) {
		if (plankType == BlockPlanks.EnumType.OAK) {
			return OAK_LEAF_MATERIAL;
		} else if (plankType == BlockPlanks.EnumType.SPRUCE) {
			return SPRUCE_LEAF_MATERIAL;
		} else if (plankType == BlockPlanks.EnumType.BIRCH) {
			return BIRCH_LEAF_MATERIAL;
		} else if (plankType == BlockPlanks.EnumType.JUNGLE) {
			return JUNGLE_LEAF_MATERIAL;
		} else if (plankType == BlockPlanks.EnumType.ACACIA) {
			return ACACIA_LEAF_MATERIAL;
		} else if (plankType == BlockPlanks.EnumType.DARK_OAK) {
			return DARK_OAK_LEAF_MATERIAL;
		} else {
			return null;
		}
	}

	public static WorldGenAbstractTree getGenerator(BlockPlanks.EnumType plankType) {
		if (plankType == BlockPlanks.EnumType.OAK) {
			return new WorldGenTrees(true);
		} else if (plankType == BlockPlanks.EnumType.SPRUCE) {
			return new WorldGenTaiga1();
		} else if (plankType == BlockPlanks.EnumType.BIRCH) {
			return new WorldGenBirchTree(true, true);
		} else if (plankType == BlockPlanks.EnumType.JUNGLE) {
			return new WorldGenTrees(false, 3 + Utils.rand.nextInt(3) + Utils.rand.nextInt(3), JUNGLE_LOG, JUNGLE_LEAF, true);
		} else if (plankType == BlockPlanks.EnumType.ACACIA) {
			return new WorldGenSavannaTree(true);
		} else if (plankType == BlockPlanks.EnumType.DARK_OAK) {
			return new WorldGenCanopyTree(true);
		} else {
			return null;
		}
	}

	public static BlockPlanks.EnumType getPlankTypeFullSet(EntityPlayer player) {
		InventoryPlayer inventory = player.inventory;
		if (inventory != null) {
			ItemStack stack = inventory.armorInventory.get(0);
			if (stack != null) {
				Item targetItem = stack.getItem();
				if (targetItem instanceof LeafArmor) {
					LeafArmor armor = (LeafArmor) targetItem;
					return armor.getPlankType();
				}
			}
		}
		return null;
	}

	public static boolean wearingFullSet(EntityPlayer player) {
		BlockPlanks.EnumType plankType = getPlankTypeFullSet(player);
		if (plankType != null) {
			return wearingFullSet(player, plankType);
		} else {
			return false;
		}
	}

	public static boolean wearingFullSet(EntityPlayer player, BlockPlanks.EnumType plankType) {
		LeafArmor[] set = getSet(plankType);
		if (set != null) {
			for (LeafArmor armor : set) {
				if (!Utils.isWearingArmor(player, armor)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public static LeafArmor[] getSet(BlockPlanks.EnumType plankType) {
		if (plankType.getName().equals("oak")) {
			return new LeafArmor[] { ModItems.OAK_LEAF_BOOTS, ModItems.OAK_LEAF_PANTS, ModItems.OAK_LEAF_ARMOR, ModItems.OAK_LEAF_HELMET };
		} else if (plankType.getName().equals("spruce")) {
			return new LeafArmor[] { ModItems.SPRUCE_LEAF_BOOTS, ModItems.SPRUCE_LEAF_PANTS, ModItems.SPRUCE_LEAF_ARMOR, ModItems.SPRUCE_LEAF_HELMET };
		} else if (plankType.getName().equals("birch")) {
			return new LeafArmor[] { ModItems.BIRCH_LEAF_BOOTS, ModItems.BIRCH_LEAF_PANTS, ModItems.BIRCH_LEAF_ARMOR, ModItems.BIRCH_LEAF_HELMET };
		} else if (plankType.getName().equals("jungle")) {
			return new LeafArmor[] { ModItems.JUNGLE_LEAF_BOOTS, ModItems.JUNGLE_LEAF_PANTS, ModItems.JUNGLE_LEAF_ARMOR, ModItems.JUNGLE_LEAF_HELMET };
		} else if (plankType.getName().equals("acacia")) {
			return new LeafArmor[] { ModItems.ACACIA_LEAF_BOOTS, ModItems.ACACIA_LEAF_PANTS, ModItems.ACACIA_LEAF_ARMOR, ModItems.ACACIA_LEAF_HELMET };
		} else if (plankType.getName().equals("dark_oak")) {
			return new LeafArmor[] { ModItems.DARK_OAK_LEAF_BOOTS, ModItems.DARK_OAK_LEAF_PANTS, ModItems.DARK_OAK_LEAF_ARMOR, ModItems.DARK_OAK_LEAF_HELMET };
		} else {
			return null;
		}
	}

	public static class LeafArmorHandler {

		public static void invisibility(LivingUpdateEvent event) {
			Entity entity = event.getEntity();
			if (entity instanceof EntityPlayer) {
				final EntityPlayer player = (EntityPlayer) entity;
				if (player.world.getTotalWorldTime() % 20L == 0L) {
					if (wearingFullSet(player)) {
						if (player.world.isRemote) {
							final UUID uuid = player.getPersistentID();
							boolean motion = (player.motionX > 0 || player.motionY > 0 || player.motionZ > 0);
							if (!TICKS_SINCE_MOVE.containsKey(uuid)) {
								TICKS_SINCE_MOVE.put(uuid, (motion) ? 0 : 20);
							} else {
								TICKS_SINCE_MOVE.put(uuid, ((motion) ? 0 : TICKS_SINCE_MOVE.get(uuid) + 20));
							}
							if (TICKS_SINCE_MOVE.get(uuid) >= 100) {
								System.out.println("player.getPersistentId();: " + player.getPersistentID().toString());
								SkrimPacketHandler.INSTANCE.sendToServer(new InvisibilityPacket(20, player.getPersistentID().toString()));
							}
						}
					}
				}
			}
		}

		public static void plantTree(PlayerInteractEvent.RightClickBlock event) {
			EntityPlayer player = event.getEntityPlayer();
			if (wearingFullSet(player)) {
				if (!player.world.isRemote) {
					if (player.getHeldItemMainhand().isEmpty()) {
						BlockPlanks.EnumType plankType = getPlankTypeFullSet(player);
						WorldGenAbstractTree generator = getGenerator(plankType);
						boolean createdTree = generator.generate(player.world, Utils.rand, event.getPos());
						if (createdTree) {
							InventoryPlayer inventory = player.inventory;
							if (inventory != null) {
								for (ItemStack stack : inventory.armorInventory) {
									stack.damageItem(10, player);
								}
							}
						}
					}
				}
			}
		}
	}

}
