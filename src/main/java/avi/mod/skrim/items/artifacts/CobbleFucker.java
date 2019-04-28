package avi.mod.skrim.items.artifacts;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.items.tools.ArtifactPickaxe;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class CobbleFucker extends ArtifactPickaxe {

  private static final int RADIUS = 2;

  public CobbleFucker() {
    super("cobble_fucker", SkrimItems.ARTIFACT_DEFAULT);
    this.setMaxDamage(9876);
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    tooltip.add("§4Destroys cobble.");
    tooltip.add("§e\"Also eats ass.\"");
  }

  @Override
  public void getSubItems(@Nonnull CreativeTabs tab, NonNullList<ItemStack> subItems) {
    ItemStack pick = new ItemStack(SkrimItems.COBBLE_FUCKER);
    pick.addEnchantment(Enchantments.EFFICIENCY, 10);
    pick.addEnchantment(Enchantments.KNOCKBACK, 2);
    subItems.add(pick);
  }

  @Mod.EventBusSubscriber(modid = Skrim.MOD_ID)
  public static class CobbleFuckerHandler {

    @SubscribeEvent
    public static void fuckCobble(BlockEvent.BreakEvent event) {
      EntityPlayer player = event.getPlayer();
      if (player.getHeldItemMainhand().getItem() != SkrimItems.COBBLE_FUCKER) return;
      if (event.getState().getBlock() != Blocks.STONE && event.getState().getBlock() != Blocks.COBBLESTONE) return;

      ItemStack cobbleFucker = player.getHeldItemMainhand();
      BlockPos startPos = event.getPos();

      for (int i = -RADIUS; i <= RADIUS; i++) {
        for (int j = -RADIUS; j <= RADIUS; j++) {
          for (int k = -RADIUS; k <= RADIUS; k++) {
            BlockPos newPos = startPos.add(i, j, k);
            IBlockState state = player.world.getBlockState(newPos);
            if (state.getBlock() != Blocks.STONE && state.getBlock() != Blocks.COBBLESTONE) continue;

            player.world.destroyBlock(newPos, true);
            cobbleFucker.damageItem(1, player);
          }
        }
      }
    }
  }

}
