package avi.mod.skrim.blocks.food;

import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.cooking.SkillCooking;
import net.minecraft.entity.player.EntityPlayer;

public class AngelCakeBlock extends CustomCakeBlock {

  public AngelCakeBlock() {
    super("angel_cake_block");
  }

  @Override
  public void applyAdditionalEffects(EntityPlayer player) {
    SkillCooking cooking = Skills.getSkill(player, Skills.COOKING, SkillCooking.class);
    cooking.initAngel(player);
  }

}