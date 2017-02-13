package avi.mod.skrim.skills.digging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import avi.mod.skrim.items.CustomSpade;
import avi.mod.skrim.network.SkrimPacketHandler;
import avi.mod.skrim.network.skillpackets.MetalDetectorPacket;
import avi.mod.skrim.skills.RandomTreasure;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillAbility;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.utils.Obfuscation;
import avi.mod.skrim.utils.Utils;
import avi.mod.skrim.world.PlayerPlacedBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockGravel;
import net.minecraft.block.BlockMycelium;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;

public class SkillDigging extends Skill implements ISkillDigging {

	public static SkillStorage<ISkillDigging> skillStorage = new SkillStorage<ISkillDigging>();
	public static Map<String, Integer> xpMap;
	static {
		xpMap = new HashMap<String, Integer>();
		xpMap.put("dirt", 50);
		xpMap.put("farmland", 50);
		xpMap.put("snow", 50);
		xpMap.put("sand", 75);
		xpMap.put("grass_block", 75); // Bonus for grass!
		xpMap.put("gravel", 125); // Fuck gravel
		xpMap.put("clay", 150);
		xpMap.put("soul_sand", 250); // Only in nether & not to common
		xpMap.put("podzol", 300); // Only in taiga
		xpMap.put("red_sand", 425); // Only in mesa
		xpMap.put("mycelium", 550); // Only in.. mushroom biomes?
	}

	private static int REQUIRED_SAND = 640;
	private static double METER_FILLED = 100;

	public double metalMeter = 0;
	public Vec3d lastPos = null;

	public static SkillAbility VITALIC_BREATHING = new SkillAbility("digging", "Vitalic Breathing", 25, "Breathe, breathe in the... dirt?",
			"No longer take suffocation damage from being trapped in walls.");
	public static SkillAbility METAL_DETECTOR = new SkillAbility("digging", "Metal Detector", 50, "Beep....Beep....Beep....",
			"Moving over dirt blocks causes random metal objects to appear!");
	public static SkillAbility ENTOMB = new SkillAbility("digging", "Entomb", 75, "Fuck Priest.",
			"Right clicking an entity with a shovel buries it in the earth.");
	public static SkillAbility CASTLE = new SkillAbility("digging", "Castles Made of Sand", 100, "Slips into the sea.  Eventually.",
			"Right cliking with a shovel creates a desert temple.");

	public SkillDigging() {
		this(1, 0);
	}

	public SkillDigging(int level, int currentXp) {
		super("Digging", level, currentXp);
		this.addAbilities(VITALIC_BREATHING, METAL_DETECTOR, ENTOMB, CASTLE);
	}

	public int getXp(String blockName) {
		return (xpMap.containsKey(blockName)) ? xpMap.get(blockName) : 0;
	}

	public double getSpeedBonus() {
		return 0.1 * this.level;
	}

	public double getTreasureChance() {
		return 0.002 * this.level;
	}

	@Override
	public List<String> getToolTip() {
		List<String> tooltip = new ArrayList<String>();
		tooltip.add("§a+" + Utils.oneDigit.format(this.getSpeedBonus()) + "§r digging speed bonus.");
		tooltip.add("§a" + Utils.formatPercent(this.getTreasureChance()) + "%§r chance to find treasure.");
		return tooltip;
	}

	public boolean validSpeedTarget(IBlockState state) {
		Block block = state.getBlock();
		String harvestTool = block.getHarvestTool(state);
		return ((harvestTool != null && harvestTool.toLowerCase().equals("shovel")) || validTreasureTarget(state)) ? true : false;
	}

	public static boolean validTreasureTarget(IBlockState state) {
		Block block = state.getBlock();
		return (block instanceof BlockDirt || block instanceof BlockGrass || block instanceof BlockSand || block instanceof BlockGravel
				|| block instanceof BlockMycelium || block instanceof BlockSoulSand);
	}

