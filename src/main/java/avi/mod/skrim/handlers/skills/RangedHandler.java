package avi.mod.skrim.handlers.skills;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.ranged.SkillRanged;

public class RangedHandler {

  @SubscribeEvent
	public void onPlayerHurt(LivingHurtEvent event) {
		DamageSource source = event.getSource();
		Entity entity = source.getEntity();
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.RANGED, EnumFacing.NORTH)) {
				if (source.damageType == "arrow") {
					SkillRanged ranged = (SkillRanged) player.getCapability(Skills.RANGED, EnumFacing.NORTH);
					event.setAmount(event.getAmount() + (float) (ranged.getExtraDamage() * event.getAmount()));
					ranged.xp += (int) (event.getAmount() * 10);
					ranged.levelUp((EntityPlayerMP) player);
				}
			}
		}
	}

}
