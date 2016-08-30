package avi.mod.skrim.handlers.skills;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import avi.mod.skrim.Utils;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.mining.SkillMining;

public class MiningHandler {

  @SubscribeEvent
	public void onBlockBreak(BlockEvent.BreakEvent event) {
		EntityPlayer player = event.getPlayer();
		if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.MINING, EnumFacing.NORTH)) {
			IBlockState state = event.getState();
			Block target = state.getBlock();
			String blockName;
			if (target instanceof BlockStone) {
				blockName = state.getValue(BlockStone.VARIANT).toString();
			} else {
				blockName = Utils.snakeCase(target.getLocalizedName());
			}
			SkillMining mining = (SkillMining) player.getCapability(Skills.MINING, EnumFacing.NORTH);
			int addXp = mining.getXp(blockName);
			if (addXp > 0) {
				mining.xp += mining.getXp(blockName);
				mining.levelUp((EntityPlayerMP) player);
			}
		}
	}

	@SubscribeEvent
	public void breakSpeed(PlayerEvent.BreakSpeed event) {
		EntityPlayer player = event.getEntityPlayer();
		if (player.hasCapability(Skills.MINING, EnumFacing.NORTH)) {
			SkillMining mining = (SkillMining) player.getCapability(Skills.MINING, EnumFacing.NORTH);
			IBlockState state = event.getState();
			if (mining.validSpeedTarget(state)) {
				event.setNewSpeed((float) (event.getOriginalSpeed() + mining.getSpeedBonus()));
			}
		}
	}

	@SubscribeEvent
	public void onMineOre(BlockEvent.HarvestDropsEvent event) {
		EntityPlayer player = event.getHarvester();
		if (player != null && player.hasCapability(Skills.MINING, EnumFacing.NORTH)) {
			SkillMining mining = (SkillMining) player.getCapability(Skills.MINING, EnumFacing.NORTH);
			IBlockState state = event.getState();
			if (mining.validFortuneTarget(state)) {
				double random = Math.random();
				if (random < mining.getFortuneChance()) {
					List<ItemStack> drops = event.getDrops();
					ItemStack copyDrop = drops.get(0);
					// Let's not loop infinitely, that seems like a good idea.
					int dropSize = drops.size();
					for (int i = 0; i < (dropSize * (mining.getFortuneAmount() - 1)); i++) {
						drops.add(copyDrop.copy());
					}
				}
			}
		}
	}

}
