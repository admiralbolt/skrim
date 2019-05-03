package avi.mod.skrim.items.artifacts;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.init.SkrimSoundEvents;
import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.items.weapons.ArtifactSword;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class PureNail extends ArtifactSword {

  public PureNail() {
    super("pure_nail", SkrimItems.ARTIFACT_DEFAULT);
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    tooltip.add("§4Sweep attacks deal full damage.");
    tooltip.add("§4Deals quadruple damage on critical hits.§r");
    tooltip.add("§e\"Once you tire of the nail, feel free to join me in my art.\"");
  }

  @Override
  public void getSubItems(@Nonnull CreativeTabs tab, NonNullList<ItemStack> subItems) {
    ItemStack nail = new ItemStack(SkrimItems.PURE_NAIL);
    nail.addEnchantment(Enchantments.SHARPNESS, 6);
    nail.addEnchantment(Enchantments.SWEEPING, 20);
    subItems.add(nail);
  }

  @Mod.EventBusSubscriber(modid = Skrim.MOD_ID)
  public static class PureNailHandler {

    @SubscribeEvent
    public static void doSweep(AttackEntityEvent event) {
      EntityPlayer player = (EntityPlayer) event.getEntity();
      if (player.world.isRemote || player.getHeldItemMainhand().getItem() != SkrimItems.PURE_NAIL) return;
      if (player.getCooledAttackStrength(0.0f) < 1) return;

      player.world.playSound(null, player.getPosition(), SkrimSoundEvents.NAIL_ART_GREAT_SLASH, SoundCategory.PLAYERS, 1.0f, 1.0f);
    }
  }

}
