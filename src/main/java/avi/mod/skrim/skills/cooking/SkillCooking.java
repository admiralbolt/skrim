package avi.mod.skrim.skills.cooking;

import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.network.SkrimPacketHandler;
import avi.mod.skrim.network.skillpackets.AngelFlyingSoundPacket;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillAbility;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.utils.ReflectionUtils;
import avi.mod.skrim.utils.Utils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemSmeltedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkillCooking extends Skill implements ISkillCooking {

  public static SkillStorage<ISkillCooking> skillStorage = new SkillStorage<>();

  private static Map<String, Integer> XP_MAP;
  private static Map<String, Item> FOOD_MAP;
  private static Map<Class, String> ENTITY_FOOD_MAP;


  private static final double FIRE_COOKED_XP_MULT = 0.25;
  private static final int ANGEL_DURATION = 600;

  public boolean hasAngel = false;
  public boolean startFlyingSound = true;

  public int currentTicks = 0;

  private static void addFood(String name, Item food, int xp) {
    FOOD_MAP.put(name, food);
    XP_MAP.put(name, xp);
  }

  static {
    FOOD_MAP = new HashMap<>();
    XP_MAP = new HashMap<>();
    ENTITY_FOOD_MAP = new HashMap<>();
    addFood("bread", SkrimItems.OVERWRITE_BREAD, 200);
    addFood("cookie", SkrimItems.OVERWRITE_COOKIE, 25);

    addFood("potatobaked", SkrimItems.OVERWRITE_BAKED_POTATO, 200);

    addFood("beetroot_soup", SkrimItems.OVERWRITE_BEETROOT_SOUP, 250);
    addFood("mushroomstew", SkrimItems.OVERWRITE_MUSHROOM_STEW, 250);

    addFood("muttoncooked", SkrimItems.OVERWRITE_MUTTON, 500);
    ENTITY_FOOD_MAP.put(EntitySheep.class, "muttoncooked");
    addFood("beefcooked", SkrimItems.OVERWRITE_STEAK, 500);
    ENTITY_FOOD_MAP.put(EntityCow.class, "beefcooked");
    addFood("porkchopcooked", SkrimItems.OVERWRITE_PORKCHOP, 500);
    ENTITY_FOOD_MAP.put(EntityPig.class, "porkchopcooked");
    addFood("chickencooked", SkrimItems.OVERWRITE_CHICKEN, 500);
    ENTITY_FOOD_MAP.put(EntityChicken.class, "chickencooked");

    addFood("cooked_fish", SkrimItems.OVERWRITE_FISH, 800);
    addFood("pumpkinpie", SkrimItems.OVERWRITE_PUMPKIN_STEW, 1500);
    addFood("cooked_salmon", SkrimItems.OVERWRITE_SALMON, 1000);

    addFood("rabbitcooked", SkrimItems.OVERWRITE_RABBIT, 2500);
    ENTITY_FOOD_MAP.put(EntityRabbit.class, "rabbitcooked");
    addFood("rabbitstew", SkrimItems.OVERWRITE_RABBIT_STEW, 3000);

    addFood("item.cake", SkrimItems.SKRIM_CAKE, 7500);
    addFood("angel_cake", SkrimItems.ANGEL_CAKE, 10000);

  }

  private static SkillAbility OVERFULL = new SkillAbility("cooking", "Overfull", 25, "Just keep eating, just keep eating, just keep " +
      "eating" +
      "...",
      "Your cooked food now ignores food and saturation limits.");
  private static SkillAbility PANACEA = new SkillAbility("cooking", "Panacea", 50, "Cures everything that's less than half dead.",
      "Your cooked food now removes nausea, hunger, and poison.");
  private static SkillAbility SUPER_FOOD = new SkillAbility("cooking", "Super Food", 75, "You won't believe how good these 11 foods are " +
      "for you!",
      "Your cooked food now grants a speed boost and a short period of regeneration.");
  private static SkillAbility ANGEL_CAKE = new SkillAbility("cooking", "Angel Cake", 100, "I believe I can fly.",
      "Gain the ability to craft angel cake, which grants 30 seconds of flight.");

  public SkillCooking() {
    this(1, 0);
  }

  public SkillCooking(int level, int currentXp) {
    super("Cooking", level, currentXp);
    this.addAbilities(OVERFULL, PANACEA, SUPER_FOOD, ANGEL_CAKE);
  }

  public static int getXp(String foodName) {
    return XP_MAP.getOrDefault(foodName, 0);
  }

  /**
   * These methods are static so they can be accessed from the CustomFood
   * class onFoodEaten() method.
   */

  public static double extraFood(int level) {
    return 0.01 * level;
  }

  public static double extraSaturation(int level) {
    return 0.005 * level;
  }

  @Override
  public List<String> getToolTip() {
    List<String> tooltip = new ArrayList<String>();
    tooltip.add("Your cooking provides §a+" + Utils.formatPercent(extraFood(this.level)) + "%§r food.");
    tooltip.add("Your cooking provides §a+" + Utils.formatPercent(extraSaturation(this.level)) + "%§r saturation");
    tooltip.add("Shift clicking crafted items provides §aregular and modded§r food.");
    tooltip.add("§eWe swear this is a feature and not a bug...§r");
    return tooltip;
  }

  private static String getFoodName(ItemStack stack) {
    Item item = stack.getItem();
    if (item instanceof ItemFishFood) {
      ItemFishFood.FishType type = ItemFishFood.FishType.byItemStack(stack);
      if (type == ItemFishFood.FishType.SALMON) {
        return "cooked_salmon";
      } else if (type == ItemFishFood.FishType.COD) {
        return "cooked_fish";
      } else {
        return null;
      }
    } else if (item instanceof ItemFood) {
      return Utils.snakeCase(item.getUnlocalizedName()).substring(5);
    } else if (item == Items.CAKE) {
      return Utils.snakeCase(item.getUnlocalizedName());
    }
    return null;
  }

  private static void injectFakeFood(PlayerEvent event, ItemStack stack, EntityPlayer player) {
    // injectFakeFood(event, event.smelting, event.player);
    ItemStack newFood = getReplaceFood(event.player, stack);
    if (newFood == null) return;

    if (event.player.inventory.getItemStack().getItem() == Items.AIR) {
      // Player shift-clicked. We'll need to add the newFood to their inventory directly and removed the wrong version.
      event.player.inventory.addItemStackToInventory(newFood);
      Utils.removeFromInventory(event.player.inventory, stack.getItem(), stack.getCount());
    } else {
      ReflectionUtils.hackValueTo(event.player.inventory, newFood, "itemStack");
    }
    SkillCooking cooking = Skills.getSkill(player, Skills.COOKING, SkillCooking.class);
    if (player instanceof EntityPlayerMP) {
      cooking.addXp((EntityPlayerMP) player, getXp(getFoodName(stack)));
    }
  }

  /**
   * Gets the replacement version of an existing minecraft food item. Sets the correct NBT tags based on cooking level.
   */
  private static ItemStack getReplaceFood(EntityPlayer player, ItemStack stack) {
    if (player == null || !Skills.hasSkill(player, Skills.COOKING)) return null;
    SkillCooking cooking = Skills.getSkill(player, Skills.COOKING, SkillCooking.class);
    Item replaceFood = FOOD_MAP.getOrDefault(getFoodName(stack), null);
    if (replaceFood == null) return null;
    NBTTagCompound compound = new NBTTagCompound();
    NBTTagCompound customName = new NBTTagCompound();
    compound.setInteger("level", cooking.level);
    ItemStack newStack = new ItemStack(replaceFood, stack.getCount());
    // Set a custom name based on who cooked it.
    customName.setString("Name", player.getName() + "'s " + newStack.getDisplayName());
    compound.setTag("display", customName);
    newStack.setTagCompound(compound);
    return newStack;
  }

  public static void injectSmeltedFood(ItemSmeltedEvent event) {
    injectFakeFood(event, event.smelting, event.player);
  }

  public static void injectCraftedFood(ItemCraftedEvent event) {
    Item targetItem = event.crafting.getItem();
    if (targetItem != SkrimItems.ANGEL_CAKE) {
      injectFakeFood(event, event.crafting, event.player);
      return;
    }

    if (!Skills.canCraft(event.player, Skills.COOKING, 100)) {
      Skills.replaceWithComponents(event);
      return;
    }

    injectFakeFood(event, event.crafting, event.player);
  }

  public static void fireCook(LivingDeathEvent event) {
    Entity targetEntity = event.getEntity();
    DamageSource source = event.getSource();
    Entity entity = source.getTrueSource();
    if (entity instanceof EntityPlayer) {
      System.out.println("Working as intended");
      EntityPlayer player = (EntityPlayer) entity;
      if (player.hasCapability(Skills.COOKING, EnumFacing.NORTH)) {
        SkillCooking cooking = (SkillCooking) player.getCapability(Skills.COOKING, EnumFacing.NORTH);
        ItemStack mainStack = player.getHeldItemMainhand();
        ItemStack offStack = player.getHeldItemOffhand();
        boolean hasFire = false;
        if (mainStack != null) {
          Map<Enchantment, Integer> mainEnchants = EnchantmentHelper.getEnchantments(mainStack);
          for (Enchantment ench : mainEnchants.keySet()) {
            if (ench == Enchantments.FLAME || ench == Enchantments.FIRE_ASPECT) {
              hasFire = true;
            }
          }
        }
        if (offStack != null) {
          Map<Enchantment, Integer> offEnchants = EnchantmentHelper.getEnchantments(offStack);
          for (Enchantment ench : offEnchants.keySet()) {
            if (ench == Enchantments.FLAME) {
              hasFire = true;
            }
          }
        }
        if (hasFire) {
          if (ENTITY_FOOD_MAP.containsKey(targetEntity.getClass())) {
            int cookingXp = (int) (FIRE_COOKED_XP_MULT * XP_MAP.get(ENTITY_FOOD_MAP.get(targetEntity.getClass())));
            if (cookingXp > 0) {
              cooking.addXp((EntityPlayerMP) player, cookingXp);
            }
          }
        }
      }
    }
  }

  public static void angelUpdate(LivingUpdateEvent event) {
    Entity entity = event.getEntity();
    if (entity instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) entity;
      if (player.hasCapability(Skills.COOKING, EnumFacing.NORTH)) {
        SkillCooking cooking = (SkillCooking) player.getCapability(Skills.COOKING, EnumFacing.NORTH);
        if (cooking.hasAngel && cooking.currentTicks > 0) {
          cooking.currentTicks--;
        } else {
          cooking.hasAngel = false;
          cooking.currentTicks = 0;
          cooking.startFlyingSound = true;
          if (!player.capabilities.isCreativeMode) {
            player.capabilities.allowFlying = false;
            player.capabilities.isFlying = false;
          }
        }
      }
    }
  }

  public static void angelFall(LivingFallEvent event) {
    Entity entity = event.getEntity();
    if (entity instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) entity;
      if (player.hasCapability(Skills.COOKING, EnumFacing.NORTH)) {
        SkillCooking cooking = (SkillCooking) player.getCapability(Skills.COOKING, EnumFacing.NORTH);
        if (cooking.hasAngel) {
          event.setDistance(0);
          event.setCanceled(true);
        }
      }
    }
  }

  public void initAngel(EntityPlayer player) {
    this.hasAngel = true;
    player.capabilities.allowFlying = true;
    this.currentTicks = ANGEL_DURATION;
    if (!player.world.isRemote) {
      if (this.startFlyingSound) {
        SkrimPacketHandler.INSTANCE.sendTo(new AngelFlyingSoundPacket(), (EntityPlayerMP) player);
        this.startFlyingSound = false;
      }
    }
  }

}
