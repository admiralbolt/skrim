package avi.mod.skrim.skills.botany;

import avi.mod.skrim.skills.ISkill;
import net.minecraftforge.event.world.BlockEvent;

public interface ISkillBotany extends ISkill {

  /**
   * This should be overriden in the implementation
   * to gain xp when breaking specific types of blocks.
   */
  public void onBlockBreak(BlockEvent.BreakEvent event);

}
