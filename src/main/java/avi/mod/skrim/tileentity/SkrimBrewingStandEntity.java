package avi.mod.skrim.tileentity;

import avi.mod.skrim.blocks.misc.SkrimBrewingStand;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.brewing.SkillBrewing;
import avi.mod.skrim.skills.brewing.SkrimPotionRecipes;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.walkers.ItemStackDataLists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.UUID;

/**
 * I'm making some executive decisions about how brewing will work:
 * <p>
 * When a player interacts with a brewing stand they are set as the 'active player'.
 * When brewing is started (the timer kicks off) the 'active player' is marked as the `brewing player`.
 * The brewing player's skill level will be used for potion adjustments, and they will get experience when potions
 * are brewed, not when
 * potions are pulled out of the brewing stand. I think this is the best choice to preventing griefing from brewing.
 * <p>
 * Forge does provide access via some brewing events, but the base game potions don't provide the functionality I'm
 * looking for. For
 * some reason, they decided it was a good idea to hard-code effect + duration combinations. So if you adjust a
 * potions duration by 1
 * second for example, the name / color doesn't get loaded correctly. Additionally, shift-clicking is still fuck for
 * the brewing event.
 * It doesn't give any insight into what the original potion was, so modification would be impossible if a player
 * shift clicked items out.
 * For these reasons, I've decided to make a brand new potion item that will be more dynamic in name / color loading.
 * Finally, I wanted to
 * increase the speed of the brewing event itself, which can't be done without attaching the player to the
 * tile-entity in some way anyway.
 */
public class SkrimBrewingStandEntity extends TileEntityLockable implements ITickable, ISidedInventory {
  /**
   * an array of the input slot indices
   */
  private static final int[] SLOTS_FOR_UP = new int[]{3};
  private static final int[] SLOTS_FOR_DOWN = new int[]{0, 1, 2, 3};
  /**
   * an array of the output slot indices
   */
  private static final int[] OUTPUT_SLOTS = new int[]{0, 1, 2};
  /**
   * Slot for the blaze powder.
   */
  private static final int FUEL_SLOT = 4;
  /**
   * The ItemStacks currently placed in the slots of the brewing stand
   */
  private NonNullList<ItemStack> brewingItemStacks = NonNullList.<ItemStack>withSize(5, ItemStack.EMPTY);
  private int brewTime;

  // A double version to better keep track of "real" progress.
  private double doubleBrewTime;
  private int actualBrewTime;
  /**
   * an integer with each bit specifying whether that slot of the stand contains a potion
   */
  private boolean[] filledSlots;
  /**
   * used to check if the current ingredient has been removed from the brewing stand during brewing
   */
  private Item ingredientID;
  private String customName;
  private int fuel;

  private EntityPlayer activePlayer;
  private EntityPlayer brewingPlayer;

  public void setPlayer(EntityPlayer player) {
    this.activePlayer = player;
  }

  /**
   * Get the name of this object. For players this returns their username
   */
  public String getName() {
    return this.hasCustomName() ? this.customName : "container.brewing";
  }

  /**
   * Returns true if this thing is named
   */
  public boolean hasCustomName() {
    return this.customName != null && !this.customName.isEmpty();
  }

  public void setName(String name) {
    this.customName = name;
  }

  /**
   * Returns the number of slots in the inventory.
   */
  public int getSizeInventory() {
    return this.brewingItemStacks.size();
  }

  public boolean isEmpty() {
    for (ItemStack itemstack : this.brewingItemStacks) {
      if (!itemstack.isEmpty()) {
        return false;
      }
    }

    return true;
  }

  /**
   * So, the ui for brewing is hard-coded to expect 400 ticks of brewing. If we increase the speed of brewing by
   * reducing the number of
   * ticks, it looks weird because the arrow starts half-filled instead of filling more quickly. To make it seem more
   * natural we will
   * update brewing time by more than 1 per tick.
   */
  private void updateBrewTime() {
    this.doubleBrewTime -= 400 / Math.max(1, this.actualBrewTime);
    this.brewTime = Math.max((int) this.doubleBrewTime, 0);
  }

