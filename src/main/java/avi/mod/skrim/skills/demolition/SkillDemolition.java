package avi.mod.skrim.skills.demolition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import avi.mod.skrim.blocks.ModBlocks;
import avi.mod.skrim.blocks.tnt.CustomExplosion;
import avi.mod.skrim.entities.monster.BioCreeper;
import avi.mod.skrim.entities.monster.NapalmCreeper;
import avi.mod.skrim.items.ModItems;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillAbility;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.utils.Reflection;
import avi.mod.skrim.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

public class SkillDemolition extends Skill implements ISkillDemolition {

	public static SkillStorage<ISkillDemolition> skillStorage = new SkillStorage<ISkillDemolition>();
	public static Map<BlockPos, EntityPlayer> validGoBoom = new HashMap<BlockPos, EntityPlayer>();

	public static SkillAbility DYNAMITE = new SkillAbility("demolition", "Dynamite", 25, "Boom goes the dynamite.",
			"Grants you the ability to craft dynamite with tnt & a pickaxe.", "Dynamite has a larger blast radius and a 100% chance to drop blocks.");
	public static SkillAbility BIOBOMB = new SkillAbility("demolition", "Bio-Bomb", 50, "A whole new meaning for c'mon BB.",
			"Grants you the ability to craft Bio-Bomb with... Stuff...", "Bio-bombs have twice the blast radius of tnt, and don't affect blocks.");
	public static SkillAbility NAPALM = new SkillAbility("demolition", "Napalm", 75, "Handle with care.",
			"Grants you the ability to craft Napalm with... Stuff...", "Napalm has triple the blast radius of tnt, starts fires, and creates lava spawns.");
	public static SkillAbility BADONKADONK = new SkillAbility("demolition", "Badonkadonk", 100, "Gut full of dynamite and booty like POW.",
			"Grants you the ability to craft a rocket launcher.");

	public SkillDemolition() {
		this(1, 0);
	}

	public SkillDemolition(int level, int currentXp) {
		super("Demolition", level, currentXp);
		this.addAbilities(DYNAMITE, BIOBOMB, NAPALM, BADONKADONK);
	}

	public double getResistance() {
		return this.level * 0.01;
	}

	public double getExtraPower() {
		return this.level * 0.01;
	}

	@Override
	public List<String> getToolTip() {
		List<String> tooltip = new ArrayList<String>();
		tooltip.add("Passively gain §a" + Utils.formatPercent(this.getResistance()) + "%§r explosive resistance.");
		tooltip.add("Your explosions are §a" + Utils.formatPercent(this.getExtraPower()) + "%§r larger.");
		return tooltip;
	}

	public static void beforeGoBoom(final ExplosionEvent.Start event) {
		Explosion boom = event.getExplosion();
		Entity source = boom.getExplosivePlacedBy();
		final BlockPos location = new BlockPos(boom.getPosition());
		if (validGoBoom.containsKey(location)) {
			EntityPlayer player = validGoBoom.get(location);
			if (player.hasCapability(Skills.DEMOLITION, EnumFacing.NORTH)) {
				SkillDemolition demolition = (SkillDemolition) player.getCapability(Skills.DEMOLITION, EnumFacing.NORTH);
				Utils.logSkillEvent(event, demolition, "explosive placed by: " + event.getExplosion().getExplosivePlacedBy());
				if (boom instanceof CustomExplosion) {
					CustomExplosion customBoom = (CustomExplosion) boom;
					customBoom.setExplosionSize((float) (customBoom.getExplosionSize() * (1 + demolition.getExtraPower())));
				} else {
					Reflection.hackValueTo(boom, (float) (4.0 * (1 + demolition.getExtraPower())), "explosionSize", "field_77280_f");
				}
			}
		}
	}

