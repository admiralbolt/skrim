package avi.mod.skrim.skills.farming;

import avi.mod.skrim.blocks.SkrimBlocks;
import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillAbility;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.digging.SkillDigging;
import avi.mod.skrim.utils.Utils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.*;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SkillFarming extends Skill implements ISkillFarming {

  public static SkillStorage<ISkillFarming> skillStorage = new SkillStorage<>();
  private static final Map<String, Integer> XP_MAP = ImmutableMap.<String, Integer>builder()
      .put("crops", 200)
      .put("beetroots", 225)
      .put("cocoa", 250)
      .put("potatoes", 275)
      .put("carrots", 275)
      .put("pumpkin", 400)
      .put("melon", 400)
      .put("nether_wart", 450)
      .build();

  private static final int TAN_DURATION = 160;
  private static final long TAN_CHECK = 40L;

  private static SkillAbility OVERALLS = new SkillAbility("farming", "Overalls", 25,
      "Overall, this ability seems pretty good! AHAHAHA Get it?  (Please help me I need sleep.)", "Grants you the ability to craft " +
      "overalls.",
      "While worn, right clicking with a hoe acts like applying bonemeal.");

  private static SkillAbility HUSBANDRY = new SkillAbility("farming", "Husbandry", 50, "Like lambs to the slaughter.", "Doubles drops " +
      "from non mob entities.");

  private static SkillAbility FARMERS_TAN = new SkillAbility("farming", "Farmer's Tan", 75, "You're a plant Vash.",
      "Being in sunlight grants you a speed boost and haste.");

  private static SkillAbility MAGIC_BEANSTALK = new SkillAbility("farming", "Magic Beanstalk", 100, "Fee-fi-fo-fum! Random chests for " +
      "everyone!",
      "Grants you the ability to craft a magic bean.");

  public SkillFarming() {
    this(1, 0);
  }

  public SkillFarming(int level, int currentXp) {
    super("Farming", level, currentXp);
    this.addAbilities(OVERALLS, HUSBANDRY, FARMERS_TAN, MAGIC_BEANSTALK);
  }

  @Override
  public List<String> getToolTip() {
    List<String> tooltip = new ArrayList<String>();
    if (this.skillEnabled) {
      tooltip.add("§a" + Utils.formatPercent(this.getFortuneChance()) + "%§r chance to §a" + Utils.getFortuneString(this.getFortuneAmount())
          + "§r harvest drops.");
      tooltip.add("   This bonus stacks with fortune.");
      if (this.getGrowthStage() > 1) {
        tooltip.add("Plants start in stage §a" + this.getGrowthStage() + "§r of growth.");
      }
    } else {
      tooltip.add(Skill.COLOR_DISABLED + Utils.formatPercent(this.getFortuneChance()) + "% chance to " + Utils.getFortuneString(this.getFortuneAmount())
          + " harvest drops.");
      tooltip.add("   This bonus stacks with fortune.");
      if (this.getGrowthStage() > 1) {
        tooltip.add(Skill.COLOR_DISABLED + "Plants start in stage " + this.getGrowthStage() + " of growth.");
      }
    }
    return tooltip;
  }

  private double getFortuneChance() {
    return 0.01 * this.level;
  }

  private int getFortuneAmount() {
    return 2 + this.level / 25;
  }

  public static int getXp(String blockName) {
    return XP_MAP.getOrDefault(blockName, 20);
  }

  public static boolean validCrop(IBlockState state) {
    Block block = state.getBlock();
    return block instanceof BlockStem || block instanceof BlockCrops || block instanceof BlockCocoa || block instanceof BlockNetherWart;
  }

  /**
   * Need to cap this shit @ 6 to avoid super OPNESS Still pretty OPOP
   */
  private int getGrowthStage() {
    int growthStage = (int) Math.floor((double) this.level / 20);
    return (growthStage > 6) ? 6 : growthStage;
  }

  private static boolean isPlantFullyGrown(IBlockState state) {
    Block block = state.getBlock();
    int age = block.getMetaFromState(state);

    // In case max ages get updated in the future, easiest place to check is the json files under blockstates/
    return block instanceof BlockMelon
        || block instanceof BlockPumpkin
        || (block instanceof BlockCrops && age == 7)
        || (block instanceof BlockBeetroot && age == 3)
        || (block instanceof BlockNetherWart && age == 3)
        || (block instanceof BlockCocoa && age == 2);
  }

  public static void addFarmingXp(BlockEvent.BreakEvent event) {
    EntityPlayer player = event.getPlayer();
    if (player.world.isRemote) return;

    IBlockState state = event.getState();
    if (!isPlantFullyGrown(state)) return;

    SkillFarming farming = Skills.getSkill(player, Skills.FARMING, SkillFarming.class);
    Block target = state.getBlock();
    farming.addXp((EntityPlayerMP) player, getXp(Utils.getBlockName(target)));
  }

  public static void giveMoreCrops(BlockEvent.HarvestDropsEvent event) {
    EntityPlayer player = event.getHarvester();
    if (player == null || player.world.isRemote) return;

    IBlockState state = event.getState();
    if (!isPlantFullyGrown(state)) return;

    SkillFarming farming = Skills.getSkill(player, Skills.FARMING, SkillFarming.class);
    if (!farming.skillEnabled) return;
    if (Math.random() >= farming.getFortuneChance()) return;

    // Crops can drop multiple types of items, so we want to copy each dropped item.
    List<ItemStack> drops = event.getDrops();
    int dropSize = drops.size();
    for (int i = 0; i < dropSize; i++) {
      drops.add(new ItemStack(drops.get(0).getItem(), farming.getFortuneAmount() - 1, drops.get(0).getMetadata()));
    }
    Skills.playFortuneSound(player);
  }

  public static void applyGrowth(BlockEvent.PlaceEvent event) {
    EntityPlayer player = event.getPlayer();
    if (player.world.isRemote) return;

    IBlockState placedState = event.getPlacedBlock();
    Block targetBlock = event.getPlacedAgainst().getBlock();
    if (validCrop(placedState) && (targetBlock instanceof BlockFarmland || targetBlock instanceof BlockOldLog)) {
      SkillFarming farming = Skills.getSkill(player, Skills.FARMING, SkillFarming.class);
      if (!farming.skillEnabled) return;

      event.getWorld().setBlockState(event.getPos(), farming.cropWithGrowth(placedState));
    }
  }

  private IBlockState cropWithGrowth(IBlockState placedState) {
    Block placedBlock = placedState.getBlock();
    PropertyInteger prop = null;
    int growthStage = this.getGrowthStage();
    if (placedBlock instanceof BlockStem) {
      prop = BlockStem.AGE;
    } else if (placedBlock instanceof BlockBeetroot) {
      prop = BlockBeetroot.BEETROOT_AGE;
      if (growthStage > 2) {
        growthStage = 2;
      }
    } else if (placedBlock instanceof BlockCocoa) {
      prop = BlockCocoa.AGE;
      if (growthStage > 1) {
        growthStage = 1;
      }
    } else if (placedBlock instanceof BlockCrops) {
      prop = BlockCrops.AGE;
    }

    return placedState.withProperty(prop, growthStage);
  }

  public static void verifyItems(ItemCraftedEvent event) {
    Item targetItem = event.crafting.getItem();
    Item magicBean = new ItemStack(SkrimBlocks.MAGIC_BEAN).getItem();

    if (targetItem == SkrimItems.OVERALLS) {
      if (!Skills.canCraft(event.player, Skills.FARMING, 25)) {
        Skills.replaceWithComponents(event);
      }
    } else if (targetItem == magicBean) {
      if (!Skills.canCraft(event.player, Skills.FARMING, 100)) {
        Skills.replaceWithComponents(event);
      }
    }
  }

  public static void createFarmland(UseHoeEvent event) {
    EntityPlayer player = event.getEntityPlayer();
    if (player.world.isRemote) return;

    String blockName = SkillDigging.getDirtName(event.getWorld().getBlockState(event.getPos()));
    if (blockName.equals("dirt") || blockName.equals("grass_block")) {
      SkillFarming farming = Skills.getSkill(player, Skills.FARMING, SkillFarming.class);
      farming.addXp((EntityPlayerMP) player, 10);
    }
  }

  public static void husbandry(LivingDropsEvent event) {
    Entity killedEntity = event.getEntity();
    if (killedEntity.isCreatureType(EnumCreatureType.MONSTER, false)) return;

    Entity entity = event.getSource().getTrueSource();
    if (!(entity instanceof EntityPlayer)) return;

    EntityPlayer player = (EntityPlayer) entity;
    SkillFarming farming = Skills.getSkill(player, Skills.FARMING, SkillFarming.class);
    if (!farming.activeAbility(2)) return;

    List<EntityItem> drops = event.getDrops();
    List<EntityItem> duplicateItems = new ArrayList<>();

    for (EntityItem item : drops) {
      duplicateItems.add(new EntityItem(player.world, item.posX, item.posY, item.posZ, item.getItem()));
    }

    drops.addAll(duplicateItems);
  }

  public static void farmersTan(LivingUpdateEvent event) {
    Entity entity = event.getEntity();
    if (!(entity instanceof EntityPlayer)) return;

    EntityPlayer player = (EntityPlayer) entity;
    if (player.world.isRemote) return;

    SkillFarming farming = Skills.getSkill(player, Skills.FARMING, SkillFarming.class);
    if (!farming.activeAbility(3)) return;

    if (player.world.getTotalWorldTime() % TAN_CHECK != 0L) return;

    BlockPos playerPos = new BlockPos(player.posX, player.posY, player.posZ);
    if (!player.world.isDaytime() || !player.world.canSeeSky(playerPos)) return;

    for (Potion potion : new Potion[]{MobEffects.SPEED, MobEffects.HASTE}) {
      PotionEffect newEffect = new PotionEffect(potion, TAN_DURATION, 0, true, false);
      Utils.addOrCombineEffect(player, newEffect);
    }
  }

}
