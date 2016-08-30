package avi.mod.skrim.handlers.skills;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import avi.mod.skrim.skills.RandomTreasure;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.digging.SkillDigging;

public class DiggingHandler {

  @SubscribeEvent
  public void onBlockBreak(BlockEvent.BreakEvent event) {
    EntityPlayer player = event.getPlayer();
    if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.DIGGING, EnumFacing.NORTH)) {
      SkillDigging digging = (SkillDigging) player.getCapability(Skills.DIGGING, EnumFacing.NORTH);
      IBlockState state = event.getState();
      Block target = state.getBlock();
      int addXp = digging.getXp(digging.getDirtName(state));
      if (addXp > 0) {
        digging.xp += digging.getXp(digging.getDirtName(state));
        digging.levelUp((EntityPlayerMP) player);
      }
    }
  }

  @SubscribeEvent
  public void breakSpeed(PlayerEvent.BreakSpeed event) {
  	EntityPlayer player = event.getEntityPlayer();
    SkillDigging digging = (SkillDigging) player.getCapability(Skills.DIGGING, EnumFacing.NORTH);
    if (player.hasCapability(Skills.DIGGING, EnumFacing.NORTH)) {
    	IBlockState state = event.getState();
    	if (digging.validSpeedTarget(state)) {
        event.setNewSpeed((float) (event.getOriginalSpeed() + digging.getSpeedBonus()));
      }
    }
  }

  @SubscribeEvent
  public void onFindTreasure(BlockEvent.HarvestDropsEvent event) {
  	EntityPlayer player = event.getHarvester();
    if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.DIGGING, EnumFacing.NORTH)) {
      SkillDigging digging = (SkillDigging) player.getCapability(Skills.DIGGING, EnumFacing.NORTH);
      IBlockState state = event.getState();
      if (digging.validTreasureTarget(state)) {
        double random = Math.random();
        if (random < digging.getTreasureChance()) {
          ItemStack treasure = RandomTreasure.generate();
          List<ItemStack> drops = event.getDrops();
          drops.add(treasure);
          digging.xp += 100; // And 100 xp!
          digging.levelUp((EntityPlayerMP) player);
        }
      }
    }
  }

}
