package avi.mod.skrim.commands;

import java.util.ArrayList;
import java.util.List;

import avi.mod.skrim.world.PlayerCoords;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class WTFDIDCommand extends CommandBase implements ICommand {

	private final List aliases;
	protected String fullEntityName;

	public WTFDIDCommand() {
		aliases = new ArrayList();
		aliases.add("wtfdid");
	}

	@Override
	public String getCommandName() {
		return "wtfdid";
	}

	@Override
	public String getCommandUsage(ICommandSender var1) {
		return "wtfdid";
	}

	@Override
	public List getCommandAliases() {
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
			System.out.println("Not processing on Client side WTFDID");
		} else {
			EntityPlayer player = getCommandSenderAsPlayer(sender);
			sender.addChatMessage(new TextComponentString(PlayerCoords.getLastDeath(player)));
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
