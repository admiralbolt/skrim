package avi.mod.skrim.entities.monster;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MegaChicken extends EntityChicken {
	
	public static float SIZE_MULT = 5.0F;

	public MegaChicken(World worldIn) {
		super(worldIn);
		this.setSize(this.width * SIZE_MULT, this.height * SIZE_MULT);
	}

	@Override
	protected void initEntityAI() {
		System.out.println("entity AI tasks registering...");
		this.tasks.addTask(0, new EntityAISwimming(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[] {EntityPigZombie.class}));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
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

	class AIMeleeAttack extends EntityAIAttackMelee {
		
		public AIMeleeAttack() {
			super(MegaChicken.this, 1.25D, true);
		}

		protected void checkAndPerformAttack(EntityLivingBase targetEntity, double distance) {
			double d0 = this.getAttackReachSqr(targetEntity);
			
			if (distance <= d0 && this.attackTick <= 0) {
				this.attackTick = 20;
				this.attacker.attackEntityAsMob(targetEntity);
			} else if (distance <= d0 * 2.0D) {
				if (this.attackTick <= 0) {
					this.attackTick = 20;
				}

			} else {
				this.attackTick = 20;
			}
		}

		/**
		 * Resets the task
		 */
		public void resetTask() {
			super.resetTask();
		}

		protected double getAttackReachSqr(EntityLivingBase attackTarget) {
			return (double) (8.0F + attackTarget.width);
		}
	}

}
