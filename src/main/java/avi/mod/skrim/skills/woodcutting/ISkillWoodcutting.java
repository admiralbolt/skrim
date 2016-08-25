package avi.mod.skrim.skills.woodcutting;

import avi.mod.skrim.skills.ISkill;
import net.minecraftforge.event.world.BlockEvent;

public interface ISkillWoodcutting extends ISkill {

  /**
   * Chop 'dem woods.
   */
  public void onBlockBreak(BlockEvent.BreakEvent event);
  
}
