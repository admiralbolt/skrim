package avi.mod.skrim.skills.mining;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import avi.mod.skrim.network.FallDistancePacket;
import avi.mod.skrim.network.SkrimPacketHandler;
import avi.mod.skrim.network.skillpackets.DrillPacket;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillAbility;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockNetherBrick;
import net.minecraft.block.BlockNetherrack;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockRedSandstone;
import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.block.BlockStoneSlabNew;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;

public class SkillMining extends Skill implements ISkillMining {

	public static SkillStorage<ISkillMining> skillStorage = new SkillStorage<ISkillMining>();
	public static Map<String, Integer> XP_MAP;

	public static int NIGHT_VISION_DURATION = 300;

	static {
		XP_MAP = new HashMap<String, Integer>();
		XP_MAP.put("stone", 50);
		XP_MAP.put("netherrack", 60); // Extra bonus for being in the nether
		XP_MAP.put("granite", 75);
		XP_MAP.put("andesite", 75);
		XP_MAP.put("diorite", 75);
		XP_MAP.put("coal_ore", 125);
		XP_MAP.put("iron_ore", 250);
		XP_MAP.put("quartz_ore", 300); // Extra bonus for being in the nether ~same rarity as iron
		XP_MAP.put("redstone_ore", 500);
		XP_MAP.put("obsidian", 600); // Common but takes a while to mine
		XP_MAP.put("gold_ore", 1000);
		XP_MAP.put("lapis_lazuli_ore", 1500); // Lapis_lazuil not just lapis, also barely rarer than diamond
		XP_MAP.put("diamond_ore", 2000);
		XP_MAP.put("emerald_ore", 3500); // Nice xp bonus for an otherwise useless ore
	}

	public static List<String> VALID_MINING_BLOCKS = new ArrayList<String>(Arrays.asList("cobblestone_stairs", "stone_brick_stairs", "quartz_stairs",
			"nether_brick_stairs", "brick_stairs", "sandstone_stairs", "red_sandstone_stairs", "purpur_block", "purpur_pillar", "iron_door"));

	public static List<String> VALID_FORTUNE_ORES = new ArrayList<String>(
			Arrays.asList("coal_ore", "iron_ore", "gold_ore", "lapis_lazuli_ore", "diamond_ore", "emerald_ore", "redstone_ore", "quartz_ore"));

	public static SkillAbility DARKVISION = new SkillAbility("mining", "Darkvision", 25, "I was born in the darkness.",
			"While close to the bottom of the world you have a constant night vision effect.");

	public static SkillAbility LAVA_SWIMMER = new SkillAbility("mining", "Lava Swimmer", 50, "Reducing the number of 'oh shit' moments.",
			"While close to the bottom of the world you take §a50%" + SkillAbility.descColor + " damage from lava, and don't get set on fire by it.");

	public static SkillAbility SPELUNKER = new SkillAbility("mining", "Spelunker", 75, "Spelunkey?  More like Spedunkey.  AHAHAHAHA.",
			"Allows you to climb walls while holding jump.");

	public static SkillAbility DRILL = new SkillAbility("mining", "Drill", 100, "Without the risk of earthquakes!",
			"Right clicking with a pickaxe instantly mines to bedrock.");

	public SkillMining() {
		this(1, 0);
	}

	public SkillMining(int level, int currentXp) {
		super("Mining", level, currentXp);
		this.addAbilities(DARKVISION, LAVA_SWIMMER, SPELUNKER, DRILL);
	}

	public int getXp(String blockName) {
		return (XP_MAP.containsKey(blockName)) ? XP_MAP.get(blockName) : 0;
	}

	public double getSpeedBonus() {
		return 0.01 * this.level;
	}

	public double getFortuneChance() {
		return 0.005 * this.level;
	}

	public int getFortuneAmount() {
		return 2 + (int) (this.level / 50);
	}

	@Override
	public List<String> getToolTip() {
		List<String> tooltip = new ArrayList<String>();
		tooltip.add("§a+" + Utils.formatPercent(this.getSpeedBonus()) + "%§r mining speed bonus.");
		tooltip.add(
				"§a" + Utils.formatPercent(this.getFortuneChance()) + "%§r chance to §a" + Utils.getFortuneString(this.getFortuneAmount()) + "§r ore drops.");
		tooltip.add("   This bonus stacks with fortune.");
		return tooltip;
	}

	public boolean validSpeedTarget(IBlockState state) {
		Block block = state.getBlock();
		String harvestTool = block.getHarvestTool(state);
		return ((harvestTool != null && harvestTool.toLowerCase().equals("pickaxe")) || VALID_MINING_BLOCKS.contains(Utils.getBlockName(block))
				|| block instanceof BlockOre || block instanceof BlockRedstoneOre || block instanceof BlockStone || block instanceof BlockStoneSlab
				|| block instanceof BlockStoneSlabNew || block instanceof BlockObsidian || block instanceof BlockStoneBrick || block instanceof BlockNetherBrick
				|| block instanceof BlockNetherrack || block instanceof BlockSandStone || block instanceof BlockRedSandstone) ? true : false;
	}

