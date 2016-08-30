package avi.mod.skrim.handlers.skills;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemSmeltedEvent;
import avi.mod.skrim.items.CustomFood;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.cooking.SkillCooking;

public class CookingHandler {

  /**
   * It's like a man in the middle attack with cake!
   */
  public void injectFakeFood(PlayerEvent event, ItemStack stack, EntityPlayer player) {
  	if (player != null && player.hasCapability(Skills.COOKING, EnumFacing.NORTH)) {
  		SkillCooking cooking = (SkillCooking) player.getCapability(Skills.COOKING, EnumFacing.NORTH);
  		String foodName = cooking.getFoodName(stack);
  		if (cooking.validCookingTarget(stack)) {
				CustomFood replaceFood = cooking.getOverwriteFood(cooking.getFoodName(stack));
				if (replaceFood != null) {
					stack.setItem(replaceFood);
	
					NBTTagCompound compound = new NBTTagCompound();
					compound.setInteger("level", cooking.level);
					stack.setTagCompound(compound);
					stack.setStackDisplayName(player.getName() + "'s " + stack.getDisplayName());
	
					if (stack.stackSize == 0) {
						int newStackSize = (event instanceof ItemSmeltedEvent) ? cooking.lastItemNumber : 1;
						ItemStack newStack = new ItemStack(replaceFood, newStackSize);
						NBTTagCompound newCompound = new NBTTagCompound();
						newCompound.setInteger("level", cooking.level);
						newStack.setTagCompound(newCompound);
						newStack.setStackDisplayName(player.getName() + "'s " + newStack.getDisplayName());
						player.inventory.addItemStackToInventory(newStack);
					}
	
					if (player instanceof EntityPlayerMP) {
						cooking.xp += cooking.getXp(foodName);
						cooking.levelUp((EntityPlayerMP) player);
					}
				}
  		}
		}
  }

  @SubscribeEvent
  public void onItemSmelted(ItemSmeltedEvent event) {
		this.injectFakeFood(event, event.smelting, event.player);
  }

	@SubscribeEvent
	public void onItemCrafted(ItemCraftedEvent event) {
		this.injectFakeFood(event, event.crafting, event.player);
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
				if (player != null && player.hasCapability(Skills.COOKING, EnumFacing.NORTH)) {
					SkillCooking cooking = (SkillCooking) player.getCapability(Skills.COOKING, EnumFacing.NORTH);
					cooking.lastItemNumber = yas.stackSize;
				}
			}
		}
	}

}
