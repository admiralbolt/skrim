package avi.mod.skrim.skills.botany;

import avi.mod.skrim.blocks.SkrimBlocks;
import avi.mod.skrim.blocks.flowers.GlowFlower;
import avi.mod.skrim.network.SkrimPacketHandler;
import avi.mod.skrim.network.SpawnParticlePacket;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillAbility;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.utils.Obfuscation;
import avi.mod.skrim.utils.ReflectionUtils;
import avi.mod.skrim.utils.Utils;
import avi.mod.skrim.world.PlayerPlacedBlocks;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

import java.util.*;

public class SkillBotany extends Skill implements ISkillBotany {

  public static SkillStorage<ISkillBotany> skillStorage = new SkillStorage<>();

  private static Map<String, Integer> XP_MAP;
  public static Set<Item> GLOW_FLOWER_ITEMS;
  public static Set<Item> ENCHANTED_FLOWER_ITEMS;

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

  static {
    XP_MAP = new HashMap<>();
    // The chart for flower rarity is at: http://minecraft.gamepedia.com/Flower
    XP_MAP.put("dandelion", 300);
    XP_MAP.put("poppy", 300);
    // 3 Biomes
    XP_MAP.put("houstonia", 600); // azure_bluet
    XP_MAP.put("red_tulip", 600);
    XP_MAP.put("orange_tulip", 600);
    XP_MAP.put("white_tulip", 600);
    XP_MAP.put("pink_tulip", 600);
    XP_MAP.put("oxeye_daisy", 600);
    // Only swamp, can respawn
    XP_MAP.put("blue_orchid", 1200);
    XP_MAP.put("allium", 1200);
    // Only forest & flower forest on generation
    XP_MAP.put("syringa", 3000); // lilac
    XP_MAP.put("double_rose", 3000);
    XP_MAP.put("paeonia", 3000); // peony
    // Only sunflower plains on generation
    XP_MAP.put("sunflower", 5000);
  }

  public SkillBotany() {
    this(1, 0);
  }

  public SkillBotany(int level, int currentXp) {
    super("Botany", level, currentXp);
    this.addAbilities(SUN_FLOWER, THORN_STYLE, SEDUCE_VILLAGER, ENCHANTED_FLOWER);
  }

  private static boolean validFlowerStack(ItemStack stack) {
    if (stack != null) {
      Item item = stack.getItem();
      Block block = Block.getBlockFromItem(stack.getItem());
      String name = Utils.snakeCase(item.getItemStackDisplayName(stack));
      return (XP_MAP.containsKey(name) || name.equals("azure_bluet") || name.equals("lilac") || name.equals("peony"))
          || validFlowerBlock(block);
    }
    return false;
  }

  private static String getFlowerName(IBlockState state) {
    Block block = state.getBlock();
    if (block instanceof BlockFlower) {
      BlockFlower flower = (BlockFlower) block;
      return state.getValue(flower.getTypeProperty()).toString();
    } else if (block instanceof BlockDoublePlant) {
      return BlockDoublePlant.EnumPlantType.byMetadata(block.getMetaFromState(state)).getName();
    } else {
      return "";
    }
  }

  private static boolean validFlowerBlock(Block block) {
    return (block instanceof BlockFlower || block instanceof GlowFlower);
  }

  private static boolean validFlowerState(IBlockState state) {
    Block flower = state.getBlock();
    String name = getFlowerName(state);
    return validFlowerBlock(flower) || (name.equals("sunflower") || name.equals("paeonia") || name.equals(
        "double_rose") || name.equals("syringa"));
  }

  public static void addBotanyXp(BlockEvent.BreakEvent event) {
    EntityPlayer player = event.getPlayer();
    SkillBotany botany = Skills.getSkill(player, Skills.BOTANY, SkillBotany.class);
    IBlockState state = event.getState();
    String flowerName = getFlowerName(state);
    System.out.println("flowerName: " + flowerName);
    int addXp = botany.getXp(flowerName);
    if (addXp > 0) {
      botany.addXp((EntityPlayerMP) player, botany.getXp(getFlowerName(state)));
    }

  }

