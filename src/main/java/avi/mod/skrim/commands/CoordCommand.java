package avi.mod.skrim.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import avi.mod.skrim.world.PlayerCoords;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class CoordCommand extends CommandBase implements ICommand {

	private final List aliases;
	protected String fullEntityName;

	public CoordCommand() {
		aliases = new ArrayList();
		aliases.add("coord");
	}

	@Override
	public String getName() {
		return "coord";
	}

	@Override
	public String getUsage(ICommandSender var1) {
		return "/coord [add|get|list|remove] [Location Name|Page Number]";
	}

	@Override
	public List<String> getAliases() {
		return this.aliases;
	}

	@Override
	public boolean isUsernameIndex(String[] var1, int var2) {
		return false;
	}

	@Override
	public int compareTo(ICommand o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		World world = sender.getEntityWorld();
		if (world.isRemote) {
			System.out.println("Not processing on Client side Coord");
		} else {
			EntityPlayer player = getCommandSenderAsPlayer(sender);
			String sendMessage = "";
			sendMessage = this.getUsage(sender);
			if (args.length > 0) {
				if (args[0].equals("list")) {
					if (args.length == 2 && StringUtils.isNumeric(args[1])) {
						sendMessage = PlayerCoords.getCoordList(player.getEntityWorld(), server, Integer.parseInt(args[1]));
					} else {
						sendMessage = PlayerCoords.getCoordList(player.getEntityWorld(), server);
					}
				} else if (args.length >= 2) {
					if (args[0].equals("add")) {
						sendMessage = PlayerCoords.addCoord(player, server, args[1]);
					} else if (args[0].equals("get")) {
						sendMessage = PlayerCoords.getCoord(player.getEntityWorld(), server, args[1]);
					} else if (args[0].equals("remove")) {
						sendMessage = PlayerCoords.deleteCoord(server, player, args[1]);
					}
				}
			}
			sender.sendMessage(new TextComponentString(sendMessage));
		}
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

}
