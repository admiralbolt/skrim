package avi.mod.skrim.skills.smelting;

import avi.mod.skrim.skills.ISkill;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemSmeltedEvent;

public interface ISkillSmelting extends ISkill {

  /**
   * This should be overriden in the implementation
   * to gain xp when breaking specific types of blocks.
   */
  public void onItemSmelted(ItemSmeltedEvent event);

}
