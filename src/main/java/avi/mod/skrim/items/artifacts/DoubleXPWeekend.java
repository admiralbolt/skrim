package avi.mod.skrim.items.artifacts;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.SkrimGlobalConfig;
import avi.mod.skrim.items.items.ArtifactItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Doubles xp gained for all players for a bit.
 */
public class DoubleXPWeekend extends ArtifactItem {

  // 3 in game days.
  private static final int TICK_DURATION = 72000;
  private static final int CHECK_TIME = 1000;
  private static int TICKS_LEFT = -1;

  public DoubleXPWeekend() {
    super("double_xp_weekend");
    this.setMaxDamage(1);
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    tooltip.add("§4Double ALL XP for 3 in game days.§r");
    tooltip.add("§e\"Or you can venmo $5 @AviKnecht for a double xp weekend anytime.\"§r");
  }

  @Override
  @Nonnull
  public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand hand) {
    ItemStack itemStackIn = playerIn.getHeldItem(hand);
    SkrimGlobalConfig.XP_MULTIPLIER.value += 1.0;
    // In case multiple activations happen, we add to the duration.
    TICKS_LEFT += TICK_DURATION;
    itemStackIn.damageItem(2, playerIn);
    return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
  }

  @Mod.EventBusSubscriber(modid = Skrim.MOD_ID)
  public static class XPHandler {

    @SubscribeEvent
    public static void countdown(TickEvent.WorldTickEvent event) {
      if (TICKS_LEFT <= 0 || event.world.getWorldTime() % CHECK_TIME != 0) return;

      TICKS_LEFT -= CHECK_TIME;
      if (TICKS_LEFT <= 0) {
        SkrimGlobalConfig.XP_MULTIPLIER.value = 1.0;
      }
    }
  }

}
