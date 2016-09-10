package avi.mod.skrim.items;

import java.util.List;

import avi.mod.skrim.Skrim;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CustomArmor extends ItemArmor implements ItemModelProvider {
	
	protected String name;
	
	public CustomArmor(String name, ArmorMaterial material, int renderIndex, EntityEquipmentSlot armorType) {
		super(material, renderIndex, armorType);
		this.name = name;
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
	}

	@Override
	public void registerItemModel(Item item) {
		Skrim.proxy.registerItemRenderer(this, 0, this.name);
	}

}
