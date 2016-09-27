package avi.mod.skrim.handlers;

import avi.mod.skrim.skills.blacksmithing.SkillBlacksmithing;
import avi.mod.skrim.skills.botany.SkillBotany;
import avi.mod.skrim.skills.cooking.SkillCooking;
import avi.mod.skrim.skills.defense.SkillDefense;
import avi.mod.skrim.skills.demolition.SkillDemolition;
import avi.mod.skrim.skills.digging.SkillDigging;
import avi.mod.skrim.skills.farming.SkillFarming;
import avi.mod.skrim.skills.fishing.SkillFishing;
import avi.mod.skrim.skills.melee.SkillMelee;
import avi.mod.skrim.skills.mining.SkillMining;
import avi.mod.skrim.skills.ranged.SkillRanged;
import avi.mod.skrim.skills.woodcutting.SkillWoodcutting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemSmeltedEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EventHandler {

	@SubscribeEvent
	public void onLivingHurt(LivingHurtEvent event) {
		SkillMelee.applyMelee(event);
		SkillRanged.applyRanged(event);
		SkillDefense.applyDefense(event);
		SkillDemolition.reduceExplosion(event);
		SkillMining.reduceLava(event);
		SkillBlacksmithing.ironHeart(event);
		SkillBotany.thornStyle(event);
		ArtifactHandler.CanesHandler.slayChicken(event);
	}

	@SubscribeEvent
	public void onLivingDeath(LivingDeathEvent event) {
		SkillMelee.handleKill(event);
		SkillDemolition.onKillCrepper(event);
	}

	@SubscribeEvent
	public void onInteractEntity(PlayerInteractEvent.EntityInteract event) {
		SkillBotany.seduceVillager(event);
	}

	@SubscribeEvent
	public void onInteract(PlayerInteractEvent.RightClickItem event) {
		SkillMelee.handleDual(event);
	}

	@SubscribeEvent
	public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		SkillMining.drill(event);
	}

	@SubscribeEvent
	public void onBlockPlaced(BlockEvent.PlaceEvent event) {
		SkillDemolition.onTntPlaced(event);
		SkillBotany.flowerSplosion(event);
		SkillFarming.applyGrowth(event);
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
		SkillMining.giveMoreOre(event);
		SkillBotany.soManyFlowers(event);
		SkillDigging.findTreasure(event);
		SkillFarming.giveMoreCrops(event);
	}

	@SubscribeEvent
	public void onBreakingBlock(PlayerEvent.BreakSpeed event) {
		SkillMining.mineFaster(event);
		SkillDigging.digFaster(event);
		SkillWoodcutting.chopFaster(event);
	}

	@SubscribeEvent
	public void onBreakBlock(BlockEvent.BreakEvent event) {
		SkillMining.addMiningXp(event);
		SkillBotany.addBotanyXp(event);
		SkillDigging.addDiggingXp(event);
		SkillFarming.addFarmingXp(event);
		SkillWoodcutting.addWoodcuttingXp(event);
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
		SkillCooking.injectSmeltedFood(event);
	}

	@SubscribeEvent
	public void onItemCrafted(ItemCraftedEvent event) {
		SkillBlacksmithing.verifyObsidian(event);
		SkillBotany.verifyFlowers(event);
		SkillCooking.injectCraftedFood(event);
		SkillFishing.craftSkrimRod(event);
	}

	@SubscribeEvent
	public void onContainerOpen(PlayerContainerEvent.Open event) {
		SkillBlacksmithing.saveItemNumber(event);
		SkillCooking.saveItemNumber(event);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderOverlay(RenderGameOverlayEvent.Pre event) {
		SkillDefense.renderArmor(event);
	}

	@SubscribeEvent
	public void onItemPickup(EntityItemPickupEvent event) {
		SkillFishing.pickupSkrimRod(event);
	}

}
