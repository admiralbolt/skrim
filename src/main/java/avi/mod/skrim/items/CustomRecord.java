package avi.mod.skrim.items;

import avi.mod.skrim.Skrim;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.translation.I18n;

public class CustomRecord extends ItemRecord implements ItemModelProvider {

	protected String name;

	protected CustomRecord(String name, SoundEvent soundIn) {
		super(name, soundIn);
		this.name = name;
		this.setRegistryName(name);
		this.setUnlocalizedName(name);
	}

	@Override
	public void registerItemModel(Item item) {
		Skrim.proxy.registerItemRenderer(this, 0, this.getUnlocalizedName());
	}

	public ResourceLocation getRecordResource() {
		return new ResourceLocation("skrim:" + this.name);
	}
	
	@Override
	public String getRecordNameLocal() {
		return I18n.translateToLocal(this.getUnlocalizedName() + ".desc");
	}


}
