package avi.mod.skrim.skills.woodcutting;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockHalfWoodSlab;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockWoodSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import avi.mod.skrim.items.CustomAxe;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.utils.Utils;

public class SkillWoodcutting extends Skill implements ISkillWoodcutting {

	public static SkillStorage<ISkillWoodcutting> skillStorage = new SkillStorage<ISkillWoodcutting>();
	public static Map<String, Integer> xpMap;
	static {
		xpMap = new HashMap<String, Integer>();
		xpMap.put("oak", 5);
		xpMap.put("spruce", 10);
		xpMap.put("birch", 15);
		// The last 3 occur only in 1 biome.
		xpMap.put("jungle", 25);
		xpMap.put("acacia", 25);
		xpMap.put("dark_oak", 25);
	}

	public static List<String> validWoodcuttingBlocks = new ArrayList<String>(Arrays.asList(
			"oak_door",
			"spruce_door",
			"birch_door",
			"jungle_door",
			"dark_oak_door",
			"acacia_door",
			"wooden_trapdoor",
			"wooden_pressure_plate",
			"oak_wood_stairs",
			"spruce_wood_stairs",
			"birch_wood_stairs",
			"jungle_wood_stairs",
			"dark_oak_wood_stairs",
			"acacia_wood_stairs",
			"crafting_table",
			"sign"
	));

	public SkillWoodcutting() {
		this(1, 0);
	}

	public SkillWoodcutting(int level, int currentXp) {
		super("Woodcutting", level, currentXp);
		this.iconTexture = new ResourceLocation("skrim", "textures/guis/skills/woodcutting.png");
	}

	public int getXp(String blockName) {
		return (xpMap.containsKey(blockName)) ? xpMap.get(blockName) : 0;
	}

	public double getSpeedBonus() {
		return 0.15 * this.level;
	}

	public double getHewingChance() {
		return 0.01 * this.level;
	}

	@Override
	public List<String> getToolTip() {
		List<String> tooltip = new ArrayList<String>();
		tooltip.add("§a+" + Utils.oneDigit.format(this.getSpeedBonus()) + "§r woodcutting speed bonus.");
		tooltip.add("§a" + Utils.formatPercent(this.getHewingChance()) + "%§r chance to level a tree.");
		return tooltip;
	}

	public boolean validSpeedTarget(IBlockState state) {
		Block block = state.getBlock();
		String harvestTool = block.getHarvestTool(state);
		return ((harvestTool != null && harvestTool.toLowerCase().equals("axe"))
			|| validWoodcuttingBlocks.contains(Utils.getBlockName(block))
			|| block instanceof BlockFence
			|| block instanceof BlockFenceGate
			|| block instanceof BlockWoodSlab
			|| block instanceof BlockHalfWoodSlab
			) ? true : false;
	}

	// Assuming its a wood block
	public String getWoodName(IBlockState state) {
		Block block = state.getBlock();
		if (block instanceof BlockOldLog) {
			return Utils.snakeCase(state.getValue(BlockOldLog.VARIANT).toString());
		} else if (block instanceof BlockNewLog) {
			return Utils.snakeCase(state.getValue(BlockNewLog.VARIANT).toString());
		} else {
			return null;
		}
	}

	public void hewTree(World world, SkillWoodcutting woodcutting, BlockPos pos, BlockPos start) {
		IBlockState state = world.getBlockState(pos);
		Block tree = state.getBlock();
		woodcutting.xp += this.getXp(this.getWoodName(state));
		world.destroyBlock(pos, true);
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					BlockPos targetPos = pos.add(i, j, k);
					if (validTarget(targetPos, start)) {
						IBlockState targetState = world.getBlockState(targetPos);
						Block targetBlock = targetState.getBlock();
						if (targetBlock instanceof BlockOldLog || targetBlock instanceof BlockNewLog) {
							this.hewTree(world, woodcutting, targetPos, start);
						}
					}
				}
			}
		}
	}

	public static boolean validTarget(BlockPos targetPos, BlockPos start) {
		int radius = 2;
		return (Math.abs(targetPos.getX() - start.getX()) <= radius
				&& Math.abs(targetPos.getZ() - start.getZ()) <= radius) ?
						true: false;
	}

	public static void addWoodcuttingXp(BlockEvent.BreakEvent event) {
		IBlockState state = event.getState();
		Block target = state.getBlock();
		if (target instanceof BlockOldLog || target instanceof BlockNewLog) {
			EntityPlayer player = event.getPlayer();
			if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.WOODCUTTING, EnumFacing.NORTH)) {
				SkillWoodcutting woodcutting = (SkillWoodcutting) player.getCapability(Skills.WOODCUTTING, EnumFacing.NORTH);
				ItemStack stack = player.getHeldItemMainhand();
				Item item = (stack == null) ? null : stack.getItem();
				if (Math.random() < woodcutting.getHewingChance() && item != null && (item instanceof ItemAxe || item instanceof CustomAxe)) {
					BlockPos start = event.getPos();
					woodcutting.hewTree(event.getWorld(), woodcutting, start, start);
				}
				woodcutting.addXp((EntityPlayerMP) player, woodcutting.getXp(woodcutting.getWoodName(state)));
			}
		}
	}

	public static void chopFaster(PlayerEvent.BreakSpeed event) {
  	EntityPlayer player = event.getEntityPlayer();
		if (player.hasCapability(Skills.WOODCUTTING, EnumFacing.NORTH)) {
			SkillWoodcutting woodcutting = (SkillWoodcutting) player.getCapability(Skills.WOODCUTTING, EnumFacing.NORTH);
			IBlockState state = event.getState();
			if (woodcutting.validSpeedTarget(state)) {
				event.setNewSpeed((float) (event.getOriginalSpeed() + woodcutting.getSpeedBonus()));
			}
		}
	}

}
