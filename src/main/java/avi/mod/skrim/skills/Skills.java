package avi.mod.skrim.skills;

import avi.mod.skrim.init.SkrimSoundEvents;
import avi.mod.skrim.skills.blacksmithing.BlacksmithingProvider;
import avi.mod.skrim.skills.blacksmithing.ISkillBlacksmithing;
import avi.mod.skrim.skills.botany.BotanyProvider;
import avi.mod.skrim.skills.botany.ISkillBotany;
import avi.mod.skrim.skills.cooking.CookingProvider;
import avi.mod.skrim.skills.cooking.ISkillCooking;
import avi.mod.skrim.skills.demolition.DemolitionProvider;
import avi.mod.skrim.skills.demolition.ISkillDemolition;
import avi.mod.skrim.skills.digging.DiggingProvider;
import avi.mod.skrim.skills.digging.ISkillDigging;
import avi.mod.skrim.skills.farming.FarmingProvider;
import avi.mod.skrim.skills.farming.ISkillFarming;
import avi.mod.skrim.skills.fishing.FishingProvider;
import avi.mod.skrim.skills.fishing.ISkillFishing;
import avi.mod.skrim.skills.melee.ISkillMelee;
import avi.mod.skrim.skills.melee.MeleeProvider;
import avi.mod.skrim.skills.mining.ISkillMining;
import avi.mod.skrim.skills.mining.MiningProvider;
import avi.mod.skrim.skills.ranged.ISkillRanged;
import avi.mod.skrim.skills.ranged.RangedProvider;
import avi.mod.skrim.skills.woodcutting.ISkillWoodcutting;
import avi.mod.skrim.skills.woodcutting.WoodcuttingProvider;
import avi.mod.skrim.utils.Obfuscation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

import java.util.*;

public class Skills {

  public static Capability<ISkillBlacksmithing> BLACKSMITHING = BlacksmithingProvider.BLACKSMITHING;
  public static Capability<ISkillBotany> BOTANY = BotanyProvider.BOTANY;
  public static Capability<ISkillCooking> COOKING = CookingProvider.COOKING;
  public static Capability<ISkillDemolition> DEMOLITION = DemolitionProvider.DEMOLITION;
  public static Capability<ISkillDigging> DIGGING = DiggingProvider.DIGGING;
  public static Capability<ISkillFarming> FARMING = FarmingProvider.FARMING;
  public static Capability<ISkillFishing> FISHING = FishingProvider.FISHING;
  public static Capability<ISkillMelee> MELEE = MeleeProvider.MELEE;
  public static Capability<ISkillMining> MINING = MiningProvider.MINING;
  public static Capability<ISkillRanged> RANGED = RangedProvider.RANGED;
  public static Capability<ISkillWoodcutting> WOODCUTTING = WoodcuttingProvider.WOODCUTTING;

  public static Map<String, Capability<? extends ISkill>> skillMap = new HashMap<>();
  public static List<Capability<? extends ISkill>> ALL_SKILLS = new ArrayList<>();

  public static String[] ALPHABETICAL_SKILLS = {"blacksmithing", "botany", "cooking", "defense", "demolition", "digging", "farming",
      "fishing", "melee", "mining", "ranged", "woodcutting"};

  public static void register() {
    MiningProvider.register();
    WoodcuttingProvider.register();
    DiggingProvider.register();
    FarmingProvider.register();
    BotanyProvider.register();
    CookingProvider.register();
    BlacksmithingProvider.register();
    FishingProvider.register();
    DemolitionProvider.register();
    MeleeProvider.register();
    RangedProvider.register();

    /**
     * This functionality should really be handled in the providers
     * themselves.
     */

    BLACKSMITHING = BlacksmithingProvider.BLACKSMITHING;
    BOTANY = BotanyProvider.BOTANY;
    COOKING = CookingProvider.COOKING;
    DEMOLITION = DemolitionProvider.DEMOLITION;
    DIGGING = DiggingProvider.DIGGING;
    FARMING = FarmingProvider.FARMING;
    FISHING = FishingProvider.FISHING;
    MELEE = MeleeProvider.MELEE;
    MINING = MiningProvider.MINING;
    RANGED = RangedProvider.RANGED;
    WOODCUTTING = WoodcuttingProvider.WOODCUTTING;

    ALL_SKILLS.add(BLACKSMITHING);
    ALL_SKILLS.add(BOTANY);
    ALL_SKILLS.add(COOKING);
    ALL_SKILLS.add(DEMOLITION);
    ALL_SKILLS.add(DIGGING);
    ALL_SKILLS.add(FARMING);
    ALL_SKILLS.add(FISHING);
    ALL_SKILLS.add(MELEE);
    ALL_SKILLS.add(MINING);
    ALL_SKILLS.add(RANGED);
    ALL_SKILLS.add(WOODCUTTING);

    skillMap.put("blacksmithing", BLACKSMITHING);
    skillMap.put("botany", BOTANY);
    skillMap.put("cooking", COOKING);
    skillMap.put("demolition", DEMOLITION);
    skillMap.put("digging", DIGGING);
    skillMap.put("farming", FARMING);
    skillMap.put("fishing", FISHING);
    skillMap.put("melee", MELEE);
    skillMap.put("mining", MINING);
    skillMap.put("ranged", RANGED);
    skillMap.put("woodcutting", WOODCUTTING);
  }