  /**
   * Like the old updateEntity(), except more generic.
   */
  public void update() {
    ItemStack itemstack = this.brewingItemStacks.get(4);

    if (this.fuel <= 0 && itemstack.getItem() == Items.BLAZE_POWDER) {
      this.fuel = 20;
      itemstack.shrink(1);
      this.markDirty();
    }

    boolean canBrew = this.canBrew();
    ItemStack itemstack1 = this.brewingItemStacks.get(3);

    if (this.brewTime > 0) {
      this.updateBrewTime();

      if (this.brewTime == 0 && canBrew) {
        this.brewPotions();
        this.markDirty();
      } else if (!canBrew) {
        this.brewTime = 0;
        this.markDirty();
      } else if (this.ingredientID != itemstack1.getItem()) {
        this.brewTime = 0;
        this.markDirty();
      }
    } else if (canBrew && this.fuel > 0) {
      --this.fuel;
      this.brewingPlayer = this.activePlayer;
      SkillBrewing brewing = Skills.getSkill(this.brewingPlayer, Skills.BREWING, SkillBrewing.class);
      this.brewTime = 400;
      this.doubleBrewTime = 400;
      this.actualBrewTime = Math.max(1, this.brewTime - (int) (this.brewTime * brewing.brewSpeed()));
      this.ingredientID = itemstack1.getItem();
      this.markDirty();
    }

    if (!this.world.isRemote) {
      boolean[] aboolean = this.createFilledSlotsArray();

      if (!Arrays.equals(aboolean, this.filledSlots)) {
        this.filledSlots = aboolean;
        IBlockState iblockstate = this.world.getBlockState(this.getPos());

        if (!(iblockstate.getBlock() instanceof SkrimBrewingStand)) {
          return;
        }

        for (int i = 0; i < SkrimBrewingStand.HAS_BOTTLE.length; ++i) {
          iblockstate = iblockstate.withProperty(SkrimBrewingStand.HAS_BOTTLE[i], Boolean.valueOf(aboolean[i]));
        }

        this.world.setBlockState(this.pos, iblockstate, 2);
      }
    }
  }

  /**
   * Creates an array of boolean values, each value represents a potion input slot, value is true if the slot is not
   * null.
   */
  public boolean[] createFilledSlotsArray() {
    boolean[] aboolean = new boolean[3];

    for (int i = 0; i < 3; ++i) {
      if (!((ItemStack) this.brewingItemStacks.get(i)).isEmpty()) {
        aboolean[i] = true;
      }
    }

    return aboolean;
  }

  /**
   * canBrew() is called before brewing actually starts, so brewingPlayer is still null. Use activePlayer instead.
   */
  private boolean canBrew() {
    ItemStack ingredient = brewingItemStacks.get(3);
    for (int index : OUTPUT_SLOTS) {
      if (SkrimPotionRecipes.hasOutput(this.activePlayer, brewingItemStacks.get(index), ingredient)) return true;
    }

    return false;
  }

  private void brewPotions() {
    ItemStack ingredient = this.brewingItemStacks.get(3);

    for (int i : OUTPUT_SLOTS) {
      ItemStack output = SkrimPotionRecipes.getOutput(this.brewingPlayer, this.brewingItemStacks.get(i), ingredient);

      System.out.println("output[" + i + "]: " + output);
      System.out.println("getPotionType(" + i + "): " + PotionUtils.getPotionTypeFromNBT(output.getTagCompound()).toString());
      System.out.println("effects[" + i + "]: " + PotionUtils.getEffectsFromStack(output));

      if (!output.isEmpty()) {
        this.brewingItemStacks.set(i, output);
      }
    }

    ingredient.shrink(1);
    BlockPos blockpos = this.getPos();

    if (ingredient.getItem().hasContainerItem(ingredient)) {
      ItemStack itemstack1 = ingredient.getItem().getContainerItem(ingredient);

      if (ingredient.isEmpty()) {
        ingredient = itemstack1;
      } else {
        InventoryHelper.spawnItemStack(this.world, (double) blockpos.getX(), (double) blockpos.getY(),
            (double) blockpos.getZ(),
            itemstack1);
      }
    }

    this.brewingItemStacks.set(3, ingredient);
    this.world.playEvent(1035, blockpos, 0);
  }

  public static void registerFixesBrewingStand(DataFixer fixer) {
    fixer.registerWalker(FixTypes.BLOCK_ENTITY, new ItemStackDataLists(SkrimBrewingStandEntity.class, new String[]{
        "Items"}));
  }

  public void readFromNBT(NBTTagCompound compound) {
    super.readFromNBT(compound);
    this.brewingItemStacks = NonNullList.<ItemStack>withSize(this.getSizeInventory(), ItemStack.EMPTY);
    ItemStackHelper.loadAllItems(compound, this.brewingItemStacks);
    this.brewTime = compound.getShort("BrewTime");

    if (compound.hasKey("CustomName", 8)) {
      this.customName = compound.getString("CustomName");
    }

    this.fuel = compound.getByte("Fuel");
    if (compound.hasKey("Player")) {
      this.brewingPlayer = this.world.getPlayerEntityByUUID(UUID.fromString(compound.getString("Player")));
    }
  }

