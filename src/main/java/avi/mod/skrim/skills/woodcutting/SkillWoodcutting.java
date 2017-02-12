package avi.mod.skrim.skills.woodcutting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import avi.mod.skrim.blocks.ModBlocks;
import avi.mod.skrim.blocks.plants.WeirwoodSapling;
import avi.mod.skrim.items.CustomAxe;
import avi.mod.skrim.items.HandSaw;
import avi.mod.skrim.items.ModItems;
import avi.mod.skrim.items.WeirwoodTotem;
import avi.mod.skrim.items.armor.LeafArmor;
import avi.mod.skrim.network.SkrimPacketHandler;
import avi.mod.skrim.network.skillpackets.WhirlingChopPacket;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillAbility;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.utils.Utils;
import avi.mod.skrim.world.PlayerPlacedBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockHalfWoodSlab;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockWoodSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

public class SkillWoodcutting extends Skill implements ISkillWoodcutting {

	public static SkillStorage<ISkillWoodcutting> skillStorage = new SkillStorage<ISkillWoodcutting>();
	/**
	 * maps wood meta values to plank meta values.
	 */
	public static Map<String, Integer> plankMap;
	static {
		plankMap = new HashMap<String, Integer>();
		plankMap.put("oak", 0);
		plankMap.put("spruce", 1);
		plankMap.put("birch", 2);
		plankMap.put("jungle", 3);
		plankMap.put("acacia", 4);
		plankMap.put("dark_oak", 5);
	}

	public static Map<String, Integer> xpMap;
	static {
		xpMap = new HashMap<String, Integer>();
		xpMap.put("oak", 100);
		xpMap.put("spruce", 200);
		xpMap.put("birch", 225);
		// The last 3 occur only in 1 biome.
		xpMap.put("jungle", 250);
		xpMap.put("acacia", 250);
		xpMap.put("dark_oak", 250);
	}

	public static List<String> validWoodcuttingBlocks = new ArrayList<String>(Arrays.asList(
			"oak_door",
			"spruce_door",
			"birch_door",
			"jungle_door",
			"dark_oak_door",
			"acacia_door",
			"wooden_trapdoor",
			"wooden_pressure_plate",
			"oak_wood_stairs",
			"spruce_wood_stairs",
			"birch_wood_stairs",
			"jungle_wood_stairs",
			"dark_oak_wood_stairs",
			"acacia_wood_stairs",
			"crafting_table",
			"sign"
	));

	public static SkillAbility HAND_SAW = new SkillAbility(
		"Hand Saw",
		25,
		"Wee Saw!",
		"Allows you to craft a hand saw!",
		"Hand saws instantly convert broken wood logs into 8 planks."
	);

	public static SkillAbility WHIRLING_CHOP = new SkillAbility(
		"Whirling Chop",
		50,
		"My roflchopter go soi soi soi soi soi.",
		"Right click with an axe to massacre trees in a 10 block radius."
	);

	public static SkillAbility LEAF_ARMOR = new SkillAbility(
		"Leaf Armor",
		75,
		"Tree!",
		"Grants you the ability to craft armor out of leaves."
	);

	public static SkillAbility WEIRWOOD = new SkillAbility(
		"Weirwood",
		100,
		"Not 'weirdwood'.",
		"Grants you the ability to craft weirwood saplings and totems."
	);

	public SkillWoodcutting() {
		this(1, 0);
	}

	public SkillWoodcutting(int level, int currentXp) {
		super("Woodcutting", level, currentXp);
		this.iconTexture = new ResourceLocation("skrim", "textures/guis/skills/woodcutting.png");
		this.addAbilities(HAND_SAW, WHIRLING_CHOP, LEAF_ARMOR, WEIRWOOD);
	}

	public int getXp(String blockName) {
		return (xpMap.containsKey(blockName)) ? xpMap.get(blockName) : 0;
	}

	public double getSpeedBonus() {
		return 0.15 * this.level;
	}

	public double getHewingChance() {
		return 0.005 * this.level;
	}

	@Override
	public List<String> getToolTip() {
		List<String> tooltip = new ArrayList<String>();
		tooltip.add("§a+" + Utils.oneDigit.format(this.getSpeedBonus()) + "§r woodcutting speed bonus.");
		tooltip.add("§a" + Utils.formatPercent(this.getHewingChance()) + "%§r chance to level a tree.");
		return tooltip;
	}

