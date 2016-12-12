package avi.mod.skrim;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.brewing.PotionBrewEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public class Scratch {
	
	@SubscribeEvent
	private void onRepair(AnvilRepairEvent event) {
		ItemStack leftInput = event.getItemInput();
		ItemStack rightInput = event.getIngredientInput();
		ItemStack output = event.getItemResult();
	}
	
	@SubscribeEvent
	private void onSpawn(EntityJoinWorldEvent event) {
		event.getEntity();
	}

  //  @SubscribeEvent
//  private void animalTest(EntityInteract event) {
//	  Entity animal = event.getTarget();
//	  EntityPlayer player = event.getEntityPlayer();
//	  if (animal instanceof EntityAnimal) {
//		  EntityAnimal animal2 = (EntityAnimal) animal;
//		  animal2.
//	  }
//  }

  @SubscribeEvent
  private void onBrew(PotionBrewEvent.Post event) {
  	event.getItem(0);
  }
//
  @SubscribeEvent
  private void onDamage(LivingHurtEvent event) {
  	Entity asdf = event.getEntityLiving();
  	if (asdf instanceof EntityPlayer) {
  		EntityPlayer player = (EntityPlayer) asdf;
  		player.getAttributeMap();
  		IAttributeInstance maxHealth = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
  		maxHealth.applyModifier(new AttributeModifier("more max health!", 0, 0));
  	}
  }
//
//  @SubscribeEvent
//  private void onEnchantment(EnchantmentEvent event) {
//
//  }

//  @SubscribeEvent
//  private void onBoom(ExplosionEvent event) {
//	  BlockTNT basdf = new BlockTNT();
//	 Explosion asdf = event.getExplosion();
//	 asdf.getExplosivePlacedBy();
//	 Enchantment blast = new Enchantment
//  }
//
//  @SubscribeEvent
//  private void onDetonate(ExplosionEvent.Detonate event) {
//	Explosion asdf = event.getExplosion();
//	asdf.getExplosivePlacedBy();
//	asdf.
//  }
//
//  private void test() {
//	  BlockTNT block = new BlockTNT();
//  }
  
  

}
