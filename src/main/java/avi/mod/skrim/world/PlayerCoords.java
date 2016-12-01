package avi.mod.skrim.world;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import avi.mod.skrim.Skrim;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class PlayerCoords extends WorldSavedData {

	private static final String DATA_NAME = Skrim.modId + "-playerCoords";
	private static final int coordsPerPage = 5;

	public Map<String, Entry<UUID, BlockPos>> coords = new HashMap<String, Entry<UUID, BlockPos>>();
	public Map<UUID, Entry<BlockPos, String>> lastDeath = new HashMap<UUID, Entry<BlockPos, String>>();
	public Map<UUID, String> usernames = new HashMap<UUID, String>();
	public Set<BlockPos> locs = new HashSet<BlockPos>();
	public int dimension;

	public PlayerCoords() {
		super(DATA_NAME);
	}

	public PlayerCoords(String name) {
		super(name);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		NBTBase dimension = compound.getTag("dimension");
		if (dimension instanceof net.minecraft.nbt.NBTTagByte) {
			this.dimension = ((net.minecraft.nbt.NBTTagByte) dimension).getByte();
		} else {
			this.dimension = ((net.minecraft.nbt.NBTTagInt) dimension).getInt();
		}
		NBTBase savedCoords = compound.getTag("savedCoords");
		if (savedCoords instanceof NBTTagList) {
			NBTTagList coordList = (NBTTagList) savedCoords;
			int listSize = coordList.tagCount();
			for (int i = 0; i < listSize; i++) {
				NBTTagCompound subTag = coordList.getCompoundTagAt(i);
				BlockPos pos = new BlockPos(subTag.getInteger("x"), subTag.getInteger("y"), subTag.getInteger("z"));
				String locName = subTag.getString("name");
				Entry<UUID, BlockPos> entry = new SimpleEntry<UUID, BlockPos>(UUID.fromString(subTag.getString("uuid")), pos);
				this.locs.add(pos);
				this.coords.put(locName, entry);
			}
		}
		NBTBase deathCoords = compound.getTag("deathCoords");
		if (deathCoords instanceof NBTTagList) {
			NBTTagList deathList = (NBTTagList) deathCoords;
			int listSize = deathList.tagCount();
			for (int i = 0; i < listSize; i++) {
				NBTTagCompound subTag = deathList.getCompoundTagAt(i);
				BlockPos pos = new BlockPos(subTag.getInteger("x"), subTag.getInteger("y"), subTag.getInteger("z"));
				String deathCause = subTag.getString("cause");
				this.lastDeath.put(UUID.fromString(subTag.getString("uuid")), new SimpleEntry(pos, deathCause));
			}
		}
		NBTBase nameTags = compound.getTag("usernames");
		if (nameTags instanceof NBTTagList) {
			NBTTagList nameList = (NBTTagList) nameTags;
			int listSize = nameList.tagCount();
			for (int i = 0; i < listSize; i++) {
				NBTTagCompound subTag = nameList.getCompoundTagAt(i);
				UUID uuid = UUID.fromString(subTag.getString("uuid"));
				String username = subTag.getString("username");
				this.usernames.put(uuid, username);
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagList savedCoords = new NBTTagList();
		for (String locName : this.coords.keySet()) {
			NBTTagCompound playerCoord = new NBTTagCompound();
			Entry<UUID, BlockPos> entry = this.coords.get(locName);
			UUID uuid = entry.getKey();
			BlockPos pos = entry.getValue();
			playerCoord.setInteger("x", pos.getX());
			playerCoord.setInteger("y", pos.getY());
			playerCoord.setInteger("z", pos.getZ());
			playerCoord.setString("uuid", uuid.toString());
			playerCoord.setString("name", locName);
			savedCoords.appendTag(playerCoord);
		}
		NBTTagList deathCoords = new NBTTagList();
		for (UUID uuid : this.lastDeath.keySet()) {
			NBTTagCompound deathCoord = new NBTTagCompound();
			Entry<BlockPos, String> entry = this.lastDeath.get(uuid);
			BlockPos deathPos = entry.getKey();
			String deathCause = entry.getValue();
			deathCoord.setString("uuid", uuid.toString());
			deathCoord.setInteger("x", deathPos.getX());
			deathCoord.setInteger("y", deathPos.getY());
			deathCoord.setInteger("z", deathPos.getZ());
			deathCoord.setString("cause", deathCause);
			deathCoords.appendTag(deathCoord);
		}
		NBTTagList nameTags = new NBTTagList();
		for (UUID uuid : this.usernames.keySet()) {
			NBTTagCompound name = new NBTTagCompound();
			name.setString("uuid", uuid.toString());
			name.setString("username", this.usernames.get(uuid));
			nameTags.appendTag(name);
		}
		compound.setInteger("dimension", this.dimension);
		compound.setTag("savedCoords", savedCoords);
		compound.setTag("deathCoords", deathCoords);
		compound.setTag("usernames", nameTags);
		return compound;
	}

	public String formatLocation(MinecraftServer server, String locName) {
		Entry<UUID, BlockPos> entry = this.coords.get(locName);
		UUID uuid = entry.getKey();
		String username = this.usernames.get(uuid);
		BlockPos pos = entry.getValue();
		return locName + ": (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ") -- logged by " + username + ".";
	}

	public List<String> getSortedCoords(MinecraftServer server) {
		Set<String> keyCoords = this.coords.keySet();
		List<String> sortedKeys = new ArrayList<String>(keyCoords);
		Collections.sort(sortedKeys);
		List<String> sortedCoords = new ArrayList<String>();
		for (String locName : sortedKeys) {
			sortedCoords.add(this.formatLocation(server, locName));
		}
		return sortedCoords;
	}

	public String getCoordPage(MinecraftServer server, int page) {
		List<String> sortedCoords = this.getSortedCoords(server);
		List<String> pageList = new ArrayList<String>();
		int totalPages = (int) Math.ceil(((double) sortedCoords.size()) / coordsPerPage);
		if (totalPages > 0) {
			if (page > totalPages) {
				page = totalPages;
			} else if (page <= 0) {
				page = 1;
			}
			pageList = sortedCoords.subList((page - 1) * coordsPerPage, Math.min(page * coordsPerPage, sortedCoords.size()));
			pageList.add("Showing page " + page + " / " + totalPages);
		} else {
			pageList.add("You haven't added any coords yet!");
		}
		return String.join("\n", pageList);
	}

	public String getCoordPage(MinecraftServer server) {
		return getCoordPage(server, 1);
	}

	public static PlayerCoords getData(World world) {
		MapStorage storage = world.getPerWorldStorage();
		PlayerCoords instance = (PlayerCoords) storage.getOrLoadData(PlayerCoords.class, DATA_NAME);
		if (instance == null) {
			instance = new PlayerCoords();
			instance.dimension = world.provider.getDimension();
			storage.setData(DATA_NAME, instance);
		}
		return instance;
	}

	/**
	 * Methods to interact with the saved data.
	 */

	public static String addCoord(EntityPlayer player, MinecraftServer server, String locName) {
		String msg = "";
		PlayerCoords playerCoords = getData(player.getEntityWorld());
		BlockPos addPos = player.getPosition();
		if (!playerCoords.coords.containsKey(locName)) {
			if (!playerCoords.locs.contains(addPos)) {
				UUID uuid = player.getPersistentID();
				Entry<UUID, BlockPos> entry = new SimpleEntry<UUID, BlockPos>(uuid, addPos);
				EntityPlayer loggingPlayer = (EntityPlayer) server.getEntityFromUuid(uuid);
				String username = loggingPlayer.getGameProfile().getName();
				playerCoords.coords.put(locName, entry);
				playerCoords.usernames.put(uuid, username);
				playerCoords.markDirty();
				msg = "Successfully added coordinates under name: " + locName;
			} else {
				msg = "Specified coordinates are already logged under a different name!";
			}
		} else {
			msg = "Coordinates already saved under name '" + locName + "'.";
		}
		return msg;
	}

	public static String getCoord(World world, MinecraftServer server, String locName) {
		String msg = "";
		PlayerCoords playerCoords = getData(world);
		if (playerCoords.coords.containsKey(locName)) {
			msg = playerCoords.formatLocation(server, locName);
		} else {
			msg = "No coordinates saved under name '" + locName + "'.";
		}
		return msg;
	}

	public static String getCoordList(World world, MinecraftServer server, int page) {
		PlayerCoords playerCoords = getData(world);
		return playerCoords.getCoordPage(server, page);
	}

	/**
	 * Default is page 1
	 */
	public static String getCoordList(World world, MinecraftServer server) {
		return getCoordList(world, server, 1);
	}

	public static String deleteCoord(MinecraftServer server, EntityPlayer player, String locName) {
		String msg = "";
		PlayerCoords playerCoords = getData(player.getEntityWorld());
		if (playerCoords.coords.containsKey(locName)) {
			Entry<UUID, BlockPos> entry = playerCoords.coords.get(locName);
			UUID uuid = entry.getKey();
			BlockPos pos = entry.getValue();
			if (player.getPersistentID().equals(uuid)) {
				playerCoords.locs.remove(pos);
				playerCoords.coords.remove(locName);
				playerCoords.markDirty();
				msg = "Delete coordinates saved under name '" + locName + "'.";
			} else {
				msg = "You cannot delete other players saved coordinates.";
			}
		} else {
			msg = "No coordinates saved under name '" + locName + "'.";
		}
		return msg;
	}

	public static String getLastDeath(EntityPlayer player) {
		PlayerCoords playerCoords = getData(player.getEntityWorld());
		UUID uuid = player.getPersistentID();
		if (playerCoords.lastDeath.containsKey(uuid)) {
			Entry<BlockPos, String> entry = playerCoords.lastDeath.get(uuid);
			BlockPos pos = entry.getKey();
			String cause = entry.getValue();
			return "(" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ") " + cause;
		} else {
			return "You haven't died since logging has started!";
		}
	}

	public static void saveDeathLocation(LivingDeathEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			World world = entity.getEntityWorld();
			String deathMessage = event.getSource().getDeathMessage(event.getEntityLiving()).getUnformattedText();
			Entry<BlockPos, String> entry = new SimpleEntry(player.getPosition(), deathMessage);
			PlayerCoords playerCoords = getData(world);
			playerCoords.lastDeath.put(player.getPersistentID(), entry);
			playerCoords.markDirty();
		}
	}

}