	public boolean validSpeedTarget(IBlockState state) {
		Block block = state.getBlock();
		String harvestTool = block.getHarvestTool(state);
		return ((harvestTool != null && harvestTool.toLowerCase().equals("axe"))
			|| validWoodcuttingBlocks.contains(Utils.getBlockName(block))
			|| block instanceof BlockFence
			|| block instanceof BlockFenceGate
			|| block instanceof BlockWoodSlab
			|| block instanceof BlockHalfWoodSlab
			) ? true : false;
	}

	// Assuming its a wood block
	public static String getWoodName(IBlockState state) {
		Block block = state.getBlock();
		if (block instanceof BlockOldLog) {
			return Utils.snakeCase(state.getValue(BlockOldLog.VARIANT).toString());
		} else if (block instanceof BlockNewLog) {
			return Utils.snakeCase(state.getValue(BlockNewLog.VARIANT).toString());
		} else {
			return null;
		}
	}

	public int hewTree(World world, SkillWoodcutting woodcutting, BlockPos pos, BlockPos start, EntityPlayer player, ItemStack axe, boolean withSaw, int damagePerBreak) {
		int addXp = 0;
		IBlockState state = world.getBlockState(pos);
		Block tree = state.getBlock();
		if (PlayerPlacedBlocks.isNaturalBlock(world, pos)) {
			addXp += this.getXp(getWoodName(state));
		}
		if (withSaw) {
			System.out.println("getWoodName: " + getWoodName(state));
			System.out.println("plankMap: " + plankMap.get(getWoodName(state)));
			world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Blocks.PLANKS, 8, plankMap.get(getWoodName(state)))));
			world.destroyBlock(pos, false);
		} else {
			world.destroyBlock(pos, true);
		}
		axe.damageItem(damagePerBreak, player);
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					BlockPos targetPos = pos.add(i, j, k);
					if (validTarget(targetPos, start)) {
						IBlockState targetState = world.getBlockState(targetPos);
						Block targetBlock = targetState.getBlock();
						if (targetBlock instanceof BlockOldLog || targetBlock instanceof BlockNewLog) {
							addXp += this.hewTree(world, woodcutting, targetPos, start, player, axe, withSaw, damagePerBreak);
						}
					}
				}
			}
		}
		return addXp;
	}

	public static boolean validTarget(BlockPos targetPos, BlockPos start) {
		int radius = 2;
		return (Math.abs(targetPos.getX() - start.getX()) <= radius
				&& Math.abs(targetPos.getZ() - start.getZ()) <= radius) ?
						true: false;
	}

	public static void addWoodcuttingXp(BlockEvent.BreakEvent event) {
		IBlockState state = event.getState();
		Block target = state.getBlock();
		if (target instanceof BlockOldLog || target instanceof BlockNewLog) {
			EntityPlayer player = event.getPlayer();
			if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.WOODCUTTING, EnumFacing.NORTH)) {
				SkillWoodcutting woodcutting = (SkillWoodcutting) player.getCapability(Skills.WOODCUTTING, EnumFacing.NORTH);
				ItemStack stack = player.getHeldItemMainhand();
				Item item = (stack == null) ? null : stack.getItem();
				int addXp = woodcutting.getXp(getWoodName(state));
				if (Math.random() < woodcutting.getHewingChance() && item != null && (item instanceof ItemAxe || item instanceof CustomAxe)) {
					BlockPos start = event.getPos();
					addXp += woodcutting.hewTree(event.getWorld(), woodcutting, start, start, player, stack, (item instanceof HandSaw), 1);
				}
				woodcutting.addXp((EntityPlayerMP) player, addXp);
			}
		}
	}

	public static void chopFaster(PlayerEvent.BreakSpeed event) {
  	EntityPlayer player = event.getEntityPlayer();
		if (player.hasCapability(Skills.WOODCUTTING, EnumFacing.NORTH)) {
			SkillWoodcutting woodcutting = (SkillWoodcutting) player.getCapability(Skills.WOODCUTTING, EnumFacing.NORTH);
			IBlockState state = event.getState();
			if (woodcutting.validSpeedTarget(state)) {
				event.setNewSpeed((float) (event.getOriginalSpeed() + woodcutting.getSpeedBonus()));
			}
		}
	}

	public static void verifyItems(ItemCraftedEvent event) {
		Item targetItem = event.crafting.getItem();
		Item weirwoodSapling = new ItemStack(ModBlocks.WEIRWOOD_SAPLING).getItem();
		if (targetItem != null && targetItem == ModItems.HAND_SAW) {
			if (!Skills.canCraft(event.player, Skills.WOODCUTTING, 25)) {
				Skills.replaceWithComponents(event);
			} else if (!event.player.world.isRemote && event.player.hasCapability(Skills.WOODCUTTING, EnumFacing.NORTH)) {
				SkillWoodcutting woodcutting = (SkillWoodcutting) event.player.getCapability(Skills.WOODCUTTING, EnumFacing.NORTH);
				woodcutting.addXp((EntityPlayerMP) event.player, 500);
			}
		} else if (targetItem != null && targetItem instanceof LeafArmor) {
			if (!Skills.canCraft(event.player, Skills.WOODCUTTING, 75)) {
				Skills.replaceWithComponents(event);
			} else if (!event.player.world.isRemote && event.player.hasCapability(Skills.WOODCUTTING, EnumFacing.NORTH)) {
				SkillWoodcutting woodcutting = (SkillWoodcutting) event.player.getCapability(Skills.WOODCUTTING, EnumFacing.NORTH);
				woodcutting.addXp((EntityPlayerMP) event.player, 1000);
			}
		} else if (targetItem != null && (targetItem instanceof WeirwoodTotem || targetItem == weirwoodSapling)) {
			if (!Skills.canCraft(event.player, Skills.WOODCUTTING, 100)) {
				Skills.replaceWithComponents(event);
			} else if (!event.player.world.isRemote && event.player.hasCapability(Skills.WOODCUTTING, EnumFacing.NORTH)) {
				SkillWoodcutting woodcutting = (SkillWoodcutting) event.player.getCapability(Skills.WOODCUTTING, EnumFacing.NORTH);
				woodcutting.addXp((EntityPlayerMP) event.player, 10000);
			}
		}
	}

	public static void sawTree(BlockEvent.HarvestDropsEvent event) {
		EntityPlayer player = event.getHarvester();
		if (player != null) {
			ItemStack mainStack = player.getHeldItemMainhand();
			if (mainStack != null) {
				Item mainItem = mainStack.getItem();
				if (mainItem instanceof HandSaw) {
					IBlockState state = event.getState();
					Block block = state.getBlock();
					if (block instanceof BlockOldLog || block instanceof BlockNewLog) {
						event.getDrops().clear();
						event.getDrops().add(new ItemStack(Blocks.PLANKS, 8, plankMap.get(getWoodName(state))));
					}
				}
			}
		}
	}

	public static void whirlingChop(PlayerInteractEvent.RightClickItem event) {
		EntityPlayer player = event.getEntityPlayer();
		if (player != null && player.hasCapability(Skills.MELEE, EnumFacing.NORTH)) {
			if (player.world.isRemote) {
				SkillWoodcutting woodcutting = (SkillWoodcutting) player.getCapability(Skills.WOODCUTTING, EnumFacing.NORTH);
				if (woodcutting.hasAbility(2)) {
					ItemStack mainStack = player.getHeldItemMainhand();
					Item mainItem = (mainStack == null) ? null : mainStack.getItem();
					ItemStack offStack = player.getHeldItemOffhand();
					Item offItem = (offStack == null) ? null : offStack.getItem();
					if (mainItem != null && (mainItem instanceof ItemAxe || mainItem instanceof CustomAxe)) {
						player.swingArm(EnumHand.MAIN_HAND);
						if (player.world.isRemote) {
							// Send packet here
							BlockPos playerPos = new BlockPos(player.posX, player.posY, player.posZ);
							for (int i = -10; i <= 10; i++) {
								for (int j = 0; j <= 2; j++) {
									for (int k = -10; k <= 10; k++) {
										BlockPos targetPos = new BlockPos(playerPos.getX() + i, playerPos.getY() + j, playerPos.getZ() + k);
										IBlockState targetState = player.world.getBlockState(targetPos);
										Block targetBlock = targetState.getBlock();
										if (targetBlock instanceof BlockOldLog || targetBlock instanceof BlockNewLog) {
											SkrimPacketHandler.INSTANCE.sendToServer(
												new WhirlingChopPacket(targetPos.getX(), targetPos.getY(), targetPos.getZ())
											);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

}
