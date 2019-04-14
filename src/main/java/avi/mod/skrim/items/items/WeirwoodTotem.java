package avi.mod.skrim.items.items;

import avi.mod.skrim.items.ItemBase;

public class WeirwoodTotem extends CustomItem implements ItemBase {

  public WeirwoodTotem() {
    super("weirwood_totem");
    this.setMaxDamage(1);
  }

  @Override
  public String getTexturePath() {
    return "items";
  }
}
