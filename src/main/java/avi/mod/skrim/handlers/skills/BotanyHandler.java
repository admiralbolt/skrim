package avi.mod.skrim.handlers.skills;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.botany.SkillBotany;

public class BotanyHandler {

  @SubscribeEvent
	public void onBlockBreak(BlockEvent.BreakEvent event) {
		EntityPlayer player = event.getPlayer();
		if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.BOTANY, EnumFacing.NORTH)) {
			SkillBotany botany = (SkillBotany) player.getCapability(Skills.BOTANY, EnumFacing.NORTH);
			IBlockState state = event.getState();
			int addXp = botany.getXp(botany.getFlowerName(state));
			if (addXp > 0) {
				botany.xp += botany.getXp(botany.getFlowerName(state));
				botany.levelUp((EntityPlayerMP) player);
			}
		}
	}

	@SubscribeEvent
	public void onHarvestFlower(BlockEvent.HarvestDropsEvent event) {
		IBlockState state = event.getState();
		EntityPlayer player = event.getHarvester();
		if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.BOTANY, EnumFacing.NORTH)) {
			SkillBotany botany = (SkillBotany) player.getCapability(Skills.BOTANY, EnumFacing.NORTH);
			if (botany.validFlower(state)) {
				Block block = state.getBlock();
				/**
				 * DOUBLE Plants are coded weirdly, so currently fortune WON'T apply to them.
				 * EDIT: Won't apply to some of them...? Why you do dis.
				 */
				double random = Math.random();
				if (random < botany.getFortuneChance()) {
					List<ItemStack> drops = event.getDrops();
					// Let's not loop infinitely, that seems like a good idea.
					int dropSize = drops.size();
          for (int j = 0; j < botany.getFortuneAmount() - 1; j++) {
            for (int i = 0; i < dropSize; i++) {
              drops.add(drops.get(i).copy());
            }
          }
          botany.xp += 100; // And 100 xp!
          botany.levelUp((EntityPlayerMP) player);
				}
			}
		}
	}



}