	public static String getDirtName(IBlockState state) {
		Block block = state.getBlock();
		if (block instanceof BlockDirt) {
			return state.getValue(BlockDirt.VARIANT).toString();
		} else if (block instanceof BlockSand) {
			return state.getValue(BlockSand.VARIANT).toString();
		} else {
			return Utils.getBlockName(block);
		}
	}

	public static void addDiggingXp(BlockEvent.BreakEvent event) {
		EntityPlayer player = event.getPlayer();
		if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.DIGGING, EnumFacing.NORTH)) {
			SkillDigging digging = (SkillDigging) player.getCapability(Skills.DIGGING, EnumFacing.NORTH);
			IBlockState state = event.getState();
			Block target = state.getBlock();
			int addXp = digging.getXp(getDirtName(state));
			if (addXp > 0) {
				digging.addXp((EntityPlayerMP) player, addXp);
			}
		}
	}

	public static void digFaster(PlayerEvent.BreakSpeed event) {
		EntityPlayer player = event.getEntityPlayer();
		SkillDigging digging = (SkillDigging) player.getCapability(Skills.DIGGING, EnumFacing.NORTH);
		if (player.hasCapability(Skills.DIGGING, EnumFacing.NORTH)) {
			IBlockState state = event.getState();
			if (digging.validSpeedTarget(state)) {
				event.setNewSpeed((float) (event.getOriginalSpeed() + digging.getSpeedBonus()));
			}
		}
	}

	public static void findTreasure(BlockEvent.HarvestDropsEvent event) {
		EntityPlayer player = event.getHarvester();
		if (player != null) {
			if (PlayerPlacedBlocks.isNaturalBlock(event.getWorld(), event.getPos())) {
				ItemStack mainStack = player.getHeldItemMainhand();
				if (mainStack != null) {
					Item mainItem = mainStack.getItem();
					if (mainItem != null && (mainItem instanceof ItemSpade || mainItem instanceof CustomSpade)) {
						if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.DIGGING, EnumFacing.NORTH)) {
							SkillDigging digging = (SkillDigging) player.getCapability(Skills.DIGGING, EnumFacing.NORTH);
							IBlockState state = event.getState();
							if (validTreasureTarget(state)) {
								double random = Math.random();
								if (random < digging.getTreasureChance()) {
									ItemStack treasure = RandomTreasure.generateStandardTreasure();
									List<ItemStack> drops = event.getDrops();
									drops.add(treasure);
									Skills.playFortuneSound(player);
									digging.addXp((EntityPlayerMP) player, 200);
								}
							}
						}
					}
				}
			}
		}
	}

	public static void vitalicBreathing(LivingHurtEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof EntityPlayer) {
			final EntityPlayer player = (EntityPlayer) entity;
			if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.DIGGING, EnumFacing.NORTH)) {
				SkillDigging digging = (SkillDigging) player.getCapability(Skills.DIGGING, EnumFacing.NORTH);
				if (digging.hasAbility(1)) {
					if (event.getSource() == DamageSource.IN_WALL) {
						// event.setAmount(0F);
						event.setCanceled(true);
					}
				}
			}
		}
	}

	public static void metalDetector(LivingUpdateEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof EntityPlayer) {
			final EntityPlayer player = (EntityPlayer) entity;
			if (player.world.isRemote) {
				if (player != null && player.hasCapability(Skills.DIGGING, EnumFacing.NORTH)) {
					SkillDigging digging = (SkillDigging) player.getCapability(Skills.DIGGING, EnumFacing.NORTH);
					if (digging.hasAbility(2)) {
						BlockPos playerLocation = new BlockPos(player.posX, player.posY, player.posZ);
						IBlockState onState = player.world.getBlockState(playerLocation.add(0, -1, 0));
						if (validTreasureTarget(onState)) {
							digging.metalMeter += Math.sqrt(player.motionX * player.motionX + player.motionZ * player.motionZ);
							if (digging.metalMeter >= SkillDigging.METER_FILLED) {
								digging.metalMeter = 0;
								SkrimPacketHandler.INSTANCE.sendToServer(new MetalDetectorPacket(player.posX, player.posY, player.posZ));
							}
						}
					}
				}
			}
		}
	}

	public static void entomb(PlayerInteractEvent.EntityInteract event) {
		EntityPlayer player = event.getEntityPlayer();
		Entity targetEntity = event.getTarget();
		if (!(targetEntity instanceof EntityGhast || targetEntity instanceof EntityBlaze || targetEntity instanceof EntityDragon)) {
			if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.DIGGING, EnumFacing.NORTH)) {
				SkillDigging digging = (SkillDigging) player.getCapability(Skills.DIGGING, EnumFacing.NORTH);
				if (digging.hasAbility(3)) {
					ItemStack mainStack = player.getHeldItemMainhand();
					Item mainItem = mainStack.getItem();
					if (mainItem instanceof ItemSpade || mainItem instanceof CustomSpade) {
						targetEntity.setPosition(targetEntity.posX, Math.max(targetEntity.posY - 5, 5), targetEntity.posZ);
						mainStack.damageItem(10, player);
					}
				}
			}
		}
	}

	public static void castles(PlayerInteractEvent.RightClickBlock event) {
		EntityPlayer player = event.getEntityPlayer();
		if (player != null && player.hasCapability(Skills.DIGGING, EnumFacing.NORTH)) {
			SkillDigging digging = (SkillDigging) player.getCapability(Skills.DIGGING, EnumFacing.NORTH);
			if (digging.hasAbility(4)) {
				Utils.logSkillEvent(event, digging, "player.world.isRemote: " + player.world.isRemote);
				if (!player.world.isRemote) {
					BlockPos pos = event.getPos();
					IBlockState state = player.world.getBlockState(pos);
					Biome biome = player.world.getBiomeForCoordsBody(pos);
					Utils.logSkillEvent(event, digging, "Biome: " + biome);
					if (biome == Biomes.DESERT || biome == Biomes.DESERT_HILLS) {
						if (state.getBlock() == Blocks.SAND) {
							ItemStack heldStack = player.getHeldItemMainhand();
							if (heldStack != null) {
								if (heldStack.getItem() == Items.DIAMOND_SHOVEL) {
									int totalSand = 0;
									for (ItemStack stack : player.inventory.mainInventory) {
										if (stack != null) {
											if (stack.getItem() == Item.getItemFromBlock(Blocks.SAND)) {
												totalSand += stack.getCount();
											}
										}
									}
									Utils.logSkillEvent(event, digging, "TotalSand: " + totalSand + ", REQUIRED_SAND: " + REQUIRED_SAND);
									if (totalSand >= REQUIRED_SAND) {
										// All temples are generated by MapGenScatteredFeature
										MapGenScatteredFeature templeGen = new MapGenScatteredFeature();
										ChunkPos chunkPos = new ChunkPos(event.getPos());
										StructureStart start = new MapGenScatteredFeature.Start(player.world, Utils.rand, pos.getX() >> 4, pos.getZ() >> 4);
										int x = (chunkPos.chunkXPos << 4) + 8;
										int y = event.getPos().getY();
										int z = (chunkPos.chunkZPos << 4) + 8;
										StructureBoundingBox bound = new StructureBoundingBox(x - 8, y - 15, z - 8, x + 8, y + 12, z + 8);
										new StructureBoundingBox();
										start.generateStructure(player.world, Utils.rand, bound);
										start.notifyPostProcessAt(chunkPos);
										if (!player.capabilities.isCreativeMode) {
											int paidSand = 0;
											for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
												ItemStack stack = player.inventory.getStackInSlot(i);
												if (stack != null) {
													if (stack.getItem() == Item.getItemFromBlock(Blocks.SAND)) {
														int remove = Math.min(Obfuscation.getStackSize(stack), (REQUIRED_SAND - paidSand));
														if (remove == Obfuscation.getStackSize(stack)) {
															player.inventory.removeStackFromSlot(i);
														} else {
															player.inventory.decrStackSize(i, remove);
														}
														paidSand += remove;
													}
												}
											}
											ItemStack diamondShovel = player.getHeldItemMainhand();
											diamondShovel.damageItem(diamondShovel.getMaxDamage(), player);
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
