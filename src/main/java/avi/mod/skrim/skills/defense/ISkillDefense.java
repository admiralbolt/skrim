package avi.mod.skrim.skills.defense;

import avi.mod.skrim.skills.ISkill;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public interface ISkillDefense extends ISkill {

  /**
   * This should be overriden in the implementation
   * to gain xp when breaking specific types of blocks.
   */
  public void onPlayerHurt(LivingHurtEvent event);

}
