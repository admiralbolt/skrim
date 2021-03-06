package avi.mod.skrim.items.items;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.items.ItemBase;
import avi.mod.skrim.items.SkrimItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Artifacts that are activated items.
 */
public class ArtifactItem extends Item implements ItemBase {

  protected String name;

  public ArtifactItem(String name) {
    super();
    this.name = name;
    this.setUnlocalizedName(name);
    this.setRegistryName(name);
    this.maxStackSize = 1;
  }

  @Override
  public boolean hasEffect(ItemStack stack) {
    return true;
  }

  @Override
  @Nonnull
  @SideOnly(Side.CLIENT)
  public EnumRarity getRarity(ItemStack stack) {
    return SkrimItems.ARTIFACT_RARITY;
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

  }

  @Override
  @Nonnull
  public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand hand) {
    return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(hand));
  }

  @Override
  @Nonnull
  public ItemStack onItemUseFinish(@Nonnull ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
    return stack;
  }

  @Override
  public int getMaxItemUseDuration(ItemStack stack) {
    return 0;
  }

  @Override
  @Nonnull
  public EnumAction getItemUseAction(ItemStack stack) {
    return EnumAction.NONE;
  }

  @Override
  public int getItemEnchantability() {
    return 0;
  }

  @Override
  public String getTexturePath() {
    return "items";
  }

  @Mod.EventBusSubscriber(modid = Skrim.MOD_ID)
  public static class ArtifactHandler {

    @SubscribeEvent
    public static void applyEnchantments(AnvilUpdateEvent event) {
      if (ArrayUtils.contains(SkrimItems.ARTIFACTS, event.getLeft().getItem())) {
        event.setCanceled(true);
      }
    }
  }

}
