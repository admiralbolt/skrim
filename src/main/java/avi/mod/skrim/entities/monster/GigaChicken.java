package avi.mod.skrim.entities.monster;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * The first time someone spawns one of these is gonna be so fucking hilarious.
 */
public class GigaChicken extends MegaChicken {

  private static float SIZE_MULT = 15.0F;
  public static String NAME = "giga_chicken";

  public GigaChicken(World worldIn) {
    super(worldIn);
    this.setSize(this.width * SIZE_MULT, this.height * SIZE_MULT);
    this.reach = 120.0;
    this.attackSpeed = 10;
  }

  @Override
  protected void initEntityAI() {
    this.tasks.addTask(0, new EntityAISwimming(this));
    // HIGHLY AGGRESIVE.
    this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
    this.tasks.addTask(2, new MegaChicken.AIMeleeAttack());
    this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
    this.tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D));
    this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
    this.tasks.addTask(8, new EntityAILookIdle(this));
  }

  @Override
  public float getSizeMult() {
    return SIZE_MULT;
  }

  @Override
  public float getSoundPitch() {
    return 0.1F;
  }

  /**
   * Look at these fucking stats.
   */
  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(120.0D);
    this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(1.3D);
    this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(22.0D);
    this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(256.0D);
  }

}