	public boolean validFortuneTarget(IBlockState state) {
		Block block = state.getBlock();
		String blockName = Utils.snakeCase(block.getLocalizedName());
		return ((block instanceof BlockOre || block instanceof BlockRedstoneOre) && VALID_FORTUNE_ORES.contains(blockName));
	}

	public static void addMiningXp(BlockEvent.BreakEvent event) {
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
				mining.addXp((EntityPlayerMP) player, addXp);
			}
		}
	}

	public static void mineFaster(PlayerEvent.BreakSpeed event) {
		EntityPlayer player = event.getEntityPlayer();
		if (player.hasCapability(Skills.MINING, EnumFacing.NORTH)) {
			SkillMining mining = (SkillMining) player.getCapability(Skills.MINING, EnumFacing.NORTH);
			IBlockState state = event.getState();
			if (mining.validSpeedTarget(state)) {
				event.setNewSpeed((float) (event.getOriginalSpeed() * (1 + mining.getSpeedBonus())));
			}
		}
	}

	public static void giveMoreOre(BlockEvent.HarvestDropsEvent event) {
		EntityPlayer player = event.getHarvester();
		if (player != null && player.hasCapability(Skills.MINING, EnumFacing.NORTH)) {
			SkillMining mining = (SkillMining) player.getCapability(Skills.MINING, EnumFacing.NORTH);
			IBlockState state = event.getState();
			if (mining.validFortuneTarget(state)) {
				double random = Utils.rand.nextDouble();
				if (random < mining.getFortuneChance()) {
					List<ItemStack> drops = event.getDrops();
					ItemStack copyDrop = drops.get(0);
					// Let's not loop infinitely, that seems like a good idea.
					int dropSize = drops.size();
					for (int i = 0; i < (dropSize * (mining.getFortuneAmount() - 1)); i++) {
						drops.add(copyDrop.copy());
					}
					Skills.playFortuneSound(player);
					mining.addXp((EntityPlayerMP) player, 200);
				}
			}
		}
	}

	public static void reduceLava(LivingHurtEvent event) {
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
							new Timer().schedule(new TimerTask() {
								@Override
								public void run() {
									player.extinguish();
								}
							}, 400);
						}
					}
				}
			}
		}
	}

	public static void climbWall(LivingUpdateEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof EntityPlayer) {
			final EntityPlayer player = (EntityPlayer) entity;
			if (player != null && player.hasCapability(Skills.MINING, EnumFacing.NORTH)) {
				Skill mining = (Skill) player.getCapability(Skills.MINING, EnumFacing.NORTH);
				if (mining.hasAbility(1)) {
					BlockPos pos = player.getPosition();
					if (pos.getY() <= 40) {
						if (player.world.getTotalWorldTime() % 80L == 0L) {
							PotionEffect effect = new PotionEffect(MobEffects.NIGHT_VISION, NIGHT_VISION_DURATION, 0, true, false);
							Utils.addOrCombineEffect(player, effect);
						}
					}
				}
				if (mining.hasAbility(3)) {
					// Since we're using a packet only need to fire on client side.
					if (player.world.isRemote) {
						if (player.collidedHorizontally) {
							KeyBinding jumpKey = Minecraft.getMinecraft().gameSettings.keyBindJump;
							if (jumpKey.isKeyDown()) {
								player.motionY = Math.min(0.2, player.motionY + 0.1);
								if (player.motionY > 0) {
									player.fallDistance = 0.0F;
								} else {
									player.fallDistance -= 1F;
								}
								SkrimPacketHandler.INSTANCE.sendToServer(new FallDistancePacket(player.fallDistance));
							}
						}
					}
				}
			}
		}
	}

	public static void drill(PlayerInteractEvent.RightClickBlock event) {
		EntityPlayer player = event.getEntityPlayer();
		if (player != null && player.hasCapability(Skills.MINING, EnumFacing.NORTH)) {
			SkillMining mining = (SkillMining) player.getCapability(Skills.MINING, EnumFacing.NORTH);
			if (mining.hasAbility(4)) {
				ItemStack mainStack = event.getItemStack();
				if (mainStack != null) {
					Item mainItem = mainStack.getItem();
					if (mainItem != null && mainItem instanceof ItemPickaxe) {
						if (player.world.isRemote) {
							RayTraceResult result = player.rayTrace(5.0D, 1.0F);
							if (result != null) {
								if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
									player.swingArm(EnumHand.MAIN_HAND);
									ItemPickaxe pic = (ItemPickaxe) mainItem;
									BlockPos targetPos = result.getBlockPos();
									SkrimPacketHandler.INSTANCE.sendToServer(new DrillPacket(targetPos.getX(), targetPos.getY(), targetPos.getZ()));
								}
							}
						}
					}
				}
			}
		}
	}

}
