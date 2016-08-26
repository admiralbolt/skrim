package avi.mod.skrim.skills.smelting;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipesFood;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemSmeltedEvent;
import avi.mod.skrim.Utils;
import avi.mod.skrim.items.CustomFood;
import avi.mod.skrim.items.ModItems;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.cooking.SkillCooking;
import net.minecraftforge.common.ForgeHooks;

public class SkillSmelting extends Skill implements ISkillSmelting {

	public static SkillStorage<ISkillSmelting> skillStorage = new SkillStorage<ISkillSmelting>();
	public static Map<String, Integer> xpMap;
	static {
		xpMap = new HashMap<String, Integer>();
		xpMap.put("tile.stone", 1);
		xpMap.put("tile.stonebricksmooth", 2);
		xpMap.put("item.netherbrick", 3); // nether bonus
		xpMap.put("tile.glass", 5);
		xpMap.put("item.brick", 10);
		xpMap.put("tile.clayhardened", 42); // xp bonus for crafting, also 42 is based
		xpMap.put("item.ingotiron", 20);
		xpMap.put("item.ingotgold", 50); // Woooooo gold!
	}

	private int lastItemNumber;

	public SkillSmelting() {
		this(1, 0);
	}

	public SkillSmelting(int level, int currentXp) {
		super("Smelting", level, currentXp);
		this.iconTexture = new ResourceLocation("skrim", "textures/guis/skills/smelting.png");
	}

	private int getXp(String blockName) {
		return (xpMap.containsKey(blockName)) ? xpMap.get(blockName) : 0;
	}

	/**
	 * These methods are static so they can be accessed from the CustomFood
	 * class onFoodEaten() method.
	 */

	public double extraIngot() {
		return 0.03 * this.level;
	}

	public String getSmeltingName(ItemStack stack) {
		return Utils.snakeCase(stack.getItem().getUnlocalizedName());
	}

	@Override
	public List<String> getToolTip() {
		DecimalFormat fmt = new DecimalFormat("0.0");
		List<String> tooltip = new ArrayList<String>();
		tooltip.add("Smelting provides §a+" + fmt.format(this.extraIngot() * 100) + "%§r items.");
		tooltip.add("Shift clicking crafted items provides §amostly accurate extra items§r.");
		tooltip.add("§eWe swear this is a bug and not a feature...§r");
		return tooltip;
	}

  private boolean validSmeltingTarget(ItemStack stack) {
		Item item = stack.getItem();
		return xpMap.containsKey(Utils.snakeCase(item.getUnlocalizedName()));
  }

  @SubscribeEvent
  public void onItemSmelted(ItemSmeltedEvent event) {
		if (this.validSmeltingTarget(event.smelting) && event.player != null && event.player.hasCapability(Skills.SMELTING, EnumFacing.NORTH)) {
			SkillSmelting smelting = (SkillSmelting) event.player.getCapability(Skills.SMELTING, EnumFacing.NORTH);
			int stackSize = (event.smelting.stackSize == 0) ? smelting.lastItemNumber : event.smelting.stackSize;
			int addItemSize = (int) (smelting.extraIngot() * stackSize); // OOO
			if (addItemSize > 0) {
				ItemStack newStack = new ItemStack(event.smelting.getItem(), addItemSize);
				event.player.inventory.addItemStackToInventory(newStack);
			}
			if (event.player instanceof EntityPlayerMP) {
				// Give xp for bonus items too!
				smelting.xp += (stackSize + addItemSize) * this.getXp(this.getSmeltingName(event.smelting));
				smelting.levelUp((EntityPlayerMP) event.player);
			}
		}
  }

	/**
	 * The hackiest of hacks.  Why does this always happen.
	 */
  @SubscribeEvent
  public void onContainerEvent(PlayerContainerEvent.Open event) {
		Container please = event.getContainer();
		if (please instanceof ContainerFurnace) {
			Slot output = please.getSlot(2);
			ItemStack yas = output.getStack();
			if (yas != null) {
				EntityPlayer player = event.getEntityPlayer();
				if (player != null && player.hasCapability(Skills.SMELTING, EnumFacing.NORTH)) {
					SkillSmelting smelting = (SkillSmelting) player.getCapability(Skills.SMELTING, EnumFacing.NORTH);
					smelting.lastItemNumber = yas.stackSize;
				}
			}
		}
	}

}
