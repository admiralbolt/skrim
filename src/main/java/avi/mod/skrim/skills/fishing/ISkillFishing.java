package avi.mod.skrim.skills.fishing;

import avi.mod.skrim.skills.ISkill;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public interface ISkillFishing extends ISkill {

  /**
   * This should be overriden in the implementation
   * to gain xp when breaking specific types of blocks.
   */
  public void onFishEvent(PlayerInteractEvent event);

}
