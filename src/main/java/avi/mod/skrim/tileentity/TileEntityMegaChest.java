package avi.mod.skrim.tileentity;

import javax.annotation.Nullable;

import avi.mod.skrim.blocks.MegaChest;
import avi.mod.skrim.inventory.ContainerMegaChest;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.walkers.ItemStackDataLists;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class TileEntityMegaChest extends TileEntityChest implements ITickable {
	private NonNullList<ItemStack> chestContents = NonNullList.<ItemStack>func_191197_a(54, ItemStack.field_190927_a);
	/** Determines if the check for adjacent chests has taken place. */
	public boolean adjacentChestChecked;
	/** Contains the chest tile located adjacent to this one (if any) */
	public TileEntityMegaChest adjacentChestZNeg;
	/** Contains the chest tile located adjacent to this one (if any) */
	public TileEntityMegaChest adjacentChestXPos;
	/** Contains the chest tile located adjacent to this one (if any) */
	public TileEntityMegaChest adjacentChestXNeg;
	/** Contains the chest tile located adjacent to this one (if any) */
	public TileEntityMegaChest adjacentChestZPos;
	/** The current angle of the lid (between 0 and 1) */
	public float lidAngle;
	/** The angle of the lid last tick */
	public float prevLidAngle;
	/** The number of players currently using this chest */
	public int numPlayersUsing;
	/** Server sync counter (once per 20 ticks) */
	private int ticksSinceSync;
	private BlockChest.Type cachedChestType;

	public TileEntityMegaChest() {
	}

	public TileEntityMegaChest(BlockChest.Type typeIn) {
		this.cachedChestType = typeIn;
	}

	/**
	 * Returns the number of slots in the inventory.
	 */
	@Override
	public int getSizeInventory() {
		return 54;
	}

	@Override
	protected NonNullList<ItemStack> func_190576_q() {
		return this.chestContents;
	}

	@SuppressWarnings("incomplete-switch")
	private void setNeighbor(TileEntityMegaChest chestTe, EnumFacing side) {
		if (chestTe.isInvalid()) {
			this.adjacentChestChecked = false;
		} else if (this.adjacentChestChecked) {
			switch (side) {
				case NORTH:

					if (this.adjacentChestZNeg != chestTe) {
						this.adjacentChestChecked = false;
					}

					break;
				case SOUTH:

					if (this.adjacentChestZPos != chestTe) {
						this.adjacentChestChecked = false;
					}

					break;
				case EAST:

					if (this.adjacentChestXPos != chestTe) {
						this.adjacentChestChecked = false;
					}

					break;
				case WEST:

					if (this.adjacentChestXNeg != chestTe) {
						this.adjacentChestChecked = false;
					}
			}
		}
	}

	/**
	 * Performs the check for adjacent chests to determine if this chest is
	 * double or not.
	 */
	@Override
	public void checkForAdjacentChests() {
		if (!this.adjacentChestChecked) {
			this.adjacentChestChecked = true;
			this.adjacentChestXNeg = this.getAdjacentChest(EnumFacing.WEST);
			this.adjacentChestXPos = this.getAdjacentChest(EnumFacing.EAST);
			this.adjacentChestZNeg = this.getAdjacentChest(EnumFacing.NORTH);
			this.adjacentChestZPos = this.getAdjacentChest(EnumFacing.SOUTH);
		}
	}

	@Override
	@Nullable
	protected TileEntityMegaChest getAdjacentChest(EnumFacing side) {
		BlockPos blockpos = this.pos.offset(side);

		if (this.isMegaChestAt(blockpos)) {
			TileEntity tileentity = this.worldObj.getTileEntity(blockpos);

			if (tileentity instanceof TileEntityMegaChest) {
				TileEntityMegaChest tileentitychest = (TileEntityMegaChest) tileentity;
				tileentitychest.setNeighbor(this, side.getOpposite());
				return tileentitychest;
			}
		}

		return null;
	}

	private boolean isMegaChestAt(BlockPos posIn) {
		if (this.worldObj == null) {
			return false;
		} else {
			Block block = this.worldObj.getBlockState(posIn).getBlock();
			return block instanceof MegaChest && ((MegaChest) block).chestType == this.getChestType();
		}
	}

	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
		this.fillWithLoot(playerIn);
		return new ContainerMegaChest(playerInventory, this, playerIn);
	}

	/**
	 * Like the old updateEntity(), except more generic.
	 */
	@Override
	public void update() {
		this.checkForAdjacentChests();
		int i = this.pos.getX();
		int j = this.pos.getY();
		int k = this.pos.getZ();
		++this.ticksSinceSync;

		if (!this.worldObj.isRemote && this.numPlayersUsing != 0 && (this.ticksSinceSync + i + j + k) % 200 == 0) {
			this.numPlayersUsing = 0;
			float f = 5.0F;

			for (EntityPlayer entityplayer : this.worldObj.getEntitiesWithinAABB(EntityPlayer.class,
					new AxisAlignedBB((double) ((float) i - 5.0F), (double) ((float) j - 5.0F), (double) ((float) k - 5.0F), (double) ((float) (i + 1) + 5.0F),
							(double) ((float) (j + 1) + 5.0F), (double) ((float) (k + 1) + 5.0F)))) {
				if (entityplayer.openContainer instanceof ContainerChest) {
					IInventory iinventory = ((ContainerChest) entityplayer.openContainer).getLowerChestInventory();

					if (iinventory == this || iinventory instanceof InventoryLargeChest && ((InventoryLargeChest) iinventory).isPartOfLargeChest(this)) {
						++this.numPlayersUsing;
					}
				}
			}
		}

		this.prevLidAngle = this.lidAngle;
		float f1 = 0.1F;

		if (this.numPlayersUsing > 0 && this.lidAngle == 0.0F && this.adjacentChestZNeg == null && this.adjacentChestXNeg == null) {
			double d1 = (double) i + 0.5D;
			double d2 = (double) k + 0.5D;

			if (this.adjacentChestZPos != null) {
				d2 += 0.5D;
			}

			if (this.adjacentChestXPos != null) {
				d1 += 0.5D;
			}

			this.worldObj.playSound((EntityPlayer) null, d1, (double) j + 0.5D, d2, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5F,
					this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
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

			if (this.lidAngle < 0.5F && f2 >= 0.5F && this.adjacentChestZNeg == null && this.adjacentChestXNeg == null) {
				double d3 = (double) i + 0.5D;
				double d0 = (double) k + 0.5D;

				if (this.adjacentChestZPos != null) {
					d0 += 0.5D;
				}

				if (this.adjacentChestXPos != null) {
					d3 += 0.5D;
				}

				this.worldObj.playSound((EntityPlayer) null, d3, (double) j + 0.5D, d0, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F,
						this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
			}

			if (this.lidAngle < 0.0F) {
				this.lidAngle = 0.0F;
			}
		}
	}

	public static void registerFixesChest(DataFixer fixer) {
		fixer.registerWalker(FixTypes.BLOCK_ENTITY, new ItemStackDataLists(TileEntityChest.class, new String[] { "Items" }));
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.chestContents = NonNullList.<ItemStack>func_191197_a(this.getSizeInventory(), ItemStack.field_190927_a);

		if (!this.checkLootAndRead(compound)) {
			ItemStackHelper.func_191283_b(compound, this.chestContents);
		}

		if (compound.hasKey("CustomName", 8)) {
			this.field_190577_o = compound.getString("CustomName");
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		return compound;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing) {
		if (capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (doubleChestHandler == null || doubleChestHandler.needsRefresh())
				doubleChestHandler = net.minecraftforge.items.VanillaDoubleChestItemHandler.get(this);
			if (doubleChestHandler != null && doubleChestHandler != net.minecraftforge.items.VanillaDoubleChestItemHandler.NO_ADJACENT_CHESTS_INSTANCE)
				return (T) doubleChestHandler;
		}
		return super.getCapability(capability, facing);
	}

	public net.minecraftforge.items.IItemHandler getSingleChestHandler() {
		return super.getCapability(net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
	}

	@Override
	public String getGuiID() {
		return "skrim:mega_chest";
	}

}