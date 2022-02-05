package avi.mod.skrim.skills.digging;

import avi.mod.skrim.network.SkrimPacketHandler;
import avi.mod.skrim.network.skillpackets.MetalDetectorPacket;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillAbility;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.utils.Utils;
import avi.mod.skrim.world.PlayerPlacedBlocks;
import avi.mod.skrim.world.gen.SkrimGenScatteredFeature;
import avi.mod.skrim.world.loot.CustomLootTables;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SkillDigging extends Skill implements ISkillDigging {

  public static SkillStorage<ISkillDigging> skillStorage = new SkillStorage<>();

  private static final Map<String, Integer> XP_MAP = ImmutableMap.<String, Integer>builder()
      .put("dirt", 75)
      .put("farmland", 75)
      .put("snow", 75)
      .put("sand", 100)
      .put("grass_block", 100)
      .put("gravel", 150)
      .put("clay", 200)
      .put("soul_sand", 250)
      .put("podzol", 325)
      .put("red_sand", 400)
      .put("mycelium", 500)
      .build();

  private static final int REQUIRED_SAND = 640;
  private static final int MAX_SAND = 1280;
  private static final double METER_FILLED = 225;

  private double metalMeter = 0;

  private static SkillAbility VITALIC_BREATHING = new SkillAbility("digging", "Vitalic Breathing", 25, "Breathe, breathe in the... dirt?",
      "No longer take suffocation damage from being trapped in walls.");

  private static SkillAbility METAL_DETECTOR = new SkillAbility("digging", "Metal Detector", 50, "Beep....Beep....Beep....",
      "Moving over dirt blocks causes random metal objects to appear!");

  private static SkillAbility ENTOMB = new SkillAbility("digging", "Entomb", 75, "Fuck Priest.",
      "Right clicking an entity with a shovel buries it in the earth.");

  private static SkillAbility CASTLE = new SkillAbility("digging", "Castles Made of Sand", 100, "Slips into the sea.  Eventually.",
      "Right cliking with a shovel creates a desert temple.");

  public SkillDigging() {
    this(1, 0);
  }

  public SkillDigging(int level, int currentXp) {
    super("Digging", level, currentXp);
    this.addAbilities(VITALIC_BREATHING, METAL_DETECTOR, ENTOMB, CASTLE);
  }

  private static int getXp(String blockName) {
    return XP_MAP.getOrDefault(blockName, 0);
  }

  private double getSpeedBonus() {
    return 0.008 * this.level;
  }

  private double getTreasureChance() {
    return 0.0008 * this.level;
  }

  @Override
  public List<String> getToolTip() {
    List<String> tooltip = new ArrayList<>();
    if (this.skillEnabled) {
      tooltip.add("§a+" + Utils.formatPercent(this.getSpeedBonus()) + "%§r digging speed bonus.");
      tooltip.add("§a" + Utils.formatPercent(this.getTreasureChance()) + "%§r chance to find treasure.");
    } else {
      tooltip.add(Skill.COLOR_DISABLED + "+" + Utils.formatPercent(this.getSpeedBonus()) + "% digging speed bonus.");
      tooltip.add(Skill.COLOR_DISABLED + "" + Utils.formatPercent(this.getTreasureChance()) + "% chance to find treasure.");
    }
    return tooltip;
  }

  private static boolean validSpeedTarget(IBlockState state) {
    Block block = state.getBlock();
    String harvestTool = block.getHarvestTool(state);
    return harvestTool != null && harvestTool.toLowerCase().equals("shovel") && validTreasureTarget(state);
  }

  private static boolean validTreasureTarget(IBlockState state) {
    Block block = state.getBlock();
    return (block instanceof BlockDirt || block instanceof BlockGrass || block instanceof BlockSand || block instanceof BlockGravel
        || block instanceof BlockMycelium || block instanceof BlockSoulSand);
  }

  public static String getDirtName(IBlockState state) {
    Block block = state.getBlock();
    if (block instanceof BlockDirt) {
      return state.getValue(BlockDirt.VARIANT).toString();
    } else if (block instanceof BlockSand) {
      return state.getValue(BlockSand.VARIANT).toString();
    } else {
      return Utils.getBlockName(block);
    }
  }

  public static void addDiggingXp(BlockEvent.BreakEvent event) {
    EntityPlayer player = event.getPlayer();
    if (player.world.isRemote) return;

    int addXp = getXp(getDirtName(event.getState()));

    if (addXp > 0) {
      SkillDigging digging = Skills.getSkill(player, Skills.DIGGING, SkillDigging.class);
      digging.addXp((EntityPlayerMP) player, addXp);
    }
  }

  public static void digFaster(PlayerEvent.BreakSpeed event) {
    EntityPlayer player = event.getEntityPlayer();
    if (!validSpeedTarget(event.getState())) return;

    SkillDigging digging = Skills.getSkill(player, Skills.DIGGING, SkillDigging.class);
    if (!digging.skillEnabled) return;

    event.setNewSpeed((float) (event.getOriginalSpeed() * (1 + digging.getSpeedBonus())));
  }

  public static void findTreasure(BlockEvent.HarvestDropsEvent event) {
    EntityPlayer player = event.getHarvester();

    if (player == null || player.world.isRemote) return;
    if (!PlayerPlacedBlocks.isNaturalBlock(player.world, event.getPos())) return;
    if (!(player.getHeldItemMainhand().getItem() instanceof ItemSpade)) return;
    if (!validTreasureTarget(event.getState())) return;

    SkillDigging digging = Skills.getSkill(player, Skills.DIGGING, SkillDigging.class);
    if (!digging.skillEnabled) return;
    if (Math.random() >= digging.getTreasureChance()) return;

    ItemStack treasure = CustomLootTables.getRandomTreasure(event.getWorld(), player, digging.level);
    List<ItemStack> drops = event.getDrops();
    drops.add(treasure);
    Skills.playRandomTreasureSound(player);
  }

  public static void vitalicBreathing(LivingHurtEvent event) {
    if (event.getSource() != DamageSource.IN_WALL) return;

    Entity entity = event.getEntity();
    if (!(entity instanceof EntityPlayer)) return;

    EntityPlayer player = (EntityPlayer) entity;
    if (player.world.isRemote) return;

    SkillDigging digging = Skills.getSkill(player, Skills.DIGGING, SkillDigging.class);
    if (!digging.activeAbility(1)) return;

    event.setAmount(0f);
    event.setCanceled(true);
  }

  /**
   * Generate metal shit every now and then.
   */
  public static void metalDetector(LivingUpdateEvent event) {
    Entity entity = event.getEntity();
    if (!(entity instanceof EntityPlayer)) return;

    final EntityPlayer player = (EntityPlayer) entity;
    if (!player.world.isRemote) return;

    SkillDigging digging = Skills.getSkill(player, Skills.DIGGING, SkillDigging.class);
    if (!digging.activeAbility(2)) return;

    BlockPos playerLocation = new BlockPos(player.posX, player.posY, player.posZ);
    IBlockState onState = player.world.getBlockState(playerLocation.add(0, -1, 0));
    if (!validTreasureTarget(onState)) return;

    digging.metalMeter += Math.sqrt(player.motionX * player.motionX + player.motionZ * player.motionZ);
    if (digging.metalMeter >= SkillDigging.METER_FILLED) {
      digging.metalMeter = 0;
      SkrimPacketHandler.INSTANCE.sendToServer(new MetalDetectorPacket(player.posX, player.posY, player.posZ));
    }
  }

  /**
   * Right clicking an entity with a shovel buries them in the earth.
   */
  public static void entomb(PlayerInteractEvent.EntityInteract event) {
    EntityPlayer player = event.getEntityPlayer();
    Entity targetEntity = event.getTarget();
    if (targetEntity instanceof EntityGhast || targetEntity instanceof EntityBlaze || targetEntity instanceof EntityDragon || targetEntity instanceof EntityWither)
      return;
    SkillDigging digging = Skills.getSkill(player, Skills.DIGGING, SkillDigging.class);
    if (!digging.activeAbility(3)) return;

    ItemStack mainStack = player.getHeldItemMainhand();
    if (!(mainStack.getItem() instanceof ItemSpade)) return;

    targetEntity.setPosition(targetEntity.posX, Math.max(targetEntity.posY - 5, 5), targetEntity.posZ);
    mainStack.damageItem(10, player);
  }


  /**
   * Generate a desert temple using a diamond shovel and a lot of sand.
   */
  public static void castles(PlayerInteractEvent.RightClickBlock event) {
    EntityPlayer player = event.getEntityPlayer();
    if (player.world.isRemote) return;

    SkillDigging digging = Skills.getSkill(player, Skills.DIGGING, SkillDigging.class);
    if (!digging.activeAbility(4)) return;

    BlockPos pos = event.getPos();
    Biome biome = player.world.getBiomeForCoordsBody(pos);
    if (biome != Biomes.DESERT && biome != Biomes.DESERT_HILLS) return;

    IBlockState state = player.world.getBlockState(pos);
    if (state.getBlock() != Blocks.SAND) return;

    ItemStack heldStack = player.getHeldItemMainhand();
    if (heldStack.getItem() != Items.DIAMOND_SHOVEL) return;

    int totalSand = 0;
    for (ItemStack stack : player.inventory.mainInventory) {
      if (stack == null) continue;

      if (stack.getItem() == Item.getItemFromBlock(Blocks.SAND)) {
        totalSand += stack.getCount();
      }
    }

    if (totalSand < REQUIRED_SAND) return;

    ChunkPos chunkPos = new ChunkPos(event.getPos());
    // All temples are generated by MapGenScatteredFeature
    StructureStart start = new SkrimGenScatteredFeature.Start(player.world, Utils.rand, pos.getX() >> 4, pos.getZ() >> 4);
    int x = (chunkPos.x << 4) + 8;
    int y = event.getPos().getY();
    int z = (chunkPos.z << 4) + 8;

    double mult = totalSand / (double) REQUIRED_SAND;
    int horizontalMod = (int) (6 * mult);
    int usedSand = Math.min(MAX_SAND, totalSand);

    StructureBoundingBox bound = new StructureBoundingBox(x - horizontalMod, y - 15, z - horizontalMod, x + horizontalMod
        , y + 12, z + horizontalMod);

    new StructureBoundingBox();
    start.generateStructure(player.world, Utils.rand, bound);
    start.notifyPostProcessAt(chunkPos);
    if (!player.capabilities.isCreativeMode) {
      int paidSand = 0;
      for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
        ItemStack stack = player.inventory.getStackInSlot(i);
        if (stack.getItem() == Item.getItemFromBlock(Blocks.SAND)) {
          int remove = Math.min(stack.getCount(), (usedSand - paidSand));
          if (remove == stack.getCount()) {
            player.inventory.removeStackFromSlot(i);
          } else {
            player.inventory.decrStackSize(i, remove);
          }
          paidSand += remove;
        }
      }
      for (int i = 0; i < player.inventory.mainInventory.size(); i++) {
        ItemStack stack = player.inventory.mainInventory.get(i);
        if (stack.getItem() == Items.DIAMOND_SHOVEL) {
          player.inventory.removeStackFromSlot(i);
          return;
        }
      }
    }
  }

}

