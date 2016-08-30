package avi.mod.skrim.handlers.skills;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.defense.SkillDefense;

public class DefenseHandler {

  @SubscribeEvent
	public void onPlayerHurt(LivingHurtEvent event) {
		DamageSource source = event.getSource();
		Entity entity = event.getEntity();
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.DEFENSE, EnumFacing.NORTH)) {
				if (source.damageType == "mob") {
					SkillDefense defense = (SkillDefense) player.getCapability(Skills.DEFENSE, EnumFacing.NORTH);
					defense.xp += defense.getXp(event.getAmount());
					event.setAmount(event.getAmount() - (float) (defense.getDamageReduction() * event.getAmount()));
					defense.levelUp((EntityPlayerMP) player);
				}
			}
		}
	}

}
