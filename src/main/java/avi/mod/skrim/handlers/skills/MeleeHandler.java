package avi.mod.skrim.handlers.skills;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.melee.SkillMelee;

public class MeleeHandler {

  @SubscribeEvent
  public void onPlayerHurt(LivingHurtEvent event) {
    DamageSource source = event.getSource();
    Entity entity = source.getEntity();
    if (entity instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) entity;
      if (player != null && player.hasCapability(Skills.MELEE, EnumFacing.NORTH)) {
        if (source.damageType == "player") {
          SkillMelee melee = (SkillMelee) player.getCapability(Skills.MELEE, EnumFacing.NORTH);
          event.setAmount(event.getAmount() + (float) (melee.getExtraDamage() * event.getAmount()));
          melee.xp += (int) (event.getAmount() * 10);
          melee.levelUp((EntityPlayerMP) player);
        }
      }
    }
  }

}
