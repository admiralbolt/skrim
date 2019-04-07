package avi.mod.skrim.inventory;

import avi.mod.skrim.tileentity.MegaChestTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;


/**
 * The container logic for the mega chest.
 */
public class MegaChestContainer extends Container {

  private MegaChestTileEntity entity;

  public MegaChestContainer(IInventory playerInventory, MegaChestTileEntity entity) {
    this.entity = entity;
    entity.openInventory(((InventoryPlayer) playerInventory).player);

    int inventoryStartX = 8 + 7 * 18;
    int inventoryStartY = 12 + 18 * 9;

    int chestStartX = 8;
    int chestStartY = 7;

    // Tile Entity, Slot 0-8, Slot IDs 0-206
    for (int y = 0; y < 9; ++y) {
      for (int x = 0; x < 23; ++x) {
        this.addSlotToContainer(new Slot(entity, x + y * 23, chestStartX + x * 18, chestStartY + y * 18));
      }
    }

    // Player Inventory, Slot 9-35, Slot IDs 207-233
    for (int y = 0; y < 3; ++y) {
      for (int x = 0; x < 9; ++x) {
        this.addSlotToContainer(new Slot(playerInventory, x + y * 9 + 9, inventoryStartX + x * 18, inventoryStartY + y * 18));
      }
    }

    // Player Inventory, Slot 0-8, Slot IDs 234-242
    for (int x = 0; x < 9; ++x) {
      this.addSlotToContainer(new Slot(playerInventory, x, inventoryStartX + x * 18, inventoryStartY + 10 + 16 * 3));
    }

  }

  @Override
  public boolean canInteractWith(@Nonnull EntityPlayer player) {
    return this.entity.isUsableByPlayer(player);
  }

  @Override
  @Nonnull
  public ItemStack transferStackInSlot(EntityPlayer playerIn, int fromSlot) {
    Slot slot = this.inventorySlots.get(fromSlot);

    if (slot == null || !slot.getHasStack()) return ItemStack.EMPTY;

    ItemStack current = slot.getStack();
    ItemStack previous = current.copy();

    if (fromSlot < 207 && !this.mergeItemStack(current, 207, 243, true)) return ItemStack.EMPTY;
    if (!this.mergeItemStack(current, 0, 207, false)) return ItemStack.EMPTY;

    if (current.getCount() == 0) {
      slot.putStack(ItemStack.EMPTY);
    } else {
      slot.onSlotChanged();
    }

    if (current.getCount() == previous.getCount()) return ItemStack.EMPTY;

    slot.onTake(playerIn, current);
    return previous;
  }

  @Override
  public void onContainerClosed(EntityPlayer player) {
    super.onContainerClosed(player);
    entity.closeInventory(player);
  }

}
