package avi.mod.skrim.handlers.skills;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import avi.mod.skrim.Utils;
import avi.mod.skrim.network.FallDistancePacket;
import avi.mod.skrim.network.SkrimPacketHandler;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.mining.SkillMining;

public class MiningHandler {
	
	public List<EntityPlayer> shouldRemove = new ArrayList<EntityPlayer>();

  @SubscribeEvent
	public void onBlockBreak(BlockEvent.BreakEvent event) {
		EntityPlayer player = event.getPlayer();
		if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.MINING, EnumFacing.NORTH)) {
			IBlockState state = event.getState();
			Block target = state.getBlock();
			String blockName;
			if (target instanceof BlockStone) {
				blockName = state.getValue(BlockStone.VARIANT).toString();
			} else {
				blockName = Utils.snakeCase(target.getLocalizedName());
			}
			SkillMining mining = (SkillMining) player.getCapability(Skills.MINING, EnumFacing.NORTH);
			int addXp = mining.getXp(blockName);
			if (addXp > 0) {
				mining.xp += mining.getXp(blockName);
				mining.levelUp((EntityPlayerMP) player);
			}
		}
	}

	@SubscribeEvent
	public void breakSpeed(PlayerEvent.BreakSpeed event) {
		EntityPlayer player = event.getEntityPlayer();
		if (player.hasCapability(Skills.MINING, EnumFacing.NORTH)) {
			SkillMining mining = (SkillMining) player.getCapability(Skills.MINING, EnumFacing.NORTH);
			IBlockState state = event.getState();
			if (mining.validSpeedTarget(state)) {
				event.setNewSpeed((float) (event.getOriginalSpeed() + mining.getSpeedBonus()));
			}
		}
	}

	@SubscribeEvent
	public void onMineOre(BlockEvent.HarvestDropsEvent event) {
		EntityPlayer player = event.getHarvester();
		if (player != null && player.hasCapability(Skills.MINING, EnumFacing.NORTH)) {
			SkillMining mining = (SkillMining) player.getCapability(Skills.MINING, EnumFacing.NORTH);
			IBlockState state = event.getState();
			if (mining.validFortuneTarget(state)) {
				double random = Math.random();
				if (random < mining.getFortuneChance()) {
					List<ItemStack> drops = event.getDrops();
					ItemStack copyDrop = drops.get(0);
					// Let's not loop infinitely, that seems like a good idea.
					int dropSize = drops.size();
					for (int i = 0; i < (dropSize * (mining.getFortuneAmount() - 1)); i++) {
						drops.add(copyDrop.copy());
					}
				}
			}
		}
	}

  @SubscribeEvent
  public void onFireDamage(LivingHurtEvent event) {
    Entity entity = event.getEntity();
    if (entity instanceof EntityPlayer) {
      final EntityPlayer player = (EntityPlayer) entity;
      if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.MINING, EnumFacing.NORTH)) {
        Skill mining = (Skill) player.getCapability(Skills.MINING, EnumFacing.NORTH);
        if (mining.hasAbility(2)) {
        	BlockPos pos = player.getPosition();
        	if (pos.getY() <= 40) {
            DamageSource source = event.getSource();
		        if (source.getDamageType().equals("lava") || source.getDamageType().equals("inFire")) {
		        	event.setAmount((float) (event.getAmount() * 0.5));
		        	player.extinguish();
		        	new Timer().schedule(
								new TimerTask() {
									@Override
									public void run() {
										player.extinguish();
									}
								}, 400
							);
		        }
        	}
        }
      }
    }
  }
  
  @SubscribeEvent
  public void climbWall(LivingUpdateEvent event) {
  	Entity entity = event.getEntity();
    if (entity instanceof EntityPlayer) {
      final EntityPlayer player = (EntityPlayer) entity;
      if (player != null && player.hasCapability(Skills.MINING, EnumFacing.NORTH)) {
      	Skill mining = (Skill) player.getCapability(Skills.MINING, EnumFacing.NORTH);
      	if (mining.hasAbility(1)) {
      		BlockPos pos = player.getPosition();
      		if (pos.getY() <= 40) {
      			player.addPotionEffect(new PotionEffect(Potion.getPotionById(16), 60, 1, true, false));
      			if (!this.shouldRemove.contains(player)) {
      				shouldRemove.add(player);
      			}
      		} else {
      			if (this.shouldRemove.contains(player)) {
      				player.removePotionEffect(Potion.getPotionById(16));
      				shouldRemove.remove(player);
      			}
      		}
      	}
        if (mining.hasAbility(3)) {
        	if (player.isCollidedHorizontally) {
        		System.out.println("player.side: " + player.worldObj.isRemote + ", fall distance: " + player.fallDistance);
        		System.out.println("player instanceof: " + (player instanceof EntityPlayerMP));
        		player.motionY = Math.min(0.5, player.motionY + 0.2);
        		if (player.motionY > 0) {
        			player.fallDistance = 0.0F;
        		} else {
        			player.fallDistance -= 1F;
        		}
        		// SkrimPacketHandler.INSTANCE.sendToServer(new FallDistancePacket(player.fallDistance));
        	}
        	
        }
      }
    }
  }

}
