package avi.mod.skrim.skills.demolition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;

public class SkillDemolition extends Skill implements ISkillDemolition {

	public static SkillStorage<ISkillDemolition> skillStorage = new SkillStorage<ISkillDemolition>();
	public static Map<BlockPos, EntityPlayer> validGoBoom = new HashMap<BlockPos, EntityPlayer>();

	public SkillDemolition() {
		this(1, 0);
	}

	public SkillDemolition(int level, int currentXp) {
		super("Demolition", level, currentXp);
		this.iconTexture = new ResourceLocation("skrim", "textures/guis/skills/demolition.png");
	}

	public double getResistance() {
		return this.level * 0.01;
	}

	public double getExplosionChance(int extra) {
		return this.level * 0.01 - ((extra - 2) * 0.1);
	}

	public int getMaxAdditional() {
		return (int) ((this.level - 1) / 10);
	}

	@Override
	public List<String> getToolTip() {
		List<String> tooltip = new ArrayList<String>();
		tooltip.add("Passively gain §a" + Utils.formatPercent(this.getResistance()) + "%§r explosive resistance.");
		int maxAdditional = this.getMaxAdditional();
		for (int i = 0; i <= maxAdditional; i++) {
			tooltip.add("Your TNT has a §a" + Utils.formatPercent(this.getExplosionChance(2 + i)) + "%§r chance to cause an §aadditional explosion§r.");
		}
		return tooltip;
	}

	public static void onGoBoom(final ExplosionEvent.Detonate event) {
		List<BlockPos> blocks = event.getAffectedBlocks();
		Explosion boom = event.getExplosion();
		Entity source = boom.getExplosivePlacedBy();
		final BlockPos location = new BlockPos(boom.getPosition());
		if (validGoBoom.containsKey(location)) {
			EntityPlayer player = validGoBoom.get(location);
			validGoBoom.remove(location);
			if (player.hasCapability(Skills.DEMOLITION, EnumFacing.NORTH)) {
				SkillDemolition demolition = (SkillDemolition) player.getCapability(Skills.DEMOLITION, EnumFacing.NORTH);
				demolition.addXp((EntityPlayerMP) player, blocks.size() * 2);
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

	public static void onTntPlaced(BlockEvent.PlaceEvent event) {
		IBlockState state = event.getPlacedBlock();
		EntityPlayer player = event.getPlayer();
		if (state != null && player != null && player.hasCapability(Skills.DEMOLITION, EnumFacing.NORTH)) {
			Block block = state.getBlock();
			if (block instanceof BlockTNT) {
				validGoBoom.put(event.getPos(), player);
			}
		}
	}

	public static void reduceExplosion(LivingHurtEvent event) {
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

	public static void onKillCrepper(LivingDeathEvent event) {
		Entity sourceEntity = event.getSource().getEntity();
		if (sourceEntity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) sourceEntity;
			Entity targetEntity = event.getEntity();
			if (targetEntity instanceof EntityCreeper) {
				if (player != null && player.hasCapability(Skills.DEMOLITION, EnumFacing.NORTH)) {
					SkillDemolition demo = (SkillDemolition) player.getCapability(Skills.DEMOLITION, EnumFacing.NORTH);
					demo.addXp((EntityPlayerMP) player, 5);
				}
			}
		}
	}

}
