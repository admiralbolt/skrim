package avi.mod.skrim.skills.cooking;

import avi.mod.skrim.entities.passive.EntityPumpkow;
import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.network.SkrimPacketHandler;
import avi.mod.skrim.network.skillpackets.AngelFlyingSoundPacket;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillAbility;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.farming.SkillFarming;
import avi.mod.skrim.utils.Obfuscation;
import avi.mod.skrim.utils.ReflectionUtils;
import avi.mod.skrim.utils.Utils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
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
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemSmeltedEvent;

import java.util.*;

public class SkillCooking extends Skill implements ISkillCooking {

  public static SkillStorage<ISkillCooking> skillStorage = new SkillStorage<>();

  private static Map<String, Integer> XP_MAP;
  private static Map<String, Item> FOOD_MAP;
  private static Map<Class, String> ENTITY_FOOD_MAP;

  // Marks entities for cooking. The logic for this is a little complex:
  // 1. On damaging an entity with a fire attack mark it for cooking.
  // 2. When it dies produce the correct cooked food type.
  // We maintain a map from the target entity UUID to the player that attacked it.
  private static Map<UUID, EntityPlayer> ENTITIES_MARKED = new HashMap<>();


  private static final double FIRE_COOKED_XP_MULT = 0.25;
  private static final int ANGEL_DURATION = 600;

  public boolean hasAngel = false;
  private boolean startFlyingSound = true;
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
    ENTITY_FOOD_MAP.put(EntityMooshroom.class, "beefcooked");
    ENTITY_FOOD_MAP.put(EntityPumpkow.class, "beefcooked");
    addFood("porkchopcooked", SkrimItems.OVERWRITE_PORKCHOP, 500);
    ENTITY_FOOD_MAP.put(EntityPig.class, "porkchopcooked");
    addFood("chickencooked", SkrimItems.OVERWRITE_CHICKEN, 500);
    ENTITY_FOOD_MAP.put(EntityChicken.class, "chickencooked");

    addFood("cooked_fish", SkrimItems.OVERWRITE_FISH, 800);
    addFood("cooked_salmon", SkrimItems.OVERWRITE_SALMON, 1000);

    addFood("pumpkinpie", SkrimItems.OVERWRITE_PUMPKIN_STEW, 1500);

    addFood("rabbitcooked", SkrimItems.OVERWRITE_RABBIT, 2500);
    ENTITY_FOOD_MAP.put(EntityRabbit.class, "rabbitcooked");
    addFood("rabbitstew", SkrimItems.OVERWRITE_RABBIT_STEW, 3000);

    addFood("item.cake", SkrimItems.SKRIM_CAKE, 5000);
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

  @Override
  public List<String> getToolTip() {
    List<String> tooltip = new ArrayList<>();
    tooltip.add("Your cooking provides §a+" + Utils.formatPercent(extraFood(this.level)) + "%§r food.");
    tooltip.add("Your cooking provides §a+" + Utils.formatPercent(extraSaturation(this.level)) + "%§r saturation");
    return tooltip;
  }

  public static int getXp(String foodName) {
    return XP_MAP.getOrDefault(foodName, 0);
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
      Obfuscation.CURRENT_ITEM.hackValueTo(event.player.inventory, newFood);
    }
    SkillCooking cooking = Skills.getSkill(player, Skills.COOKING, SkillCooking.class);

