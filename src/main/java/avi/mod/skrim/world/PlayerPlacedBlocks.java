package avi.mod.skrim.world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.SkrimGlobalConfig;
import avi.mod.skrim.utils.Utils;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

public class PlayerPlacedBlocks extends WorldSavedData {

	private static final String DATA_NAME = Skrim.MOD_ID + "-playerBlockPos";
	/**
	 * Each instance is saved per world i.e. dimension. We store placed positions in a hash set for fast access.
	 */
	public Set<BlockPos> placedPos = new HashSet<BlockPos>();
	public int dimension;

	public PlayerPlacedBlocks() {
		super(DATA_NAME);
	}

	public PlayerPlacedBlocks(String name) {
		super(name);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		net.minecraft.nbt.NBTBase dimension = compound.getTag("dimension");
		if (dimension instanceof net.minecraft.nbt.NBTTagByte) {
			this.dimension = ((net.minecraft.nbt.NBTTagByte) dimension).getByte();
		} else {
			this.dimension = ((net.minecraft.nbt.NBTTagInt) dimension).getInt();
		}
		NBTBase positions = compound.getTag("positions");
		if (positions instanceof NBTTagList) {
			NBTTagList posList = (NBTTagList) positions;
			int listSize = posList.tagCount();
			for (int i = 0; i < listSize; i++) {
				NBTTagCompound subTag = posList.getCompoundTagAt(i);
				this.placedPos.add(new BlockPos(subTag.getInteger("x"), subTag.getInteger("y"), subTag.getInteger("z")));
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setInteger("dimension", this.dimension);
		NBTTagList positions = new NBTTagList();
		for (BlockPos pos : placedPos) {
			NBTTagCompound subTag = new NBTTagCompound();
			subTag.setInteger("x", pos.getX());
			subTag.setInteger("y", pos.getY());
			subTag.setInteger("z", pos.getZ());
			positions.appendTag(subTag);
		}
		compound.setTag("positions", positions);
		return compound;
	}

	public static boolean isNaturalBlock(World world, BlockPos pos) {
		return getData(world).isNaturalBlock(pos);
	}

	public static void addBlock(World world, BlockPos pos) {
		getData(world).addBlock(pos);
	}

	public static void removeBlock(World world, BlockPos pos) {
		getData(world).removeBlock(pos);
	}

	public boolean isNaturalBlock(BlockPos pos) {
		return !placedPos.contains(pos) || !SkrimGlobalConfig.ENFORCE_NATURAL.value;
	}

	public void addBlock(BlockPos pos) {
		if (!placedPos.contains(pos)) {
			placedPos.add(pos);
			this.markDirty();
		}
	}

	public void removeBlock(BlockPos pos) {
		if (placedPos.contains(pos)) {
			placedPos.remove(pos);
			this.markDirty();
		}
	}

	private static PlayerPlacedBlocks getData(World world) {
		MapStorage storage = world.getPerWorldStorage();
		PlayerPlacedBlocks instance = (PlayerPlacedBlocks) storage.getOrLoadData(PlayerPlacedBlocks.class, DATA_NAME);
		if (instance == null) {
			instance = new PlayerPlacedBlocks();
			instance.dimension = world.provider.getDimension();
			storage.setData(DATA_NAME, instance);
		}
		return instance;
	}

	public static void clean(World world) {
		PlayerPlacedBlocks placedBlocks = getData(world);
		List<BlockPos> remove = new ArrayList<BlockPos>();
		for (BlockPos pos : placedBlocks.placedPos) {
			if (!Utils.isRawXpBlock(world.getBlockState(pos).getBlock())) {
				remove.add(pos);
			}
		}
		for (BlockPos removePos : remove) {
			placedBlocks.placedPos.remove(removePos);
		}
		placedBlocks.markDirty();
	}

}
