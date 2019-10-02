package avi.mod.skrim.items.artifacts;

import avi.mod.skrim.items.items.ArtifactItem;
import avi.mod.skrim.network.SkrimPacketHandler;
import avi.mod.skrim.network.TeleportPacket;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class Teleportal extends ArtifactItem {

  public static double RANGE = 100;

  public Teleportal() {
    super("teleportal");
    this.setMaxDamage(1000);
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    tooltip.add("§4Teleport to where you're looking.");
    tooltip.add("Max range of " + RANGE + "m");
    tooltip.add("§e\"Gotta go fast.\"§r");
  }

  @Override
  @Nonnull
  public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand hand) {
    ItemStack itemStackIn = playerIn.getHeldItem(hand);
    // Can only raytrace on the client side.
    if (!worldIn.isRemote) return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);

    RayTraceResult result = playerIn.rayTrace(RANGE, 1.0F);
    if (result == null || result.typeOfHit != RayTraceResult.Type.BLOCK) return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);

    System.out.println("hitPos: " + result.getBlockPos() + ", sideHit: " + result.sideHit);
    BlockPos targetPos = result.getBlockPos();
    // We need to make a slight adjustment to the teleport location based on which face of the block we hit, otherwise we'll try to
    // teleport inside the block. We can do this with the result.sideHit which gives an enum corresponding to a cardinal direction OR up /
    // down.
    //
    // south -> positive Z
    // north -> negative Z
    // east -> positive X
    // west -> negative X
    double x = targetPos.getX() + 0.5;
    if (result.sideHit == EnumFacing.EAST) {
      x += 1;
    } else if (result.sideHit == EnumFacing.WEST) {
      x -= 1;
    }
    double y = targetPos.getY();
    if (result.sideHit == EnumFacing.UP) {
      y += 1;
    } else if (result.sideHit == EnumFacing.DOWN) {
      y -= 1;
    }
    double z = targetPos.getZ() + 0.5;
    if (result.sideHit == EnumFacing.SOUTH) {
      z += 1;
    } else if (result.sideHit == EnumFacing.NORTH) {
      z -= 1;
    }
    SkrimPacketHandler.INSTANCE.sendToServer(new TeleportPacket(x, y, z));
    itemStackIn.damageItem(1, playerIn);
    return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
  }


}
