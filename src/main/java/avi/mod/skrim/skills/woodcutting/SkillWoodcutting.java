package avi.mod.skrim.skills.woodcutting;

import avi.mod.skrim.blocks.SkrimBlocks;
import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.items.armor.LeafArmor;
import avi.mod.skrim.items.items.WeirwoodTotem;
import avi.mod.skrim.items.tools.HandSaw;
import avi.mod.skrim.network.SkrimPacketHandler;
import avi.mod.skrim.network.skillpackets.WhirlingChopPacket;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillAbility;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.utils.Utils;
import avi.mod.skrim.world.PlayerPlacedBlocks;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SkillWoodcutting extends Skill implements ISkillWoodcutting {

  public static SkillStorage<ISkillWoodcutting> skillStorage = new SkillStorage<>();
  /**
   * maps wood meta values to plank meta values.
   */
  private static Map<String, Integer> PLANK_MAP = ImmutableMap.<String, Integer>builder()
      .put("oak", 0)
      .put("spruce", 1)
      .put("birch", 2)
      .put("jungle", 3)
      .put("acacia", 4)
      .put("dark_oak", 5)
      .build();


  private static Map<String, Integer> XP_MAP = ImmutableMap.<String, Integer>builder()
      .put("oak", 125)
      .put("spruce", 175)
      .put("birch", 200)
      .put("jungle", 225)
      .put("dark_oak", 250)
      .put("acacia", 300)
      .build();

  private static List<String> validWoodcuttingBlocks = new ArrayList<>(Arrays.asList("oak_door", "spruce_door", "birch_door",
      "jungle_door",
      "dark_oak_door", "acacia_door", "wooden_trapdoor", "wooden_pressure_plate", "oak_wood_stairs", "spruce_wood_stairs",
      "birch_wood_stairs",
      "jungle_wood_stairs", "dark_oak_wood_stairs", "acacia_wood_stairs", "crafting_table", "sign"));

  private static SkillAbility HAND_SAW = new SkillAbility("woodcutting", "Hand Saw", 25, "Wee Saw!", "Allows you to craft a hand saw!",
      "Hand saws instantly convert broken wood logs into 8 planks.");

  private static SkillAbility WHIRLING_CHOP = new SkillAbility("woodcutting", "Whirling Chop", 50, "My roflchopter go soi soi soi soi soi.",
      "Right click with an axe to massacre trees in a 10 block radius.");

  private static SkillAbility LEAF_ARMOR = new SkillAbility("woodcutting", "Leaf Armor", 75, "Tree!", "Grants you the ability to craft " +
      "armor out of leaves.");

  private static SkillAbility WEIRWOOD = new SkillAbility("woodcutting", "Weirwood", 100, "Not 'weirdwood'.",
      "Grants you the ability to craft weirwood saplings and totems.");

  public SkillWoodcutting() {
    this(1, 0);
  }

  public SkillWoodcutting(int level, int currentXp) {
    super("Woodcutting", level, currentXp);
    this.addAbilities(HAND_SAW, WHIRLING_CHOP, LEAF_ARMOR, WEIRWOOD);
  }

  @Override
  public List<String> getToolTip() {
    List<String> tooltip = new ArrayList<>();
    tooltip.add("§a+" + Utils.oneDigit.format(this.getSpeedBonus()) + "§r woodcutting speed bonus.");
    tooltip.add("§a" + Utils.formatPercent(this.getHewingChance()) + "%§r chance to level a tree.");
    return tooltip;
  }

  public int getXp(String blockName) {
    return XP_MAP.getOrDefault(blockName, 0);
  }

  private double getSpeedBonus() {
    return 0.15 * this.level;
  }

  private double getHewingChance() {
    return 0.003 * this.level;
  }

  private static boolean validSpeedTarget(IBlockState state) {
    Block block = state.getBlock();
    String harvestTool = block.getHarvestTool(state);
    return (harvestTool != null && harvestTool.toLowerCase().equals("axe")) || validWoodcuttingBlocks.contains(Utils.getBlockName(block))
        || block instanceof BlockFence || block instanceof BlockFenceGate || block instanceof BlockWoodSlab;
  }

  // Assuming its a wood block
  private static String getWoodName(IBlockState state) {
    Block block = state.getBlock();
    if (block instanceof BlockOldLog) {
      return Utils.snakeCase(state.getValue(BlockOldLog.VARIANT).toString());
    } else if (block instanceof BlockNewLog) {
      return Utils.snakeCase(state.getValue(BlockNewLog.VARIANT).toString());
    } else {
      return null;
    }
  }


  public int hewTree(World world, BlockPos pos, EntityPlayer player, ItemStack axe,
                     boolean withSaw,
                     int damagePerBreak) {
    if (!PlayerPlacedBlocks.isNaturalBlock(world, pos)) return 0;

    int addXp = 0;
    IBlockState state = world.getBlockState(pos);
    addXp += this.getXp(getWoodName(state));

    if (withSaw) {
      world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Blocks.PLANKS, 8,
          PLANK_MAP.get(getWoodName(state)))));
      world.destroyBlock(pos, false);
    } else {
      world.destroyBlock(pos, true);
    }
    axe.damageItem(damagePerBreak, player);
    for (int i = -1; i <= 1; i++) {
      for (int j = -1; j <= 1; j++) {
        for (int k = -1; k <= 1; k++) {
          BlockPos targetPos = pos.add(i, j, k);
          IBlockState targetState = world.getBlockState(targetPos);
          Block targetBlock = targetState.getBlock();
          if (targetBlock instanceof BlockLog) {
            addXp += this.hewTree(world, targetPos, player, axe, withSaw, damagePerBreak);
          }
        }
      }
    }
    return addXp;
  }

  public static void addWoodcuttingXp(BlockEvent.BreakEvent event) {
    IBlockState state = event.getState();
    Block target = state.getBlock();
    if (!(target instanceof BlockLog)) return;

    EntityPlayer player = event.getPlayer();
    if (player.world.isRemote) return;

    SkillWoodcutting woodcutting = Skills.getSkill(player, Skills.WOODCUTTING, SkillWoodcutting.class);
    ItemStack stack = player.getHeldItemMainhand();
    Item item = stack.getItem();
    int addXp = woodcutting.getXp(getWoodName(state));
    if (Math.random() < woodcutting.getHewingChance() && item instanceof ItemAxe) {
      BlockPos start = event.getPos();
      addXp += woodcutting.hewTree(event.getWorld(), start, player, stack, (item instanceof HandSaw), 1);
    }
    woodcutting.addXp((EntityPlayerMP) player, addXp);
  }


  public static void chopFaster(PlayerEvent.BreakSpeed event) {
    if (!validSpeedTarget(event.getState())) return;

    SkillWoodcutting woodcutting = Skills.getSkill(event.getEntityPlayer(), Skills.WOODCUTTING, SkillWoodcutting.class);
    event.setNewSpeed((float) (event.getOriginalSpeed() + woodcutting.getSpeedBonus()));
  }

  public static void verifyItems(ItemCraftedEvent event) {
    Item targetItem = event.crafting.getItem();
    Item weirwoodSapling = new ItemStack(SkrimBlocks.WEIRWOOD_SAPLING).getItem();

    if (targetItem == SkrimItems.HAND_SAW) {
      if (!Skills.canCraft(event.player, Skills.WOODCUTTING, 25)) {
        Skills.replaceWithComponents(event);
      }
    } else if (targetItem instanceof LeafArmor) {
      if (!Skills.canCraft(event.player, Skills.WOODCUTTING, 75)) {
        Skills.replaceWithComponents(event);
      }
    } else if (targetItem instanceof WeirwoodTotem || targetItem == weirwoodSapling) {
      if (!Skills.canCraft(event.player, Skills.WOODCUTTING, 100)) {
        Skills.replaceWithComponents(event);
      }
    }
  }

  public static void sawTree(BlockEvent.HarvestDropsEvent event) {
    EntityPlayer player = event.getHarvester();
    if (player == null) return;
    if (!(player.getHeldItemMainhand().getItem() instanceof HandSaw)) return;

    IBlockState state = event.getState();
    Block block = state.getBlock();
    if (!(block instanceof BlockLog)) return;

    event.getDrops().clear();
    event.getDrops().add(new ItemStack(Blocks.PLANKS, 8, PLANK_MAP.get(getWoodName(state))));
  }

  public static void whirlingChop(PlayerInteractEvent.RightClickItem event) {
    EntityPlayer player = event.getEntityPlayer();
    if (!player.world.isRemote) return;

    SkillWoodcutting woodcutting = Skills.getSkill(player, Skills.WOODCUTTING, SkillWoodcutting.class);
    if (!woodcutting.hasAbility(2)) return;

    if (!(player.getHeldItemMainhand().getItem() instanceof ItemAxe)) return;
    player.swingArm(EnumHand.MAIN_HAND);

    // Send packet here
    BlockPos playerPos = new BlockPos(player.posX, player.posY, player.posZ);
    for (int i = -10; i <= 10; i++) {
      for (int j = 0; j <= 2; j++) {
        for (int k = -10; k <= 10; k++) {
          BlockPos targetPos = new BlockPos(playerPos.getX() + i, playerPos.getY() + j, playerPos.getZ() + k);
          if (!(player.world.getBlockState(targetPos).getBlock() instanceof BlockLog)) continue;
          SkrimPacketHandler.INSTANCE
              .sendToServer(new WhirlingChopPacket(targetPos.getX(), targetPos.getY(), targetPos.getZ()));
        }
      }
    }
  }

}
