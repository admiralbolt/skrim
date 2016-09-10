package avi.mod.skrim.handlers;

import avi.mod.skrim.network.SkillPacket;
import avi.mod.skrim.network.SkrimPacketHandler;
import avi.mod.skrim.skills.ISkill;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.defense.SkillDefense;
import avi.mod.skrim.utils.Reflection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class JoinWorldHandler {

	@SubscribeEvent
	public void onLoggedIn(PlayerLoggedInEvent event) {
		if (event.player instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) event.player;
			for (int i = 0; i < Skills.ALL_SKILLS.size(); i++) {
				Capability<? extends ISkill> cap = Skills.ALL_SKILLS.get(i);
				if (player.hasCapability(cap, EnumFacing.NORTH)) {
					Skill skill = (Skill) player.getCapability(cap, EnumFacing.NORTH);
					SkrimPacketHandler.INSTANCE.sendTo(new SkillPacket(skill.name, skill.level, skill.xp), player);
					if (cap == Skills.DEFENSE) {
						IAttributeInstance armor = player.getEntityAttribute(SharedMonsterAttributes.ARMOR);
						Reflection.hackAttributeTo(armor, "maximumValue", 20.0 + ((SkillDefense) skill).getExtraArmor());
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onJoinedWorld(EntityJoinWorldEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) entity;
			for (int i = 0; i < Skills.ALL_SKILLS.size(); i++) {
				Capability<? extends ISkill> cap = Skills.ALL_SKILLS.get(i);
				if (player.hasCapability(cap, EnumFacing.NORTH)) {
					Skill skill = (Skill) player.getCapability(cap, EnumFacing.NORTH);
					SkrimPacketHandler.INSTANCE.sendTo(new SkillPacket(skill.name, skill.level, skill.xp), player);
					if (cap == Skills.DEFENSE) {
						IAttributeInstance armor = player.getEntityAttribute(SharedMonsterAttributes.ARMOR);
						Reflection.hackAttributeTo(armor, "maximumValue", 20.0 + ((SkillDefense) skill).getExtraArmor());
					}
				}
			}
		}
	}

}
