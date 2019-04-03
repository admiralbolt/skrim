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

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Where The Fuck Did I Die?
 *
 * Spits out the last location of player death!
 */
public class WTFDIDCommand extends CommandBase implements ICommand {

  private final List<String> aliases = new ArrayList<>();

  public WTFDIDCommand() {
    aliases.add("wtfdid");
  }

  @Override
  @Nonnull
  public String getName() {
    return "wtfdid";
  }

  @Override
  @Nonnull
  public String getUsage(@Nonnull ICommandSender var1) {
    return "wtfdid";
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
  public void execute(@Nonnull MinecraftServer server, ICommandSender sender, @Nonnull String[] args) throws CommandException {
    World world = sender.getEntityWorld();
    if (world.isRemote) return;

    EntityPlayer player = getCommandSenderAsPlayer(sender);
    sender.sendMessage(new TextComponentString(PlayerCoords.getLastDeath(player)));
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
