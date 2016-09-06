package avi.mod.skrim.handlers.skills;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBeetroot;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.BlockStem;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import avi.mod.skrim.Utils;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.farming.SkillFarming;

public class FarmingHandler {

  @SubscribeEvent
	public void onBlockBreak(BlockEvent.BreakEvent event) {
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

	@SubscribeEvent
	public void onHarvestPlant(BlockEvent.HarvestDropsEvent event) {
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
          farming.addXp((EntityPlayerMP) player, 100);
				}
			}
		}
	}

  @SubscribeEvent
  public void onSeedPlanted(BlockEvent.PlaceEvent event) {
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
