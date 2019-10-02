package avi.mod.skrim.items.artifacts;

import avi.mod.skrim.items.items.ArtifactItem;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class Detonator extends ArtifactItem {

  private static final int RANGE = 50;

  public Detonator() {
    super("detonator");
    this.setMaxDamage(100);
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    tooltip.add("§4Blow up all TNT in a " + RANGE + "m radius.§r");
    tooltip.add("§e\"Boom goes the... Everything.\"§r");
  }

  @Override
  @Nonnull
  public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand hand) {
    ItemStack itemStackIn = playerIn.getHeldItem(hand);
    BlockPos pos = playerIn.getPosition();
    int x = pos.getX();
    int y = pos.getY();
    int z = pos.getZ();
    BlockPos from = new BlockPos(x - RANGE, Math.max(y - RANGE, 0), z - RANGE);
    BlockPos to = new BlockPos(x + RANGE, Math.min(y + RANGE, 150), z + RANGE);
    // getAllInBoxMutable is insanely fast. You should ALWAYS use this when you can.
    for (BlockPos checkPos : BlockPos.getAllInBoxMutable(from, to)) {
      IBlockState state = worldIn.getBlockState(checkPos);
      if (!(state.getBlock() instanceof BlockTNT)) continue;

      ((BlockTNT) state.getBlock()).explode(worldIn, checkPos, state.withProperty(BlockTNT.EXPLODE, true), playerIn);
      worldIn.setBlockState(checkPos, Blocks.AIR.getDefaultState());
    }

    itemStackIn.damageItem(1, playerIn);
    return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
  }

}
