package avi.mod.skrim.items;

import net.minecraft.item.ItemRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.translation.I18n;

public class CustomRecord extends ItemRecord implements ItemBase {

  protected String name;

  protected CustomRecord(String name, SoundEvent soundIn) {
    super(name, soundIn);
    this.name = name;
    this.setRegistryName(name);
    this.setUnlocalizedName(name);
  }

  @Override
  public String getTexturePath() {
    return "records";
  }

  @Override
  public String getRecordNameLocal() {
    return I18n.translateToLocal(this.getUnlocalizedName() + ".desc");
  }

}
