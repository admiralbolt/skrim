package avi.mod.skrim.skills;

import avi.mod.skrim.SkrimGlobalConfig;
import avi.mod.skrim.init.SkrimSoundEvents;
import avi.mod.skrim.skills.blacksmithing.BlacksmithingProvider;
import avi.mod.skrim.skills.blacksmithing.ISkillBlacksmithing;
import avi.mod.skrim.skills.botany.BotanyProvider;
import avi.mod.skrim.skills.botany.ISkillBotany;
import avi.mod.skrim.skills.brewing.BrewingProvider;
import avi.mod.skrim.skills.brewing.ISkillBrewing;
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
import avi.mod.skrim.utils.Utils;
import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Skills {

  public static Capability<ISkillBlacksmithing> BLACKSMITHING = BlacksmithingProvider.BLACKSMITHING;
  public static Capability<ISkillBotany> BOTANY = BotanyProvider.BOTANY;
  public static Capability<ISkillBrewing> BREWING = BrewingProvider.BREWING;
  public static Capability<ISkillCooking> COOKING = CookingProvider.COOKING;
  public static Capability<ISkillDemolition> DEMOLITION = DemolitionProvider.DEMOLITION;
  public static Capability<ISkillDigging> DIGGING = DiggingProvider.DIGGING;
  public static Capability<ISkillFarming> FARMING = FarmingProvider.FARMING;
  public static Capability<ISkillFishing> FISHING = FishingProvider.FISHING;
  public static Capability<ISkillMelee> MELEE = MeleeProvider.MELEE;
  public static Capability<ISkillMining> MINING = MiningProvider.MINING;
  public static Capability<ISkillRanged> RANGED = RangedProvider.RANGED;
  public static Capability<ISkillWoodcutting> WOODCUTTING = WoodcuttingProvider.WOODCUTTING;

  public static Map<String, Capability<? extends ISkill>> SKILL_MAP = new HashMap<>();
  public static List<Capability<? extends ISkill>> ALL_SKILLS = new ArrayList<>();

  public static String[] ALPHABETICAL_SKILLS = {"blacksmithing", "botany", "brewing", "cooking", "demolition", "digging", "farming",
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
    BrewingProvider.register();

    BLACKSMITHING = BlacksmithingProvider.BLACKSMITHING;
    BOTANY = BotanyProvider.BOTANY;
    BREWING = BrewingProvider.BREWING;
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
    ALL_SKILLS.add(BREWING);
    ALL_SKILLS.add(COOKING);
    ALL_SKILLS.add(DEMOLITION);
    ALL_SKILLS.add(DIGGING);
    ALL_SKILLS.add(FARMING);
    ALL_SKILLS.add(FISHING);
    ALL_SKILLS.add(MELEE);
    ALL_SKILLS.add(MINING);
    ALL_SKILLS.add(RANGED);
    ALL_SKILLS.add(WOODCUTTING);

    SKILL_MAP.put("blacksmithing", BLACKSMITHING);
    SKILL_MAP.put("botany", BOTANY);
    SKILL_MAP.put("brewing", BREWING);
    SKILL_MAP.put("cooking", COOKING);
    SKILL_MAP.put("demolition", DEMOLITION);
    SKILL_MAP.put("digging", DIGGING);
    SKILL_MAP.put("farming", FARMING);
    SKILL_MAP.put("fishing", FISHING);
    SKILL_MAP.put("melee", MELEE);
    SKILL_MAP.put("mining", MINING);
    SKILL_MAP.put("ranged", RANGED);
    SKILL_MAP.put("woodcutting", WOODCUTTING);
  }

  public static boolean canCraft(EntityPlayer player, Capability<? extends ISkill> cap, int level) {
    Skill skill = getSkill(player, cap, Skill.class);
    return skill.level >= level && skill.activeAbility(level / 25);
  }

  public static boolean hasSkill(EntityPlayer player, Capability<? extends ISkill> skill) {
    return player.hasCapability(skill, EnumFacing.NORTH);
  }

  public static <T extends Skill> T getSkill(EntityPlayer player, Capability<? extends ISkill> skillCap, Class<T> skillClass) {
    T skill = skillClass.cast(player.getCapability(skillCap, EnumFacing.NORTH));
    if (skill == null) throw new NullPointerException("No such skill: [" + skillCap + ", " + skillClass + "] exists.");
    return skill;
  }

  public static void replaceWithComponents(ItemCraftedEvent event) {
    if (event.player.inventory == null) return;

    final Item targetItem = event.crafting.getItem();

    event.player.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, (float) (Math.random() - Math.random()) * 0.2F);
    event.crafting.setCount(0);

    if (event.player.inventory.getItemStack().getItem() == Items.AIR) {
      // Player shift clicked, remove all items from their inventory, and decrement stacks.
      Utils.removeAllFromInventory(event.player, targetItem);
      for (int i = 0; i < event.craftMatrix.getSizeInventory(); i++) {
        event.craftMatrix.decrStackSize(i, 1);
      }
    }
  }

  public static int getTotalSkillLevels(EntityPlayer player) {
    int totalLevels = 0;
    for (Capability<? extends ISkill> cap : ALL_SKILLS) {
      Skill skill = getSkill(player, cap, Skill.class);
      totalLevels += skill.level;
    }
    return totalLevels;
  }

  public static double getTotalXpBonus(EntityPlayer player) {
    return (0.015 * player.experienceLevel) + (0.002 * getTotalSkillLevels(player));
  }

  public static double getTotalXp(EntityPlayer player, int xp) {
    return SkrimGlobalConfig.XP_MULTIPLIER.value * (1 + getTotalXpBonus(player)) * xp;
  }

  public static void playFortuneSound(EntityPlayer player) {
    player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_NOTE_PLING, player.getSoundCategory(), 0.4F, 1.0F);
  }

  public static void playRandomTreasureSound(EntityPlayer player) {
    player.world.playSound(null, player.getPosition(), SkrimSoundEvents.RANDOM_TREASURE, player.getSoundCategory(), 0.2F, 1.0F);
  }

  private static Map<Class, Integer> killXp = ImmutableMap.<Class, Integer>builder()
      .put(EntityZombie.class, 750)
      .put(EntityZombieVillager.class, 850)
      .put(EntityHusk.class, 1000)
      .put(EntitySkeleton.class, 850)
      .put(EntityStray.class, 1000)
      .put(EntityWitherSkeleton.class, 1500)
      .put(EntityPlayer.class, 100)
      .put(EntitySlime.class, 100)
      .put(EntityMagmaCube.class, 125)
      .put(EntityCreeper.class, 1000)
      .put(EntitySpider.class, 400)
      .put(EntityCaveSpider.class, 550)
      .put(EntityEnderman.class, 1600)
      .put(EntityPolarBear.class, 1000)
      .put(EntityPigZombie.class, 1150)
      .put(EntityBlaze.class, 1000)
      .put(EntityEndermite.class, 300)
      .put(EntityGhast.class, 600)
      .put(EntityGuardian.class, 1500)
      .put(EntityShulker.class, 850)
      .put(EntitySilverfish.class, 400)
      .put(EntityWitch.class, 1400)
      .put(EntityDragon.class, 60000)
      .put(EntityWither.class, 100000)
      .build();

  /**
   * Bonus xp for killing shit.
   */
  public static int entityKillXp(Entity entity) {
    return killXp.getOrDefault(entity.getClass(), 0);
  }

  public static void copyToClient(EntityJoinWorldEvent event) {
    if (!(event.getEntity() instanceof EntityPlayer)) return;

    EntityPlayer player = (EntityPlayer) event.getEntity();
    if (player.world.isRemote) return;

  }

}
