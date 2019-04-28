package avi.mod.skrim.tileentity;

import avi.mod.skrim.blocks.SkrimBlocks;
import avi.mod.skrim.blocks.misc.MegaChest;
import avi.mod.skrim.init.SkrimSoundEvents;
import avi.mod.skrim.inventory.MegaChestContainer;
import avi.mod.skrim.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.Collections;
import java.util.Comparator;

public class MegaChestTileEntity extends TileEntity implements IInventory, ITickable {

  private NonNullList<ItemStack> inventory;
  private String customName;

  /**
   * The previous angle of the lid (between 0 and 1)
   */
  public float prevLidAngle = 0.0F;
  /**
   * The current angle of the lid (between 0 and 1)
   */
  public float lidAngle = 0.0F;
  public int numPlayersUsing = 0;
  /**
   * Server sync counter (once per 20 ticks)
   */
  private int ticksSinceSync;

  public MegaChestTileEntity() {
    this.inventory = NonNullList.<ItemStack>withSize(this.getSizeInventory(), ItemStack.EMPTY);
  }

  public String getCustomName() {
    return this.customName;
  }

  public void setCustomName(String customName) {
    this.customName = customName;
  }

  public void sort() {
    Collections.sort(this.inventory, new Comparator<ItemStack>() {
      @Override
      public int compare(ItemStack stack1, ItemStack stack2) {
        if (stack1 == ItemStack.EMPTY && stack2 == ItemStack.EMPTY) {
          return 0;
        } else if (stack1 == ItemStack.EMPTY) {
          return 1;
        } else if (stack2 == ItemStack.EMPTY) {
          return -1;
        } else {
          return stack1.getDisplayName().compareTo(stack2.getDisplayName());
        }
      }
    });

    // After the items are sorted, they'll be stacked next to each at the beginning of the inventory, but won't be compact. This next step
    // merges stacks when available. This is accomplished by maintaining two pointers, an input slot and an output slot:
    // 1. If the output slot is empty, move the input slot to it, and update the read pointer.
    // 2. If the input / output slots can't be merged, increment the write pointer.
    // 3. Otherwise, merge to the best of our abilities. Increment read & write pointers as slots fill up.

    int writeIndex = 0;
    int readIndex = 1;
    ItemStack read;
    while (readIndex < this.inventory.size()) {
      read = this.inventory.get(readIndex);
      ItemStack write = this.inventory.get(writeIndex);

      if (write.isEmpty()) {
        moveStack(readIndex, writeIndex);
        readIndex++;
        continue;
      }

      // If the stacks can't be merged skip ahead to the current read location.
      if (!Utils.areSimilarStacks(write, read)) {
        writeIndex++;
        if (writeIndex == readIndex) {
          readIndex++;
        }
        continue;
      }

      // Merge as many stacks as we can:
      mergeFrom(read, write);
      if (write.getCount() == write.getMaxStackSize()) {
        writeIndex++;
        if (writeIndex == readIndex) {
          readIndex++;
        }
      }
      if (read.isEmpty()) {
        readIndex++;
      }
    }
    this.markDirty();
  }

  private static void mergeFrom(ItemStack from, ItemStack to) {
    if (Utils.areSimilarStacks(from, to)) {
      int maxSize = to.getMaxStackSize();
      int transferSize = Math.min(maxSize - to.getCount(), from.getCount());
      to.setCount(to.getCount() + transferSize);
      from.setCount(from.getCount() - transferSize);
    }
  }

  private void moveStack(int fromIndex, int toIndex) {
    this.inventory.set(toIndex, this.inventory.get(fromIndex));
    this.inventory.set(fromIndex, ItemStack.EMPTY);
  }

  @Override
  public boolean hasCustomName() {
    return this.customName != null && !this.customName.equals("");
  }

  @Override
  public String getName() {
    return (this.hasCustomName()) ? this.customName : "container.mega_chest";
  }