	public static void onGoBoom(final ExplosionEvent.Detonate event) {
		Explosion explosion = event.getExplosion();
		List<BlockPos> blocks = event.getAffectedBlocks();
		Explosion boom = event.getExplosion();
		final BlockPos location = new BlockPos(boom.getPosition());
		if (validGoBoom.containsKey(location)) {
			EntityPlayer player = validGoBoom.get(location);
			validGoBoom.remove(location);
			if (player.hasCapability(Skills.DEMOLITION, EnumFacing.NORTH)) {
				SkillDemolition demolition = (SkillDemolition) player.getCapability(Skills.DEMOLITION, EnumFacing.NORTH);
				Utils.logSkillEvent(event, demolition, "explosive placed by: " + event.getExplosion().getExplosivePlacedBy());
				demolition.addXp((EntityPlayerMP) player, 7500);
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

	public static void onKillCreeper(LivingDeathEvent event) {
		Entity sourceEntity = event.getSource().getTrueSource();
		if (sourceEntity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) sourceEntity;
			Entity targetEntity = event.getEntity();
			if (targetEntity instanceof EntityCreeper) {
				if (player != null && player.hasCapability(Skills.DEMOLITION, EnumFacing.NORTH)) {
					SkillDemolition demo = (SkillDemolition) player.getCapability(Skills.DEMOLITION, EnumFacing.NORTH);
					int addXp = 0;
					if (targetEntity instanceof NapalmCreeper) {
						addXp = 2500;
					} else if (targetEntity instanceof BioCreeper) {
						addXp = 1000;
					} else {
						addXp = 350;
					}
					demo.addXp((EntityPlayerMP) player, addXp);
				}
			}
		}
	}

	public static void verifyExplosives(ItemCraftedEvent event) {
		Item targetItem = event.crafting.getItem();
		Item dynamite = new ItemStack(ModBlocks.DYNAMITE).getItem();
		Item biobomb = new ItemStack(ModBlocks.BIOBOMB).getItem();
		Item napalm = new ItemStack(ModBlocks.NAPALM).getItem();
		if (targetItem != null && targetItem == dynamite) {
			if (!Skills.canCraft(event.player, Skills.DEMOLITION, 25)) {
				Skills.replaceWithComponents(event);
				event.player.world.createExplosion(null, event.player.posX, event.player.posY, event.player.posZ, 5.0F, true);
			} else if (!event.player.world.isRemote && event.player.hasCapability(Skills.DEMOLITION, EnumFacing.NORTH)) {
				SkillDemolition demolition = (SkillDemolition) event.player.getCapability(Skills.DEMOLITION, EnumFacing.NORTH);
				demolition.addXp((EntityPlayerMP) event.player, 500);
			}
		} else if (targetItem != null && targetItem == biobomb) {
			if (!Skills.canCraft(event.player, Skills.DEMOLITION, 50)) {
				Skills.replaceWithComponents(event);
				event.player.world.createExplosion(null, event.player.posX, event.player.posY, event.player.posZ, 6.0F, true);
			} else if (!event.player.world.isRemote && event.player.hasCapability(Skills.DEMOLITION, EnumFacing.NORTH)) {
				SkillDemolition demolition = (SkillDemolition) event.player.getCapability(Skills.DEMOLITION, EnumFacing.NORTH);
				demolition.addXp((EntityPlayerMP) event.player, 750);
			}
		} else if (targetItem != null && targetItem == napalm) {
			if (!Skills.canCraft(event.player, Skills.DEMOLITION, 75)) {
				Skills.replaceWithComponents(event);
				event.player.world.createExplosion(null, event.player.posX, event.player.posY, event.player.posZ, 12.0F, true);
			} else if (!event.player.world.isRemote && event.player.hasCapability(Skills.DEMOLITION, EnumFacing.NORTH)) {
				SkillDemolition demolition = (SkillDemolition) event.player.getCapability(Skills.DEMOLITION, EnumFacing.NORTH);
				demolition.addXp((EntityPlayerMP) event.player, 1000);
			}
		} else if (targetItem != null && targetItem == ModItems.ROCKET_LAUNCHER) {
			if (!Skills.canCraft(event.player, Skills.DEMOLITION, 100)) {
				Skills.replaceWithComponents(event);
			} else if (!event.player.world.isRemote && event.player.hasCapability(Skills.DEMOLITION, EnumFacing.NORTH)) {
				SkillDemolition demolition = (SkillDemolition) event.player.getCapability(Skills.DEMOLITION, EnumFacing.NORTH);
				demolition.addXp((EntityPlayerMP) event.player, 10000);
			}
		}
	}

	public static boolean isExplosive(ItemStack stack) {
		if (stack != null) {
			Item targetItem = stack.getItem();
			if (targetItem != null) {
				Item tnt = new ItemStack(Blocks.TNT).getItem();
				Item dynamite = new ItemStack(ModBlocks.DYNAMITE).getItem();
				Item biobomb = new ItemStack(ModBlocks.BIOBOMB).getItem();
				Item napalm = new ItemStack(ModBlocks.NAPALM).getItem();
				return (targetItem == tnt || targetItem == dynamite || targetItem == biobomb || targetItem == napalm);
			}
		}
		return false;
	}

}
