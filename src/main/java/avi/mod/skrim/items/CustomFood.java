package avi.mod.skrim.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.FoodStats;
import net.minecraft.world.World;
import avi.mod.skrim.Skrim;
import avi.mod.skrim.skills.cooking.SkillCooking;

public class CustomFood extends ItemFood implements ItemModelProvider {

	protected String name;

	public CustomFood(String name, int amount, float saturation, boolean isWolfFood) {
		super(amount, saturation, isWolfFood);
		this.name = name;
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		this.setCreativeTab(Skrim.creativeTab);
	}

	/**
	 * Right now we do nothing with this, but in the future we will
	 * add some cool benefits.
	 */
	@Override
	protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
		super.onFoodEaten(stack, world, player);
		if (stack.hasTagCompound()) {
			NBTTagCompound compound = stack.getTagCompound();
			if (compound.hasKey("level")) {
				ItemFood food = (ItemFood) stack.getItem();
				float satMod = food.getSaturationModifier(stack);
				int foodHeal = food.getHealAmount(stack);
				int level = compound.getInteger("level");
				boolean overFull = SkillCooking.overFull(level);
				double extraFood = SkillCooking.extraFood(level);
				double extraSaturation = SkillCooking.extraSaturation(level);
				FoodStats playerStats = player.getFoodStats();
				int additionalHeal = (int)(foodHeal * extraFood);

				int newFood = playerStats.getFoodLevel() + additionalHeal;
				// Apply the old sat mod to the new healing, and the new sat mod to everything
				float newSaturation = playerStats.getSaturationLevel() +
						(satMod * additionalHeal + (int)(extraSaturation * (foodHeal + additionalHeal)));

				playerStats.setFoodLevel((newFood > 20 && !overFull) ? 20 : newFood);
				playerStats.setFoodSaturationLevel((newSaturation > playerStats.getFoodLevel() && !overFull) ? playerStats.getFoodLevel() : newSaturation);
			}
		}
	}

	@Override
  public void registerItemModel(Item item) {
    Skrim.proxy.registerItemRenderer(this, 0, this.getUnlocalizedName());
  }

  @Override
  public CustomFood setCreativeTab(CreativeTabs tab) {
    super.setCreativeTab(tab);
    return this;
  }

}
