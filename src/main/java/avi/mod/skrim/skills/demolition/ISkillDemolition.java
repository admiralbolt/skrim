package avi.mod.skrim.skills.demolition;

import avi.mod.skrim.skills.ISkill;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;

public interface ISkillDemolition extends ISkill {

  /**
   * This should be overriden in the implementation
   * to gain xp when breaking specific types of blocks.
   */
  public void onGoBoom(ExplosionEvent.Detonate event);
  public void onTnt(BlockEvent.PlaceEvent event);
  public void onPlayerHurt(LivingHurtEvent event);

}