    if (player instanceof EntityPlayerMP) {
      cooking.addXp((EntityPlayerMP) player, getXp(getFoodName(stack)) * stack.getCount());
    }
  }

  /**
   * Gets the replacement version of an existing minecraft food item. Sets the correct NBT tags based on cooking level.
   */
  public static ItemStack getReplaceFood(EntityPlayer player, ItemStack stack) {
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

  // These methods are static since they are used by the custom food & cake classes.
  public static double extraFood(int level) {
    return 0.004 * level;
  }

  public static double extraSaturation(int level) {
    return 0.004 * level;
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

  private static boolean hasFireEnchantment(ItemStack stack) {
    Map<Enchantment, Integer> mainEnchants = EnchantmentHelper.getEnchantments(stack);
    for (Enchantment ench : mainEnchants.keySet()) {
      if (ench == Enchantments.FLAME || ench == Enchantments.FIRE_ASPECT) {
        return true;
      }
    }
    return false;
  }

  public static void markEntities(LivingHurtEvent event) {
    DamageSource source = event.getSource();
    Entity entity = source.getTrueSource();
    if (entity == null || entity.world.isRemote) return;

    Entity targetEntity = event.getEntity();
    if (!(entity instanceof EntityPlayer) || !(ENTITY_FOOD_MAP.containsKey(targetEntity.getClass()))) return;

    EntityPlayer player = (EntityPlayer) entity;
    ItemStack mainStack = player.getHeldItemMainhand();

    if (!hasFireEnchantment(mainStack)) return;
    ENTITIES_MARKED.put(targetEntity.getUniqueID(), player);
  }

  private static void cookDrops(EntityPlayer player, List<EntityItem> drops) {
    for (int i = 0; i < drops.size(); i++) {
      EntityItem item = drops.get(i);
      ItemStack replaceFood = getReplaceFood(player, item.getItem());
      if (replaceFood == null) continue;

      drops.set(i, new EntityItem(player.world, item.posX, item.posY, item.posZ, replaceFood));
      SkillCooking cooking = Skills.getSkill(player, Skills.COOKING, SkillCooking.class);
      if (player instanceof EntityPlayerMP) {
        cooking.addXp((EntityPlayerMP) player, (int) (getXp(getFoodName(item.getItem())) * FIRE_COOKED_XP_MULT));
      }
    }
  }

  public static void fireCook(LivingDropsEvent event) {
    Entity targetEntity = event.getEntity();
    if (!ENTITIES_MARKED.containsKey(targetEntity.getUniqueID())) return;

    EntityPlayer mappedPlayer = ENTITIES_MARKED.get(targetEntity.getUniqueID());
    ENTITIES_MARKED.remove(targetEntity.getUniqueID());

    DamageSource source = event.getSource();

    int fire = (int) ReflectionUtils.getSuperXField(targetEntity, 6, Obfuscation.ENTITY_FIRE.getFieldNames());

    if (source.isFireDamage() || fire > 0) {
      // Some special handling here, want to trigger husbandry from player caused fire damage.
      // Stealing the logic for doubling the drops.
      SkillFarming farming = Skills.getSkill(mappedPlayer, Skills.FARMING, SkillFarming.class);
      if (farming.hasAbility(2)) {
        List<EntityItem> drops = event.getDrops();
        List<EntityItem> duplicateItems = new ArrayList<>();

        for (EntityItem item : drops) {
          duplicateItems.add(new EntityItem(mappedPlayer.world, item.posX, item.posY, item.posZ, item.getItem()));
        }

        drops.addAll(duplicateItems);
      }
      cookDrops(mappedPlayer, event.getDrops());
      return;
    }

    Entity entity = source.getTrueSource();
    if (!(entity instanceof EntityPlayer)) return;

    // See if the player hit the entity with a fire sword.
    EntityPlayer player = (EntityPlayer) entity;
    ItemStack mainStack = player.getHeldItemMainhand();

    if (!hasFireEnchantment(mainStack)) return;

    cookDrops(player, event.getDrops());
  }

  public static void mooshroom(PlayerInteractEvent.EntityInteract event) {
    if (!(event.getTarget() instanceof EntityMooshroom)) return;

    EnumHand hand = event.getHand();
    EntityMooshroom cow = (EntityMooshroom) event.getTarget();
    EntityPlayer player = event.getEntityPlayer();
    ItemStack itemstack = player.getHeldItem(hand);

    if (itemstack.getItem() != Items.BOWL || cow.getGrowingAge() < 0) return;
    itemstack.shrink(1);

    ItemStack newFood = SkillCooking.getReplaceFood(player, new ItemStack(Items.MUSHROOM_STEW));

    if (itemstack.isEmpty()) {
      player.setHeldItem(hand, newFood);
    } else if (!player.inventory.addItemStackToInventory(newFood)) {
      player.dropItem(newFood, false);
    }

    event.setCanceled(true);
  }

  public static void angelUpdate(LivingUpdateEvent event) {
    Entity entity = event.getEntity();
    if (!(entity instanceof EntityPlayer)) return;

    EntityPlayer player = (EntityPlayer) entity;
    SkillCooking cooking = Skills.getSkill(player, Skills.COOKING, SkillCooking.class);
    if (cooking.hasAngel && cooking.currentTicks > 0) {
      cooking.currentTicks--;
      return;
    }

    cooking.hasAngel = false;
    cooking.currentTicks = 0;
    cooking.startFlyingSound = true;
    if (!player.capabilities.isCreativeMode) {
      player.capabilities.allowFlying = false;
      player.capabilities.isFlying = false;
    }

  }

  public static void angelFall(LivingFallEvent event) {
    Entity entity = event.getEntity();
    if (!(entity instanceof EntityPlayer)) return;

    EntityPlayer player = (EntityPlayer) entity;
    SkillCooking cooking = Skills.getSkill(player, Skills.COOKING, SkillCooking.class);
    if (!cooking.hasAngel) return;

    event.setDistance(0);
    event.setCanceled(true);

  }

  public void initAngel(EntityPlayer player) {
    this.hasAngel = true;
    player.capabilities.allowFlying = true;
    this.currentTicks = ANGEL_DURATION;
    if (player.world.isRemote || !this.startFlyingSound) return;

    SkrimPacketHandler.INSTANCE.sendTo(new AngelFlyingSoundPacket(), (EntityPlayerMP) player);
    this.startFlyingSound = false;
  }

}
