package avi.mod.skrim.skills.cooking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import avi.mod.skrim.items.CustomCake;
import avi.mod.skrim.items.CustomFood;
import avi.mod.skrim.items.ModItems;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillAbility;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.utils.Obfuscation;
import avi.mod.skrim.utils.Utils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemSmeltedEvent;

public class SkillCooking extends Skill implements ISkillCooking {

	public static SkillStorage<ISkillCooking> skillStorage = new SkillStorage<ISkillCooking>();
	public static Map<String, Integer> xpMap;
	public static Map<String, CustomFood> foodMap;
	public static Map<Class, String> entityFoodMap;
	public static double fireCookedMult = 0.25;
	public static int ANGEL_DURATION = 600;
	public static long CHECK_TICKS = 60;

	public boolean hasAngel = false;
	public int currentTicks = 0;
	public int lastItemNumber;

	private static void addFood(String name, CustomFood food, int xp) {
		foodMap.put(name, food);
		xpMap.put(name, xp);
	}

	static {
		foodMap = new HashMap<String, CustomFood>();
		xpMap = new HashMap<String, Integer>();
		entityFoodMap = new HashMap<Class, String>();
		addFood("bread", ModItems.OVERWRITE_BREAD, 200);
		addFood("cookie", ModItems.OVERWRITE_COOKIE, 25);

		addFood("potatobaked", ModItems.OVERWRITE_BAKED_POTATO, 300);

		addFood("beetroot_soup", ModItems.OVERWRITE_BEETROOT_SOUP, 400);
		addFood("mushroomstew", ModItems.OVERWRITE_MUSHROOM_STEW, 400);

		addFood("muttoncooked", ModItems.OVERWRITE_MUTTON, 600);
		entityFoodMap.put(EntitySheep.class, "muttoncooked");
		addFood("beefcooked", ModItems.OVERWRITE_STEAK, 600);
		entityFoodMap.put(EntityCow.class, "beefcooked");
		addFood("porkchopcooked", ModItems.OVERWRITE_PORKCHOP, 600);
		entityFoodMap.put(EntityPig.class, "porkchopcooked");
		addFood("chickencooked", ModItems.OVERWRITE_CHICKEN, 600);
		entityFoodMap.put(EntityChicken.class, "chickencooked");

		addFood("cooked_fish", ModItems.OVERWRITE_FISH, 700);
		addFood("pumpkinpie", ModItems.OVERWRITE_PUMPKIN_STEW, 750);
		addFood("cooked_salmon", ModItems.OVERWRITE_SALMON, 800);

		addFood("rabbitcooked", ModItems.OVERWRITE_RABBIT, 1000);
		entityFoodMap.put(EntityRabbit.class, "rabbitcooked");
		addFood("rabbitstew", ModItems.OVERWRITE_RABBIT_STEW, 1000);

		xpMap.put("item.cake", 1500);
		xpMap.put("angel_cake", 2000);

	}

	public static SkillAbility OVERFULL = new SkillAbility("cooking", "Overfull", 25, "Just keep eating, just keep eating, just keep eating...",
			"Your cooked food now ignores food and saturation limits.");
	public static SkillAbility PANACEA = new SkillAbility("cooking", "Panacea", 50, "Cures everything that's less than half dead.",
			"Your cooked food now removes nausea, hunger, and poison.");
	public static SkillAbility SUPER_FOOD = new SkillAbility("cooking", "Super Food", 75, "You won't believe how good these 11 foods are for you!",
			"Your cooked food now grants a speed boost and a short period of regeneration.");
	public static SkillAbility ANGEL_CAKE = new SkillAbility("cooking", "Angel Cake", 100, "I believe I can fly.",
			"Gain the ability to craft angel cake, which grants 30 seconds of flight.");

	public SkillCooking() {
		this(1, 0);
	}

	public SkillCooking(int level, int currentXp) {
		super("Cooking", level, currentXp);
		this.addAbilities(OVERFULL, PANACEA, SUPER_FOOD, ANGEL_CAKE);
	}

	public CustomFood getOverwriteFood(String name) {
		return (foodMap.containsKey(name)) ? foodMap.get(name) : null;
	}

