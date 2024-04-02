package avi.mod.skrim.skills.blacksmithing;

import avi.mod.skrim.advancements.SkrimAdvancements;
import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillAbility;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.utils.Utils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemSmeltedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SkillBlacksmithing extends Skill implements ISkillBlacksmithing {

  private static final String GLAZED_TERRACOTA_NAME = "tile.glazedterracotta";

  private static final Map<String, Integer> XP_MAP = ImmutableMap.<String, Integer>builder()
      .put("tile.stone", 100)
      .put("tile.stonebricksmooth", 100)
      .put("item.netherbrick", 110)
      .put("tile.glass", 100)
      .put("item.brick", 200)
      .put("tile.clayhardened", 400)
      .put(GLAZED_TERRACOTA_NAME, 300)
      .put("item.ingotiron", 500)
      .put("item.ingotgold", 2000)
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

  public static SkillStorage<ISkillBlacksmithing> STORAGE = new SkillStorage<>();

  private static SkillAbility MASTER_CRAFTS_PERSON = new SkillAbility("blacksmithing", "Master Crafter", 25,
      "This one had a hilarious game affecting bug so I had to nerf it.",
      "No longer risk breaking the anvil when repairing items.");

  private static SkillAbility PERSISTENCE = new SkillAbility("blacksmithing", "Persistence", 50, "3 days later...",
      "Significantly reduce prior work cost when repairing items.");

  private static SkillAbility IRON_HEART = new SkillAbility("blacksmithing", "Iron Heart", 75, "Can still pump blood.",
      "Passively gain §a25%" + SkillAbility.DESC_COLOR + " fire resistance.");

  private static SkillAbility OBSIDIAN_SMITH = new SkillAbility("blacksmithing", "Obsidian Smith", 100, "How can " +
      "obsidian be real if our " +
      "eyes aren't real?",
      "Allows you to craft obsidian armor, weapons, and tools.", "To craft, encase an undamaged diamond item in " +
      "obsidian.");

  public SkillBlacksmithing() {
    this(1, 0);
  }

  public SkillBlacksmithing(int level, int currentXp) {
    super("Blacksmithing", level, currentXp);
    this.addAbilities(MASTER_CRAFTS_PERSON, PERSISTENCE, IRON_HEART, OBSIDIAN_SMITH);
  }

  @Override
  public List<String> getToolTip() {
    List<String> tooltip = new ArrayList<>();
    if (this.skillEnabled) {
      tooltip.add("Repairing items provides §a" + Utils.formatPercent(this.extraRepair()) + "%§r extra durability.");
      tooltip.add("Smelting provides §a+" + Utils.formatPercent(this.extraIngot()) + "%§r items.");
    } else {
      tooltip.add(Skill.COLOR_DISABLED + "Repairing items provides " + Utils.formatPercent(this.extraRepair()) + "% extra durability.");
      tooltip.add(Skill.COLOR_DISABLED + "Smelting provides +" + Utils.formatPercent(this.extraIngot()) + "% items.");
    }
    return tooltip;
  }

  @Override
  public void ding(EntityPlayerMP player) {
    super.ding(player);
    if (this.level >= 100) {
      SkrimAdvancements.OBSIDIAN_SMITH.grant(player);
    }
  }

  private static String getBlacksmithingName(ItemStack stack) {
    String name = Utils.snakeCase(stack.getItem().getUnlocalizedName());
    return (name.startsWith(GLAZED_TERRACOTA_NAME)) ? GLAZED_TERRACOTA_NAME : name;
  }

  private static boolean validBlacksmithingTarget(ItemStack stack) {
    return XP_MAP.containsKey(getBlacksmithingName(stack));
  }

  public static int getXp(String blockName) {
    return XP_MAP.getOrDefault(blockName, 10);
  }

  private double extraIngot() {
    return 0.01 * this.level;
  }

  private double extraRepair() {
    return 0.02 * this.level;
  }

  public static void giveMoreIngots(ItemSmeltedEvent event) {
    if (event.player == null || !Skills.hasSkill(event.player, Skills.BLACKSMITHING)) return;

    SkillBlacksmithing blacksmithing = Skills.getSkill(event.player, Skills.BLACKSMITHING, SkillBlacksmithing.class);
    int stackSize = event.smelting.getCount();

    // This check should be returning, but isn't because it isn't funny.
    if (!validBlacksmithingTarget(event.smelting)) {
      if (event.player instanceof EntityPlayerMP) {
        blacksmithing.addXp((EntityPlayerMP) event.player, stackSize * getXp(getBlacksmithingName(event.smelting)));
      }
    }

    int addItemSize = (int) (blacksmithing.extraIngot() * stackSize);
    if (addItemSize > 0 && blacksmithing.skillEnabled) {
      ItemStack newStack = new ItemStack(event.smelting.getItem(), addItemSize);
      event.player.inventory.addItemStackToInventory(newStack);
    }
    if (event.player instanceof EntityPlayerMP) {
      blacksmithing.addXp((EntityPlayerMP) event.player, stackSize * getXp(getBlacksmithingName(event.smelting)));
    }
  }

  /**
   * Reduce fire damage by 25% if blacksmithing level is 75.
   */
  public static void ironHeart(LivingHurtEvent event) {
    Entity entity = event.getEntity();
    if (!(entity instanceof EntityPlayer)) return;

    DamageSource source = event.getSource();
    if (!source.isFireDamage()) return;

    EntityPlayer player = (EntityPlayer) entity;
    SkillBlacksmithing blacksmithing = Skills.getSkill(player, Skills.BLACKSMITHING, SkillBlacksmithing.class);
    if (!blacksmithing.activeAbility(3)) return;

    event.setAmount((float) (event.getAmount() * 0.75));
  }

  /**
   * A few things here: 1. We want to apply the base blacksmithing bonus when
   * repairing, that is the extra%/lvl repair 2. We want to apply the two
   * special repair skills that blacksmithing has.
   */
  public static void enhanceRepair(AnvilRepairEvent event) {
    EntityPlayer player = event.getEntityPlayer();
    if (player.world.isRemote) return;

    SkillBlacksmithing blacksmithing = Skills.getSkill(player, Skills.BLACKSMITHING, SkillBlacksmithing.class);
    ItemStack left = event.getItemInput();
    ItemStack middle = event.getIngredientInput();
    ItemStack output = event.getItemResult();

    // If the two input are items are the same we want to calculate the repair based on the healthiest item.
    int baseDamage = left.getItem() == middle.getItem() ? Math.min(left.getItemDamage(), middle.getItemDamage()) :
        left.getItemDamage();
    int baseRepair = baseDamage - output.getItemDamage();
    int additionalRepair = (blacksmithing.skillEnabled) ? (int) (baseRepair * blacksmithing.extraRepair()) : 0;
    int finalItemDamage = Math.max(output.getItemDamage() - additionalRepair, 0);
    // System.out.println("baseDamage: " + baseDamage + ", baseRepair: " + baseRepair + ", additionalRepair: " + additionalRepair + ", finalItemDamage: " + finalItemDamage);
    output.setItemDamage(finalItemDamage);
    blacksmithing.addXp((EntityPlayerMP) player, 10 * (baseDamage - finalItemDamage));

    if (!blacksmithing.activeAbility(1)) return;
    event.setBreakChance(0);

    if (!blacksmithing.activeAbility(2)) return;
    output.setRepairCost(1 + (output.getRepairCost() - 1) / 2);
  }


  public static void verifyObsidian(PlayerEvent.ItemCraftedEvent event) {
    if (!OBSIDIAN_ITEMS.contains(event.crafting.getItem())) return;

    if (!Skills.canCraft(event.player, Skills.BLACKSMITHING, 100)) {
      Skills.replaceWithComponents(event);
    }

  }

}
