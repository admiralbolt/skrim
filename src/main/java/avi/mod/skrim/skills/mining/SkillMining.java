package avi.mod.skrim.skills.mining;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import avi.mod.skrim.Utils;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;
import net.minecraft.block.Block;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockNetherBrick;
import net.minecraft.block.BlockNetherrack;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.block.BlockStoneSlabNew;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.BlockRedSandstone;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SkillMining extends Skill implements ISkillMining {

	public static SkillStorage<ISkillMining> skillStorage = new SkillStorage<ISkillMining>();
	public static Map<String, Integer> xpMap;
	static {
		xpMap = new HashMap<String, Integer>();
		xpMap.put("stone", 1);
		xpMap.put("netherrack", 3); // Extra bonus for being in the nether
		xpMap.put("granite", 5);
		xpMap.put("andesite", 5);
		xpMap.put("diorite", 5);
		xpMap.put("coal_ore", 10);
		xpMap.put("iron_ore", 20);
		xpMap.put("quartz_ore", 25); // Extra bonus for being in the nether ~same rarity as iron
		xpMap.put("redstone_ore", 35);
		xpMap.put("obsidian", 50); // Common but takes a while to mine
		xpMap.put("gold_ore", 100);
		xpMap.put("lapis_lazuli_ore", 150); // Lapis_lazuil not just lapis, also barely rarer than diamond
		xpMap.put("diamond_ore", 250);
		xpMap.put("emerald_ore", 500); // Nice xp bonus for an otherwise useless ore
	}

	public static List<String> validMiningBlocks = new ArrayList<String>(Arrays.asList(
		"cobblestone_stairs",
		"stone_brick_stairs",
		"quartz_stairs",
		"nether_brick_stairs",
		"brick_stairs",
		"sandstone_stairs",
		"red_sandstone_stairs",
		"purpur_block",
		"purpur_pillar",
		"iron_door"
	));

	public static List<String> validFortuneOres = new ArrayList<String>(Arrays.asList(
		"coal_ore",
		"lapis_lazuli_ore",
		"diamond_ore",
		"emerald_ore",
		"redstone_ore",
		"quartz_ore"
	));

	public SkillMining() {
		this(1, 0);
	}

	public SkillMining(int level, int currentXp) {
		super("Mining", level, currentXp);
		this.iconTexture = new ResourceLocation("skrim", "textures/guis/skills/mining.png");
	}

	private int getXp(String blockName) {
		return (xpMap.containsKey(blockName)) ? xpMap.get(blockName) : 0;
	}

	private double getSpeedBonus() {
		return 0.1 * this.level;
	}

	private double getFortuneChance() {
		return 0.003 * this.level;
	}

	private int getFortuneAmount() {
		return (this.level >= 50) ? 3 : 2;
	}

	private String getFortuneString() {
		return (this.level >= 50) ? "triple" : "double";
	}

	@Override
	public List<String> getToolTip() {
		DecimalFormat fmt = new DecimalFormat("0.0");
		List<String> tooltip = new ArrayList<String>();
		tooltip.add("§a+" + fmt.format(this.getSpeedBonus()) + "§r mining speed bonus.");
		tooltip.add("§a" + (this.getFortuneChance() * 100) + "%§r chance to §a" + this.getFortuneString() + "§r ore drops.");
		tooltip.add("   This bonus stacks with fortune.");
		return tooltip;
	}

	private boolean validSpeedTarget(IBlockState state) {
		Block block = state.getBlock();
		String harvestTool = block.getHarvestTool(state);
		return ((harvestTool != null && harvestTool.toLowerCase().equals("pickaxe"))
			|| validMiningBlocks.contains(Utils.getBlockName(block))
			|| block instanceof BlockOre
			|| block instanceof BlockRedstoneOre
			|| block instanceof BlockStone
			|| block instanceof BlockStoneSlab
			|| block instanceof BlockStoneSlabNew
			|| block instanceof BlockObsidian
			|| block instanceof BlockStoneBrick
			|| block instanceof BlockNetherBrick
			|| block instanceof BlockNetherrack
			|| block instanceof BlockSandStone
			|| block instanceof BlockRedSandstone
			) ? true : false;
	}

	private boolean validFortuneTarget(IBlockState state) {
		Block block = state.getBlock();
		String blockName = Utils.snakeCase(block.getLocalizedName());
		return ((block instanceof BlockOre || block instanceof BlockRedstoneOre) && validFortuneOres.contains(blockName));
	}

	@SubscribeEvent
	public void onBlockBreak(BlockEvent.BreakEvent event) {
		EntityPlayer player = event.getPlayer();
		IBlockState state = event.getState();
		Block target = state.getBlock();
		String blockName;
		if (target instanceof BlockStone) {
			blockName = state.getValue(BlockStone.VARIANT).toString();
		} else {
			blockName = Utils.snakeCase(target.getLocalizedName());
		}
		this.xp += this.getXp(blockName);
		this.levelUp();
		// System.out.println("name: " + Utils.snakeCase(target.getLocalizedName()) + ", class: " + target.getClass());
	}

	@SubscribeEvent
	public void breakSpeed(PlayerEvent.BreakSpeed event) {
		IBlockState state = event.getState();
		if (this.validSpeedTarget(state)) {
			EntityPlayer player = event.getEntityPlayer();
			if (player.hasCapability(Skills.MINING, EnumFacing.NORTH)) {
				SkillMining mining = (SkillMining) player.getCapability(Skills.MINING, EnumFacing.NORTH);
				event.setNewSpeed((float) (event.getOriginalSpeed() + this.getSpeedBonus()));
			}
		}
	}

	@SubscribeEvent
	public void onMineOre(BlockEvent.HarvestDropsEvent event) {
		IBlockState state = event.getState();
		if (this.validFortuneTarget(state)) {
			EntityPlayer player = event.getHarvester();
			if (player != null && player.hasCapability(Skills.MINING, EnumFacing.NORTH)) {
				SkillMining mining = (SkillMining) player.getCapability(Skills.MINING, EnumFacing.NORTH);
				double random = Math.random();
				System.out.println("random number: " + random + " chance: " + mining.getFortuneChance());
				if (random < mining.getFortuneChance()) {
					System.out.println("Fortune activated!");
					List<ItemStack> drops = event.getDrops();
					ItemStack copyDrop = drops.get(0);
					// Let's not loop infinitely, that seems like a good idea.
					int dropSize = drops.size();
					for (int i = 0; i < (dropSize * (this.getFortuneAmount() - 1)); i++) {
						drops.add(copyDrop.copy());
					}
				}
			}
		}
	}

}
