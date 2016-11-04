package avi.mod.skrim.items.artifacts;

import java.util.List;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.items.ItemModelProvider;
import avi.mod.skrim.items.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ArtifactArmor extends ItemArmor implements ItemModelProvider {

	protected String name;

	public ArtifactArmor(String name, EntityEquipmentSlot armorType) {
		this(name, getAndCreateMaterial(name), armorType);
	}

	public ArtifactArmor(String name, ArmorMaterial material, EntityEquipmentSlot armorType) {
		super(material, 1, armorType);
		this.name = name;
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
	}

	// Default armor stats
	public static ArmorMaterial getAndCreateMaterial(String name) {
		return EnumHelper.addArmorMaterial(name, "skrim:" + name, 50, new int[] {3, 8, 6, 3}, 30, null, 0.0F);
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
		tooltip.add("Custom tooltip");
	}

	@Override
	public void registerItemModel(Item item) {
		Skrim.proxy.registerItemRenderer(this, 0, this.name);
	}

}
