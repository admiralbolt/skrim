package avi.mod.skrim.items.items;

import avi.mod.skrim.entities.projectile.SkrimEntityPotion;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.brewing.SkillBrewing;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class SplashSkrimPotion extends SkrimPotion {

  private static final String NAME = "splash_skrim_potion";

  public SplashSkrimPotion() {
    super(NAME);
  }

  @Nonnull
  public String getItemStackDisplayName(@Nonnull ItemStack stack) {
    return "Splash " + this.getBaseDisplayName(stack);
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand handIn) {
    ItemStack itemstack = playerIn.getHeldItem(handIn);
    ItemStack itemstack1 = playerIn.capabilities.isCreativeMode ? itemstack.copy() : itemstack.splitStack(1);
    worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_SPLASH_POTION_THROW,
        SoundCategory.PLAYERS, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

    if (!worldIn.isRemote) {
      SkillBrewing brewing = Skills.getSkill(playerIn, Skills.BREWING, SkillBrewing.class);
      SkrimEntityPotion entitypotion = new SkrimEntityPotion(worldIn, playerIn, itemstack1);
      entitypotion.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, -20.0F, (brewing.hasAbility(2)) ? 1.0F : 0.5F, 1.0F);
      worldIn.spawnEntity(entitypotion);
    }

    playerIn.addStat(StatList.getObjectUseStats(this));
    return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
  }
}
