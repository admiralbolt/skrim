package avi.mod.skrim.items.artifacts;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
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

public class SheepFinder3000 extends ArtifactItem {
	
	private static final int RANGE = 200;
	private static final int GLOW_DURATION = 300 * 20;

	public SheepFinder3000() {
		super("sheep_finder_3000");
		this.setMaxDamage(3000);
	}
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add("§4\"Detect all sheep in a " + RANGE + "m radius.\"§r");
		tooltip.add("§e\"Control the sheep, control the world.\"§r");
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
		ItemStack itemStackIn = playerIn.getHeldItem(hand);
		BlockPos pos = playerIn.getPosition();
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		AxisAlignedBB bound = new AxisAlignedBB(x - RANGE, y - RANGE, z - RANGE, x + RANGE, y + RANGE, z + RANGE);
		List<EntitySheep> sheepies = worldIn.getEntitiesWithinAABB(EntitySheep.class, bound);
		for (EntitySheep sheep : sheepies) {
			sheep.addPotionEffect(new PotionEffect(MobEffects.GLOWING, GLOW_DURATION, 0, false, true));
		}
		itemStackIn.damageItem(1, playerIn);
		return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
	}
	

}
