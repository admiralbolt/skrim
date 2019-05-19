package avi.mod.skrim.skills.mining;

import avi.mod.skrim.network.FallDistancePacket;
import avi.mod.skrim.network.SkrimPacketHandler;
import avi.mod.skrim.network.skillpackets.DrillPacket;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillAbility;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.utils.Utils;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;

import java.util.*;

public class SkillMining extends Skill implements ISkillMining {

  public static SkillStorage<ISkillMining> skillStorage = new SkillStorage<>();
  private static Map<String, Integer> XP_MAP = ImmutableMap.<String, Integer>builder()
      .put("stone", 50)
      .put("netherrack", 60)
      .put("granite", 75)
      .put("andesite", 75)
      .put("diorite", 75)
      .put("sandstone", 100)
      .put("end_stone", 100)
      .put("stained_terracotta", 175)
      .put("coal_ore", 500)
      .put("iron_ore", 750)
      .put("nether_quartz_ore", 800)
      .put("obsidian", 1000)
      .put("redstone_ore", 1500)
      .put("gold_ore", 2000)
      .put("lapis_lazuli_ore", 3000)
      .put("diamond_ore", 5000)
      .put("emerald_ore", 10000)
      .build();

  private static final long DARKVISION_CHECK = 80L;
  private static final int NIGHT_VISION_DURATION = 300;

  private static final List<String> VALID_MINING_BLOCKS = new ArrayList<>(Arrays.asList("cobblestone_stairs",
      "stone_brick_stairs", "quartz_stairs",
      "nether_brick_stairs", "brick_stairs", "sandstone_stairs", "red_sandstone_stairs", "purpur_block",
      "purpur_pillar", "iron_door"));

  private static final List<String> VALID_FORTUNE_ORES = new ArrayList<>(
      Arrays.asList("coal_ore", "iron_ore", "gold_ore", "lapis_lazuli_ore", "diamond_ore", "emerald_ore",
          "redstone_ore", "quartz_ore"));

  private static SkillAbility DARKVISION = new SkillAbility("mining", "Darkvision", 25, "I was born in the darkness.",
      "While close to the bottom of the world you have a constant night vision effect.");

  private static SkillAbility LAVA_SWIMMER = new SkillAbility("mining", "Lava Swimmer", 50, "Reducing the number of " +
      "'oh shit' moments.",
      "While close to the bottom of the world you take §a50%" + SkillAbility.DESC_COLOR + " damage from lava, and " +
          "don't get set on fire by it.");

  private static SkillAbility SPELUNKER = new SkillAbility("mining", "Spelunker", 75, "Spelunkey?  More like " +
      "Spedunkey.  AHAHAHAHA.",
      "Allows you to climb walls while holding jump.");

  private static SkillAbility DRILL = new SkillAbility("mining", "Drill", 100, "Without the risk of earthquakes!",
      "Right clicking with a pickaxe instantly mines to bedrock.");

  public SkillMining() {
    this(1, 0);
  }

  public SkillMining(int level, int currentXp) {
    super("Mining", level, currentXp);
    this.addAbilities(DARKVISION, LAVA_SWIMMER, SPELUNKER, DRILL);
  }

  public static int getXp(String blockName) {
    return XP_MAP.getOrDefault(blockName, 0);
  }

  private double getSpeedBonus() {
    return 0.008 * this.level;
  }

  private double getFortuneChance() {
    return 0.005 * this.level;
  }

  private int getFortuneAmount() {
    return 2 + this.level / 50;
  }

  @Override
  public List<String> getToolTip() {
    List<String> tooltip = new ArrayList<>();
    tooltip.add("§a+" + Utils.formatPercent(this.getSpeedBonus()) + "%§r mining speed bonus.");
    tooltip.add(
        "§a" + Utils.formatPercent(this.getFortuneChance()) + "%§r chance to §a" + Utils.getFortuneString(this.getFortuneAmount()) + "§r " +
            "ore drops.");
    tooltip.add("   This bonus stacks with fortune.");
    return tooltip;
  }

  private boolean validSpeedTarget(IBlockState state) {
    Block block = state.getBlock();
    String harvestTool = block.getHarvestTool(state);
    return (harvestTool != null && harvestTool.toLowerCase().equals("pickaxe")) || VALID_MINING_BLOCKS.contains(Utils.getBlockName(block))
        || block instanceof BlockOre || block instanceof BlockRedstoneOre || block instanceof BlockStone || block instanceof BlockStoneSlab
        || block instanceof BlockStoneSlabNew || block instanceof BlockObsidian || block instanceof BlockStoneBrick || block instanceof BlockNetherBrick
        || block instanceof BlockNetherrack || block instanceof BlockSandStone || block instanceof BlockRedSandstone;
  }

  private boolean validFortuneTarget(IBlockState state) {
    Block block = state.getBlock();
    String blockName = Utils.snakeCase(block.getLocalizedName());
    return ((block instanceof BlockOre || block instanceof BlockRedstoneOre) && VALID_FORTUNE_ORES.contains(blockName));
  }