  public static void soManyFlowers(BlockEvent.HarvestDropsEvent event) {
    IBlockState state = event.getState();
    EntityPlayer player = event.getHarvester();
    if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.BOTANY, EnumFacing.NORTH)) {
      SkillBotany botany = (SkillBotany) player.getCapability(Skills.BOTANY, EnumFacing.NORTH);
      Utils.logSkillEvent(event, botany,
          "state: " + state + ", NAME: " + SkillBotany.getFlowerName(state) + ", valid: " + SkillBotany.validFlowerState(state));
      if (validFlowerState(state)) {
        Block block = state.getBlock();
        double random = Utils.rand.nextDouble();
        if (random < botany.getFortuneChance()) {
          List<ItemStack> drops = event.getDrops();
          // Let's not loop infinitely, that seems like a good idea.
          int dropSize = drops.size();
          for (int j = 0; j < botany.getFortuneAmount() - 1; j++) {
            for (int i = 0; i < dropSize; i++) {
              drops.add(drops.get(i).copy());
            }
          }
          Skills.playFortuneSound(player);
          botany.addXp((EntityPlayerMP) player, 200); // and 200 xp!
        }
      }
    }
  }

  public static void flowerSplosion(BlockEvent.PlaceEvent event) {
    EntityPlayer player = event.getPlayer();
    if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.BOTANY, EnumFacing.NORTH)) {
      SkillBotany botany = (SkillBotany) player.getCapability(Skills.BOTANY, EnumFacing.NORTH);
      IBlockState placedState = event.getPlacedBlock();
      Block placedBlock = placedState.getBlock();
      BlockDoublePlant doublePlant = null;
      if (placedBlock instanceof BlockDoublePlant) {
        doublePlant = (BlockDoublePlant) placedBlock;
      }
      if (validFlowerState(placedState)) {
        if (Utils.rand.nextDouble() < botany.getSplosionChance()) {
          botany.addXp((EntityPlayerMP) player, 100);
          BlockPos placedPos = event.getPos();
          int radius = botany.getSplosionRadius();
          for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
              if (i != 0 || j != 0) {
                BlockPos airPos = new BlockPos(placedPos.getX() + i, placedPos.getY(), placedPos.getZ() + j);
                IBlockState airState = player.world.getBlockState(airPos);
                if (airState != null) {
                  Block airBlock = airState.getBlock();
                  if (airBlock.isAir(airState, player.world, airPos)) {
                    BlockPos dirtPos = new BlockPos(airPos.getX(), airPos.getY() - 1, airPos.getZ());
                    IBlockState dirtState = player.world.getBlockState(dirtPos);
                    if (dirtState != null) {
                      Block dirtBlock = dirtState.getBlock();
                      if (dirtBlock instanceof BlockDirt || dirtBlock instanceof BlockGrass || dirtBlock instanceof BlockFarmland) {
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
                }
              }
            }
          }
        }
      }
    }
  }

  public static void thornStyle(LivingHurtEvent event) {
    Entity entity = event.getEntity();
    if (entity instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) entity;
      if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.BOTANY, EnumFacing.NORTH)) {
        SkillBotany botany = (SkillBotany) player.getCapability(Skills.BOTANY, EnumFacing.NORTH);
        if (botany.hasAbility(2)) {
          ItemStack mainStack = player.getHeldItemMainhand();
          ItemStack offStack = player.getHeldItemOffhand();
          if (validFlowerStack(mainStack) || validFlowerStack(offStack)) {
            DamageSource source = event.getSource();
            if (source.damageType == "mob" || source.damageType == "player") {
              Entity target = source.getTrueSource();
              target.attackEntityFrom(DamageSource.MAGIC, (float) (event.getAmount() * 0.25));
              Utils.logSkillEvent(event, botany, "I'll give you a taste of my thorn style!");
            }
          }
        }
      }
    }
  }

  public static void seduceVillager(PlayerInteractEvent.EntityInteract event) {
    EntityPlayer player = event.getEntityPlayer();
    Entity targetEntity = event.getTarget();
    if (targetEntity instanceof EntityVillager) {
      EntityVillager villager = (EntityVillager) targetEntity;
      if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.BOTANY, EnumFacing.NORTH)) {
        SkillBotany botany = (SkillBotany) player.getCapability(Skills.BOTANY, EnumFacing.NORTH);
        if (botany.hasAbility(3)) {
          ItemStack mainStack = player.getHeldItemMainhand();
          if (validFlowerStack(mainStack)) {
            villager.setIsWillingToMate(true);
            MerchantRecipeList buyingList = (MerchantRecipeList) ReflectionUtils.getPrivateField(villager, "buyingList",
                "field_70963_i");
            for (MerchantRecipe recipe : buyingList) {
              ItemStack first = recipe.getItemToBuy();
              if (Obfuscation.getStackSize(first) > 4) {
                Obfuscation.setStackSize(first, Obfuscation.getStackSize(first) - 1);
              }
              if (recipe.hasSecondItemToBuy()) {
                ItemStack second = recipe.getSecondItemToBuy();
                if (Obfuscation.getStackSize(second) > 4) {
                  Obfuscation.setStackSize(second, Obfuscation.getStackSize(second) - 1);
                }
              }
            }
            SkrimPacketHandler.INSTANCE.sendTo(
                new SpawnParticlePacket("HEART", villager.posX, villager.posY, villager.posZ, villager.height,
                    villager.width),
                (EntityPlayerMP) player);
            Obfuscation.setStackSize(mainStack, Obfuscation.getStackSize(mainStack) - 1);
            if (Obfuscation.getStackSize(mainStack) == 0) {
              player.inventory.deleteStack(mainStack);
              botany.addXp((EntityPlayerMP) player, 100);
            }
            event.setCanceled(true);
            Utils.logSkillEvent(event, botany, "Seducing villager intiated...");
          }
        }
      }
    }
  }

  public static void verifyFlowers(ItemCraftedEvent event) {
    Item targetItem = event.crafting.getItem();
    Item glowFlowerPoppy = new ItemStack(SkrimBlocks.GLOW_FLOWER_POPPY).getItem();
    if (targetItem == glowFlowerPoppy) {
      if (!Skills.canCraft(event.player, Skills.BOTANY, 25)) {
        Skills.replaceWithComponents(event);
      } else if (!event.player.world.isRemote && event.player.hasCapability(Skills.BOTANY, EnumFacing.NORTH)) {
        SkillBotany botany = (SkillBotany) event.player.getCapability(Skills.BOTANY, EnumFacing.NORTH);
        botany.addXp((EntityPlayerMP) event.player, 500);
      }
    }
//    else if (targetItem != null && targetItem instanceof EnchantedFlowerVariants) {
//      if (!Skills.canCraft(event.player, Skills.BOTANY, 100)) {
//        Skills.replaceWithComponents(event);
//      } else if (!event.player.world.isRemote && event.player.hasCapability(Skills.BOTANY, EnumFacing.NORTH)) {
//        SkillBotany botany = (SkillBotany) event.player.getCapability(Skills.BOTANY, EnumFacing.NORTH);
//        botany.addXp((EntityPlayerMP) event.player, 10000);
//      }
//    }
  }

  public int getXp(String blockName) {
    return (XP_MAP.containsKey(blockName)) ? XP_MAP.get(blockName) : 0;
  }

  public double getSplosionChance() {
    return this.level * 0.01;
  }

  public int getSplosionRadius() {
    return (this.level / 25) + 1;
  }

  public double getFortuneChance() {
    return 0.01 * this.level;
  }

  public int getFortuneAmount() {
    return (int) (((double) this.level) / 12) + 2;
  }

  @Override
  public List<String> getToolTip() {
    List<String> tooltip = new ArrayList<String>();
    tooltip.add("§a" + Utils.formatPercent(this.getFortuneChance()) + "%§r chance to §a" + Utils.getFortuneString(this.getFortuneAmount())
        + "§r flower drops.");
    tooltip.add("§a" + Utils.formatPercent(this.getSplosionChance()) + "%§r chance to cause a flowersplosion with " +
        "radius §a" + this.getSplosionRadius()
        + "§r.");
    return tooltip;
  }

}
