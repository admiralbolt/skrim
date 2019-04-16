package avi.mod.skrim.skills.blacksmithing;

import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillAbility;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.utils.Obfuscation;
import avi.mod.skrim.utils.Utils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemSmeltedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SkillBlacksmithing extends Skill implements ISkillBlacksmithing {

  public static SkillStorage<ISkillBlacksmithing> skillStorage = new SkillStorage<ISkillBlacksmithing>();
  private static final Map<String, Integer> XP_MAP = ImmutableMap.<String, Integer>builder()
      .put("tile.stone", 50)
      .put("tile.stonebricksmooth", 50)
      .put("item.netherbrick", 60)
      .put("tile.glass", 50)
      .put("item.brick", 100)
      .put("tile.clayhardened", 400)
      .put("item.ingotiron", 500)
      .put("item.ingotgold", 240)
      .build();

  private static final Set<Item> OBSIDIAN_ITEMS = ImmutableSet.of(
      SkrimItems.OBSIDIAN_AXE,
      SkrimItems.OBSIDIAN_BOOTS,
      SkrimItems.OBSIDIAN_CHEST,
      SkrimItems.OBSIDIAN_HELMET,
      SkrimItems.OBSIDIAN_HOE,
      SkrimItems.OBSIDIAN_PANTS,
      SkrimItems.OBSIDIAN_PICKAXE,
      SkrimItems.OBSIDIAN_SHOVEL,
      SkrimItems.OBSIDIAN_SWORD
  );

  public int lastItemNumber;

  public static SkillAbility MASTER_CRAFTS_PERSON = new SkillAbility("blacksmithing", "Master Craftsperson", 25,
      "Due to legal action against Skrim® modding industries we have renamed the skill to be more inclusive.",
      "No longer risk breaking the anvil when repairing items.",
      "Repairing an item with an undamaged equivalent provides a one time §a+50%" + SkillAbility.DESC_COLOR + " durability bonus.");

  public static SkillAbility PERSISTENCE = new SkillAbility("blacksmithing", "Persistence", 50, "3 days later...",
      "Significantly reduce prior work cost when repairing items.");

  public static SkillAbility IRON_HEART = new SkillAbility("blacksmithing", "Iron Heart", 75, "Can still pump blood.",
      "Passively gain §a25%" + SkillAbility.DESC_COLOR + " fire resistance.");

  public static SkillAbility OBSIDIAN_SMITH = new SkillAbility("blacksmithing", "Obsidian Smith", 100, "How can obsidian be real if our " +
      "eyes aren't real?",
      "Allows you to craft obsidian armor, weapons, and tools.");

  public SkillBlacksmithing() {
    this(1, 0);
  }

  public SkillBlacksmithing(int level, int currentXp) {
    super("Blacksmithing", level, currentXp);
    this.addAbilities(MASTER_CRAFTS_PERSON, PERSISTENCE, IRON_HEART, OBSIDIAN_SMITH);
  }

  public int getXp(String blockName) {
    return (XP_MAP.containsKey(blockName)) ? XP_MAP.get(blockName) : 0;
  }

  public double extraIngot() {
    return 0.015 * this.level;
  }

  public double extraRepair() {
    return 0.02 * this.level;
  }

  public String getBlacksmithingName(ItemStack stack) {
    return Utils.snakeCase(stack.getItem().getUnlocalizedName());
  }

  @Override
  public List<String> getToolTip() {
    List<String> tooltip = new ArrayList<String>();
    tooltip.add("Repairing items provides §a" + Utils.formatPercent(this.extraRepair()) + "%§r extra durability.");
    tooltip.add("Smelting provides §a+" + Utils.formatPercent(this.extraIngot()) + "%§r items.");
    tooltip.add("Shift clicking crafted items provides §amostly accurate extra items§r.");
    tooltip.add("§eWe swear this is a bug and not a feature...§r");
    return tooltip;
  }

  public boolean validBlacksmithingTarget(ItemStack stack) {
    Item item = stack.getItem();
    return XP_MAP.containsKey(Utils.snakeCase(item.getUnlocalizedName()));
  }

  public static void giveMoreIngots(ItemSmeltedEvent event) {
    if (event.player != null && event.player.hasCapability(Skills.BLACKSMITHING, EnumFacing.NORTH)) {
      SkillBlacksmithing blacksmithing = (SkillBlacksmithing) event.player.getCapability(Skills.BLACKSMITHING, EnumFacing.NORTH);
      if (blacksmithing.validBlacksmithingTarget(event.smelting)) {
        int stackSize = (event.smelting.getCount() == 0) ? blacksmithing.lastItemNumber : Obfuscation.getStackSize(event.smelting);
        int addItemSize = (int) (blacksmithing.extraIngot() * stackSize); // OOO
        if (addItemSize > 0) {
          ItemStack newStack = new ItemStack(event.smelting.getItem(), addItemSize);
          event.player.inventory.addItemStackToInventory(newStack);
        }
        Utils.logSkillEvent(event, blacksmithing, " stackSize: " + stackSize + ", addItemSize: " + addItemSize);
        if (event.player instanceof EntityPlayerMP) {
          // Give xp for bonus items too!
          blacksmithing.addXp((EntityPlayerMP) event.player,
              (stackSize + addItemSize) * blacksmithing.getXp(blacksmithing.getBlacksmithingName(event.smelting)));
        }
      }
    }
  }

  /**
   * The hackiest of hacks. Why does this always happen. Basically, shift
   * clicking is really FUCK and does not correctly report the number of
   * smelted items. When we open the furnace we log the number of items
   * currently in it, and use that instead when shift clicking.
   */
  public static void saveItemNumber(PlayerContainerEvent.Open event) {
    Container please = event.getContainer();
    if (please instanceof ContainerFurnace) {
      Slot output = please.getSlot(2);
      ItemStack yas = output.getStack();
      if (yas != null) {
        EntityPlayer player = event.getEntityPlayer();
        if (player != null && player.hasCapability(Skills.BLACKSMITHING, EnumFacing.NORTH)) {
          SkillBlacksmithing blacksmithing = (SkillBlacksmithing) player.getCapability(Skills.BLACKSMITHING, EnumFacing.NORTH);
          blacksmithing.lastItemNumber = Obfuscation.getStackSize(yas);
        }
      }
    }
  }

  /**
   * Reduce fire damage by half! Pretty straightforward, just lots of if
   * checking.
   */
  public static void ironHeart(LivingHurtEvent event) {
    Entity entity = event.getEntity();
    if (entity instanceof EntityPlayer) {
      DamageSource source = event.getSource();
      if (source.isFireDamage()) {
        EntityPlayer player = (EntityPlayer) entity;
        if (player != null && player.hasCapability(Skills.BLACKSMITHING, EnumFacing.NORTH)) {
          SkillBlacksmithing blacksmithing = (SkillBlacksmithing) player.getCapability(Skills.BLACKSMITHING, EnumFacing.NORTH);
          if (blacksmithing.hasAbility(3)) {
            Utils.logSkillEvent(event, blacksmithing, "Applying iron heart.");
            event.setAmount((float) (event.getAmount() * 0.75));
          }
        }
      }
    }
  }

  /**
   * A few things here: 1. We want to apply the base blacksmithing bonus when
   * repairing, that is the extra%/lvl repair 2. We want to apply the two
   * special repair skills that blacksmithing has.
   */
  public static void enhanceRepair(AnvilRepairEvent event) {
    Entity player = event.getEntityPlayer();
    if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.BLACKSMITHING, EnumFacing.NORTH)) {
      SkillBlacksmithing blacksmithing = (SkillBlacksmithing) player.getCapability(Skills.BLACKSMITHING, EnumFacing.NORTH);
      ItemStack left = event.getItemInput();
      ItemStack middle = event.getIngredientInput();
      ItemStack output = event.getItemResult();

      int baseRepair = left.getItemDamage() - output.getItemDamage();
      blacksmithing.addXp((EntityPlayerMP) player, (int) (baseRepair * (1 + blacksmithing.extraRepair())));
      int finalRepair = output.getItemDamage() - (int) (baseRepair * blacksmithing.extraRepair());
      Utils.logSkillEvent(event, blacksmithing, "baseRepair: " + baseRepair + ", finalRepair: " + finalRepair);
      if (blacksmithing.hasAbility(1)) {
        event.setBreakChance(0);
        if (blacksmithing.hasAbility(2)) {
          output.setRepairCost(1 + (output.getRepairCost() - 1) / 2);
          // Ensure +25% durability hasn't already been applied
          NBTTagCompound compound = output.getTagCompound();
          if (!compound.hasKey("enhanced_durability")) {
            compound.setBoolean("enhanced_durability", false);
          }
          if (middle.getItemDamage() == 0 && middle.getItem() == output.getItem() && !compound.getBoolean("enhanced_durability")) {
            /**
             * Yo dawg I heard you capped the max damage for items,
             * but you see, I want to make the cap go higher. So uh,
             * I'm gonna break your shit.
             */
            Item outputItem = (Item) output.getItem();
            Utils.logSkillEvent(event, blacksmithing,
                "applying durability bonus, setting max to: " + (int) (outputItem.getMaxDamage(output) * 1.5));
            outputItem.setMaxDamage((int) (outputItem.getMaxDamage(output) * 1.5));
            // ReflectionUtils.hackSuperValueTo(outputItem, (int) (outputItem.getMaxDamage(output) * 1.5),  "maxDamage", "field_77699_b");
          }
        }
      }
    }
  }

  public static void verifyObsidian(ItemCraftedEvent event) {
    Item targetItem = event.crafting.getItem();
    if (targetItem != null && OBSIDIAN_ITEMS.contains(targetItem)) {
      if (!Skills.canCraft(event.player, Skills.BLACKSMITHING, 100)) {
        Skills.replaceWithComponents(event);
      } else if (!event.player.world.isRemote && event.player.hasCapability(Skills.BLACKSMITHING, EnumFacing.NORTH)) {
        SkillBlacksmithing blacksmithing = (SkillBlacksmithing) event.player.getCapability(Skills.BLACKSMITHING, EnumFacing.NORTH);
        blacksmithing.addXp((EntityPlayerMP) event.player, 5000);
      }
    }
  }

}
