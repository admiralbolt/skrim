package avi.mod.skrim.handlers.skills;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTNT;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.audio.Sound;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.minecart.MinecartUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.demolition.SkillDemolition;

public class DemolitionHandler {
	
	public Map<BlockPos, EntityPlayer> validGoBoom = new HashMap<BlockPos, EntityPlayer>();

  @SubscribeEvent
	public void onGoBoom(final ExplosionEvent.Detonate event) {
		List<BlockPos> blocks = event.getAffectedBlocks();
		Explosion boom = event.getExplosion();
		Entity source = boom.getExplosivePlacedBy();
		final BlockPos location = new BlockPos(boom.getPosition());
		if (this.validGoBoom.containsKey(location)) {
			EntityPlayer player = this.validGoBoom.get(location);
			this.validGoBoom.remove(location);
			if (player.hasCapability(Skills.DEMOLITION, EnumFacing.NORTH)) {
				SkillDemolition demolition = (SkillDemolition) player.getCapability(Skills.DEMOLITION, EnumFacing.NORTH);
				demolition.xp += blocks.size() * 2;
				demolition.levelUp((EntityPlayerMP) player);
				int maxAdditional = demolition.getMaxAdditional();
				int delay = 500;
				for (int i = 0; i <= maxAdditional; i++) {
					double random = Math.random();
					if (random < demolition.getExplosionChance(2 + i)) {
						new Timer().schedule(
							new TimerTask() {
								@Override
								public void run() {
									event.getExplosion().doExplosionA();
									event.getExplosion().doExplosionB(true);
								}
							}, delay
						);
						delay += 500;
						demolition.xp += 50;
						demolition.levelUp((EntityPlayerMP) player);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onTnt(BlockEvent.PlaceEvent event) {
		IBlockState state = event.getPlacedBlock();
		EntityPlayer player = event.getPlayer();
		if (state != null && player != null && player.hasCapability(Skills.DEMOLITION, EnumFacing.NORTH)) {
			Block block = state.getBlock();
			if (block instanceof BlockTNT) {
				this.validGoBoom.put(event.getPos(), player);
			}
		}
	}

	@SubscribeEvent
	public void onPlayerHurt(LivingHurtEvent event) {
		Entity entity = event.getEntity();
		DamageSource source = event.getSource();
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if (source.isExplosion()) {
				if (player != null && player.hasCapability(Skills.DEMOLITION, EnumFacing.NORTH)) {
					SkillDemolition demo = (SkillDemolition) player.getCapability(Skills.DEMOLITION, EnumFacing.NORTH);
					event.setAmount(event.getAmount() - (float) (event.getAmount() * demo.getResistance()));
				}
			}
		}
	}

}
