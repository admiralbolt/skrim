package avi.mod.skrim.commands;

import avi.mod.skrim.world.PlayerCoords;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Command using for storing coordinate locations.
 */
public class CoordCommand extends CommandBase implements ICommand {

  private final List<String> aliases;

  public CoordCommand() {
    aliases = new ArrayList<>();
    aliases.add("coord");
  }

  @Override
  @Nonnull
  public String getName() {
    return "coord";
  }

  @Override
  @Nonnull
  public String getUsage(@Nonnull ICommandSender var1) {
    return "/coord [add|get|list|remove] [Location Name|Page Number]";
  }

  @Override
  @Nonnull
  public List<String> getAliases() {
    return this.aliases;
  }

  @Override
  public boolean isUsernameIndex(@Nonnull String[] var1, int var2) {
    return false;
  }

  @Override
  public int compareTo(@Nonnull ICommand o) {
    return 0;
  }

  @Override
  public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
    World world = sender.getEntityWorld();
    if (world.isRemote) return;
    if (args.length == 0) {
      sender.sendMessage(new TextComponentString(this.getUsage(sender)));
      return;
    }

    EntityPlayer player = getCommandSenderAsPlayer(sender);
    String sendMessage = this.getUsage(sender);
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
    sender.sendMessage(new TextComponentString(sendMessage));
  }

  @Override
  public boolean checkPermission(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender) {
    return true;
  }

  @Override
  public int getRequiredPermissionLevel() {
    return 0;
  }

}