  @Override
  public int getSizeInventory() {
    return 207;
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public ItemStack getStackInSlot(int index) {
    if (index < 0 || index >= this.getSizeInventory()) {
      return ItemStack.EMPTY;
    }
    return this.inventory.get(index);
  }

  @Override
  public ItemStack decrStackSize(int index, int count) {
    if (this.getStackInSlot(index) != ItemStack.EMPTY) {
      ItemStack itemstack;

      if (this.getStackInSlot(index).getCount() <= count) {
        itemstack = this.getStackInSlot(index);
        this.setInventorySlotContents(index, ItemStack.EMPTY);
        this.markDirty();
        return itemstack;
      } else {
        itemstack = this.getStackInSlot(index).splitStack(count);

        if (this.getStackInSlot(index).getCount() <= 0) {
          this.setInventorySlotContents(index, ItemStack.EMPTY);
        } else {
          //Just to show that changes happened
          this.setInventorySlotContents(index, this.getStackInSlot(index));
        }

        this.markDirty();
        return itemstack;
      }
    } else {
      return ItemStack.EMPTY;
    }
  }

  @Override
  public void setInventorySlotContents(int index, ItemStack stack) {
    if (index < 0 || index >= this.getSizeInventory())
      return;

    if (stack != null && stack.getCount() > this.getInventoryStackLimit()) {
      stack.setCount(this.getInventoryStackLimit());
    }

    if (stack != null && stack.getCount() == 0) {
      stack = ItemStack.EMPTY;
    }

    this.inventory.set(index, stack);
    this.markDirty();
  }

  @Override
  public int getInventoryStackLimit() {
    return 64;
  }

  @Override
  public boolean isUsableByPlayer(EntityPlayer player) {
    return this.world.getTileEntity(this.getPos()) == this && player.getDistanceSq(this.pos.add(0.5, 0.5, 0.5)) <= 64;
  }

  @Override
  public boolean isItemValidForSlot(int index, ItemStack stack) {
    return true;
  }

  @Override
  public int getField(int id) {
    return 0;
  }

  @Override
  public void setField(int id, int value) {

  }

  @Override
  public int getFieldCount() {
    return 0;
  }

  @Override
  public void clear() {
    for (int i = 0; i < this.getSizeInventory(); i++) {
      this.removeStackFromSlot(i);
    }
  }

  @Override
  public ItemStack removeStackFromSlot(int index) {
    ItemStack stack = this.getStackInSlot(index);
    this.setInventorySlotContents(index, ItemStack.EMPTY);
    return stack;
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
    super.writeToNBT(nbt);

    NBTTagList list = new NBTTagList();
    for (int i = 0; i < this.getSizeInventory(); ++i) {
      if (this.getStackInSlot(i) != ItemStack.EMPTY) {
        NBTTagCompound stackTag = new NBTTagCompound();
        stackTag.setByte("Slot", (byte) i);
        this.getStackInSlot(i).writeToNBT(stackTag);
        list.appendTag(stackTag);
      }
    }
    nbt.setTag("Items", list);

    if (this.hasCustomName()) {
      nbt.setString("CustomName", this.getCustomName());
    }
    return nbt;
  }

  @Override
  public void readFromNBT(NBTTagCompound nbt) {
    super.readFromNBT(nbt);

    NBTTagList list = nbt.getTagList("Items", 10);
    for (int i = 0; i < list.tagCount(); ++i) {
      NBTTagCompound stackTag = list.getCompoundTagAt(i);
      int slot = stackTag.getByte("Slot") & 255;
      this.setInventorySlotContents(slot, new ItemStack(stackTag));
    }

    if (nbt.hasKey("CustomName", 8)) {
      this.setCustomName(nbt.getString("CustomName"));
    }
  }

  @Override
  public void update() {
    int i = this.pos.getX();
    int j = this.pos.getY();
    int k = this.pos.getZ();
    ++this.ticksSinceSync;


    if (!this.world.isRemote && this.numPlayersUsing != 0 && (this.ticksSinceSync + i + j + k) % 200 == 0) {
      this.numPlayersUsing = 0;
      float f = 5.0F;

      for (EntityPlayer entityplayer : this.world.getEntitiesWithinAABB(EntityPlayer.class,
          new AxisAlignedBB((double) ((float) i - 5.0F), (double) ((float) j - 5.0F), (double) ((float) k - 5.0F),
              (double) ((float) (i + 1) + 5.0F),
              (double) ((float) (j + 1) + 5.0F), (double) ((float) (k + 1) + 5.0F)))) {
        if (entityplayer.openContainer instanceof MegaChestContainer) {
          this.numPlayersUsing++;
        }
      }
    }

    this.prevLidAngle = this.lidAngle;
    float f1 = 0.1F;

    if (this.numPlayersUsing > 0 && this.lidAngle == 0.0F) {
      double d1 = (double) i + 0.5D;
      double d2 = (double) k + 0.5D;

      this.world.playSound((EntityPlayer) null, d1, (double) j + 0.5D, d2, SkrimSoundEvents.randomZeldaSound(), SoundCategory.BLOCKS, 0.5F,
          this.world.rand.nextFloat() * 0.1F + 0.9F);
    }

    if (this.numPlayersUsing == 0 && this.lidAngle > 0.0F || this.numPlayersUsing > 0 && this.lidAngle < 1.0F) {
      float f2 = this.lidAngle;

      if (this.numPlayersUsing > 0) {
        this.lidAngle += 0.1F;
      } else {
        this.lidAngle -= 0.1F;
      }

      if (this.lidAngle > 1.0F) {
        this.lidAngle = 1.0F;
      }

      float f3 = 0.5F;

      if (this.lidAngle < 0.5F && f2 >= 0.5F) {
        double d3 = (double) i + 0.5D;
        double d0 = (double) k + 0.5D;

        this.world.playSound((EntityPlayer) null, d3, (double) j + 0.5D, d0, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F,
            this.world.rand.nextFloat() * 0.1F + 0.9F);
      }

      if (this.lidAngle < 0.0F) {
        this.lidAngle = 0.0F;
      }
    }
  }

  public boolean receiveClientEvent(int id, int numPlayers) {
    if (id == 1) {
      this.numPlayersUsing = numPlayers;
      return true;
    } else {
      return super.receiveClientEvent(id, numPlayers);
    }
  }

  @Override
  public void openInventory(EntityPlayer player) {
    if (!this.world.isRemote) {
      if (!player.isSpectator()) {
        if (this.numPlayersUsing < 0) {
          this.numPlayersUsing = 0;
        }

        this.numPlayersUsing++;
        this.world.addBlockEvent(this.pos, SkrimBlocks.MEGA_CHEST, 1, this.numPlayersUsing);
      }
    }
  }

  @Override
  public void closeInventory(EntityPlayer player) {
    if (!this.world.isRemote) {
      if (!player.isSpectator() && this.getBlockType() instanceof MegaChest) {
        this.numPlayersUsing--;
        this.world.addBlockEvent(this.pos, SkrimBlocks.MEGA_CHEST, 1, this.numPlayersUsing);
      }
    }
  }

}