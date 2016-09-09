package avi.mod.skrim.handlers;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemSmeltedEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import avi.mod.skrim.skills.blacksmithing.SkillBlacksmithing;
import avi.mod.skrim.skills.defense.SkillDefense;
import avi.mod.skrim.skills.demolition.SkillDemolition;
import avi.mod.skrim.skills.melee.SkillMelee;
import avi.mod.skrim.skills.mining.SkillMining;
import avi.mod.skrim.skills.ranged.SkillRanged;

public class EventHandler {

	@SubscribeEvent
	public void onLivingHurt(LivingHurtEvent event) {
		SkillMelee.applyMelee(event);
		SkillRanged.applyRanged(event);
		SkillDefense.reduceDamage(event);
		SkillDemolition.reduceExplosion(event);
		SkillMining.reduceLava(event);
		SkillBlacksmithing.ironHeart(event);
		
		ArtifactHandler.CanesHandler.slayChicken(event);
	}

	@SubscribeEvent
	public void onLivingDeath(LivingDeathEvent event) {
		SkillMelee.handleKill(event);
	}

	@SubscribeEvent
	public void onInteract(PlayerInteractEvent.RightClickItem event) {
		SkillMelee.handleDual(event);
	}

	@SubscribeEvent
	public void onBlockPlaced(BlockEvent.PlaceEvent event) {
		SkillDemolition.onTntPlaced(event);
	}

	@SubscribeEvent
	public void onExplosion(ExplosionEvent.Detonate event) {
		SkillDemolition.onGoBoom(event);
	}

	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event) {
		SkillMining.climbWall(event);
	}

	@SubscribeEvent
	public void onHarvest(BlockEvent.HarvestDropsEvent event) {
		SkillMining.onMineOre(event);
	}

	@SubscribeEvent
	public void onBreakingBlock(PlayerEvent.BreakSpeed event) {
		SkillMining.mineFaster(event);
	}

	@SubscribeEvent
	public void onBreakBlock(BlockEvent.BreakEvent event) {
		SkillMining.addMiningXp(event);
	}

	@SubscribeEvent
	public void onLivingDrop(LivingDropsEvent event) {
		ArtifactHandler.CanesHandler.fryChicken(event);
	}
	
	@SubscribeEvent
	public void onJump(LivingEvent.LivingJumpEvent event) {
		ArtifactHandler.SpringheelHandler.jumpHigh(event);
	}
	
	@SubscribeEvent
	public void onFall(LivingFallEvent event) {
		ArtifactHandler.SpringheelHandler.preventFallDamage(event);
	}
	
	@SubscribeEvent
	public void onTick(PlayerTickEvent event) {
		SkillMelee.tickLeft(event);
	}
	
	@SubscribeEvent
	public void onItemRepair(AnvilRepairEvent event) {
		SkillBlacksmithing.enhanceRepair(event);
	}
	
	@SubscribeEvent
	public void onItemSmelted(ItemSmeltedEvent event) {
		SkillBlacksmithing.giveMoreIngots(event);
	}
	
	@SubscribeEvent
	public void onContainerOpen(PlayerContainerEvent.Open event) {
		SkillBlacksmithing.saveItemNumber(event);
	}

}
