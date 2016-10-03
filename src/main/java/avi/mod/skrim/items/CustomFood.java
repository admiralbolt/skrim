package avi.mod.skrim.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.FoodStats;
import net.minecraft.world.World;
import avi.mod.skrim.Skrim;
import avi.mod.skrim.skills.cooking.SkillCooking;
import avi.mod.skrim.utils.PotionList;

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

				boolean overFull = level >= 25;
				boolean panacea = level >= 50;
				boolean superFood = level >= 75;

				double extraFood = SkillCooking.extraFood(level);
				double extraSaturation = SkillCooking.extraSaturation(level);
				FoodStats playerStats = player.getFoodStats();
				int additionalHeal = (int)(foodHeal * extraFood);

				int newFood = playerStats.getFoodLevel() + additionalHeal;
				// Apply the old sat mod to the new healing, and the new sat mod to everything
				float newSaturation = playerStats.getSaturationLevel() +
						(satMod * additionalHeal + (int)(extraSaturation * (foodHeal + additionalHeal)));

				newFood = (newFood > 20 && !overFull) ? 20 : newFood;
				newSaturation = (newSaturation > newFood && !overFull) ? newFood : newSaturation;

				if (panacea) {
					player.removePotionEffect(PotionList.POISON);
					player.removePotionEffect(PotionList.HUNGER);
					player.removePotionEffect(PotionList.NAUSEAU);
				}

				if (superFood) {
					player.addPotionEffect(PotionList.REGENERATION, 200, 1, false, false);
					player.addPotionEffect(PotionList.SPEED, 200, 1, false, false);
				}
				/**
				 * A valiant attempt to keep me from over-filling.
				 * But not valiant enough.
				 * For some reason setFoodSaturationLevel is client side only.
				 * But setFoodLevel bypasses the maximum for food...
				 * BUT, addStats will reset the maximums for both.....
				 * BUUTTT, readNBT(NBTTagCompound compound) assigns directly so.....
				 * we need to set foodLevel, foodTimer, foodSaturationLevel, foodExhaustionLevel
				 */
				NBTTagCompound storeCompound = new NBTTagCompound();
				storeCompound.setInteger("foodLevel", newFood);
				storeCompound.setInteger("foodTimer", 0); // Starts at 0, counts up to 80 ticks
				storeCompound.setFloat("foodSaturationLevel", newSaturation);
				storeCompound.setFloat("foodExhaustionLevel", 0); // Food exhaustion max 40 when fully exhausted
				playerStats.readNBT(storeCompound);
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
