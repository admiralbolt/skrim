package avi.mod.skrim.skills.fishing;

import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillAbility;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.utils.Obfuscation;
import avi.mod.skrim.utils.ReflectionUtils;
import avi.mod.skrim.utils.Utils;
import avi.mod.skrim.world.loot.CustomLootTables;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;

/**
 * Most of the interesting logic of the fishing skill is actually in the fishing hook entity itself.
 * See the SkrimFishHook.java.
 */
public class SkillFishing extends Skill implements ISkillFishing {

  public static SkillStorage<ISkillFishing> skillStorage = new SkillStorage<>();

  private static Map<UUID, UUID> PLAYER_ACTIVE_ROD = new HashMap<>();

  private static SkillAbility BATMAN = new SkillAbility("fishing", "Batman", 25, "na na na na na na na na",
      "Your fishing rod can now be used as a grappling hook.");

  private static SkillAbility TRIPLE_HOOK = new SkillAbility("fishing", "Triple Hook", 50, "Triple the hooks, triple " +
      "the pleasure.",
      "You now catch §a3x" + SkillAbility.DESC_COLOR + " as many items.");

  private static SkillAbility BOUNTIFUL_CATCH = new SkillAbility("fishing", "Bountiful Catch", 75, "On that E-X-P " +
      "grind.",
      "Catching a fish provides an additional§a 9-24" + SkillAbility.DESC_COLOR + " xp.");

  private static SkillAbility FLING = new SkillAbility("fishing", "Fling", 100, "Sometimes I don't know my own " +
      "strength.",
      "Launch hooked entities into the air.");

  public SkillFishing() {
    this(1, 0);
  }

  private SkillFishing(int level, int currentXp) {
    super("Fishing", level, currentXp);
    this.addAbilities(BATMAN, TRIPLE_HOOK, BOUNTIFUL_CATCH, FLING);
  }

  @Override
  public List<String> getToolTip() {
    List<String> tooltip = new ArrayList<String>();
    if (this.skillEnabled) {
      tooltip.add("§a" + Utils.formatPercentTwo(this.getDelayReduction()) + "%§r reduced fishing time.");
      tooltip.add("§a" + Utils.formatPercent(this.getTreasureChance()) + "%§r chance to fish additional treasure.");
    } else {
      tooltip.add(Skill.COLOR_DISABLED + Utils.formatPercentTwo(this.getDelayReduction()) + "% reduced fishing time.");
      tooltip.add(Skill.COLOR_DISABLED + Utils.formatPercent(this.getTreasureChance()) + "% chance to fish additional treasure.");
    }
    return tooltip;
  }

  public double getTreasureChance() {
    return 0.01 * this.level;
  }

  public double getDelayReduction() {
    return 0.0075 * this.level;
  }


  /**
   * We need to handle a few things here:
   * 1. The passive for additional treasure.
   * 2. The triple hook ability.
   * 3. The bountiful catch ability.
   *
   * Note, that this ONLY applies when we actually catch a fish.
   */
  public static void onItemFished(ItemFishedEvent event) {
    EntityPlayer angler = event.getEntityPlayer();
    SkillFishing fishing = Skills.getSkill(angler, Skills.FISHING, SkillFishing.class);
    EntityFishHook hook = event.getHookEntity();

    double x = angler.posX - hook.posX;
    double y = angler.posY - hook.posY;
    double z = angler.posZ - hook.posZ;
    double dist = MathHelper.sqrt(x * x + y * y + z * z);

    if (fishing.skillEnabled && Utils.rand.nextDouble() < fishing.getTreasureChance()) {
      EntityItem treasure = new EntityItem(hook.world, hook.posX, hook.posY, hook.posZ,
              CustomLootTables.getRandomTreasure(hook.world, angler, fishing.level));
      treasure.motionX = x * 0.1D;
      treasure.motionY = y * 0.1D + dist * 0.08D;
      treasure.motionZ = z * 0.1D;
      hook.world.spawnEntity(treasure);
      Skills.playRandomTreasureSound(angler);
    }

    // Apply triple hook.
    if (fishing.activeAbility(2)) {
      for (int q = 0; q < 2; ++q) {
        EntityItem copy = new EntityItem(hook.world, hook.posX, hook.posY, hook.posZ, event.getDrops().get(0));
        copy.motionX = x * 0.1D;
        copy.motionY = y * 0.1D + (double) MathHelper.sqrt(dist) * 0.08D;
        copy.motionZ = z * 0.1D;
        hook.world.spawnEntity(copy);
      }
    }

    // Apply bountiful catch.
    if (fishing.activeAbility(3)) {
      hook.world.spawnEntity(new EntityXPOrb(angler.world, angler.posX, angler.posY + 0.5D, angler.posZ + 0.5D, Utils.rand.nextInt(16) + 9));
    }

  }

