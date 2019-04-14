package avi.mod.skrim.items.tools;

import avi.mod.skrim.items.ItemBase;
import net.minecraft.item.ItemAxe;

/**
 * And my axe.
 */
public class CustomAxe extends ItemAxe implements ItemBase {

  private String name;

  private static final float[] ATTACK_DAMAGES = new float[]{6.0F, 8.0F, 8.0F, 8.0F, 6.0F};
  private static final float[] ATTACK_SPEEDS = new float[]{-3.2F, -3.2F, -3.1F, -3.0F, -3.0F};

  public CustomAxe(String name, ToolMaterial material) {
    this(name, material, ATTACK_DAMAGES[material.ordinal()], ATTACK_SPEEDS[material.ordinal()]);
  }

  public CustomAxe(String name, ToolMaterial material, float damage, float speed) {
    super(material, damage, speed);
    this.name = name;
    this.setUnlocalizedName(name);
    this.setRegistryName(name);
  }

  @Override
  public String getTexturePath() {
    return "tools";
  }

}