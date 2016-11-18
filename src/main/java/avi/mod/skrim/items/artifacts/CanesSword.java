package avi.mod.skrim.items.artifacts;

import java.util.List;

import avi.mod.skrim.items.ModItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class CanesSword extends ArtifactSword {

	public CanesSword() {
		super("raising_canes_fry_sword", ModItems.ARTIFACT_DEFAULT);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean par4) {
		tooltip.add("§4Sweep attack ignites enemies.");
		tooltip.add("§4Deals 20x damage to chickens & fries them.§r");
		tooltip.add("§e\"Chicken chicken chicken, which combo you pickin'?\"");
	}

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
			      if (sword == ModItems.CANES_SWORD) {
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
		      if (sword == ModItems.CANES_SWORD) {
		      	List<EntityItem> drops = event.getDrops();
		      	for (int i = 0; i < drops.size(); i++) {
		      		EntityItem item = drops.get(i);
		      		if (item.getName().equals("item.item.chickenCooked") || item.getName().equals("item.item.chickenRaw")) {
		      			drops.set(i, new EntityItem(player.worldObj, item.posX, item.posY, item.posZ, new ItemStack(ModItems.CANES_CHICKEN)));
		      		}
		      	}
		      }
		    }
			}
		}
	}

	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, List subItems) {
		ItemStack caneStack = new ItemStack(ModItems.CANES_SWORD);
		caneStack.addEnchantment(Enchantments.FIRE_ASPECT, 2);
		subItems.add(caneStack);
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		if (attacker instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) attacker;
			Item sword = stack.getItem();
			if (sword == ModItems.CANES_SWORD) {
				if (canSweep(player, target)) {
					doFireSweep(player, target);
				}
			}
		}
		return true;
	}

	public static boolean canSweep(EntityPlayer player, EntityLivingBase targetEntity) {
		boolean flag1 = player.isSprinting();
	  boolean flag2 = player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder() && !player.isInWater() && !player.isPotionActive(MobEffects.BLINDNESS) && !player.isRiding() && targetEntity instanceof EntityLivingBase;
	  double d0 = (double)(player.distanceWalkedModified - player.prevDistanceWalkedModified);
	  return (!flag2 && !flag1 && player.onGround && d0 < (double) player.getAIMoveSpeed());
	}


	public static void doFireSweep(EntityPlayer player, EntityLivingBase targetEntity) {
		for (EntityLivingBase entitylivingbase : player.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, targetEntity.getEntityBoundingBox().expand(1.0D, 0.25D, 1.0D))) {
			if (entitylivingbase != player && entitylivingbase != targetEntity && !player.isOnSameTeam(entitylivingbase) && player.getDistanceSqToEntity(entitylivingbase) < 9.0D) {
	      entitylivingbase.setFire(4);
	    }
		}
	}

}
