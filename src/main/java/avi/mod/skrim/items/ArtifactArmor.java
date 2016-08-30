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

public class ArtifactArmor extends ItemArmor implements ItemModelProvider {
	
	protected String name;
	
	public ArtifactArmor(String name, ArmorMaterial material, int renderIndex, EntityEquipmentSlot armorType) {
		super(material, renderIndex, armorType);
		this.name = name;
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
	}
	
	@Override
	public boolean hasEffect(ItemStack stack) {
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return ModItems.ARTIFACT_RARITY;
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean par4) {
		tooltip.add("§4Massively increases jump height.§r");
		tooltip.add("§4Prevents all fall damage.§r");
		tooltip.add("§e\"Falco mode engaged.\"");
	}

	@Override
	public void registerItemModel(Item item) {
		Skrim.proxy.registerItemRenderer(this, 0, this.name);
	}

}
