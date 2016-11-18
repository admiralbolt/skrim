package avi.mod.skrim.items.artifacts;

import java.util.List;

import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class IcarianScroll extends ArtifactItem {
	
	private static final int JUMP_DURATION = 200;
	private static final int JUMP_LEVEL = 200;
	
	public IcarianScroll() {
		super("icarian_scroll");
		this.setMaxDamage(1);
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean par4) {
		tooltip.add("§4I feel like jumping for joy.§r");
		tooltip.add("§e\"It's raining wizards.\"§r");
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		playerIn.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, JUMP_DURATION, JUMP_LEVEL, false, true));
		itemStackIn.damageItem(2, playerIn);
		return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
	}

}