  @Override
  public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
    return oldState.getBlock() != newState.getBlock();
  }

  public NBTTagCompound writeToNBT(NBTTagCompound compound) {
    super.writeToNBT(compound);
    compound.setShort("BrewTime", (short) this.brewTime);
    ItemStackHelper.saveAllItems(compound, this.brewingItemStacks);

    if (this.hasCustomName()) {
      compound.setString("CustomName", this.customName);
    }

    compound.setByte("Fuel", (byte) this.fuel);
    if (this.brewingPlayer != null) {
      compound.setString("Player", this.brewingPlayer.getUniqueID().toString());
    }
    return compound;
  }

  /**
   * Returns the stack in the given slot.
   */
  public ItemStack getStackInSlot(int index) {
    return index >= 0 && index < this.brewingItemStacks.size() ? (ItemStack) this.brewingItemStacks.get(index) :
        ItemStack.EMPTY;
  }

  /**
   * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
   */
  public ItemStack decrStackSize(int index, int count) {
    return ItemStackHelper.getAndSplit(this.brewingItemStacks, index, count);
  }

  /**
   * Removes a stack from the given slot and returns it.
   */
  public ItemStack removeStackFromSlot(int index) {
    return ItemStackHelper.getAndRemove(this.brewingItemStacks, index);
  }

  /**
   * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
   */
  public void setInventorySlotContents(int index, ItemStack stack) {
    if (index >= 0 && index < this.brewingItemStacks.size()) {
      this.brewingItemStacks.set(index, stack);
    }
  }

  /**
   * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
   */
  public int getInventoryStackLimit() {
    return 64;
  }

  /**
   * Don't rename this method to canInteractWith due to conflicts with Container
   */
  public boolean isUsableByPlayer(EntityPlayer player) {
    if (this.world.getTileEntity(this.pos) != this) {
      return false;
    } else {
      return player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D,
          (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }
  }

  public void openInventory(EntityPlayer player) {
  }

  public void closeInventory(EntityPlayer player) {
  }

  /**
   * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot. For
   * guis use Slot.isItemValid
   */
  public boolean isItemValidForSlot(int index, ItemStack stack) {
    if (index == 3) {
      return net.minecraftforge.common.brewing.BrewingRecipeRegistry.isValidIngredient(stack);
    } else {
      Item item = stack.getItem();

      if (index == 4) {
        return item == Items.BLAZE_POWDER;
      } else {
        return net.minecraftforge.common.brewing.BrewingRecipeRegistry.isValidInput(stack) && this.getStackInSlot(index).isEmpty();
      }
    }
  }

  public int[] getSlotsForFace(EnumFacing side) {
    if (side == EnumFacing.UP) {
      return SLOTS_FOR_UP;
    } else {
      return side == EnumFacing.DOWN ? SLOTS_FOR_DOWN : OUTPUT_SLOTS;
    }
  }

  /**
   * Returns true if automation can insert the given item in the given slot from the given side.
   */
  public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
    return this.isItemValidForSlot(index, itemStackIn);
  }

  /**
   * Returns true if automation can extract the given item in the given slot from the given side.
   */
  public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
    if (index == 3) {
      return stack.getItem() == Items.GLASS_BOTTLE;
    } else {
      return true;
    }
  }

  public String getGuiID() {
    return "minecraft:brewing_stand";
  }

  public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
    return new ContainerBrewingStand(playerInventory, this);
  }

  public int getField(int id) {
    switch (id) {
      case 0:
        return this.brewTime;
      case 1:
        return this.fuel;
      default:
        return 0;
    }
  }

  public void setField(int id, int value) {
    switch (id) {
      case 0:
        this.brewTime = value;
        break;
      case 1:
        this.fuel = value;
    }
  }

  net.minecraftforge.items.IItemHandler handlerInput = new net.minecraftforge.items.wrapper.SidedInvWrapper(this,
      net.minecraft.util.EnumFacing.UP);
  net.minecraftforge.items.IItemHandler handlerOutput = new net.minecraftforge.items.wrapper.SidedInvWrapper(this,
      net.minecraft.util.EnumFacing.DOWN);
  net.minecraftforge.items.IItemHandler handlerSides = new net.minecraftforge.items.wrapper.SidedInvWrapper(this,
      net.minecraft.util.EnumFacing.NORTH);

  @SuppressWarnings("unchecked")
  @Override
  @javax.annotation.Nullable
  public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability,
                             @javax.annotation.Nullable net.minecraft.util.EnumFacing facing) {
    if (facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
      if (facing == EnumFacing.UP)
        return (T) handlerInput;
      else if (facing == EnumFacing.DOWN)
        return (T) handlerOutput;
      else
        return (T) handlerSides;
    }
    return super.getCapability(capability, facing);
  }

  public int getFieldCount() {
    return 2;
  }

  public void clear() {
    this.brewingItemStacks.clear();
  }
}
