package avi.mod.skrim.items;

import avi.mod.skrim.Skrim;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemPickaxe;

public class CustomPickaxe extends ItemPickaxe implements ItemBase {
	
	private String name;
	
	public CustomPickaxe(String name, ToolMaterial material) {
    super(material);
    this.name = name;
    this.setUnlocalizedName(name);
    this.setRegistryName(name);
    setCreativeTab(Skrim.creativeTab);
	}

	@Override
	public String getTexturePath() {
		return "tools";
	}
}
