package avi.mod.skrim.commands;

import avi.mod.skrim.SkrimGlobalConfig;
import com.google.common.collect.ImmutableList;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Command for getting / setting skrim global settings.
 */
public class SkrimOptionCommand extends CommandBase implements ICommand {

  private final List<String> aliases = ImmutableList.of("skrimoption", "sko");

  public SkrimOptionCommand() {

  }

  @Override
  @Nonnull
  public String getName() {
    return "skrimoption";
  }

  @Override
  @Nonnull
  public String getUsage(@Nonnull ICommandSender sender) {
    return "/skrimoption [set|get] <Option Name> [value]";
  }

  @Override
  public int getRequiredPermissionLevel() {
    return 2;
  }

  @Override
  @Nonnull
  public List<String> getAliases() {
    return this.aliases;
  }

  @Override
  public void execute(@Nonnull MinecraftServer server, ICommandSender sender, @Nonnull String[] args) throws CommandException {
    World world = sender.getEntityWorld();
    if (world.isRemote) return;

    if (args.length < 2 || args.length > 3) {
      sender.sendMessage(new TextComponentString(this.getUsage(sender)));
      return;
    }

    if (!SkrimGlobalConfig.CONFIG_OPTIONS.containsKey(args[1])) {
      sender.sendMessage(new TextComponentString("No such config option '" + args[1] + "'."));
      return;
    }

    // Handle get.
    if (args.length == 2 && args[0].toLowerCase().equals("get")) {
      sender.sendMessage(new TextComponentString("Config option " + args[1] + " = " + SkrimGlobalConfig.CONFIG_OPTIONS.get(args[1]).value + "."));
      return;
    }

    // Handle set.
    if (args.length == 3 && args[0].toLowerCase().equals("set")) {
      SkrimGlobalConfig.CONFIG_OPTIONS.get(args[1]).setValue(args[2]);
      return;
    }

    sender.sendMessage(new TextComponentString(this.getUsage(sender)));
  }

}
