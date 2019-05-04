package avi.mod.skrim.entities.monster;

import avi.mod.skrim.network.SkrimPacketHandler;
import avi.mod.skrim.network.SpawnEntityPacket;
import avi.mod.skrim.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

/**
 * This is mostly copied from entity chicken.
 */
public class MegaChicken extends EntityChicken {

  private static float SIZE_MULT = 5.0F;

  public static String NAME = "mega_chicken";
  public double reach;
  // Attack speed in ticks.
  public int attackSpeed;

  public MegaChicken(World worldIn) {
    super(worldIn);
    this.setSize(this.width * SIZE_MULT, this.height * SIZE_MULT);
    this.reach = 8.0;
    this.attackSpeed = 20;
  }

  public float getSizeMult() {
    return SIZE_MULT;
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
  protected void playStepSound(BlockPos pos, Block blockIn) {
    this.playSound(SoundEvents.ENTITY_CHICKEN_STEP, 0.15F, 0.4F);
  }

  @Override
  public float getSoundPitch() {
    return 0.4F;
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
    this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.7D);
    this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
    this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
    this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
  }

  @Override
  public boolean attackEntityAsMob(Entity entityIn) {
    boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this),
        (float) ((int) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
    if (flag) {
      this.applyEnchantments(this, entityIn);
    }
    return flag;
  }

  @Override
  protected int getExperiencePoints(EntityPlayer player) {
    return 20;
  }

  /**
   * Spawn a Mega Chicken if a chicken dies and you roll poorly.
   * Spawn a Giga Chicken if a mega chicken dies and you roll poorly.
   */
  public static void onChickenDeath(LivingDeathEvent event) {
    if (!(event.getEntity() instanceof EntityChicken)) return;
    if (!event.getEntity().world.isRemote) return;

    Entity chicken = event.getEntity();
    if (event.getEntity() instanceof MegaChicken) {
      if (Utils.rand.nextDouble() < 0.02) {
        SkrimPacketHandler.INSTANCE.sendToServer(new SpawnEntityPacket(GigaChicken.NAME, true, chicken.posX, chicken.posY, chicken.posZ));
      }
      return;
    }

    if (Utils.rand.nextDouble() < 0.02) {
      SkrimPacketHandler.INSTANCE.sendToServer(new SpawnEntityPacket(MegaChicken.NAME, true, chicken.posX, chicken.posY, chicken.posZ));
    }
  }

  class AIMeleeAttack extends EntityAIAttackMelee {

    public AIMeleeAttack() {
      super(MegaChicken.this, 1.0D, true);
    }

    protected void checkAndPerformAttack(EntityLivingBase targetEntity, double distance) {
      double d0 = this.getAttackReachSqr(targetEntity);
      int attackSpeed = MegaChicken.this.attackSpeed;

      if (distance <= d0 && this.attackTick <= 0) {
        this.attackTick = attackSpeed;
        this.attacker.attackEntityAsMob(targetEntity);
      } else if (distance <= d0 * 2.0D) {
        if (this.attackTick <= 0) {
          this.attackTick = attackSpeed;
        }

      } else {
        this.attackTick = attackSpeed;
      }
    }

    /**
     * Resets the task
     */
    public void resetTask() {
      super.resetTask();
    }

    protected double getAttackReachSqr(EntityLivingBase attackTarget) {
      return MegaChicken.this.reach + attackTarget.width;
    }
  }

}