  // Weeeeeee.
  public static void handleBatmanAndFling(PlayerInteractEvent.RightClickItem event) {
    if (event.getResult() == Event.Result.DENY) return;

    EntityFishHook hook = event.getEntityPlayer().fishEntity;
    EntityPlayer angler = event.getEntityPlayer();
    SkillFishing fishing = Skills.getSkill(angler, Skills.FISHING, SkillFishing.class);
    if (hook == null) return;

    boolean inGround = (boolean) ReflectionUtils.findTheFuckingFieldNoMatterTheCost(hook, Obfuscation.FISH_HOOK_IN_GROUND.getFieldNames());
    if (fishing.activeAbility(1) && inGround) {
      Utils.teleport(angler, hook.posX, hook.posY, hook.posZ, false);
    }

    if (hook.caughtEntity == null || !fishing.activeAbility(4)) return;

    // The method bringInHookedEntity() doesn't set the motion of the entity,
    // just applies a force to it. Which is great for us, we also want to apply a force.
    // A very strong, upward force.
    hook.caughtEntity.motionY += 5;
  }

  // this.ticksCaughtDelay of the EntityFishHook is not set when the entity itself is
  // created, but instead inside of the onUpdate() function.
  // This is really annoying to adjust.
  // Here we are subscribing to a player tick event, we are checking if they have a
  // fish hook attached them, and then modifying the ticksCaughtDelay time.
  public static void reduceFishingTime(TickEvent.PlayerTickEvent event) {
    // We don't want to run this function every tick, that's for fucking sure.
    if (event.player.world.getWorldTime() % 10 != 0) return;

    EntityFishHook hook = event.player.fishEntity;
    if (hook == null || !hook.isInWater()) return;

    SkillFishing fishing = Skills.getSkill(event.player, Skills.FISHING, SkillFishing.class);
    if (!fishing.skillEnabled) return;

    // Check to see if we've applied our reduction before.
    if (PLAYER_ACTIVE_ROD.get(event.player.getPersistentID()) == hook.getPersistentID()) return;

    // Apply our reduction.
    int ticksCaughtDelay = (int) ReflectionUtils.findTheFuckingFieldNoMatterTheCost(hook, Obfuscation.FISH_HOOK_CAUGHT_DELAY.getFieldNames());
    System.out.println("SKRIM TICKS_CAUGHT_DELAY: " + ticksCaughtDelay);
    if (ticksCaughtDelay <= 0) return;

    int newDelay = (int) (ticksCaughtDelay * (1 - fishing.getDelayReduction()));
    ReflectionUtils.fuckingHackValueTo(hook, newDelay, Obfuscation.FISH_HOOK_CAUGHT_DELAY.getFieldNames());
    ticksCaughtDelay = (int) ReflectionUtils.findTheFuckingFieldNoMatterTheCost(hook, Obfuscation.FISH_HOOK_CAUGHT_DELAY.getFieldNames());
    System.out.println("SKRIM TICKS_CAUGHT_DELAY: " + ticksCaughtDelay);
    PLAYER_ACTIVE_ROD.put(event.player.getPersistentID(), hook.getPersistentID());
  }


}
