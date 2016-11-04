package avi.mod.skrim.skills.farming;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import avi.mod.skrim.items.CustomHoe;
import avi.mod.skrim.items.ModItems;
import avi.mod.skrim.network.SkrimPacketHandler;
import avi.mod.skrim.network.skillpackets.ApplyBonemealPacket;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillAbility;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.digging.SkillDigging;
import avi.mod.skrim.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockBeetroot;
import net.minecraft.block.BlockCarrot;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockMelon;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPotato;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.BlockStem;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

public class SkillFarming extends Skill implements ISkillFarming {

	public static SkillStorage<ISkillFarming> skillStorage = new SkillStorage<ISkillFarming>();
	public static Map<String, Integer> xpMap;
	public static List<Block> cropBlocks = new ArrayList<Block>();

	static {
		xpMap = new HashMap<String, Integer>();
		xpMap.put("crops", 200);
		xpMap.put("beetroots", 250);
		xpMap.put("cocoa", 300);
		xpMap.put("potatoes", 400);
		xpMap.put("carrots", 400);
		xpMap.put("pumpkin", 500);
		xpMap.put("melon", 500);
		xpMap.put("nether_wart", 600);

		cropBlocks.add(Blocks.WHEAT);
		cropBlocks.add(Blocks.CARROTS);
		cropBlocks.add(Blocks.POTATOES);
		cropBlocks.add(Blocks.BEETROOTS);
	}

	public static int tanDuration = 160;
	public static long tanCheck = 40L;
	public int ticks = 0;

	public static SkillAbility overalls = new SkillAbility(
		"Overalls",
		25,
		"Overall, this ability seems pretty good! AHAHAHA Get it?  (Please help me I need sleep.)",
		"Grants you the ability to craft overalls.",
		"While worn, right clicking with a hoe acts like applying bonemeal."
	);

	public static SkillAbility sideChick = new SkillAbility(
		"Side Chick",
		50,
		"This IS my other hoe.",
		"Killing an entity while holding a hoe automatically plants a random plant."
	);

	public static SkillAbility farmersTan = new SkillAbility(
		"Farmer's Tan",
		75,
		"You're a plant Vash.",
		"Being in sunlight grants you a speed boost and saturation."
	);

	public SkillFarming() {
		this(1, 0);
	}

	public SkillFarming(int level, int currentXp) {
		super("Farming", level, currentXp);
		this.iconTexture = new ResourceLocation("skrim", "textures/guis/skills/farming.png");
		this.addAbilities(overalls, sideChick, farmersTan);
	}

	public int getXp(String blockName) {
		return (xpMap.containsKey(blockName)) ? xpMap.get(blockName) : 0;
	}

	public double getFortuneChance() {
		return 0.01 * this.level;
	}

	public int getFortuneAmount() {
		return 2 + (int) (this.level / 25);
	}

	@Override
	public List<String> getToolTip() {
		List<String> tooltip = new ArrayList<String>();
		tooltip.add("§a" + Utils.formatPercent(this.getFortuneChance()) + "%§r chance to §a" + Utils.getFortuneString(this.getFortuneAmount()) + "§r harvest drops.");
		tooltip.add("   This bonus stacks with fortune.");
		if (this.getGrowthStage() > 1) {
			tooltip.add("Plants start in stage §a" + this.getGrowthStage() + "§r of growth.");
		}
		return tooltip;
	}

	public static boolean validCrop(IBlockState state) {
		Block block = state.getBlock();
		return (block instanceof BlockStem
				|| block instanceof BlockCarrot
				|| block instanceof BlockPotato
				|| block instanceof BlockCrops
				|| block instanceof BlockCocoa
				|| block instanceof BlockNetherWart
				);
	}

	/**
	 * Need to cap this shit @ 6 to avoid super OPNESS
	 * Still pretty OPOP
	 */
	public int getGrowthStage() {
		int growthStage = (int) Math.floor((double) this.level / 10) + 1;
		return (growthStage > 6) ? 6 : growthStage;
	}

	public boolean validFortuneTarget(IBlockState state) {
		Block block = state.getBlock();
		/**
		 * They decided to make every plants growth go from 0-7 EXCEPT for beets
		 * for some reason they go from 0-3.  I guess you could say it...
		 * Beets me!  AHAHAHAHAHAHAHAHA! (Can't wake up)
		 *
		 * UPDATE: Okay seriously?  Cocoa beans have 3 stages, but instead of
		 * using 0-1-2 like the established standard, they use 2/6/10?
		 * WTF?
		 *
		 * UPDATE: Netherwart has FOUR growth stages.  The legend continues.
		 */
		return (block instanceof BlockMelon
				|| ((block instanceof BlockCarrot || block instanceof BlockPotato)
						&& block.getMetaFromState(state) == 7)
				|| (block instanceof BlockCrops && block.getMetaFromState(state) == 7)
				|| (block instanceof BlockBeetroot && block.getMetaFromState(state) == 3)
				|| (block instanceof BlockCocoa && block.getMetaFromState(state) == 10)
				|| (block instanceof BlockNetherWart && block.getMetaFromState(state) == 4)
				) ? true : false;
	}

