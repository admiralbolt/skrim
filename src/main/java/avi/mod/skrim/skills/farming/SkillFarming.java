package avi.mod.skrim.skills.farming;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBeetroot;
import net.minecraft.block.BlockCarrot;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockMelon;
import net.minecraft.block.BlockPotato;
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
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;

public class SkillFarming extends Skill implements ISkillFarming {

	public static SkillStorage<ISkillFarming> skillStorage = new SkillStorage<ISkillFarming>();
	public static Map<String, Integer> xpMap;
	static {
		xpMap = new HashMap<String, Integer>();
		xpMap.put("crops", 20); // Wheat is worth 20, 50 for all else.
		xpMap.put("beetroots", 50);
		xpMap.put("potatoes", 50);
		xpMap.put("carrots", 50);
		xpMap.put("pumpkin", 50);
		xpMap.put("melon", 50);
	}

	public SkillFarming() {
		this(1, 0);
	}

	public SkillFarming(int level, int currentXp) {
		super("Farming", level, currentXp);
		this.iconTexture = new ResourceLocation("skrim", "textures/guis/skills/farming.png");
	}

	private int getXp(String blockName) {
		return (xpMap.containsKey(blockName)) ? xpMap.get(blockName) : 0;
	}

	private double getFortuneChance() {
		return 0.01 * this.level;
	}

	private int getFortuneAmount() {
		if (this.level >= 75) {
			return 5;
		} else if (this.level >= 50) {
			return 4;
		} else if (this.level >= 25) {
			return 3;
		} else {
			return 2;
		}
	}

	@Override
	public List<String> getToolTip() {
		DecimalFormat fmt = new DecimalFormat("0.0");
		List<String> tooltip = new ArrayList<String>();
		tooltip.add("§a" + (this.getFortuneChance() * 100) + "%§r chance to §a" + Utils.getFortuneString(this.getFortuneAmount()) + "§r harvest drops.");
		tooltip.add("   This bonus stacks with fortune.");
		if (this.getGrowthStage() > 0) {
			tooltip.add("Plants start in stage §a" + this.getGrowthStage() + "§r of growth.");
		}
		return tooltip;
	}

	private boolean validCrop(IBlockState state) {
		Block block = state.getBlock();
		return (block instanceof BlockStem
				|| block instanceof BlockCarrot
				|| block instanceof BlockPotato
				|| block instanceof BlockCrops
				);
	}

	/**
	 * Need to cap this shit @ 6 to avoid super OPNESS
	 * Still pretty OPOP
	 */
	private int getGrowthStage() {
		int growthStage = (int) Math.floor((double) this.level / 10);
		return (growthStage > 6) ? 6 : growthStage;
	}

	private boolean validFortuneTarget(IBlockState state) {
		Block block = state.getBlock();
		/**
		 * They decided to make every plants growth go from 0-7 EXCEPT for beets
		 * for some reason they go from 0-3.  I guess you could say it...
		 * Beets me!  AHAHAHAHAHAHAHAHA! (Can't wake up)
		 */
		return (block instanceof BlockMelon
				|| ((block instanceof BlockCarrot || block instanceof BlockPotato)
						&& block.getMetaFromState(state) == 7)
				|| (block instanceof BlockCrops && block.getMetaFromState(state) == 7)
				|| (block instanceof BlockBeetroot && block.getMetaFromState(state) == 3)
				) ? true : false;
	}

	@SubscribeEvent
	public void onBlockBreak(BlockEvent.BreakEvent event) {
		EntityPlayer player = event.getPlayer();
		if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.FARMING, EnumFacing.NORTH)) {
			SkillFarming farming = (SkillFarming) player.getCapability(Skills.FARMING, EnumFacing.NORTH);
			IBlockState state = event.getState();
			Block target = state.getBlock();
			int addXp = this.getXp(Utils.getBlockName(target));
			if (addXp > 0) {
				farming.xp += this.getXp(Utils.getBlockName(target));
				farming.levelUp((EntityPlayerMP) player);
			}
		}
	}

	@SubscribeEvent
	public void onHarvestPlant(BlockEvent.HarvestDropsEvent event) {
		IBlockState state = event.getState();
		if (this.validFortuneTarget(state)) {
			Block block = state.getBlock();
			EntityPlayer player = event.getHarvester();
			if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.FARMING, EnumFacing.NORTH)) {
				SkillFarming farming = (SkillFarming) player.getCapability(Skills.FARMING, EnumFacing.NORTH);
				double random = Math.random();
				if (random < farming.getFortuneChance()) {
					List<ItemStack> drops = event.getDrops();
					// Let's not loop infinitely, that seems like a good idea.
					int dropSize = drops.size();
          for (int j = 0; j < this.getFortuneAmount() - 1; j++) {
            for (int i = 0; i < dropSize; i++) {
              drops.add(drops.get(i).copy());
            }
          }
          farming.xp += 100; // And 100 xp!
          farming.levelUp((EntityPlayerMP) player);
				}
			}
		}
	}

  @SubscribeEvent
  public void onSeedPlanted(BlockEvent.PlaceEvent event) {
  	IBlockState placedState = event.getPlacedBlock();
  	IBlockState targetState = event.getPlacedAgainst();
  	Block placedBlock = placedState.getBlock();
  	Block targetBlock = targetState.getBlock();
  	if (this.validCrop(placedState) && targetBlock instanceof BlockFarmland) {
  		World world = event.getWorld();
  		PropertyInteger prop = null;
  		int growthStage = this.getGrowthStage();
  		if (placedBlock instanceof BlockStem) {
  			prop = BlockStem.AGE;
  		} else if (placedBlock instanceof BlockBeetroot) {
  			prop = BlockBeetroot.BEETROOT_AGE;
  			if (growthStage > 2) {
  				growthStage = 2;
  			}
  		} else if (placedBlock instanceof BlockCrops) {
  			prop = BlockCrops.AGE;
  		}
  		world.setBlockState(event.getPos(), placedState.withProperty(prop, growthStage));
  	}
  }

}