  public static boolean canCraft(EntityPlayer player, Capability<? extends ISkill> cap, int level) {
    if (player != null && player.hasCapability(cap, EnumFacing.NORTH)) {
      Skill skill = (Skill) player.getCapability(cap, EnumFacing.NORTH);
      if (skill.level >= level) {
        return true;
      }
    }
    return false;
  }

  public static boolean hasSkill(EntityPlayer player, Capability<? extends ISkill> skill) {
    return player.hasCapability(skill, EnumFacing.NORTH);
  }

  public static <T extends Skill> T getSkill(EntityPlayer player, Capability<? extends ISkill> skillCap, Class<T> skillClass) {
    T skill = skillClass.cast(player.getCapability(skillCap, EnumFacing.NORTH));
    if (skill == null) throw new NullPointerException("No such skill: " + skillCap + " exists.");
    return skill;
  }


  public static void destroyComponents(ItemCraftedEvent event) {
    if (event.player.inventory != null) {
      event.player.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, (float) (Math.random() - Math.random()) * 0.2F);
      Obfuscation.setStackSize(event.crafting, 0);
      event.crafting.damageItem(event.crafting.getMaxDamage(), event.player);
    }
  }

  public static void replaceWithComponents(ItemCraftedEvent event) {
    if (event.player.inventory != null) {
      final Item targetItem = event.crafting.getItem();
      event.player.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, (float) (Math.random() - Math.random()) * 0.2F);

      ItemStack slot;
      Item slotItem;
      ItemStack addStack;
      boolean first = true;
      Obfuscation.setStackSize(event.crafting, 0);
      /**
       * Iterate through slots and destroy all items :/
       */
      ItemStack inventoryAt;
      Item itemAt;

      final EntityPlayer player = event.player;
      removeTargetItem(player, targetItem);

      new Timer().schedule(new TimerTask() {
        @Override
        public void run() {
          removeTargetItem(player, targetItem);
        }
      }, 1000);

    }
  }

  public static void removeTargetItem(EntityPlayer player, Item removeItem) {
    ItemStack inventoryAt;
    Item itemAt;
    for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
      inventoryAt = player.inventory.getStackInSlot(i);
      if (inventoryAt != null) {
        itemAt = inventoryAt.getItem();
        if (itemAt != null && itemAt == removeItem) {
          player.inventory.removeStackFromSlot(i);
        }
      }
    }
  }

  public static int getTotalSkillLevels(EntityPlayer player) {
    int totalLevels = 0;
    for (Capability<? extends ISkill> cap : ALL_SKILLS) {
      if (player.hasCapability(cap, EnumFacing.NORTH)) {
        Skill skill = (Skill) player.getCapability(cap, EnumFacing.NORTH);
        totalLevels += skill.level;
      }
    }
    return totalLevels;
  }

  public static double getTotalXpBonus(EntityPlayer player) {
    return (0.015 * player.experienceLevel) + (0.002 * getTotalSkillLevels(player));
  }

  public static double getTotalXp(EntityPlayer player, int xp) {
    return getTotalXpBonus(player) * xp + xp;
  }

  public static void playFortuneSound(EntityPlayer player) {
    player.world.playSound((EntityPlayer) null, player.getPosition(), SoundEvents.BLOCK_NOTE_PLING, player.getSoundCategory(), 0.4F, 1.0F);
  }

  public static void playRandomTreasureSound(EntityPlayer player) {
    player.world.playSound((EntityPlayer) null, player.getPosition(), SkrimSoundEvents.RANDOM_TREASURE, player.getSoundCategory(), 0.2F,
        1.0F);
  }

  public static Map<Class, Integer> killXp = new HashMap<Class, Integer>();

  static {
    //
    killXp.put(EntityZombie.class, 750);
    killXp.put(EntityZombieVillager.class, 850);
    killXp.put(EntityHusk.class, 1000);

    killXp.put(EntitySkeleton.class, 850);
    killXp.put(EntityStray.class, 1000);
    killXp.put(EntityWitherSkeleton.class, 1500);

    killXp.put(EntityPlayer.class, 100);

    // Slimes are weird because they have babies.
    killXp.put(EntitySlime.class, 100);
    killXp.put(EntityMagmaCube.class, 125);

    killXp.put(EntityCreeper.class, 1000);

    killXp.put(EntitySpider.class, 400);
    killXp.put(EntityCaveSpider.class, 550);

    killXp.put(EntityEnderman.class, 1600);
    killXp.put(EntityPolarBear.class, 1000);
    killXp.put(EntityPigZombie.class, 1150);
    killXp.put(EntityBlaze.class, 1000);
    killXp.put(EntityEndermite.class, 300);
    killXp.put(EntityGhast.class, 600);
    killXp.put(EntityGuardian.class, 1500);
    killXp.put(EntityShulker.class, 850);
    killXp.put(EntitySilverfish.class, 400);
    killXp.put(EntityWitch.class, 1400);

    killXp.put(EntityDragon.class, 60000);
    killXp.put(EntityWither.class, 100000);
  }

  /**
   * Bonus xp for killing shit.
   */
  public static int entityKillXp(Entity entity) {
    return killXp.getOrDefault(entity.getClass(), 0);
  }

}