	public static int getXp(String blockName) {
		return (xpMap.containsKey(blockName)) ? xpMap.get(blockName) : 0;
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

	public boolean validCookingTarget(ItemStack stack) {
		Item item = stack.getItem();
		return (item instanceof ItemFood || item instanceof ItemFishFood || item == Items.CAKE) ? true : false;
	}

	public static String getFoodName(ItemStack stack) {
		Item item = stack.getItem();
		if (item instanceof ItemFishFood) {
			ItemFishFood fish = (ItemFishFood) item;
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
		} else {
			return null;
		}
	}

	public static void injectFakeFood(PlayerEvent event, ItemStack stack, EntityPlayer player) {
		if (player != null && player.hasCapability(Skills.COOKING, EnumFacing.NORTH)) {
			SkillCooking cooking = (SkillCooking) player.getCapability(Skills.COOKING, EnumFacing.NORTH);
			String foodName = getFoodName(stack);
			Utils.logSkillEvent(event, cooking, "crafting food: " + foodName);
			if (cooking.validCookingTarget(stack)) {
				Item replaceFood;
				if (stack.getItem() == Items.CAKE || stack.getItem() == ModItems.ANGEL_CAKE) {
					replaceFood = getOverwriteCake(stack.getItem());
				} else {
					replaceFood = cooking.getOverwriteFood(cooking.getFoodName(stack));
				}
				if (replaceFood != null) {
					NBTTagCompound compound = new NBTTagCompound();
					compound.setInteger("level", cooking.level);
					ItemStack addStack = new ItemStack(replaceFood, 1);
					addStack.setStackDisplayName(player.getName() + "'s " + addStack.getDisplayName());
					addStack.setTagCompound(compound);
					player.inventory.addItemStackToInventory(addStack);
					if (player instanceof EntityPlayerMP) {
						cooking.addXp((EntityPlayerMP) player, getXp(foodName));
					}
				}
			}
		}
	}

	public static CustomCake getOverwriteCake(Item item) {
		if (item == ModItems.ANGEL_CAKE) {
			return ModItems.ANGEL_CAKE;
		} else if (item == Items.CAKE) {
			return ModItems.SKRIM_CAKE;
		} else {
			return null;
		}
	}

	public static void injectSmeltedFood(ItemSmeltedEvent event) {
		injectFakeFood(event, event.smelting, event.player);
	}

	public static void injectCraftedFood(ItemCraftedEvent event) {
		Item targetItem = event.crafting.getItem();
		if (targetItem != null && targetItem == ModItems.ANGEL_CAKE) {
			if (!Skills.canCraft(event.player, Skills.COOKING, 100)) {
				Skills.replaceWithComponents(event);
			} else {
				injectFakeFood(event, event.crafting, event.player);
			}
		} else {
			injectFakeFood(event, event.crafting, event.player);
		}
	}

	/**
	 * The hackiest of hacks. Why does this always happen.
	 */
	public static void saveItemNumber(PlayerContainerEvent.Open event) {
		Container please = event.getContainer();
		if (please instanceof ContainerFurnace) {
			Slot output = please.getSlot(2);
			ItemStack yas = output.getStack();
			if (yas != null) {
				EntityPlayer player = event.getEntityPlayer();
				if (player != null && player.hasCapability(Skills.COOKING, EnumFacing.NORTH)) {
					SkillCooking cooking = (SkillCooking) player.getCapability(Skills.COOKING, EnumFacing.NORTH);
					cooking.lastItemNumber = Obfuscation.getStackSize(yas);
				}
			}
		}
	}

	public static void fireCook(LivingDeathEvent event) {
		Entity targetEntity = event.getEntity();
		DamageSource source = event.getSource();
		Entity entity = source.getEntity();
		if (entity instanceof EntityPlayer) {
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
					if (entityFoodMap.containsKey(targetEntity.getClass())) {
						int cookingXp = (int) (fireCookedMult * xpMap.get(entityFoodMap.get(targetEntity.getClass())));
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
			if (player.world.getTotalWorldTime() % CHECK_TICKS == 0L) {
				if (player.hasCapability(Skills.COOKING, EnumFacing.NORTH)) {
					SkillCooking cooking = (SkillCooking) player.getCapability(Skills.COOKING, EnumFacing.NORTH);
					if (cooking.hasAngel && cooking.currentTicks > 0) {
						cooking.currentTicks -= (int) CHECK_TICKS;
					} else {
						cooking.hasAngel = false;
						cooking.currentTicks = 0;
						if (!player.capabilities.isCreativeMode) {
							player.capabilities.allowFlying = false;
							player.capabilities.isFlying = false;
						}
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
	}

}
