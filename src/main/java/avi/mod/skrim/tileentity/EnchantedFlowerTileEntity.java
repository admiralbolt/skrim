package avi.mod.skrim.tileentity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EnchantedFlowerTileEntity extends TileEntity implements ITickable {

	/** List of effects that Enchanted Flowers can apply */
	public static final Potion[][] EFFECTS_LIST = new Potion[][] { { MobEffects.SPEED, MobEffects.HASTE }, { MobEffects.RESISTANCE, MobEffects.JUMP_BOOST },
			{ MobEffects.STRENGTH }, { MobEffects.REGENERATION } };
	private static final Set<Potion> VALID_EFFECTS = Sets.<Potion>newHashSet();
	private final List<EnchantedFlowerTileEntity.BeamSegment> beamSegments = Lists.<EnchantedFlowerTileEntity.BeamSegment>newArrayList();
	@SideOnly(Side.CLIENT)
	private long beamRenderCounter;

	@SideOnly(Side.CLIENT)
	private float beamRenderScale;

	private boolean isComplete = true;
	/** Strength of the enchanted flower */
	private int levels = 2;
	/** Effect given by the enchanted flower */
	@Nullable
	private Potion primaryEffect = MobEffects.SPEED;

	/** Item given to this beacon as payment. */
	private ItemStack payment;
	private String customName;

	/**
	 * Like the old updateEntity(), except more generic.
	 */
	public void update() {
		if (this.world.getTotalWorldTime() % 80L == 0L) {
			this.updateBeacon();
		}
	}

	public void updateBeacon() {
		if (this.world != null) {
			this.updateSegmentColors();
			this.addEffectsToPlayers();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	private void addEffectsToPlayers() {
		if (this.isComplete && this.levels > 0 && !this.world.isRemote && this.primaryEffect != null) {
			/**
			 * This levels will ALWAYS be = 2 I am going to increase the radius
			 * to be the full level 4 beacon radius though.
			 */
			double d0 = (double) (4 * 10 + 10);
			int i = 0;

			if (this.levels >= 4) {
				i = 1;
			}

			int j = (9 + this.levels * 2) * 20;
			int k = this.pos.getX();
			int l = this.pos.getY();
			int i1 = this.pos.getZ();
			AxisAlignedBB axisalignedbb = (new AxisAlignedBB((double) k, (double) l, (double) i1, (double) (k + 1), (double) (l + 1), (double) (i1 + 1)))
					.grow(d0).expand(0.0D, (double) this.world.getHeight(), 0.0D);
			List<EntityPlayer> list = this.world.<EntityPlayer>getEntitiesWithinAABB(EntityPlayer.class, axisalignedbb);

			for (EntityPlayer entityplayer : list) {
				entityplayer.addPotionEffect(new PotionEffect(this.primaryEffect, j, i, true, true));
			}
		}
	}

	/**
	 * The colors of the beacon segment are changed based on the glas they pass
	 * through. I *may* modify this in the future to have the color match the
	 * flower color, but that is not a priority now.
	 */
	private void updateSegmentColors() {
		int i = this.levels;
		int j = this.pos.getX();
		int k = this.pos.getY();
		int l = this.pos.getZ();
		this.levels = 2;
		this.beamSegments.clear();
		this.isComplete = true;
		EnchantedFlowerTileEntity.BeamSegment customTileEntityBeacon$beamsegment = new EnchantedFlowerTileEntity.BeamSegment(
				EntitySheep.getDyeRgb(EnumDyeColor.WHITE));
		this.beamSegments.add(customTileEntityBeacon$beamsegment);
		boolean flag = true;
		BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

		for (int i1 = k + 1; i1 < 256; ++i1) {
			IBlockState iblockstate = this.world.getBlockState(blockpos$mutableblockpos.setPos(j, i1, l));
			float[] afloat;

			if (iblockstate.getBlock() == Blocks.STAINED_GLASS) {
				afloat = EntitySheep.getDyeRgb((EnumDyeColor) iblockstate.getValue(BlockStainedGlass.COLOR));
			} else {
				if (iblockstate.getBlock() != Blocks.STAINED_GLASS_PANE) {
					if (iblockstate.getLightOpacity(this.world, blockpos$mutableblockpos) >= 15 && iblockstate.getBlock() != Blocks.BEDROCK) {
						this.isComplete = false;
						this.beamSegments.clear();
						break;
					}
					customTileEntityBeacon$beamsegment.incrementHeight();
					continue;
				}

				afloat = EntitySheep.getDyeRgb((EnumDyeColor) iblockstate.getValue(BlockStainedGlassPane.COLOR));
			}

			if (!flag) {
				afloat = new float[] { (customTileEntityBeacon$beamsegment.getColors()[0] + afloat[0]) / 2.0F,
						(customTileEntityBeacon$beamsegment.getColors()[1] + afloat[1]) / 2.0F,
						(customTileEntityBeacon$beamsegment.getColors()[2] + afloat[2]) / 2.0F };
			}

			if (Arrays.equals(afloat, customTileEntityBeacon$beamsegment.getColors())) {
				customTileEntityBeacon$beamsegment.incrementHeight();
			} else {
				customTileEntityBeacon$beamsegment = new EnchantedFlowerTileEntity.BeamSegment(afloat);
				this.beamSegments.add(customTileEntityBeacon$beamsegment);
			}

			flag = false;
		}

		if (this.isComplete) {
			for (int l1 = 1; l1 <= 4; this.levels = l1++) {
				int i2 = k - l1;

				if (i2 < 0) {
					break;
				}

				boolean flag1 = true;

				for (int j1 = j - l1; j1 <= j + l1 && flag1; ++j1) {
					for (int k1 = l - l1; k1 <= l + l1; ++k1) {
						Block block = this.world.getBlockState(new BlockPos(j1, i2, k1)).getBlock();

						if (!block.isBeaconBase(this.world, new BlockPos(j1, i2, k1), getPos())) {
							flag1 = false;
							break;
						}
					}
				}

				if (!flag1) {
					break;
				}
			}

			if (this.levels == 0) {
				this.isComplete = false;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public List<EnchantedFlowerTileEntity.BeamSegment> getBeamSegments() {
		return this.beamSegments;
	}

	@SideOnly(Side.CLIENT)
	public float shouldBeamRender() {
		if (!this.isComplete) {
			return 0.0F;
		} else {
			int i = (int) (this.world.getTotalWorldTime() - this.beamRenderCounter);
			this.beamRenderCounter = this.world.getTotalWorldTime();

			if (i > 1) {
				this.beamRenderScale -= (float) i / 40.0F;

				if (this.beamRenderScale < 0.0F) {
					this.beamRenderScale = 0.0F;
				}
			}

			this.beamRenderScale += 0.025F;

			if (this.beamRenderScale > 1.0F) {
				this.beamRenderScale = 1.0F;
			}

			return this.beamRenderScale;
		}
	}

	@Nullable
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.pos, 3, this.getUpdateTag());
	}

	public NBTTagCompound getUpdateTag() {
		return this.writeToNBT(new NBTTagCompound());
	}

	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return 65536.0D;
	}

	@Nullable
	private static Potion isBeaconEffect(int p_184279_0_) {
		Potion potion = Potion.getPotionById(p_184279_0_);
		return VALID_EFFECTS.contains(potion) ? potion : null;
	}

	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.primaryEffect = isBeaconEffect(compound.getInteger("Primary"));
		this.levels = compound.getInteger("Levels");
	}

	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("Primary", Potion.getIdFromPotion(this.primaryEffect));
		compound.setInteger("Levels", this.levels);
		return compound;
	}

	/**
	 * Returns the number of slots in the inventory.
	 */
	public int getSizeInventory() {
		return 1;
	}

	/**
	 * Returns the stack in the given slot.
	 */
	@Nullable
	public ItemStack getStackInSlot(int index) {
		return index == 0 ? this.payment : null;
	}

	/**
	 * Removes a stack from the given slot and returns it.
	 */
	@Nullable
	public ItemStack removeStackFromSlot(int index) {
		if (index == 0) {
			ItemStack itemstack = this.payment;
			this.payment = null;
			return itemstack;
		} else {
			return null;
		}
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be
	 * crafting or armor sections).
	 */
	public void setInventorySlotContents(int index, @Nullable ItemStack stack) {
		if (index == 0) {
			this.payment = stack;
		}
	}

	/**
	 * Get the name of this object. For players this returns their username
	 */
	public String getName() {
		return this.hasCustomName() ? this.customName : "container.beacon";
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
	 * Returns the maximum stack size for a inventory slot. Seems to always be
	 * 64, possibly will be extended.
	 */
	public int getInventoryStackLimit() {
		return 1;
	}

	/**
	 * Do not make give this method the name canInteractWith because it clashes
	 * with Container
	 */
	public boolean isUseableByPlayer(EntityPlayer player) {
		return this.world.getTileEntity(this.pos) != this ? false
				: player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
	}

	public void openInventory(EntityPlayer player) {
	}

	public void closeInventory(EntityPlayer player) {
	}

	/**
	 * Returns true if automation is allowed to insert the given stack (ignoring
	 * stack size) into the given slot.
	 */
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return stack.getItem() != null && stack.getItem().isBeaconPayment(stack);
	}

	public String getGuiID() {
		return "minecraft:enchanted_flower";
	}

	public int getField(int id) {
		switch (id) {
			case 0:
				return this.levels;
			case 1:
				return Potion.getIdFromPotion(this.primaryEffect);
			default:
				return 0;
		}
	}

	public void setField(int id, int value) {
		switch (id) {
			case 0:
				this.levels = value;
				break;
			case 1:
				this.primaryEffect = isBeaconEffect(value);
				break;
		}
	}

	public int getFieldCount() {
		return 3;
	}

	public void clear() {
		this.payment = null;
	}

	public boolean receiveClientEvent(int id, int type) {
		if (id == 1) {
			this.updateBeacon();
			return true;
		} else {
			return super.receiveClientEvent(id, type);
		}
	}

	static {
		for (Potion[] apotion : EFFECTS_LIST) {
			Collections.addAll(VALID_EFFECTS, apotion);
		}
	}

	public static class BeamSegment {
		/** RGB (0 to 1.0) colors of this beam segment */
		private final float[] colors;
		private int height;

		public BeamSegment(float[] colorsIn) {
			this.colors = colorsIn;
			this.height = 1;
		}

		protected void incrementHeight() {
			++this.height;
		}

		/**
		 * Returns RGB (0 to 1.0) colors of this beam segment
		 */
		public float[] getColors() {
			return this.colors;
		}

		@SideOnly(Side.CLIENT)
		public int getHeight() {
			return this.height;
		}
	}
}