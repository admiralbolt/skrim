package avi.mod.skrim.skills.botany;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.BlockEvent;
import avi.mod.skrim.Utils;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;

public class SkillBotany extends Skill implements ISkillBotany {

	public static SkillStorage<ISkillBotany> skillStorage = new SkillStorage<ISkillBotany>();
	public static Map<String, Integer> xpMap;
	static {
		xpMap = new HashMap<String, Integer>();
		// The chart for flower rarity is at: http://minecraft.gamepedia.com/Flower
		xpMap.put("dandelion", 10);
		xpMap.put("poppy", 10);
		// 3 Biomes
		xpMap.put("houstonia", 20); // azure_bluet
		xpMap.put("red_tulip", 20);
		xpMap.put("orange_tulip", 20);
		xpMap.put("white_tulip", 20);
		xpMap.put("pink_tulip", 20);
		xpMap.put("oxeye_daisy", 20);
		// Only swamp, can respawn
		xpMap.put("blue_orchid", 30);
		// Only forest & flower forest on generation
		xpMap.put("syringa", 40); // lilac
		xpMap.put("rose_bush", 40);
		xpMap.put("paeonia", 40); // peony
		xpMap.put("allium", 40);
		// Only sunflower plains on generation
		xpMap.put("sunflower", 50);
	}

	public SkillBotany() {
		this(1, 0);
	}

	public SkillBotany(int level, int currentXp) {
		super("Botany", level, currentXp);
		this.iconTexture = new ResourceLocation("skrim", "textures/guis/skills/botany.png");
	}

	public int getXp(String blockName) {
		return (xpMap.containsKey(blockName)) ? xpMap.get(blockName) : 0;
	}
	
	public boolean validFlowerStack(ItemStack stack) {
		if (stack != null) {
			Item item = stack.getItem();
			String name = Utils.snakeCase(item.getItemStackDisplayName(stack));
			if (item != null && (
					xpMap.containsKey(name)
					|| name.equals("azure_bluet")
					|| name.equals("lilac")
					|| name.equals("peony")
				)
			) {
				return true;
			}
		}
		return false;
	}

	public double getFortuneChance() {
		return 0.01 * this.level;
	}

	public int getFortuneAmount() {
		return (int) (((double) this.level) / 12) + 2;
	}

	public String getFlowerName(IBlockState state) {
		Block block = state.getBlock();
		if (block instanceof BlockFlower) {
			BlockFlower flower = (BlockFlower) block;
			return state.getValue(flower.getTypeProperty()).toString();
		} else if (block instanceof BlockDoublePlant) {
			return state.getValue(BlockDoublePlant.VARIANT).toString();
		} else {
			return "";
		}
	}

	public boolean validFlower(IBlockState state) {
		Block flower = state.getBlock();
		String flowerName = this.getFlowerName(state);
		return (flower instanceof BlockFlower
			// || (flower instanceof BlockDoublePlant && !(flowerName.equals("double_tallgrass")) && !(flowerName.equals("large_fern"))	)
		);
	}

	@Override
	public List<String> getToolTip() {
		DecimalFormat fmt = new DecimalFormat("0.0");
		List<String> tooltip = new ArrayList<String>();
		tooltip.add("§a" + fmt.format(this.getFortuneChance() * 100) + "%§r chance to §a" + Utils.getFortuneString(this.getFortuneAmount()) + "§r flower drops.");
		return tooltip;
	}

	public static void addBotanyXp(BlockEvent.BreakEvent event) {
		EntityPlayer player = event.getPlayer();
		if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.BOTANY, EnumFacing.NORTH)) {
			SkillBotany botany = (SkillBotany) player.getCapability(Skills.BOTANY, EnumFacing.NORTH);
			IBlockState state = event.getState();
			int addXp = botany.getXp(botany.getFlowerName(state));
			if (addXp > 0) {
				botany.addXp((EntityPlayerMP) player, botany.getXp(botany.getFlowerName(state)));
			}
		}
	}

	public static void soManyFlowers(BlockEvent.HarvestDropsEvent event) {
		IBlockState state = event.getState();
		EntityPlayer player = event.getHarvester();
		if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.BOTANY, EnumFacing.NORTH)) {
			SkillBotany botany = (SkillBotany) player.getCapability(Skills.BOTANY, EnumFacing.NORTH);
			if (botany.validFlower(state)) {
				Block block = state.getBlock();
				/**
				 * DOUBLE Plants are coded weirdly, so currently fortune WON'T apply to them.
				 * EDIT: Won't apply to some of them...? Why you do dis.
				 */
				double random = Math.random();
				if (random < botany.getFortuneChance()) {
					List<ItemStack> drops = event.getDrops();
					// Let's not loop infinitely, that seems like a good idea.
					int dropSize = drops.size();
          for (int j = 0; j < botany.getFortuneAmount() - 1; j++) {
            for (int i = 0; i < dropSize; i++) {
              drops.add(drops.get(i).copy());
            }
          }
          botany.addXp((EntityPlayerMP) player, 50); // and 50 xp!
				}
			}
		}
	}

	public static void flowerSplosion(BlockEvent.PlaceEvent event) {
		EntityPlayer player = event.getPlayer();
		if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.BOTANY, EnumFacing.NORTH)) {
			SkillBotany botany = (SkillBotany) player.getCapability(Skills.BOTANY, EnumFacing.NORTH);
			if (botany.hasAbility(1)) {
				IBlockState placedState = event.getPlacedBlock();
				if (botany.validFlower(placedState)) {
					BlockPos placedPos = event.getPos();
					for (int i = -1; i <= 1; i++) {
						for (int j = -1; j <= 1; j++) {
							if (i != 0 || j != 0) {
								System.out.println("checking pos: (" + (placedPos.getX() + i) + ", " + placedPos.getY() + ", " + (placedPos.getZ() + j) + ")");
								BlockPos airPos = new BlockPos(placedPos.getX() + i, placedPos.getY(), placedPos.getZ() + j);
								IBlockState airState = player.worldObj.getBlockState(airPos);
								if (airState != null) {
									Block airBlock = airState.getBlock();
									System.out.println(airBlock);
									if (airBlock.isAir(airState, player.worldObj, airPos)) {
										BlockPos dirtPos = new BlockPos(airPos.getX(), airPos.getY() - 1, airPos.getZ());
										System.out.println("checking dirtPos: " + dirtPos);
										IBlockState dirtState = player.worldObj.getBlockState(dirtPos);
										if (dirtState != null) {
											Block dirtBlock = dirtState.getBlock();
											if (dirtBlock instanceof BlockDirt || dirtBlock instanceof BlockGrass) {
												player.worldObj.setBlockState(airPos, placedState);
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

	public static void thornStyle(LivingHurtEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.BOTANY, EnumFacing.NORTH)) {
				SkillBotany botany = (SkillBotany) player.getCapability(Skills.BOTANY, EnumFacing.NORTH);
				if (botany.hasAbility(2)) {
					ItemStack mainStack = player.getHeldItemMainhand();
					if (botany.validFlowerStack(mainStack)) {
						System.out.println("fuck yeah");
						DamageSource source = event.getSource();
						if (source.damageType == "mob" || source.damageType == "player") {
							Entity target = source.getEntity();
							System.out.println("attacking back");
							target.attackEntityFrom(DamageSource.magic, (float) (event.getAmount() * 0.3));
						}
					}
				}
			}
		}
	}

}
