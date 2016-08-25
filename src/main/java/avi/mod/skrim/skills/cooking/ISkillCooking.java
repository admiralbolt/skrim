package avi.mod.skrim.skills.cooking;

import avi.mod.skrim.skills.ISkill;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemSmeltedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

public interface ISkillCooking extends ISkill {

  /**
   * This should be overriden in the implementation
   * to gain xp when breaking specific types of blocks.
   */
  public void onItemSmelted(ItemSmeltedEvent event);
  public void onItemCrafted(ItemCraftedEvent event);

}
