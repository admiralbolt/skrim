package avi.mod.skrim.utils;

import avi.mod.skrim.blocks.SkrimBlocks;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.*;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.world.BlockEvent;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

public class Utils {

  public static final Integer TICKS_PER_SECOND = 20;

  public static String[] tuplets = {"zero-adsf", "one-asdf", "double", "triple", "quadruple", "quintuple", "sextuple", "septuple",
      "octople", "nontople", "decuple"};
  public static Random rand = new Random();
  public static DecimalFormat oneDigit = new DecimalFormat("0.0");
  public static DecimalFormat twoDigit = new DecimalFormat("0.00");
  private static final Set<Potion> negativeEffects = ImmutableSet.<Potion>builder()
      .add(MobEffects.BLINDNESS)
      .add(MobEffects.GLOWING)
      .add(MobEffects.HUNGER)
      .add(MobEffects.LEVITATION)
      .add(MobEffects.MINING_FATIGUE)
      .add(MobEffects.NAUSEA)
      .add(MobEffects.POISON)
      .add(MobEffects.SLOWNESS)
      .add(MobEffects.UNLUCK)
      .add(MobEffects.WEAKNESS)
      .add(MobEffects.WITHER)
      .build();

  public static boolean isPointInRegion(int left, int top, int right, int bottom, int pointX, int pointY) {
    return (pointX > left && pointX < right && pointY > top && pointY < bottom);
  }

  public static String snakeCase(String str) {
    return str.toLowerCase().replace(" ", "_").replace("'", "");
  }

  public static String titleizeLowerCamel(String str) {
    StringBuilder builder = new StringBuilder();
    builder.append(Character.toUpperCase(str.charAt(0)));
    char c;
    for (int i = 1; i < str.length(); i++) {
      c = str.charAt(i);
      if (Character.isUpperCase(c)) {
        builder.append(" ");
      }
      builder.append(c);
    }

    return builder.toString();
  }

  public static String getBlockName(Block block) {
    return snakeCase(block.getLocalizedName());
  }

  public static String getFortuneString(int fortuneAmount) {
    return (fortuneAmount >= tuplets.length) ? "fucktuple" : tuplets[fortuneAmount];
  }

  public static int gaussianSum(int n) {
    return (n * n + n) / 2;
  }

  public static String formatPercent(double percent) {
    return oneDigit.format(Math.round(percent * 100));
  }

  public static String formatPercentTwo(double percent) {
    return twoDigit.format(Math.round(percent * 100));
  }

