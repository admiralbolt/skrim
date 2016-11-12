package avi.mod.skrim.blocks;

import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.cooking.SkillCooking;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class AngelCakeBlock extends CustomCakeBlock {

	public AngelCakeBlock() {
		super("angel_cake_block");
	}

	public void applyAdditionalEffects(EntityPlayer player) {
		if (player.hasCapability(Skills.COOKING, EnumFacing.NORTH)) {
			SkillCooking cooking = (SkillCooking) player.getCapability(Skills.COOKING, EnumFacing.NORTH);
			cooking.initAngel(player);
		}
	}

}