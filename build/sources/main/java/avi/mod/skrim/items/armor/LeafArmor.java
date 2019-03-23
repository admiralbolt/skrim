package avi.mod.skrim.items.armor;

import avi.mod.skrim.items.CustomArmor;
import avi.mod.skrim.network.InvisibilityPacket;
import avi.mod.skrim.network.SkrimPacketHandler;
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
import net.minecraft.world.gen.feature.*;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.*;

public class LeafArmor extends CustomArmor {

  private static final IBlockState JUNGLE_LOG = Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT,
      BlockPlanks.EnumType.JUNGLE);
  private static final IBlockState JUNGLE_LEAF = Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT,
      BlockPlanks.EnumType.JUNGLE)
      .withProperty(BlockLeaves.CHECK_DECAY, false);

  private static ArmorMaterial OAK_LEAF_MATERIAL = EnumHelper.addArmorMaterial("oak_leaf_armor", "skrim:oak_leaf_armor"
      , 20, new int[]{2, 4, 3, 2}, 30,
      null, 0.0F);
  private static ArmorMaterial SPRUCE_LEAF_MATERIAL = EnumHelper.addArmorMaterial("spruce_leaf_armor", "skrim" +
          ":spruce_leaf_armor", 20, new int[]{2, 4, 3, 2},
      30, null, 0.0F);
  private static ArmorMaterial BIRCH_LEAF_MATERIAL = EnumHelper.addArmorMaterial("birch_leaf_armor", "skrim" +
          ":birch_leaf_armor", 20, new int[]{2, 4, 3, 2},
      30, null, 0.0F);
  private static ArmorMaterial JUNGLE_LEAF_MATERIAL = EnumHelper.addArmorMaterial("jungle_leaf_armor", "skrim" +
          ":jungle_leaf_armor", 20, new int[]{2, 4, 3, 2},
      30, null, 0.0F);
  private static ArmorMaterial ACACIA_LEAF_MATERIAL = EnumHelper.addArmorMaterial("acacia_leaf_armor", "skrim" +
          ":acacia_leaf_armor", 20, new int[]{2, 4, 3, 2},
      30, null, 0.0F);
  private static ArmorMaterial DARK_OAK_LEAF_MATERIAL = EnumHelper.addArmorMaterial("dark_oak_leaf_armor", "skrim" +
          ":dark_oak_leaf_armor", 20,
      new int[]{2, 4, 3, 2}, 30, null, 0.0F);

  private BlockPlanks.EnumType plankType;

  public LeafArmor(BlockPlanks.EnumType plankType, String name, int renderIndex, EntityEquipmentSlot armorType) {
    super(plankType.getName() + "_" + name, getMaterial(plankType), renderIndex, armorType);
    this.plankType = plankType;
  }

  /**
   * Maps the plank enum -> material. All the materials defined above are identical in stats, but differ based on name
   * to have a slightly altered appearance.
   *
   * @param plankType The plank type of the tree where the leaf armor came from.
   * @return The correct corresponding armor material.
   */
  private static ArmorMaterial getMaterial(BlockPlanks.EnumType plankType) {
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

  /**
   * Maps the plank enum -> tree generator. The generator can be used to create a full tree.
   *
   * @param plankType The plank type of the tree to create.
   * @return A tree generator!
   */
  private static WorldGenAbstractTree getGenerator(BlockPlanks.EnumType plankType) {
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

  /**
   * Gets the plank type of the first piece of armor a player is wearing. Should only be called once you're sure a
   * full set is being worn.
   *
   * @param player The player to check.
   * @return The plank type of the first piece of armor a player is wearing.
   */
  private static BlockPlanks.EnumType getPlankTypeOfArmor(EntityPlayer player) {
    return ((LeafArmor) player.inventory.armorInventory.get(0).getItem()).getPlankType();
  }

  /**
   * Checks if a player is wearing a full set of LeafArmor. Does not check what the type is, only ensures that there is
   * a consistent type across pieces of armor.
   *
   * @param player The player to check.
   * @return Whether or not they are wearing a full set of LeafArmor.
   */
  private static boolean wearingFullSet(EntityPlayer player) {
    Set<BlockPlanks.EnumType> plankTypes = new HashSet<>();
    for (ItemStack armorStack : player.inventory.armorInventory) {
      Item armor = armorStack.getItem();
      if (!(armor instanceof LeafArmor)) {
        return false;
      }
      plankTypes.add(((LeafArmor) armor).getPlankType());
    }
    return plankTypes.size() == 1;
  }

  /**
   * @return The plankType of this piece of armor.
   */
  private BlockPlanks.EnumType getPlankType() {
    return this.plankType;
  }

  /**
   * Helper subclass for handling special events, specifically turning invisible and generating trees. Both of these
   * require that a full set of armor is worn.
   */
  public static class LeafArmorHandler {

    /**
     * Map for tracking player UUID with how long it's been since they've moved.
     */
    private static Map<UUID, Integer> TICKS_SINCE_MOVE = new HashMap<>();

    /**
     * Turns a player invisible if they are wearing a full set of leaf armor and are immobile for 100 ticks.
     *
     * @param event LivingUpdateEvent, these fire constantly for all living events.
     */
    public static void invisibility(LivingUpdateEvent event) {
      Entity entity = event.getEntity();
      if (!(entity instanceof EntityPlayer)) return;

      final EntityPlayer player = (EntityPlayer) entity;
      if (player.world.getTotalWorldTime() % 20L != 0L || !wearingFullSet(player)) return;

      final UUID uuid = player.getPersistentID();
      boolean motion = (player.motionX > 0 || player.motionY > 0 || player.motionZ > 0);
      if (!TICKS_SINCE_MOVE.containsKey(uuid)) {
        TICKS_SINCE_MOVE.put(uuid, (motion) ? 0 : 20);
      } else {
        TICKS_SINCE_MOVE.put(uuid, ((motion) ? 0 : TICKS_SINCE_MOVE.get(uuid) + 20));
      }
      if (TICKS_SINCE_MOVE.get(uuid) < 100) {
        return;
      }

      SkrimPacketHandler.INSTANCE.sendToServer(new InvisibilityPacket(25,
          player.getPersistentID().toString()));
    }

    /**
     * Makes a tree if the player right clicks while wearing a full set of leaf armor AND has nothing in their hand.
     *
     * @param event PlayerInteractEvent.RightClickBlock,fired when a block is right clicked.
     */
    public static void plantTree(PlayerInteractEvent.RightClickBlock event) {
      EntityPlayer player = event.getEntityPlayer();
      if (player.world.isRemote || !wearingFullSet(player) || !player.getHeldItemMainhand().isEmpty()) return;

      BlockPlanks.EnumType plankType = getPlankTypeOfArmor(player);
      WorldGenAbstractTree generator = getGenerator(plankType);
      if (!generator.generate(player.world, Utils.rand, event.getPos())) {
        return;
      }
      InventoryPlayer inventory = player.inventory;
      if (inventory != null) {
        for (ItemStack stack : inventory.armorInventory) {
          stack.damageItem(10, player);
        }
      }
    }
  }

}
