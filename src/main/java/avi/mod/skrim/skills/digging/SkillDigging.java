package avi.mod.skrim.skills.digging;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockGravel;
import net.minecraft.block.BlockMycelium;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import avi.mod.skrim.Utils;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillStorage;

public class SkillDigging extends Skill implements ISkillDigging {

	public static SkillStorage<ISkillDigging> skillStorage = new SkillStorage<ISkillDigging>();
	public static Map<String, Integer> xpMap;
	static {
		xpMap = new HashMap<String, Integer>();
		xpMap.put("dirt", 2);
		xpMap.put("sand", 3);
		xpMap.put("grass_block", 5); // Bonus for grass!
		xpMap.put("gravel", 10); // Fuck gravel
		xpMap.put("coarse_dirt", 7); // Requires 2 dirt & 2 gravel to make 4, is worth slightly more than the components
		xpMap.put("podzol", 25); // Only in taiga
		xpMap.put("red_sand", 35); // Only in mesa
		xpMap.put("soul_sand", 55); // Only in nether & not to common
		xpMap.put("mycelium", 100); // Only in.. mushroom biomes?
	}

	public SkillDigging() {
		this(1, 0);
	}

	public SkillDigging(int level, int currentXp) {
		super("Digging", level, currentXp);
		this.iconTexture = new ResourceLocation("skrim", "textures/guis/skills/digging.png");
	}

	public int getXp(String blockName) {
		return (xpMap.containsKey(blockName)) ? xpMap.get(blockName) : 0;
	}

	public double getSpeedBonus() {
		return 0.1 * this.level;
	}

	public double getTreasureChance() {
		return 0.002 * this.level;
	}

	@Override
	public List<String> getToolTip() {
		DecimalFormat fmt = new DecimalFormat("0.0");
		DecimalFormat three_dec = new DecimalFormat("0.00");
		List<String> tooltip = new ArrayList<String>();
		tooltip.add("§a+" + fmt.format(this.getSpeedBonus()) + "§r digging speed bonus.");
		tooltip.add("§a" + three_dec.format(this.getTreasureChance() * 100) + "%§r chance to find treasure.");
		return tooltip;
	}

	public boolean validSpeedTarget(IBlockState state) {
		Block block = state.getBlock();
		String harvestTool = block.getHarvestTool(state);
		return ((harvestTool != null && harvestTool.toLowerCase().equals("shovel"))
			|| this.validTreasureTarget(state)
			) ? true : false;
	}

	public boolean validTreasureTarget(IBlockState state) {
		Block block = state.getBlock();
		return (block instanceof BlockDirt
			|| block instanceof BlockGrass
			|| block instanceof BlockSand
			|| block instanceof BlockGravel
			|| block instanceof BlockMycelium
			|| block instanceof BlockSoulSand);
	}

  public String getDirtName(IBlockState state) {
    Block block = state.getBlock();
    if (block instanceof BlockDirt) {
    	return state.getValue(BlockDirt.VARIANT).toString();
    } else if (block instanceof BlockSand) {
    	return state.getValue(BlockSand.VARIANT).toString();
    } else {
      return Utils.getBlockName(block);
    }
  }

}
