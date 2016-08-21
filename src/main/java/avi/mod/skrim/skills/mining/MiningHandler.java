package avi.mod.skrim.skills.mining;

import avi.mod.skrim.skills.Skills;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MiningHandler {

	@SubscribeEvent
	public void breakSpeed(PlayerEvent.BreakSpeed event) {
    Block targetBlock = event.getState().getBlock();
    EntityPlayer player = event.getEntityPlayer();
    if (player.hasCapability(Skills.MINING, EnumFacing.NORTH)) {
      SkillMining mining = (SkillMining) player.getCapability(Skills.MINING, EnumFacing.NORTH);
      event.setNewSpeed(mining.level);
    }
  }

}