	public static void addFarmingXp(BlockEvent.BreakEvent event) {
		EntityPlayer player = event.getPlayer();
		if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.FARMING, EnumFacing.NORTH)) {
			SkillFarming farming = (SkillFarming) player.getCapability(Skills.FARMING, EnumFacing.NORTH);
			IBlockState state = event.getState();
			Block target = state.getBlock();
			// Don't want to always give xp, only for fully grown stuff.
			if (farming.validFortuneTarget(state) || target instanceof BlockPumpkin) {
				int addXp = farming.getXp(Utils.getBlockName(target));
				farming.addXp((EntityPlayerMP) player, addXp);
			}
		}
	}

	public static void giveMoreCrops(BlockEvent.HarvestDropsEvent event) {
		EntityPlayer player = event.getHarvester();
		if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.FARMING, EnumFacing.NORTH)) {
			SkillFarming farming = (SkillFarming) player.getCapability(Skills.FARMING, EnumFacing.NORTH);
			IBlockState state = event.getState();
			if (farming.validFortuneTarget(state)) {
				Block block = state.getBlock();
				double random = Math.random();
				if (random < farming.getFortuneChance()) {
					List<ItemStack> drops = event.getDrops();
					// Let's not loop infinitely, that seems like a good idea.
					int dropSize = drops.size();
          for (int j = 0; j < farming.getFortuneAmount() - 1; j++) {
            for (int i = 0; i < dropSize; i++) {
              drops.add(drops.get(i).copy());
            }
          }
					Skills.playFortuneSound(player);
          farming.addXp((EntityPlayerMP) player, 200);
				}
			}
		}
	}

  public static void applyGrowth(BlockEvent.PlaceEvent event) {
  	EntityPlayer player = event.getPlayer();
		if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.FARMING, EnumFacing.NORTH)) {
			SkillFarming farming = (SkillFarming) player.getCapability(Skills.FARMING, EnumFacing.NORTH);
	  	IBlockState placedState = event.getPlacedBlock();
	  	IBlockState targetState = event.getPlacedAgainst();
	  	Block placedBlock = placedState.getBlock();
	  	Block targetBlock = targetState.getBlock();
	  	if (validCrop(placedState) && (targetBlock instanceof BlockFarmland || targetBlock instanceof BlockOldLog)) {
	  		World world = event.getWorld();
	  		world.setBlockState(event.getPos(), farming.cropWithGrowth(placedState));
	  	}
		}
  }

  public IBlockState cropWithGrowth(IBlockState placedState) {
  	Block placedBlock = placedState.getBlock();
  	PropertyInteger prop = null;
		int growthStage = this.getGrowthStage();
		if (placedBlock instanceof BlockStem) {
			prop = BlockStem.AGE;
		} else if (placedBlock instanceof BlockBeetroot) {
			prop = BlockBeetroot.BEETROOT_AGE;
			if (growthStage > 2) {
				growthStage = 2;
			}
		} else if (placedBlock instanceof BlockCocoa) {
			// Because fuck it.
			int[] cocoaStages = {2, 2, 2, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6};
			growthStage = cocoaStages[growthStage];
			prop = BlockCocoa.AGE;
		} else if (placedBlock instanceof BlockCrops) {
			prop = BlockCrops.AGE;
		}
		return placedState.withProperty(prop, growthStage);

  }

	public static void verifyItems(ItemCraftedEvent event) {
		Item targetItem = event.crafting.getItem();
		if (targetItem != null && targetItem == ModItems.overalls) {
			if (!Skills.canCraft(event.player, Skills.FARMING, 25)) {
				Skills.replaceWithComponents(event);
			} else if (!event.player.worldObj.isRemote && event.player.hasCapability(Skills.FARMING, EnumFacing.NORTH)) {
				SkillFarming farming = (SkillFarming) event.player.getCapability(Skills.FARMING, EnumFacing.NORTH);
				farming.addXp((EntityPlayerMP) event.player, 500);
			}
		}
	}

	public static void applyOveralls(PlayerInteractEvent.RightClickBlock event) {
		EntityPlayer player = event.getEntityPlayer();
		if (player.worldObj.isRemote) {
			RayTraceResult result = player.rayTrace(5.0D, 1.0F);
			if (result != null) {
				if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
					BlockPos targetPos = result.getBlockPos();
					IBlockState targetState = player.worldObj.getBlockState(targetPos);
					Block targetBlock = targetState.getBlock();
					if (validCrop(targetState)) {
						InventoryPlayer inventory = player.inventory;
						if (inventory != null) {
							ItemStack stack = inventory.armorInventory[2];
							if (stack != null) {
								Item chest = stack.getItem();
								if (chest == ModItems.overalls) {
									ItemStack mainStack = player.getHeldItemMainhand();
									Item mainItem = mainStack.getItem();
									if (mainItem instanceof ItemHoe || mainItem instanceof CustomHoe) {
										SkrimPacketHandler.INSTANCE.sendToServer(new ApplyBonemealPacket(targetPos.getX(), targetPos.getY(), targetPos.getZ()));
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public static void createFarmland(UseHoeEvent event) {
		EntityPlayer player = event.getEntityPlayer();
		if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.FARMING, EnumFacing.NORTH)) {
			SkillFarming farming = (SkillFarming) player.getCapability(Skills.FARMING, EnumFacing.NORTH);
			BlockPos targetPos = event.getPos();
			String blockName = SkillDigging.getDirtName(event.getWorld().getBlockState(targetPos));
			if (blockName.equals("dirt") || blockName.equals("grass_block")) {
				farming.addXp((EntityPlayerMP) player, 10);
			}
		}
	}

	public static void sideChick(LivingDeathEvent event) {
		Entity sourceEntity = event.getSource().getEntity();
		if (sourceEntity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) sourceEntity;
			Entity targetEntity = event.getEntity();
			if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.FARMING, EnumFacing.NORTH)) {
				SkillFarming farming = (SkillFarming) player.getCapability(Skills.FARMING, EnumFacing.NORTH);
				if (farming.hasAbility(2)) {
					BlockPos aboveLocation = new BlockPos(targetEntity.posX, targetEntity.posY, targetEntity.posZ);
					BlockPos groundLocation = new BlockPos(aboveLocation.getX(), aboveLocation.getY() - 1, aboveLocation.getZ());
					IBlockState aboveState = player.worldObj.getBlockState(aboveLocation);
					IBlockState groundState = player.worldObj.getBlockState(groundLocation);
					Block aboveBlock = aboveState.getBlock();
					Block groundBlock = groundState.getBlock();
					if (aboveBlock instanceof BlockAir && (groundBlock instanceof BlockDirt || groundBlock instanceof BlockGrass || groundBlock instanceof BlockFarmland)) {
						ItemStack mainStack = player.getHeldItemMainhand();
						ItemStack offStack = player.getHeldItemOffhand();
						if (mainStack != null || offStack != null) {
							Item mainItem = (mainStack != null) ? mainStack.getItem() : null;
							Item offItem = (offStack != null) ? offStack.getItem() : null;
							if (mainItem instanceof ItemHoe || mainItem instanceof CustomHoe || offItem instanceof ItemHoe || offItem instanceof CustomHoe) {
								player.worldObj.setBlockState(groundLocation, Blocks.FARMLAND.getDefaultState());
								IBlockState placedState = cropBlocks.get(Utils.rand.nextInt(cropBlocks.size())).getDefaultState();
								player.worldObj.setBlockState(
									aboveLocation,
									farming.cropWithGrowth(placedState)
								);
								farming.addXp((EntityPlayerMP) player, 200);
								Skills.playFortuneSound(player);
							}
						}
					}
				}
			}
		}
	}

	public static void farmersTan(LivingUpdateEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if (!player.worldObj.isRemote) {
				if (player != null && player.hasCapability(Skills.FARMING, EnumFacing.NORTH)) {
					SkillFarming farming = (SkillFarming) player.getCapability(Skills.FARMING, EnumFacing.NORTH);
					if (farming.hasAbility(3)) {
						if (player.worldObj.getTotalWorldTime() % tanCheck == 0L) {
							BlockPos playerPos = new BlockPos(player.posX, player.posY, player.posZ);
							if (player.worldObj.isDaytime() && player.worldObj.canSeeSky(playerPos)) {
								player.addPotionEffect(new PotionEffect(MobEffects.SATURATION, tanDuration, 0, true, false));
								player.addPotionEffect(new PotionEffect(MobEffects.SPEED, tanDuration, 0, true, false));
							}
						}
					}
				}
			}
		}
	}

}
