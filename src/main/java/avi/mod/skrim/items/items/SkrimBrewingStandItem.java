package avi.mod.skrim.items.items;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.blocks.SkrimBlocks;
import avi.mod.skrim.items.ItemBase;
import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.utils.Utils;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class SkrimBrewingStandItem extends ItemBlockSpecial implements ItemBase {

  public static final String NAME = "skrim_brewing_stand_item";

  public SkrimBrewingStandItem() {
    super(SkrimBlocks.BREWING_STAND);
    this.setUnlocalizedName(NAME);
    this.setRegistryName(NAME);
  }

  @Override
  public String getTexturePath() {
    return "items";
  }

  @Mod.EventBusSubscriber(modid = Skrim.MOD_ID)
  public static class Handler {

    @SubscribeEvent
    public static void pickupBrewingStand(PlayerEvent.ItemPickupEvent event) {
      ItemStack stack = event.getStack();
      if (stack.getItem() == Items.BREWING_STAND) {
        Utils.removeFromInventory(event.player.inventory, Items.BREWING_STAND, stack.getCount());
        ItemStack newStack = new ItemStack(SkrimItems.BREWING_STAND, stack.getCount(), stack.getMetadata());
        newStack.setTagCompound(stack.getTagCompound());
        event.player.addItemStackToInventory(newStack);
      }
    }

  }
}
