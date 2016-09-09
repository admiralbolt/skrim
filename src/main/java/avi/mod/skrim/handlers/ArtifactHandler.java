package avi.mod.skrim.handlers;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import avi.mod.skrim.items.ModItems;

public class ArtifactHandler {
	
	/**
	 * Handlers for the raisin cane's sword of frying
	 */
	public static class CanesHandler {
	
	  public static void slayChicken(LivingHurtEvent event) {
	    DamageSource source = event.getSource();
	    Entity entity = source.getEntity();
	    if (entity instanceof EntityPlayer) {
	    	if (event.getEntity() instanceof EntityChicken) {
		      EntityPlayer player = (EntityPlayer) entity;
		      ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
		      if (stack != null) {
			      Item sword = stack.getItem();
			      if (sword == ModItems.raisingCanesFrySword) {
			      	event.setAmount(event.getAmount() * 10);
			      }
		      }
	    	}
	    }
	  }
		
		public static void fryChicken(LivingDropsEvent event) {
			if (event.getEntity() instanceof EntityChicken) {
				DamageSource source = event.getSource();
		    Entity sourceEntity = source.getEntity();
		    if (sourceEntity instanceof EntityPlayer) {
		      EntityPlayer player = (EntityPlayer) sourceEntity;
		      ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
		      Item sword = stack.getItem();
		      if (sword == ModItems.raisingCanesFrySword) {
		      	List<EntityItem> drops = event.getDrops();
		      	for (int i = 0; i < drops.size(); i++) {
		      		EntityItem item = drops.get(i);
		      		System.out.println(item.getName());
		      		if (item.getName().equals("item.item.chickenCooked") || item.getName().equals("item.item.chickenRaw")) {
		      			drops.set(i, new EntityItem(player.worldObj, item.posX, item.posY, item.posZ, new ItemStack(ModItems.canesChicken)));
		      		}
		      	}
		      }
		    }
			}
		}
	}
	
	/**
	 * Handlers for boots of springheel jack
	 */
	public static class SpringheelHandler {
	
		public static void jumpHigh(LivingEvent.LivingJumpEvent event) {
			Entity entity = event.getEntity();
			if (entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entity;
				if (player.worldObj.isRemote) {
					InventoryPlayer inventory = player.inventory;
					if (inventory != null) {
						ItemStack stack = inventory.armorInventory[0];
						if (stack != null) {
							Item boots = stack.getItem();
							if (boots == ModItems.bootsOfSpringheelJak) {
								player.motionY *= 3;
								player.setVelocity(player.motionX, player.motionY, player.motionZ);
							}
						}
					}
				}
			}
		}
	
		public static void preventFallDamage(LivingFallEvent event) {
			Entity entity = event.getEntity();
			if (entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entity;
				InventoryPlayer inventory = player.inventory;
				event.setDistance(player.fallDistance);
				if (player.worldObj.isRemote && player.motionY > -0.08) {
					event.setDistance(0.0F);
				}
				if (inventory != null) {
					ItemStack stack = inventory.armorInventory[0];
					if (stack != null) {
						Item boots = stack.getItem();
						if (boots == ModItems.bootsOfSpringheelJak) {
							event.setDistance(0);
							event.setCanceled(true);
						}
					}
				}
			}
		}
	}

}
