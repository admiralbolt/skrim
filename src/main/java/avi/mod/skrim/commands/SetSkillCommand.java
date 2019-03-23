package avi.mod.skrim.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import avi.mod.skrim.network.SkillPacket;
import avi.mod.skrim.network.SkrimPacketHandler;
import avi.mod.skrim.skills.ISkill;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.utils.Utils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import scala.actors.threadpool.Arrays;

public class SetSkillCommand extends CommandBase implements ICommand {

  private final List aliases;

    protected String fullEntityName;

    public SetSkillCommand() {
        aliases = new ArrayList();
        aliases.add("setskill");
    }

    @Override
    public String getName() {
        return "setskill";
    }

    @Override
    public String getUsage(ICommandSender var1) {
        return "setskill <name> <level> [player]";
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
      	System.out.println("Not processing on Client side");
      } else {
        EntityPlayer player = args.length == 3 ? getPlayer(server, sender, args[2]) : getCommandSenderAsPlayer(sender);
      	if (args.length == 2 || args.length == 3) {
      		if (Skills.skillMap.containsKey(args[0])) {
      			int level = Integer.parseInt(args[1]);
      			if (level >= 1) {
          		Capability<? extends ISkill> iskill = Skills.skillMap.get(args[0]);
          		Skill skill = (Skill) player.getCapability(iskill, EnumFacing.NORTH);
          		skill.setLevel(level);
          		skill.setXp(Skill.xpFactor * Utils.gaussianSum(level) - 1);
          		if (player instanceof EntityPlayerMP) {
          			SkrimPacketHandler.INSTANCE.sendTo(new SkillPacket(skill.name, skill.level, skill.xp), (EntityPlayerMP) player);
          		}
          		sender.sendMessage(new TextComponentString("Set skill: " + args[0] + " to level: " + level));
      			}
      		}
      	} else {
      		sender.sendMessage(new TextComponentString(this.getUsage(sender)));
      	}
      }
		}

		@Override
		public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
			return sender.canUseCommand(this.getRequiredPermissionLevel(), this.getName());
		}

		public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        return args.length == 3 ? getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : Collections.<String>emptyList();
    }

		public int getRequiredPermissionLevel() {
			return 2;
		}

}
