package avi.mod.skrim.items;

import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.Sets;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.entities.CustomFishHook;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CustomFishingRod extends Item implements ItemModelProvider {

	private static Set<Block> effectiveBlockSet = Sets.newHashSet(new Block[] {});
	protected String name;
	public String type;
	public int enchantability;

	public CustomFishingRod(String name) {
		this.name = name;
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		this.setCreativeTab(Skrim.creativeTab);
		this.setMaxDamage(64);
		this.setMaxStackSize(1);
		this.setCreativeTab(CreativeTabs.TOOLS);
		this.addPropertyOverride(new ResourceLocation("cast"), new IItemPropertyGetter() {
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				return entityIn == null ? 0.0F
						: (entityIn.getHeldItemMainhand() == stack && entityIn instanceof EntityPlayer && ((EntityPlayer) entityIn).fishEntity != null ? 1.0F
								: 0.0F);
			}
		});
	}

	@Override
	public void registerItemModel(Item item) {
		Skrim.proxy.registerItemRenderer(this, 0, this.getUnlocalizedName());
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
		ItemStack itemStackIn = playerIn.getHeldItem(hand);
		if (playerIn.fishEntity != null) {
			int i = playerIn.fishEntity.handleHookRetraction();
			itemStackIn.damageItem(i, playerIn);
			playerIn.swingArm(hand);
		} else {
			worldIn.playSound((EntityPlayer) null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5F,
					0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
			if (!worldIn.isRemote) {
				worldIn.spawnEntityInWorld(new CustomFishHook(worldIn, playerIn));
			}
			playerIn.swingArm(hand);
			playerIn.addStat(StatList.getObjectUseStats(this));
		}
		return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
	}

	/**
	 * Checks isDamagable and if it cannot be stacked
	 */
	@Override
	public boolean isItemTool(ItemStack stack) {
		return super.isItemTool(stack);
	}

	/**
	 * Return the enchantability factor of the item, most of the time is based
	 * on material.
	 */
	@Override
	public int getItemEnchantability() {
		return 1;
	}

}
