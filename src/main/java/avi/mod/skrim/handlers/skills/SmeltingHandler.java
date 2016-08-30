package avi.mod.skrim.handlers.skills;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemSmeltedEvent;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.smelting.SkillSmelting;

public class SmeltingHandler {

  @SubscribeEvent
  public void onItemSmelted(ItemSmeltedEvent event) {
  	if (event.player != null && event.player.hasCapability(Skills.SMELTING, EnumFacing.NORTH)) {
  		SkillSmelting smelting = (SkillSmelting) event.player.getCapability(Skills.SMELTING, EnumFacing.NORTH);
	    if (smelting.validSmeltingTarget(event.smelting)) {
	      int stackSize = (event.smelting.stackSize == 0) ? smelting.lastItemNumber : event.smelting.stackSize;
	      int addItemSize = (int) (smelting.extraIngot() * stackSize); // OOO
	      if (addItemSize > 0) {
	        ItemStack newStack = new ItemStack(event.smelting.getItem(), addItemSize);
	        event.player.inventory.addItemStackToInventory(newStack);
	      }
	      if (event.player instanceof EntityPlayerMP) {
	        // Give xp for bonus items too!
	        smelting.xp += (stackSize + addItemSize) * smelting.getXp(smelting.getSmeltingName(event.smelting));
	        smelting.levelUp((EntityPlayerMP) event.player);
	      }
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
