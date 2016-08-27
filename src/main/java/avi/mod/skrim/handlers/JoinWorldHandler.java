package avi.mod.skrim.handlers;

import avi.mod.skrim.network.SkillPacket;
import avi.mod.skrim.network.SkrimPacketHandler;
import avi.mod.skrim.skills.ISkill;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.Skills;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class JoinWorldHandler {

	@SubscribeEvent
	public void onLoggedIn(PlayerLoggedInEvent event) {
		System.out.println("LOGGED IN EVENT: entity is MP: " + (event.player instanceof EntityPlayerMP));
		if (event.player instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) event.player;
			for (int i = 0; i < Skills.ALL_SKILLS.size(); i++) {
				Capability<? extends ISkill> cap = Skills.ALL_SKILLS.get(i);
				if (player.hasCapability(cap, EnumFacing.NORTH)) {
					Skill skill = (Skill) player.getCapability(cap, EnumFacing.NORTH);
					System.out.println("Sending packet for skill: " + skill.name);
					SkrimPacketHandler.INSTANCE.sendTo(new SkillPacket(skill.name, skill.level, skill.xp), player);
				}
			}
		}
	}

}