  public static void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBaseIn,
                                                      Map<IAttribute, AttributeModifier> attributeMap, int amplifier) {
    AbstractAttributeMap entityAttributes = entityLivingBaseIn.getAttributeMap();
    for (Entry<IAttribute, AttributeModifier> entry : attributeMap.entrySet()) {
      IAttributeInstance iattributeinstance = entityAttributes.getAttributeInstance((IAttribute) entry.getKey());
      AttributeModifier attributemodifier = entry.getValue();
      iattributeinstance.removeModifier(attributemodifier);
      iattributeinstance.applyModifier(new AttributeModifier(attributemodifier.getID(), attributemodifier.getName(),
          getAttributeModifierAmount(amplifier, attributemodifier), attributemodifier.getOperation()));
    }

  }

  public static double getAttributeModifierAmount(int amplifier, AttributeModifier modifier) {
    return modifier.getAmount() * (double) (amplifier + 1);
  }

  public static boolean isRawXpBlock(Block block) {
    return (block instanceof BlockOldLog || block instanceof BlockNewLog || block instanceof BlockRedFlower || block instanceof BlockYellowFlower
        || block instanceof BlockSand || block instanceof BlockGravel || block instanceof BlockDirt || block instanceof BlockMycelium
        || block instanceof BlockGrass || block instanceof BlockSoulSand || block instanceof BlockPumpkin || block instanceof BlockMelon
        || block instanceof BlockDoublePlant || block == Blocks.IRON_ORE || block == Blocks.GOLD_ORE || block == Blocks.DIAMOND_ORE
        || block == Blocks.LAPIS_ORE || block == Blocks.REDSTONE_ORE || block == Blocks.COAL_ORE || block == Blocks.EMERALD_ORE
        || block == SkrimBlocks.WEIRWOOD_WOOD);
  }

  public static boolean isSilkTouching(BlockEvent.BreakEvent event) {
    EntityPlayer player = event.getPlayer();
    ItemStack mainStack = player.getHeldItemMainhand();
    int silkTouch = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, mainStack);
    return silkTouch > 0;
  }

  public static boolean isNegativeEffect(PotionEffect effect) {
    Potion potion = (Potion) ReflectionUtils.getPrivateField(effect, "potion", "field_188420_b");
    return (potion != null && negativeEffects.contains(potion));
  }

  public static boolean isWearingArmor(EntityPlayer player, ItemArmor armor) {
    InventoryPlayer inventory = player.inventory;
    if (inventory != null) {
      ItemStack stack = inventory.armorInventory.get(armor.armorType.getIndex());
      Item targetItem = stack.getItem();
      return targetItem == armor;
    }
    return false;
  }

  public static ItemStack getArmor(EntityPlayer player, EntityEquipmentSlot armorType) {
    return player.inventory.armorInventory.get(armorType.getIndex());
  }

  public static void addOrCombineEffect(EntityLivingBase entity, PotionEffect effect) {
    PotionEffect activeEffect = entity.getActivePotionEffect(effect.getPotion());
    if (activeEffect != null) {
      activeEffect.combine(effect);
    } else {
      activeEffect = effect;
    }
    entity.addPotionEffect(activeEffect);
  }

  public static void removeFromInventory(InventoryPlayer inventory, Item removeItem, int amount) {
    int totalRemoved = 0;
    int remove;
    ItemStack stack;
    for (int i = 0; i < inventory.getSizeInventory(); i++) {
      stack = inventory.getStackInSlot(i);
      if (stack.getItem() != removeItem) continue;
      remove = Math.min(stack.getCount(), (amount - totalRemoved));
      if (remove == stack.getCount()) {
        inventory.removeStackFromSlot(i);
      } else {
        inventory.decrStackSize(i, remove);
      }
      totalRemoved += remove;
      if (totalRemoved >= amount) return;
    }
  }

  public static void removeFromInventoryNoNBT(InventoryPlayer inventory, Item removeItem, int amount) {
    int totalRemoved = 0;
    int remove;
    ItemStack stack;
    for (int i = 0; i < inventory.getSizeInventory(); i++) {
      stack = inventory.getStackInSlot(i);
      if (stack.getItem() != removeItem || stack.getTagCompound() != null) continue;

      remove = Math.min(stack.getCount(), (amount - totalRemoved));
      if (remove == stack.getCount()) {
        inventory.removeStackFromSlot(i);
      } else {
        inventory.decrStackSize(i, remove);
      }

      totalRemoved += remove;
      if (totalRemoved >= amount) return;
    }
  }

  public static void removeAllFromInventory(EntityPlayer player, Item removeItem) {
    for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
      if (player.inventory.getStackInSlot(i).getItem() != removeItem) continue;

      player.inventory.removeStackFromSlot(i);
    }
  }

  public static boolean areSimilarStacks(ItemStack stack1, ItemStack stack2) {
    return
        stack1.getItem() == stack2.getItem() &&
        stack1.getMetadata() == stack2.getMetadata() &&
        ItemStack.areItemStackTagsEqual(stack1, stack2) &&
        stack1 != ItemStack.EMPTY &&
        stack2 != ItemStack.EMPTY &&
        stack1.getCount() > 0 &&
        stack2.getCount() > 0;
  }

  public static int randInt(int min, int max) {
    return rand.nextInt(max - min) + min;
  }

  public static void teleport(Entity entity, double x, double y, double z, boolean playSound) {
    if (entity instanceof EntityPlayerMP) {
      ((EntityPlayerMP) entity).connection.setPlayerLocation(x, y, z, entity.rotationYaw, entity.rotationPitch);
    } else {
      entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
    }
    if (playSound) {
      entity.getEntityWorld().playSound((EntityPlayer) null, x, y, z, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
    }
  }
}

