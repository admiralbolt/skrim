package avi.mod.skrim.items.artifacts;

import java.util.List;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.items.ItemModelProvider;
import avi.mod.skrim.items.ModItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ArtifactSword extends ItemSword implements ItemModelProvider {

	protected String name;

	public ArtifactSword(String name, ToolMaterial material) {
		super(material);
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
		tooltip.add("§4Sweep attack ignites enemies.");
		tooltip.add("§4Deals 20x damage to chickens & fries them.§r");
		tooltip.add("§e\"Chicken chicken chicken, which combo you pickin'?\"");
	}

	@Override
	public void registerItemModel(Item item) {
		Skrim.proxy.registerItemRenderer(this, 0, this.name);
	}
	
	@Override
	public int getItemEnchantability() {
		return 0;
	}

}
