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
import net.minecraft.block.BlockOldLog;
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
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillAbility;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.utils.Utils;

public class SkillFarming extends Skill implements ISkillFarming {

	public static SkillStorage<ISkillFarming> skillStorage = new SkillStorage<ISkillFarming>();
	public static Map<String, Integer> xpMap;
	static {
		xpMap = new HashMap<String, Integer>();
		xpMap.put("crops", 10);
		xpMap.put("beetroots", 25);
		xpMap.put("potatoes", 25);
		xpMap.put("carrots", 25);
		xpMap.put("pumpkin", 5);
		xpMap.put("melon", 30);
		xpMap.put("cocoa", 35);
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
		List<String> tooltip = new ArrayList<String>();
		tooltip.add("§a" + Utils.formatPercent(this.getFortuneChance()) + "%§r chance to §a" + Utils.getFortuneString(this.getFortuneAmount()) + "§r harvest drops.");
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

	public static void addFarmingXp(BlockEvent.BreakEvent event) {
		EntityPlayer player = event.getPlayer();
		if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.FARMING, EnumFacing.NORTH)) {
			SkillFarming farming = (SkillFarming) player.getCapability(Skills.FARMING, EnumFacing.NORTH);
			IBlockState state = event.getState();
			Block target = state.getBlock();
			// Don't want to always give xp, only for fully grown stuff.
			if (farming.validFortuneTarget(state) || target instanceof BlockPumpkin) {
				int addXp = farming.getXp(Utils.getBlockName(target));
				farming.addXp((EntityPlayerMP) player, addXp);
			}
		}
	}

	public static void giveMoreCrops(BlockEvent.HarvestDropsEvent event) {
		EntityPlayer player = event.getHarvester();
		if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.FARMING, EnumFacing.NORTH)) {
			SkillFarming farming = (SkillFarming) player.getCapability(Skills.FARMING, EnumFacing.NORTH);
			IBlockState state = event.getState();
			if (farming.validFortuneTarget(state)) {
				Block block = state.getBlock();
				double random = Math.random();
				if (random < farming.getFortuneChance()) {
					List<ItemStack> drops = event.getDrops();
					// Let's not loop infinitely, that seems like a good idea.
					int dropSize = drops.size();
          for (int j = 0; j < farming.getFortuneAmount() - 1; j++) {
            for (int i = 0; i < dropSize; i++) {
              drops.add(drops.get(i).copy());
            }
          }
          farming.addXp((EntityPlayerMP) player, 25);
				}
			}
		}
	}

  public static void applyGrowth(BlockEvent.PlaceEvent event) {
  	EntityPlayer player = event.getPlayer();
		if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.FARMING, EnumFacing.NORTH)) {
			SkillFarming farming = (SkillFarming) player.getCapability(Skills.FARMING, EnumFacing.NORTH);
	  	IBlockState placedState = event.getPlacedBlock();
	  	IBlockState targetState = event.getPlacedAgainst();
	  	Block placedBlock = placedState.getBlock();
	  	Block targetBlock = targetState.getBlock();
	  	if (farming.validCrop(placedState) && (targetBlock instanceof BlockFarmland || targetBlock instanceof BlockOldLog)) {
	  		World world = event.getWorld();
	  		PropertyInteger prop = null;
	  		int growthStage = farming.getGrowthStage();
	  		if (placedBlock instanceof BlockStem) {
	  			prop = BlockStem.AGE;
	  		} else if (placedBlock instanceof BlockBeetroot) {
	  			prop = BlockBeetroot.BEETROOT_AGE;
	  			if (growthStage > 2) {
	  				growthStage = 2;
	  			}
				} else if (placedBlock instanceof BlockCocoa) {
					// Because fuck it.
					int[] cocoaStages = {2, 2, 2, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6};
					growthStage = cocoaStages[growthStage];
	  		} else if (placedBlock instanceof BlockCrops) {
	  			prop = BlockCrops.AGE;
	  		}
	  		world.setBlockState(event.getPos(), placedState.withProperty(prop, growthStage));
	  	}
		}
  }

}
