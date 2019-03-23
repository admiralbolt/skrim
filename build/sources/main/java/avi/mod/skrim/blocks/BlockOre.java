package avi.mod.skrim.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import avi.mod.skrim.Skrim;

public class BlockOre extends BlockBase {

  public BlockOre(String name) {
    super(Material.ROCK, name);
    this.setHardness(3f);
    this.setResistance(5f);
  }

  @Override
  public BlockOre setCreativeTab(CreativeTabs tab) {
    super.setCreativeTab(tab);
    return this;
  }

}
