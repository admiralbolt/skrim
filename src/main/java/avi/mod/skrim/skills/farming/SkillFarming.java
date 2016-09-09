package avi.mod.skrim.skills.farming;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBeetroot;
import net.minecraft.block.BlockCarrot;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockMelon;
import net.minecraft.block.BlockPotato;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.BlockStem;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import avi.mod.skrim.Utils;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillAbility;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;

public class SkillFarming extends Skill implements ISkillFarming {

	public static SkillStorage<ISkillFarming> skillStorage = new SkillStorage<ISkillFarming>();
	public static Map<String, Integer> xpMap;
	static {
		xpMap = new HashMap<String, Integer>();
		xpMap.put("crops", 20);
		xpMap.put("beetroots", 50);
		xpMap.put("potatoes", 50);
		xpMap.put("carrots", 50);
		xpMap.put("pumpkin", 10);
		xpMap.put("melon", 60);
		xpMap.put("cocoa", 75);
	}

	public SkillFarming() {
		this(1, 0);
	}

	public SkillFarming(int level, int currentXp) {
		super("Farming", level, currentXp);
		this.iconTexture = new ResourceLocation("skrim", "textures/guis/skills/farming.png");
	}

	public int getXp(String blockName) {
		return (xpMap.containsKey(blockName)) ? xpMap.get(blockName) : 0;
	}

	public double getFortuneChance() {
		return 0.01 * this.level;
	}

	public int getFortuneAmount() {
		return 2 + (int) (this.level / 25);
	}

	@Override
	public List<String> getToolTip() {
		DecimalFormat fmt = new DecimalFormat("0.0");
		List<String> tooltip = new ArrayList<String>();
		tooltip.add("§a" + fmt.format(this.getFortuneChance() * 100) + "%§r chance to §a" + Utils.getFortuneString(this.getFortuneAmount()) + "§r harvest drops.");
		tooltip.add("   This bonus stacks with fortune.");
		if (this.getGrowthStage() > 1) {
			tooltip.add("Plants start in stage §a" + this.getGrowthStage() + "§r of growth.");
		}
		return tooltip;
	}

	public boolean validCrop(IBlockState state) {
		Block block = state.getBlock();
		return (block instanceof BlockStem
				|| block instanceof BlockCarrot
				|| block instanceof BlockPotato
				|| block instanceof BlockCrops
				|| block instanceof BlockCocoa
				);
	}

	/**
	 * Need to cap this shit @ 6 to avoid super OPNESS
	 * Still pretty OPOP
	 */
	public int getGrowthStage() {
		int growthStage = (int) Math.floor((double) this.level / 10) + 1;
		return (growthStage > 6) ? 6 : growthStage;
	}

	public boolean validFortuneTarget(IBlockState state) {
		Block block = state.getBlock();
		/**
		 * They decided to make every plants growth go from 0-7 EXCEPT for beets
		 * for some reason they go from 0-3.  I guess you could say it...
		 * Beets me!  AHAHAHAHAHAHAHAHA! (Can't wake up)
		 *
		 * UPDATE: Okay seriously?  Cocoa beans have 3 stages, but instead of
		 * using 0-1-2 like the established standard, they use 2/6/10?
		 * WTF?
		 */
		return (block instanceof BlockMelon
				|| ((block instanceof BlockCarrot || block instanceof BlockPotato)
						&& block.getMetaFromState(state) == 7)
				|| (block instanceof BlockCrops && block.getMetaFromState(state) == 7)
				|| (block instanceof BlockBeetroot && block.getMetaFromState(state) == 3)
				|| (block instanceof BlockCocoa && block.getMetaFromState(state) == 10)
				) ? true : false;
	}

}
