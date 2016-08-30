package avi.mod.skrim.handlers;

import avi.mod.skrim.network.SkillPacket;
import avi.mod.skrim.network.SkrimPacketHandler;
import avi.mod.skrim.skills.ISkill;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.digging.ISkillDigging;
import avi.mod.skrim.skills.digging.SkillDigging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DeathEvent {

	@SubscribeEvent
	public void onPlayerDeath(PlayerEvent.Clone event) {
		if (event.isWasDeath()) {
			EntityPlayer newPlayer = event.getEntityPlayer();
			EntityPlayer original = event.getOriginal();
			if (newPlayer instanceof EntityPlayerMP) {
				for (int i = 0; i < Skills.ALL_SKILLS.size(); i++) {
					ISkill oldSkill = original.getCapability(Skills.ALL_SKILLS.get(i), EnumFacing.NORTH);
					Skill yeOldeSkill = (Skill) oldSkill;
					ISkill newSkill = newPlayer.getCapability(Skills.ALL_SKILLS.get(i), EnumFacing.NORTH);
					newSkill.setLevel(oldSkill.getLevel());
					newSkill.setXp(oldSkill.getXp());
					Skill skill = (Skill) newSkill;
					//SkrimPacketHandler.INSTANCE.sendTo(new SkillPacket(skill.name, skill.level, skill.xp), (EntityPlayerMP) newPlayer);
				}
			}
		}
	}

}
