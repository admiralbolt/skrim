package avi.mod.skrim.items.artifacts;

import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.items.items.ArtifactItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class RaffleTicket extends ArtifactItem {

  public RaffleTicket() {
    super("raffle_ticket");
    this.setMaxDamage(1);
    this.setMaxStackSize(10);
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    tooltip.add("§4Spend 10 raffle tickets to win a prize!§r");
    tooltip.add("§e\"Better luck next time!\"§r");
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
    ItemStack itemStackIn = playerIn.getHeldItem(hand);
    if (itemStackIn.getCount() < 10) {
      playerIn.sendMessage(new TextComponentString("You need at least 10 raffle tickets to enter."));
      return new ActionResult<>(EnumActionResult.FAIL, itemStackIn);
    }

    // Take 10 tickets, and give them a random artifact.
    itemStackIn.setCount(0);
    int random = new Random().nextInt(SkrimItems.ARTIFACTS.length);
    playerIn.addItemStackToInventory(new ItemStack(SkrimItems.ARTIFACTS[random]));
    return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
  }
}