  public static void addMiningXp(BlockEvent.BreakEvent event) {
    EntityPlayer player = event.getPlayer();
    if (player.world.isRemote) return;

    SkillMining mining = Skills.getSkill(player, Skills.MINING, SkillMining.class);
    IBlockState state = event.getState();
    Block target = state.getBlock();
    String blockName = (target instanceof BlockStone) ? state.getValue(BlockStone.VARIANT).toString() :
        Utils.snakeCase(target.getLocalizedName());
    mining.addXp((EntityPlayerMP) player, getXp(blockName));
  }

  public static void mineFaster(PlayerEvent.BreakSpeed event) {
    EntityPlayer player = event.getEntityPlayer();
    SkillMining mining = Skills.getSkill(player, Skills.MINING, SkillMining.class);
    if (!mining.validSpeedTarget(event.getState())) return;

    event.setNewSpeed((float) (event.getOriginalSpeed() * (1 + mining.getSpeedBonus())));
  }

  public static void giveMoreOre(BlockEvent.HarvestDropsEvent event) {
    EntityPlayer player = event.getHarvester();
    if (player == null) return;

    SkillMining mining = Skills.getSkill(player, Skills.MINING, SkillMining.class);
    IBlockState state = event.getState();
    if (!mining.validFortuneTarget(state) || Utils.rand.nextDouble() >= mining.getFortuneChance()) return;

    List<ItemStack> drops = event.getDrops();
    drops.add(new ItemStack(drops.get(0).getItem(), mining.getFortuneAmount() - 1, drops.get(0).getMetadata()));
    Skills.playFortuneSound(player);
  }


  public static void reduceLava(LivingHurtEvent event) {
    Entity entity = event.getEntity();
    if (!(entity instanceof EntityPlayer)) return;

    final EntityPlayer player = (EntityPlayer) entity;
    SkillMining mining = Skills.getSkill(player, Skills.MINING, SkillMining.class);
    if (!mining.hasAbility(2)) return;

    BlockPos pos = player.getPosition();
    if (pos.getY() > 40) return;


    DamageSource source = event.getSource();
    if (!source.getDamageType().equals("lava") && !source.getDamageType().equals("inFire")) return;

    event.setAmount(event.getAmount() * 0.5f);
    player.extinguish();
    new Timer().schedule(new TimerTask() {
      @Override
      public void run() {
        player.extinguish();
      }
    }, 400);
  }


  /**
   * Handles both the night vision ability and the wall climbing ability.
   */
  public static void miningUpdate(LivingEvent.LivingUpdateEvent event) {
    Entity entity = event.getEntity();
    if (!(entity instanceof EntityPlayer)) return;

    final EntityPlayer player = (EntityPlayer) entity;
    SkillMining mining = Skills.getSkill(player, Skills.MINING, SkillMining.class);

    if (!mining.hasAbility(1)) return;
    // Night vision.
    BlockPos pos = player.getPosition();
    if (pos.getY() <= 40) {
      if (player.world.getTotalWorldTime() % DARKVISION_CHECK == 0L) {
        PotionEffect effect = new PotionEffect(MobEffects.NIGHT_VISION, NIGHT_VISION_DURATION, 0, true, false);
        Utils.addOrCombineEffect(player, effect);
      }
    }

    // This is handled by sending a packet to the server, so only run on client side.
    if (!player.world.isRemote) return;

    KeyBinding jumpKey = Minecraft.getMinecraft().gameSettings.keyBindJump;
    if (!mining.hasAbility(3) || !player.collidedHorizontally || !jumpKey.isKeyDown()) return;

    // Spooderman, spooderman.
    player.motionY = Math.min(0.2, player.motionY + 0.1);
    if (player.motionY > 0) {
      player.fallDistance = 0.0F;
    } else {
      player.fallDistance -= 1F;
    }

    // The way that falling damage works in minecraft is not based on fall speed but rather fall distance. We send a
    // packet to the server to update the players fall distance to a lower value.
    SkrimPacketHandler.INSTANCE.sendToServer(new FallDistancePacket(player.fallDistance));
  }


  public static void drill(PlayerInteractEvent.RightClickBlock event) {
    EntityPlayer player = event.getEntityPlayer();
    if (!player.world.isRemote) return;

    SkillMining mining = Skills.getSkill(player, Skills.MINING, SkillMining.class);
    if (!mining.hasAbility(4)) return;

    if (!(event.getItemStack().getItem() instanceof ItemPickaxe)) return;

    RayTraceResult result = player.rayTrace(5.0D, 1.0F);
    if (result == null || result.typeOfHit != RayTraceResult.Type.BLOCK) return;

    player.swingArm(EnumHand.MAIN_HAND);
    BlockPos targetPos = result.getBlockPos();
    SkrimPacketHandler.INSTANCE.sendToServer(new DrillPacket(targetPos.getX(), targetPos.getY(),
        targetPos.getZ()));
  }

}
