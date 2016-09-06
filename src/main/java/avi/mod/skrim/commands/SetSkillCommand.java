package avi.mod.skrim.commands;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import avi.mod.skrim.Utils;
import avi.mod.skrim.network.SkillPacket;
import avi.mod.skrim.network.SkrimPacketHandler;
import avi.mod.skrim.skills.ISkill;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.Skills;

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

public class SetSkillCommand implements ICommand {

  private final List aliases;

    protected String fullEntityName;

    public SetSkillCommand() {
        aliases = new ArrayList();
        aliases.add("setskill");
    }

    @Override
    public String getCommandName() {
        return "setskill";
    }

    @Override
    public String getCommandUsage(ICommandSender var1) {
        return "setskill <name> <level>";
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
      EntityPlayer player = (EntityPlayer) sender;

      if (world.isRemote) {
      	System.out.println("Not processing on Client side");
      } else {
      	if (args.length == 2) {
      		if (Skills.skillMap.containsKey(args[0])) {
      			int level = Integer.parseInt(args[1]);
      			if (level >= 1) {
          		Capability<? extends ISkill> iskill = Skills.skillMap.get(args[0]);
          		Skill skill = (Skill) player.getCapability(iskill, EnumFacing.NORTH);
          		skill.setLevel(level);
          		skill.setXp(1000 * Utils.gaussianSum(level) - 1);
          		if (player instanceof EntityPlayerMP) {
          			SkrimPacketHandler.INSTANCE.sendTo(new SkillPacket(skill.name, skill.level, skill.xp), (EntityPlayerMP) player);
          		}
          		sender.addChatMessage(new TextComponentString("Set skill: " + args[0] + " to level: " + level));
      			}
      		}
      	} else {
      		sender.addChatMessage(new TextComponentString(this.getCommandUsage(sender)));
      	}
      }
		}

		@Override
		public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
      return sender.canCommandSenderUseCommand(this.getRequiredPermissionLevel(), this.getCommandName());
		}

		@Override
		public List<String> getTabCompletionOptions(MinecraftServer server,
				ICommandSender sender, String[] args, @Nullable BlockPos pos) {
			// TODO Auto-generated method stub
			return null;
		}
		
		public int getRequiredPermissionLevel() {
			return 2;
		}

}
