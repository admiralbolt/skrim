package avi.mod.skrim.handlers;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.entities.SkrimFishHook;
import avi.mod.skrim.entities.monster.MegaChicken;
import avi.mod.skrim.items.armor.LeafArmor;
import avi.mod.skrim.items.armor.Overalls;
import avi.mod.skrim.items.artifacts.*;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.blacksmithing.SkillBlacksmithing;
import avi.mod.skrim.skills.botany.SkillBotany;
import avi.mod.skrim.skills.cooking.SkillCooking;
import avi.mod.skrim.skills.defense.SkillDefense;
import avi.mod.skrim.skills.demolition.SkillDemolition;
import avi.mod.skrim.skills.digging.SkillDigging;
import avi.mod.skrim.skills.farming.SkillFarming;
import avi.mod.skrim.skills.melee.SkillMelee;
import avi.mod.skrim.skills.mining.SkillMining;
import avi.mod.skrim.skills.ranged.SkillRanged;
import avi.mod.skrim.skills.woodcutting.SkillWoodcutting;
import avi.mod.skrim.utils.Utils;
import avi.mod.skrim.world.PlayerCoords;
import avi.mod.skrim.world.PlayerPlacedBlocks;
import avi.mod.skrim.world.loot.AddTreasure;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.brewing.PlayerBrewedPotionEvent;
import net.minecraftforge.event.brewing.PotionBrewEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemSmeltedEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class EventHandler {


  @SubscribeEvent
  public void onLivingHurt(LivingHurtEvent event) {
    if (event.getSource().getTrueSource() instanceof EntityPlayer) {
      SkillMelee.applyMelee(event);
      SkillRanged.applyRanged(event);

      CanesSword.CanesHandler.slayChicken(event);
      GruesomeMask.GruesomeHandler.doubleAllDamage(event);
    } else if (event.getEntity() instanceof EntityPlayer) {
      SkillDefense.applyDefense(event);
      SkillDemolition.reduceExplosion(event);
      SkillDigging.vitalicBreathing(event);
      SkillMining.reduceLava(event);
      SkillBlacksmithing.ironHeart(event);
      SkillBotany.thornStyle(event);
      GruesomeMask.GruesomeHandler.doubleAllDamage(event);
      FireStaff.FireStaffHandler.fireImmunity(event);
    }
  }

  @SubscribeEvent
  public void onLivingDeath(LivingDeathEvent event) {
    if (event.getSource().getTrueSource() instanceof EntityPlayer) {
      SkillMelee.handleKill(event);
      SkillRanged.handleKill(event);
      SkillDemolition.onKillCreeper(event);
      SkillFarming.sideChick(event);
      SkillCooking.fireCook(event);
    } else if (event.getEntity() instanceof EntityPlayer) {
      PlayerCoords.saveDeathLocation(event);
    } else if (event.getEntity() instanceof EntityChicken) {
      MegaChicken.onChickenDeath(event);
    }
  }

  @SubscribeEvent
  public void onInteractEntity(PlayerInteractEvent.EntityInteract event) {
    SkillBotany.seduceVillager(event);
    SkillDigging.entomb(event);
  }

  @SubscribeEvent
  public void onInteract(PlayerInteractEvent.RightClickItem event) {
    SkillMelee.handleDual(event);
    SkillWoodcutting.whirlingChop(event);
  }

  @SubscribeEvent
  public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
    SkillMining.drill(event);
    Overalls.applyOveralls(event);
    SkillDigging.castles(event);
    LeafArmor.LeafArmorHandler.plantTree(event);
  }

  @SubscribeEvent
  public void onBlockPlaced(BlockEvent.PlaceEvent event) {
    SkillDemolition.onTntPlaced(event);
    SkillBotany.flowerSplosion(event);
    SkillFarming.applyGrowth(event);
    IBlockState state = event.getPlacedBlock();
    Block block = state.getBlock();
    if (Utils.isRawXpBlock(block)) {
      World world = event.getWorld();
      PlayerPlacedBlocks.addBlock(world, event.getPos());
      if (block instanceof BlockDoublePlant) {
        PlayerPlacedBlocks.addBlock(world, event.getPos().add(0, 1, 0));
      }
    }
  }

  @SubscribeEvent
  public void onStartExplosion(ExplosionEvent.Start event) {
    SkillDemolition.beforeGoBoom(event);
  }

  @SubscribeEvent
  public void onExplosion(ExplosionEvent.Detonate event) {
    SkillDemolition.onGoBoom(event);
  }

  @SubscribeEvent
  public void onLivingUpdate(LivingUpdateEvent event) {
    if (event.getEntity() instanceof EntityPlayer) {
      SkillMining.climbWall(event);
      SkillCooking.angelUpdate(event);
      SkillDefense.update(event);
      SkillDigging.metalDetector(event);
      SkillFarming.farmersTan(event);
      Skills.applyAttributes(event);

      LeafArmor.LeafArmorHandler.invisibility(event);

      // Artifact handlers
      PowerSuitChestplate.applyChozoTech(event);
      BlindingBoots.BlindingBootsHandler.goFast(event);
      FoxMask.FoxHandler.beAFox(event);
    }
  }

  @SubscribeEvent
  public void onHarvest(BlockEvent.HarvestDropsEvent event) {
    World world = event.getWorld();
    if (PlayerPlacedBlocks.isNaturalBlock(world, event.getPos())) {
      SkillMining.giveMoreOre(event);
      SkillBotany.soManyFlowers(event);
      SkillDigging.findTreasure(event);
      SkillFarming.giveMoreCrops(event);
      SkillWoodcutting.sawTree(event);
    }
    PlayerPlacedBlocks.removeBlock(world, event.getPos());
    // Fuck you double plants
    IBlockState state = event.getState();
    Block block = state.getBlock();
    if (block instanceof BlockDoublePlant) {
      for (int i = -1; i <= 1; i += 2) {
        BlockPos targetPos = event.getPos().add(0, i, 0);
        IBlockState checkState = world.getBlockState(targetPos);
        if (checkState.getBlock() instanceof BlockDoublePlant) {
          PlayerPlacedBlocks.removeBlock(world, targetPos);
        }
      }
    }
  }

  @SubscribeEvent
  public void onBreakingBlock(PlayerEvent.BreakSpeed event) {
    SkillMining.mineFaster(event);
    SkillDigging.digFaster(event);
    SkillWoodcutting.chopFaster(event);
  }

  @SubscribeEvent
  public void onBreakBlock(BlockEvent.BreakEvent event) {
    if (Skrim.DEBUG) {
      Utils.logBlockState(event.getState());
    }
    World world = event.getWorld();
    if (PlayerPlacedBlocks.isNaturalBlock(world, event.getPos())) {
      if (!Utils.isSilkTouching(event)) {
        SkillMining.addMiningXp(event);
      }
      SkillBotany.addBotanyXp(event);
      SkillDigging.addDiggingXp(event);
      SkillFarming.addFarmingXp(event);
      SkillWoodcutting.addWoodcuttingXp(event);
    }
  }

  @SubscribeEvent
  public void onLivingDrop(LivingDropsEvent event) {

    // Artifact handlers
    CanesSword.CanesHandler.fryChicken(event);
  }

  @SubscribeEvent
  public void onJump(LivingEvent.LivingJumpEvent event) {
    if (event.getEntity() instanceof EntityPlayer) {

      // Artifact handlers
      SpringheelBoots.SpringheelHandler.jumpHigh(event);
    }
  }

  @SubscribeEvent
  public void onFall(LivingFallEvent event) {
    if (event.getEntity() instanceof EntityPlayer) {
      SkillCooking.angelFall(event);

      // Artifact handlers
      SpringheelBoots.SpringheelHandler.preventFallDamage(event);
    }
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
    SkillFarming.verifyItems(event);
    SkillDemolition.verifyExplosives(event);
    SkillWoodcutting.verifyItems(event);
    SkillRanged.verifyItems(event);
  }

  @SubscribeEvent
  public void onContainerOpen(PlayerContainerEvent.Open event) {
    SkillBlacksmithing.saveItemNumber(event);
    SkillCooking.saveItemNumber(event);
  }

  @SubscribeEvent
  public void onUseHoe(UseHoeEvent event) {
    SkillFarming.createFarmland(event);
  }

  @SubscribeEvent
  public void onLoadLoot(LootTableLoadEvent event) {
    AddTreasure.addTreasure(event);
  }

  @SubscribeEvent
  public void onEntitySpawn(EntityJoinWorldEvent event) {
    SkrimFishHook.overrideDefaultHook(event);
  }

  @SubscribeEvent
  public void onPreBrew(PotionBrewEvent.Pre event) {
    // System.out.println("onPreBrew, length: " + event.getLength() + ", isCancellable: " + event.isCancelable());
    // System.out.println("onPreBrew, item0: " + event.getItem(0) + ", item1: " + event.getItem(1) + ", item2: " + event.getItem(2) + ",
    // item3: " + event.getItem(3));
  }

  @SubscribeEvent
  public void onBrew(PotionBrewEvent.Post event) {
    // System.out.println("onPostBrew, length: " + event.getLength() + ", isCancellable: " + event.isCancelable());
    // System.out.println("onPostBrew, item0: " + event.getItem(0) + ", item1: " + event.getItem(1) + ", item2: " + event.getItem(2) + ",
    // item3: " + event.getItem(3));
  }

  @SubscribeEvent
  public void grabPotion(PlayerBrewedPotionEvent event) {
    // System.out.println("PlayerBrewedPotion isCancellable: " + event.isCancelable());
    // System.out.println("player: " + event.getEntityPlayer());
    // System.out.println("item: " + event.getStack());
  }

}
