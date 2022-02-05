package avi.mod.skrim.items.tools;

import avi.mod.skrim.items.ItemBase;
import net.minecraft.item.ItemFishingRod;

/**
 * This is legitimately a 100% copy of ItemFishingRod, but coded as a new item,
 * to hopefully avoid whatever bullshit registry shenanigans rlcraft is pulling
 * on us.
 *
 * I'm not a fan of fishing made better.
 */
public class SkrimRod extends ItemFishingRod implements ItemBase {

  public SkrimRod() {
    super();
    this.setUnlocalizedName("skrim_rod");
    this.setRegistryName("skrim_rod");
  }

  @Override
  public String getTexturePath() {
    return "tools";
  }
}
