package avi.mod.skrim.blocks.flowers;

import net.minecraft.block.BlockBush;

public class GlowFlower extends BlockBush {

  public String name;

  public GlowFlower(String name) {
    super();
    this.name = name;
    this.setRegistryName(name);
    this.setUnlocalizedName(name);
    this.setLightLevel(1.0F);
  }

}
