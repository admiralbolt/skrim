package avi.mod.skrim.handlers.skills;

import net.minecraft.block.Block;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import avi.mod.skrim.items.CustomAxe;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.woodcutting.SkillWoodcutting;

public class WoodcuttingHandler {

  @SubscribeEvent
	public void onBlockBreak(BlockEvent.BreakEvent event) {
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
				woodcutting.xp += woodcutting.getXp(woodcutting.getWoodName(state));
				woodcutting.levelUp((EntityPlayerMP) player);
			}
		}
	}

  @SubscribeEvent
	public void breakSpeed(PlayerEvent.BreakSpeed event) {
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
