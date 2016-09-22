package avi.mod.skrim.skills.blacksmithing;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import avi.mod.skrim.items.ModItems;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillAbility;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.utils.Utils;
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
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemSmeltedEvent;

public class SkillBlacksmithing extends Skill implements ISkillBlacksmithing {

	public static SkillStorage<ISkillBlacksmithing> skillStorage = new SkillStorage<ISkillBlacksmithing>();
	public static Map<String, Integer> xpMap;
	static {
		xpMap = new HashMap<String, Integer>();
		xpMap.put("tile.stone", 1);
		xpMap.put("tile.stonebricksmooth", 2);
		xpMap.put("item.netherbrick", 3); // nether bonus
		xpMap.put("tile.glass", 4);
		xpMap.put("item.brick", 5);
		xpMap.put("tile.clayhardened", 21); // xp bonus for crafting
		xpMap.put("item.ingotiron", 10);
		xpMap.put("item.ingotgold", 25); // Woooooo gold!
	}

	public int lastItemNumber;
	public static List<Item> obsidianItems = new ArrayList<Item>();
	static {
		obsidianItems.add(ModItems.obsidianAxe);
		obsidianItems.add(ModItems.obsidianBoots);
		obsidianItems.add(ModItems.obsidianChest);
		obsidianItems.add(ModItems.obsidianHelmet);
		obsidianItems.add(ModItems.obsidianHoe);
		obsidianItems.add(ModItems.obsidianPants);
		obsidianItems.add(ModItems.obsidianPickaxe);
		obsidianItems.add(ModItems.obsidianShovel);
		obsidianItems.add(ModItems.obsidianSword);
	}

	public static SkillAbility persistence = new SkillAbility(
		"Persistence",
		25,
		"3 days later...",
		"Remove prior work cost when repairing items."
	);

	public static SkillAbility masterCraftsPerson = new SkillAbility(
		"Master Craftsperson",
		50,
		"Due to legal action against Skrim® modding industries we have renamed the skill to be more inclusive.",
		"No longer risk breaking the anvil when repairing items.",
		"Repairing an item with an undamaged equivalent provides a one time §a+25%" + SkillAbility.descColor + " durability bonus."
	);

	public static SkillAbility ironHeart = new SkillAbility(
		"Iron Heart",
		75,
		"Can still pump blood.",
		"Passively gain §a50%" + SkillAbility.descColor + " fire resistance."
	);

	public static SkillAbility obsidianSmith = new SkillAbility(
		"Obsidian Smith",
		100,
		"How can obsidian be real if our eyes aren't real?",
		"Allows you to craft obsidian armor, weapons, and tools."
	);

	public SkillBlacksmithing() {
		this(1, 0);
	}

	public SkillBlacksmithing(int level, int currentXp) {
		super("Blacksmithing", level, currentXp);
		this.iconTexture = new ResourceLocation("skrim", "textures/guis/skills/blacksmithing.png");
		this.addAbilities(persistence, masterCraftsPerson, ironHeart, obsidianSmith);
	}

	public int getXp(String blockName) {
		return (xpMap.containsKey(blockName)) ? xpMap.get(blockName) : 0;
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
		return xpMap.containsKey(Utils.snakeCase(item.getUnlocalizedName()));
  }

  public static void giveMoreIngots(ItemSmeltedEvent event) {
  	if (event.player != null && event.player.hasCapability(Skills.BLACKSMITHING, EnumFacing.NORTH)) {
  		SkillBlacksmithing blacksmithing = (SkillBlacksmithing) event.player.getCapability(Skills.BLACKSMITHING, EnumFacing.NORTH);
	    if (blacksmithing.validBlacksmithingTarget(event.smelting)) {
	      int stackSize = (event.smelting.stackSize == 0) ? blacksmithing.lastItemNumber : event.smelting.stackSize;
	      int addItemSize = (int) (blacksmithing.extraIngot() * stackSize); // OOO
	      if (addItemSize > 0) {
	        ItemStack newStack = new ItemStack(event.smelting.getItem(), addItemSize);
	        event.player.inventory.addItemStackToInventory(newStack);
	      }
	      if (event.player instanceof EntityPlayerMP) {
	        // Give xp for bonus items too!
	      	blacksmithing.addXp((EntityPlayerMP) event.player, (stackSize + addItemSize) * blacksmithing.getXp(blacksmithing.getBlacksmithingName(event.smelting)));
	      }
	    }
    }
  }

  /**
   * The hackiest of hacks.  Why does this always happen.
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
          blacksmithing.lastItemNumber = yas.stackSize;
        }
      }
    }
  }

  public static void ironHeart(LivingHurtEvent event) {
  	Entity entity = event.getEntity();
  	if (entity instanceof EntityPlayer) {
  		DamageSource source = event.getSource();
  		if (source.isFireDamage()) {
	  		EntityPlayer player = (EntityPlayer) entity;
	  		if (player != null && player.hasCapability(Skills.BLACKSMITHING, EnumFacing.NORTH)) {
	  			SkillBlacksmithing blacksmithing = (SkillBlacksmithing) player.getCapability(Skills.BLACKSMITHING, EnumFacing.NORTH);
	  			if (blacksmithing.hasAbility(3)) {
	  				System.out.println("input amount: " + event.getAmount());
	  				event.setAmount(event.getAmount() / 2);
	  			}
	  		}
	  	}
  	}
  }

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
			/**
			 * Persistence!
			 */
			if (blacksmithing.hasAbility(1)) {
				output.setRepairCost(0);
				if (blacksmithing.hasAbility(2)) {
					event.setBreakChance(0);
					// Ensure +25% durability hasn't already been applied
					NBTTagCompound compound = output.getTagCompound();
					if (!compound.hasKey("enhanced_durability")) {
						compound.setBoolean("enhanced_durability", false);
					}
					if (middle.getItemDamage() == 0 && !compound.getBoolean("enhanced_durability")) {
						/**
						 * Yo dawg I heard you capped the max damage for items,
						 * but you see, I want to make the cap go higher.
						 * So uh, I'm gonna break your shit.
						 */
						Item outputItem = (Item) output.getItem();
						Field field;
						try {
							field = outputItem.getClass().getSuperclass().getDeclaredField("maxDamage");
							field.setAccessible(true);
							try {
								field.set(outputItem, (int) (outputItem.getMaxDamage() * 1.25));
								compound.setBoolean("enhanced_durability", true);
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							}
						} catch (NoSuchFieldException e) {
							e.printStackTrace();
						} catch (SecurityException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	public static void verifyObsidian(ItemCraftedEvent event) {
		Item targetItem = event.crafting.getItem();
		if (targetItem != null && obsidianItems.contains(targetItem)) {
			if (!Skills.canCraft(event.player, Skills.BLACKSMITHING, 100)) {
				Skills.replaceWithComponents(event);
			}
		}
	}

}
