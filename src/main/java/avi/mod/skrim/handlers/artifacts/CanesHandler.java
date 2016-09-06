package avi.mod.skrim.handlers.artifacts;

import java.util.List;

import avi.mod.skrim.items.ModItems;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.melee.SkillMelee;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CanesHandler {
	
	@SubscribeEvent
  public void onPlayerHurt(LivingHurtEvent event) {
    DamageSource source = event.getSource();
    Entity entity = source.getEntity();
    if (entity instanceof EntityPlayer) {
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
	
	@SubscribeEvent
	public void fryChicken(LivingDropsEvent event) {
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
