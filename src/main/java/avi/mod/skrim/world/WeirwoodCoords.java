package avi.mod.skrim.world;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import avi.mod.skrim.Skrim;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

public class WeirwoodCoords extends WorldSavedData {
	
	private static final String DATA_NAME = Skrim.modId + "-WeirwoodCoords";

	public Map<UUID, BlockPos> weirwood = new HashMap<UUID, BlockPos>();
	public int dimension;
	
	public WeirwoodCoords() {
		super(DATA_NAME);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		NBTBase weirTags = compound.getTag("weirwood");
		if (weirTags instanceof NBTTagList) {
			NBTTagList weirList = (NBTTagList) weirTags;
			int listSize = weirList.tagCount();
			for (int i = 0; i < listSize; i++) {
				NBTTagCompound subTag = weirList.getCompoundTagAt(i);
				UUID uuid = UUID.fromString(subTag.getString("uuid"));
				BlockPos pos = new BlockPos(subTag.getInteger("x"), subTag.getInteger("y"), subTag.getInteger("z"));
				this.weirwood.put(uuid, pos);
			}
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagList weirTags = new NBTTagList();
		for (Entry<UUID, BlockPos> entry : this.weirwood.entrySet()) {
			UUID uuid = entry.getKey();
			BlockPos pos = entry.getValue();
			NBTTagCompound weir = new NBTTagCompound();
			weir.setString("uuid", uuid.toString());
			weir.setInteger("x", pos.getX());
			weir.setInteger("y", pos.getZ());
			weir.setInteger("z", pos.getZ());
			weirTags.appendTag(weir);
		}
		compound.setTag("weirwood", weirTags);
		return compound;
	}
	
	public static boolean addCoord(EntityPlayer player, BlockPos pos) {
		if (validCoord(player, pos)) {
			WeirwoodCoords weirCoords = getData(player.getEntityWorld());
			UUID uuid = player.getPersistentID();
			weirCoords.weirwood.put(uuid, pos);
			weirCoords.markDirty();
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean validCoord(EntityPlayer player, BlockPos pos) {
		World world = player.getEntityWorld();
		int totalNatural = 0;
		int startY = pos.getY();
		for (int q = -2; q <= 0; q++) {
			boolean allNatural = true;
			for (int y = q; y <= q + 2; y++) {
				if (!PlayerPlacedBlocks.isNaturalBlock(world, new BlockPos(pos.getX(), pos.getY() + y, pos.getZ()))) {
					allNatural = false;
				}
			}
			if (allNatural) {
				return allNatural;
			}
		}
		return false;
	}
	
	public static BlockPos getCoord(EntityPlayer player) {
		WeirwoodCoords weirCoords = getData(player.getEntityWorld());
		UUID uuid = player.getPersistentID();
		if (weirCoords.weirwood.containsKey(uuid)) {
			if (validCoord(player, weirCoords.weirwood.get(uuid))) {
				return weirCoords.weirwood.get(uuid);
			}
		}
		return null;
	}

	public static WeirwoodCoords getData(World world) {
		MapStorage storage = world.getPerWorldStorage();
		WeirwoodCoords instance = (WeirwoodCoords) storage.getOrLoadData(WeirwoodCoords.class, DATA_NAME);
		if (instance == null) {
			instance = new WeirwoodCoords();
			instance.dimension = world.provider.getDimension();
			storage.setData(DATA_NAME, instance);
		}
		return instance;
	}
	


}