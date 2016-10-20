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
import net.minecraft.command.ICommandManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class SkillDigging extends Skill implements ISkillDigging {

	public static SkillStorage<ISkillDigging> skillStorage = new SkillStorage<ISkillDigging>();
	public static Map<String, Integer> xpMap;
	static {
		xpMap = new HashMap<String, Integer>();
		xpMap.put("dirt", 50);
		xpMap.put("farmland", 50);
		xpMap.put("sand", 50);
		xpMap.put("snow", 50);
		xpMap.put("grass_block", 60); // Bonus for grass!
		xpMap.put("gravel", 125); // Fuck gravel
		xpMap.put("clay", 150);
		xpMap.put("soul_sand", 200); // Only in nether & not to common
		xpMap.put("podzol", 250); // Only in taiga
		xpMap.put("red_sand", 375); // Only in mesa
		xpMap.put("mycelium", 500); // Only in.. mushroom biomes?
	}

	public double metalMeter = 0;
	public static double meterFilled = 100;
	public Vec3d lastPos = null;

	public static SkillAbility vitalicBreathing = new SkillAbility(
		"Vitalic Breathing",
		25,
		"Breathe, breathe in the ...dirt?",
		"No longer take suffocation damage from being trapped in walls."
	);

	public static SkillAbility metalDetector = new SkillAbility(
		"Metal Detector",
		50,
		"Beep....Beep....Beep....",
		"Moving over dirt blocks causes random metal objects to appear!"
	);

	public static SkillAbility entomb = new SkillAbility(
		"Entomb",
		75,
		"Fuck Priest.",
		"Right clicking an entity with a shovel buries it in the earth."
	);

	public SkillDigging() {
		this(1, 0);
	}

	public SkillDigging(int level, int currentXp) {
		super("Digging", level, currentXp);
		this.iconTexture = new ResourceLocation("skrim", "textures/guis/skills/digging.png");
		this.addAbilities(vitalicBreathing, metalDetector, entomb);
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
		return ((harvestTool != null && harvestTool.toLowerCase().equals("shovel"))
			|| validTreasureTarget(state)
			) ? true : false;
	}

	public static boolean validTreasureTarget(IBlockState state) {
		Block block = state.getBlock();
		return (block instanceof BlockDirt
			|| block instanceof BlockGrass
			|| block instanceof BlockSand
			|| block instanceof BlockGravel
			|| block instanceof BlockMycelium
			|| block instanceof BlockSoulSand);
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
		if (PlayerPlacedBlocks.isNaturalBlock(event.getWorld(), event.getPos())) {
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

  public static void vitalicBreathing(LivingHurtEvent event) {
  	Entity entity = event.getEntity();
		if (entity instanceof EntityPlayer) {
			final EntityPlayer player = (EntityPlayer) entity;
			if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.DIGGING, EnumFacing.NORTH)) {
				SkillDigging digging = (SkillDigging) player.getCapability(Skills.DIGGING, EnumFacing.NORTH);
				if (digging.hasAbility(1)) {
					if (event.getSource() == DamageSource.inWall) {
						event.setAmount(0F);
					}
				}
			}
		}
  }

	public static void metalDetector(LivingUpdateEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof EntityPlayer) {
			final EntityPlayer player = (EntityPlayer) entity;
			if (player.worldObj.isRemote) {
				if (player != null && player.hasCapability(Skills.DIGGING, EnumFacing.NORTH)) {
					SkillDigging digging = (SkillDigging) player.getCapability(Skills.DIGGING, EnumFacing.NORTH);
					if (digging.hasAbility(2)) {
						BlockPos playerLocation = new BlockPos(player.posX, player.posY, player.posZ);
						IBlockState onState = player.worldObj.getBlockState(playerLocation.add(0, -1, 0));
						if (validTreasureTarget(onState)) {
							digging.metalMeter += Math.sqrt(player.motionX * player.motionX + player.motionZ * player.motionZ);
							if (digging.metalMeter >= SkillDigging.meterFilled) {
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
						targetEntity.setPosition(targetEntity.posX, targetEntity.posY - 5, targetEntity.posZ);
						mainStack.damageItem(10, player);
					}
				}
			}
		}
	}


}
