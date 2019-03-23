package avi.mod.skrim.items;

import java.util.List;

import javax.annotation.Nullable;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.items.ItemModelProvider;
import avi.mod.skrim.items.ModItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CustomItem extends Item implements ItemModelProvider {

	protected String name;

	public CustomItem(String name) {
		super();
		this.name = name;
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		this.maxStackSize = 1;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

	}

	@Override
	public void registerItemModel(Item item) {
		Skrim.proxy.registerItemRenderer(this, 0, this.name);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World itemStackIn, EntityPlayer worldIn, EnumHand playerIn) {
		return new ActionResult(EnumActionResult.PASS, worldIn.getHeldItem(playerIn));
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer stack, World playerIn, BlockPos worldIn, EnumHand pos, EnumFacing hand, float facing, float hitX,
			float hitY) {
		return EnumActionResult.PASS;
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
		return stack;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 0;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.NONE;
	}

	@Override
	public int getItemEnchantability() {
		return 0;
	}

}
