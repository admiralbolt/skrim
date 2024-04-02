package avi.mod.skrim.skills.botany;

import avi.mod.skrim.advancements.SkrimAdvancements;
import avi.mod.skrim.blocks.flowers.GlowFlower;
import avi.mod.skrim.network.SkrimPacketHandler;
import avi.mod.skrim.network.SpawnParticlePacket;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillAbility;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.utils.Obfuscation;
import avi.mod.skrim.utils.Utils;
import avi.mod.skrim.world.PlayerPlacedBlocks;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class SkillBotany extends Skill implements ISkillBotany {

  private static Map<UUID, List<Pair<Integer, Integer>>> VILLAGER_TRADES = new HashMap<>();

  public static SkillStorage<ISkillBotany> skillStorage = new SkillStorage<>();
  public static Set<Item> GLOW_FLOWER_ITEMS = new HashSet<>();
  public static Set<Item> ENCHANTED_FLOWER_ITEMS = new HashSet<>();
  private static Set<String> DOUBLE_FLOWER_NAMES = ImmutableSet.of("double_rose", "sunflower", "syringa", "paeonia");

  // The chart for flower rarity is at: http://minecraft.gamepedia.com/Flower
  private static Map<String, Integer> XP_MAP = ImmutableMap.<String, Integer>builder()
      .put("dandelion", 300)
      .put("poppy", 300)
      // 3 Biomes
      .put("houstonia", 600) // azure_bluet
      .put("red_tulip", 600)
      .put("orange_tulip", 600)
      .put("white_tulip", 600)
      .put("pink_tulip", 600)
      .put("oxeye_daisy", 600)
      // Only swamp, can respawn
      .put("blue_orchid", 1200)
      .put("allium", 1200)
      // Only forest & flower forest on generation
      .put("syringa", 3000) // lilac
      .put("double_rose", 3000)
      .put("paeonia", 3000) // peony
      // Only sunflower plains on generation
      .put("sunflower", 4000)
      // A hack to make inspiration flowers work.
      .put("tile.inspirations.flower", 50)
      .build();

  private static SkillAbility SUN_FLOWER = new SkillAbility("botany", "Sun Flower", 25, "It was either this or " +
      "mariglow, don't know which one is worse.",
      "Enables you to craft glowing flowers with a flower & glowstone dust.");
  private static SkillAbility THORN_STYLE = new SkillAbility("botany", "Thorn Style", 50, "I'll let you try my thorn " +
      "style.",
      "While holding a flower return §a25%" + SkillAbility.DESC_COLOR + " of melee damage.");
  private static SkillAbility SEDUCE_VILLAGER = new SkillAbility("botany", "Seduce Villager", 75, "[Tongue waggling " +
      "intensifies]",
      "Using a flower on a villager consumes it and reduces the cost of all trades by §a1" + SkillAbility.DESC_COLOR + ".");
  private static SkillAbility ENCHANTED_FLOWER = new SkillAbility("botany", "Enchanted Flower", 100, "It shares a " +
      "giant friendliness beam! :D",
      "Enables you to craft enchanted flowers that function like speed beacons.");


  public SkillBotany() {
    this(1, 0);
  }

  public SkillBotany(int level, int currentXp) {
    super("Botany", level, currentXp);
    this.addAbilities(SUN_FLOWER, THORN_STYLE, SEDUCE_VILLAGER, ENCHANTED_FLOWER);
  }

  @Override
  public List<String> getToolTip() {
    List<String> tooltip = new ArrayList<String>();
    if (this.skillEnabled) {
      tooltip.add("§a" + Utils.formatPercent(this.getFortuneChance()) + "%§r chance to §a" + Utils.getFortuneString(this.getFortuneAmount())
          + "§r flower drops.");
      tooltip.add("§a" + Utils.formatPercent(this.getSplosionChance()) + "%§r chance to cause a flowersplosion with " +
          "radius §a" + this.getSplosionRadius()
          + "§r.");
    } else {
      tooltip.add(Skill.COLOR_DISABLED + Utils.formatPercent(this.getFortuneChance()) + "% chance to " + Utils.getFortuneString(this.getFortuneAmount())
          + " flower drops.");
      tooltip.add(Skill.COLOR_DISABLED + Utils.formatPercent(this.getSplosionChance()) + "% chance to cause a flowersplosion with " +
          "radius " + this.getSplosionRadius() + ".");
    }
    return tooltip;
  }

  @Override
  public void ding(EntityPlayerMP player) {
    super.ding(player);
    if (this.level >= 25) {
      SkrimAdvancements.GLOW_FLOWER.grant(player);
      if (this.level >= 100) {
        SkrimAdvancements.ENCHANTED_FLOWER.grant(player);
      }
    }
  }

  private static boolean validFlowerStack(ItemStack stack) {
    if (stack == null) return false;

    Item item = stack.getItem();
    Block block = Block.getBlockFromItem(stack.getItem());
    String name = Utils.snakeCase(item.getItemStackDisplayName(stack));
    return XP_MAP.containsKey(name) || DOUBLE_FLOWER_NAMES.contains(name) || validFlowerBlock(block);

  }

  private static String getFlowerName(IBlockState state) {
    Block block = state.getBlock();
    if (block instanceof BlockFlower) {
      BlockFlower flower = (BlockFlower) block;
      return state.getValue(flower.getTypeProperty()).toString();
    } else if (block instanceof BlockDoublePlant) {
      return BlockDoublePlant.EnumPlantType.byMetadata(block.getMetaFromState(state)).getName();
    } else {
      return block.getUnlocalizedName();
    }
  }

  private static boolean validFlowerBlock(Block block) {
    return (block instanceof BlockFlower || block instanceof GlowFlower);
  }

  private static boolean validFlowerState(IBlockState state) {
    // return XP_MAP.containsKey(getFlowerName(state)) || validFlowerBlock(state.getBlock()) || DOUBLE_FLOWER_NAMES.contains(getFlowerName(state));
    return validFlowerBlock(state.getBlock()) || DOUBLE_FLOWER_NAMES.contains(getFlowerName(state));
  }

  public static void addBotanyXp(BlockEvent.BreakEvent event) {
    EntityPlayer player = event.getPlayer();
    String flowerName = getFlowerName(event.getState());
    SkillBotany botany = Skills.getSkill(player, Skills.BOTANY, SkillBotany.class);
    if (event.getState().getBlock() instanceof BlockDoublePlant) {
      IBlockState targetState = event.getState();
      // So if you break the top block of a double plant, the game thinks it's a sunflower. Also, the top blocks of
      // plants aren't actually responsible for the drops, only the bottom blocks are, so we spawn the correct number
      // of fortune drops here if necessary.
      if (event.getWorld().getBlockState(event.getPos().down()).getBlock() instanceof BlockDoublePlant) {
        targetState = event.getWorld().getBlockState(event.getPos().down());
      }

      flowerName = getFlowerName(targetState);
      if (!XP_MAP.containsKey(flowerName)) return;

      if (Utils.rand.nextDouble() < botany.getFortuneChance()) {
        Item droppedItem = targetState.getBlock().getItemDropped(targetState, Utils.rand, 0);
        int meta = targetState.getBlock().damageDropped(targetState);
        ItemStack flowerStack = new ItemStack(droppedItem, botany.getFortuneAmount() - 1, meta);
        EntityItem entityItem = new EntityItem(event.getWorld(), event.getPos().getX(), event.getPos().getY(),
            event.getPos().getZ(), flowerStack);
        event.getWorld().spawnEntity(entityItem);
        Skills.playFortuneSound(player);
      }
    }

    int addXp = botany.getXp(flowerName);
    if (addXp > 0) {
      botany.addXp((EntityPlayerMP) player, addXp);
    }

  }

  public static void soManyFlowers(BlockEvent.HarvestDropsEvent event) {
    IBlockState state = event.getState();
    if (!validFlowerState(state)) return;

    EntityPlayer player = event.getHarvester();
    if (player == null) return;

    SkillBotany botany = Skills.getSkill(player, Skills.BOTANY, SkillBotany.class);
    if (!botany.skillEnabled) return;

    double random = Utils.rand.nextDouble();
    if (random >= botany.getFortuneChance()) return;

    List<ItemStack> drops = event.getDrops();
    drops.add(new ItemStack(drops.get(0).getItem(), botany.getFortuneAmount() - 1, drops.get(0).getMetadata()));

    Skills.playFortuneSound(player);
  }

  public static void flowerSplosion(BlockEvent.PlaceEvent event) {
    EntityPlayer player = event.getPlayer();
    SkillBotany botany = Skills.getSkill(player, Skills.BOTANY, SkillBotany.class);
    if (!botany.skillEnabled) return;

    IBlockState placedState = event.getPlacedBlock();
    Block placedBlock = placedState.getBlock();
    BlockDoublePlant doublePlant = null;
    if (placedBlock instanceof BlockDoublePlant) {
      doublePlant = (BlockDoublePlant) placedBlock;
    }
    if (!validFlowerState(placedState) || Utils.rand.nextDouble() >= botany.getSplosionChance()) return;

    BlockPos placedPos = event.getPos();
    int radius = botany.getSplosionRadius();
    for (int i = -radius; i <= radius; i++) {
      for (int j = -radius; j <= radius; j++) {
        if (i == 0 && j == 0) continue;
        BlockPos airPos = new BlockPos(placedPos.getX() + i, placedPos.getY(), placedPos.getZ() + j);
        IBlockState airState = player.world.getBlockState(airPos);
        if (!airState.getBlock().isAir(airState, player.world, airPos)) continue;

        BlockPos dirtPos = airPos.down();
        Block dirtBlock = player.world.getBlockState(dirtPos).getBlock();
        if (!(dirtBlock instanceof BlockDirt) && !(dirtBlock instanceof BlockGrass) && !(dirtBlock instanceof BlockFarmland)) continue;

        if (placedBlock instanceof BlockDoublePlant) {
          doublePlant.placeAt(player.world, airPos, placedState.getValue(BlockDoublePlant.VARIANT), 3);
          PlayerPlacedBlocks.addBlock(player.world, airPos);
          PlayerPlacedBlocks.addBlock(player.world, airPos.up());
        } else {
          player.world.setBlockState(airPos, placedState);
          PlayerPlacedBlocks.addBlock(player.world, airPos);
        }
      }
    }
  }

  public static void thornStyle(LivingHurtEvent event) {
    Entity entity = event.getEntity();
    if (!(entity instanceof EntityPlayer)) return;

    EntityPlayer player = (EntityPlayer) entity;
    SkillBotany botany = Skills.getSkill(player, Skills.BOTANY, SkillBotany.class);
    if (!botany.activeAbility(2)) return;
    if (!validFlowerStack(player.getHeldItemMainhand()) && !validFlowerStack(player.getHeldItemOffhand())) return;

    DamageSource source = event.getSource();
    if (!source.damageType.equals("mob") && !source.damageType.equals("player")) return;

    Entity target = source.getTrueSource();
    if (target == null) return;

    target.attackEntityFrom(DamageSource.MAGIC, (float) (event.getAmount() * 0.25));
  }

  public static void seduceVillager(PlayerInteractEvent.EntityInteract event) {
    EntityPlayer player = event.getEntityPlayer();
    if (player.world.isRemote) return;


    Entity targetEntity = event.getTarget();
    if (!(targetEntity instanceof EntityVillager)) return;

    EntityVillager villager = (EntityVillager) targetEntity;
    SkillBotany botany = Skills.getSkill(player, Skills.BOTANY, SkillBotany.class);
    if (!botany.activeAbility(3)) return;

    ItemStack mainStack = player.getHeldItemMainhand();
    if (!validFlowerStack(mainStack)) return;

    // Lol, why am I setting this?
    villager.setIsWillingToMate(true);
    MerchantRecipeList buyingList = (MerchantRecipeList) Obfuscation.VILLAGER_BUY_LIST.getValue(villager);

    if (buyingList.size() > 0) {
      UUID villagerId = villager.getUniqueID();

      // The original count of the items for trading. We maintain what the originals were before seducing them, and limit the amount to
      // half of the original.
      List<Pair<Integer, Integer>> baseCounts = new ArrayList<>();
      if (!VILLAGER_TRADES.containsKey(villagerId)) {
        for (MerchantRecipe recipe : buyingList) {
          Pair<Integer, Integer> buyingAmount = Pair.of(recipe.getItemToBuy().getCount(),
              recipe.getSecondItemToBuy().getCount());
          baseCounts.add(buyingAmount);
        }
        VILLAGER_TRADES.put(villagerId, baseCounts);
      } else {
        baseCounts = VILLAGER_TRADES.get(villagerId);
      }

      // Really I want to zip the buyingList & baseCounts collections, but this ain't python dorthy.
      int i = 0;
      for (MerchantRecipe recipe : buyingList) {
        Pair<Integer, Integer> baseCount = baseCounts.get(i);
        ItemStack first = recipe.getItemToBuy();
        first.setCount(Math.max(Math.round(baseCount.getLeft() / 2f), first.getCount() - 1));
        if (recipe.hasSecondItemToBuy()) {
          ItemStack second = recipe.getSecondItemToBuy();
          second.setCount(Math.max(baseCount.getRight() / 2, second.getCount() - 1));
        }
        ++i;
      }


      SkrimPacketHandler.INSTANCE.sendTo(
          new SpawnParticlePacket("HEART", villager.posX, villager.posY, villager.posZ, villager.height,
              villager.width),
          (EntityPlayerMP) player);

      mainStack.setCount(mainStack.getCount() - 1);
      if (mainStack.getCount() == 0) {
        player.inventory.deleteStack(mainStack);
      }
      event.setCanceled(true);
    }
  }

  public static void verifyFlowers(ItemCraftedEvent event) {
    Item targetItem = event.crafting.getItem();

    if (GLOW_FLOWER_ITEMS.contains(targetItem)) {
      if (!Skills.canCraft(event.player, Skills.BOTANY, 25)) {
        Skills.replaceWithComponents(event);
      }
    } else if (ENCHANTED_FLOWER_ITEMS.contains(targetItem)) {
      if (!Skills.canCraft(event.player, Skills.BOTANY, 100)) {
        Skills.replaceWithComponents(event);
      }
    }
  }



  public int getXp(String blockName) {
    return XP_MAP.getOrDefault(blockName, 0);
  }

  private double getSplosionChance() {
    return this.level * 0.01;
  }

  private int getSplosionRadius() {
    return (this.level / 25) + 1;
  }

  private double getFortuneChance() {
    return 0.01 * this.level;
  }

  private int getFortuneAmount() {
    return (int) (((double) this.level) / 12) + 2;
  }

}
