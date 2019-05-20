package avi.mod.skrim.items.items;

import avi.mod.skrim.blocks.SkrimBlocks;
import avi.mod.skrim.items.ItemBase;
import net.minecraft.item.ItemBlockSpecial;

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
}
