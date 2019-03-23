package avi.mod.skrim.items;

import avi.mod.skrim.Skrim;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This is an implementation of the ItemAxe. This class was created due to a bug
 * in ItemAxe that results in an "Array out of Bound" exception in the
 * constructor.
 * <p>
 * Note: You don't need permission from me to use this code. Just use it if your
 * having the same problem I'm having. :)
 *
 * @author derf6060
 */
public class CustomAxe extends ItemTool implements ItemBase {
  // This is a list of
  private static Set<Block> blocks = null;
  // A holder object for the tool material
  private ToolMaterial material = null;
  private String name;

  /**
   * This initializes the ItemAxeCustom object.
   *
   * @param ToolMaterial material
   */
  public CustomAxe(ToolMaterial material) {
    super(material, getEffectedBlocks());
    this.material = material;
    setCreativeTab(Skrim.creativeTab);
  }

  public CustomAxe(String name, ToolMaterial material) {
    this(material);
    this.name = name;
    this.setUnlocalizedName(name);
    this.setRegistryName(name);
  }

  /**
   * This create a list of vanilla blocks that the custom axe can be used on.
   *
   * @return Set<Block>
   */
  private static Set<Block> getEffectedBlocks() {
    // TODO Auto-generated method stub

    if (blocks == null) {
      blocks = new HashSet<Block>();
      // Arcacia
      blocks.add(Blocks.ACACIA_DOOR);
      blocks.add(Blocks.ACACIA_DOOR);
      blocks.add(Blocks.ACACIA_FENCE);
      blocks.add(Blocks.ACACIA_FENCE_GATE);
      blocks.add(Blocks.ACACIA_STAIRS);
      // Birch
      blocks.add(Blocks.BIRCH_DOOR);
      blocks.add(Blocks.BIRCH_FENCE);
      blocks.add(Blocks.BIRCH_FENCE_GATE);
      blocks.add(Blocks.BIRCH_STAIRS);
      // DarkOak
      blocks.add(Blocks.DARK_OAK_DOOR);
      blocks.add(Blocks.DARK_OAK_FENCE);
      blocks.add(Blocks.DARK_OAK_FENCE_GATE);
      blocks.add(Blocks.DARK_OAK_STAIRS);
      // Jungle
      blocks.add(Blocks.JUNGLE_DOOR);
      blocks.add(Blocks.JUNGLE_FENCE);
      blocks.add(Blocks.JUNGLE_FENCE_GATE);
      blocks.add(Blocks.JUNGLE_STAIRS);
      // Oak
      blocks.add(Blocks.OAK_DOOR);
      blocks.add(Blocks.OAK_FENCE);
      blocks.add(Blocks.OAK_FENCE_GATE);
      blocks.add(Blocks.OAK_STAIRS);
      // Spruce
      blocks.add(Blocks.SPRUCE_DOOR);
      blocks.add(Blocks.SPRUCE_FENCE);
      blocks.add(Blocks.SPRUCE_FENCE_GATE);
      blocks.add(Blocks.SPRUCE_STAIRS);
      // Logs
      blocks.add(Blocks.LOG);
      blocks.add(Blocks.LOG2);
      // Leaves
      blocks.add(Blocks.LEAVES);
      blocks.add(Blocks.LEAVES2);
      // Planks
      blocks.add(Blocks.PLANKS);
      // CraftingTable
      blocks.add(Blocks.CRAFTING_TABLE);
      // Pumkin
      blocks.add(Blocks.PUMPKIN);
      // LitPumkin
      blocks.add(Blocks.LIT_PUMPKIN);
      // Vines
      blocks.add(Blocks.VINE);
      // Melon
      blocks.add(Blocks.MELON_BLOCK);
    }
    return blocks;
  }

  /**
   * This check if the block can be mined by the custom axe
   *
   * @param ItemStack   stack
   * @param IBlockState state
   * @return
   */
  protected boolean checkStrVsBlock(ItemStack stack, IBlockState state) {

    boolean b = false;

    // Check Block List that the axe can mine...
    Iterator<Block> it = blocks.iterator();

    while (it.hasNext()) {
      Block block = it.next();

      if (block == state.getBlock()) {
        b = true;
        break;
      }
    }

    // Check Materials
    Material material = state.getMaterial();

    // Added in harvest tool and harvest level
    return b || material == Material.WOOD || material == Material.PLANTS || material == Material.VINE
            || ((state.getBlock().getHarvestTool(state) != null
            && state.getBlock().getHarvestTool(state).equals("axe"))
            && state.getBlock().getHarvestLevel(state) <= this.material.getHarvestLevel());
  }

  // GetDestroySpeed is the new checkStrVsBlock().
  // Also, ItemTool.java now supports passing in a set of blocks a tool is
  // effective against, so the constructor should be updated.

  @Override
  public float getDestroySpeed(ItemStack stack, IBlockState state) {
    for (String type : getToolClasses(stack)) {
      if (state.getBlock().isToolEffective(type, state))
        return efficiency;
    }
    return this.efficiency;
    // return this.effectiveBlocks.contains(state.getBlock()) ? this.efficiency : 1.0F;
  }

  @Override
  public String getTexturePath() {
    return "tools";
  }
}
