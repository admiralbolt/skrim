package avi.mod.skrim.items;

import avi.mod.skrim.Skrim;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemHoe;

public class CustomHoe extends ItemHoe implements ItemModelProvider {
	
	private String name;
	
	public CustomHoe(String name, ToolMaterial material) {
    super(material);
    this.name = name;
    this.setUnlocalizedName(name);
    this.setRegistryName(name);
    setCreativeTab(Skrim.creativeTab);
	}

	@Override
	public void registerItemModel(Item item) {
		Skrim.proxy.registerItemRenderer(this, 0, this.name);
	}
	
}
