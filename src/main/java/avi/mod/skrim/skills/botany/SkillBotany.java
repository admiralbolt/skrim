package avi.mod.skrim.skills.botany;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import avi.mod.skrim.blocks.flowers.EnchantedFlowerVariants;
import avi.mod.skrim.blocks.flowers.GlowFlower;
import avi.mod.skrim.blocks.flowers.GlowFlowerVariants;
import avi.mod.skrim.network.SkrimPacketHandler;
import avi.mod.skrim.network.SpawnHeartPacket;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillAbility;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.utils.Reflection;
import avi.mod.skrim.utils.Utils;
import avi.mod.skrim.world.PlayerPlacedBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

public class SkillBotany extends Skill implements ISkillBotany {

	public static SkillStorage<ISkillBotany> skillStorage = new SkillStorage<ISkillBotany>();
	public static Map<String, Integer> xpMap;
	static {
		xpMap = new HashMap<String, Integer>();
		// The chart for flower rarity is at: http://minecraft.gamepedia.com/Flower
		xpMap.put("dandelion", 150);
		xpMap.put("poppy", 150);
		// 3 Biomes
		xpMap.put("houstonia", 300); // azure_bluet
		xpMap.put("red_tulip", 300);
		xpMap.put("orange_tulip", 300);
		xpMap.put("white_tulip", 300);
		xpMap.put("pink_tulip", 300);
		xpMap.put("oxeye_daisy", 300);
		// Only swamp, can respawn
		xpMap.put("blue_orchid", 600);
		xpMap.put("allium", 600);
		// Only forest & flower forest on generation
		xpMap.put("syringa", 1500); // lilac
		xpMap.put("rose_bush", 1500);
		xpMap.put("paeonia", 1500); // peony
		// Only sunflower plains on generation
		xpMap.put("sunflower", 3000);
	}

	public static SkillAbility sunFlower = new SkillAbility(
		"Sun Flower",
		25,
		"It was either this or mariglow, don't know which one is worse.",
		"Enables you to craft glowing flowers with a flower & glowstone dust."
	);

	public static SkillAbility thornStyle = new SkillAbility(
		"Thorn Style",
		50,
		"I'll let you try my thorn style.",
		"While holding a flower return §a25%" + SkillAbility.descColor + " of melee damage."
	);

	public static SkillAbility seduceVillager = new SkillAbility(
		"Seduce Villager",
		75,
		"[Tongue waggling intensifies]",
		"Using a flower on a villager consumes it and reduces the cost of all trades by §a1" + SkillAbility.descColor + "."
	);

	public static SkillAbility enchantedFlower = new SkillAbility(
		"Enchanted Flower",
		100,
		"It shares a giant friendliness beam! :D",
		"Enables you to craft enchanted flowers that function like speed beacons."
	);

	public SkillBotany() {
		this(1, 0);
	}

	public SkillBotany(int level, int currentXp) {
		super("Botany", level, currentXp);
		this.iconTexture = new ResourceLocation("skrim", "textures/guis/skills/botany.png");
		this.addAbilities(sunFlower, thornStyle, seduceVillager, enchantedFlower);
	}

	public int getXp(String blockName) {
		return (xpMap.containsKey(blockName)) ? xpMap.get(blockName) : 0;
	}

	public double getSplosionChance() {
		return this.level * 0.01;
	}

	public int getSplosionRadius() {
		return (int) (this.level / 25) + 1;
	}

	public double getFortuneChance() {
		return 0.01 * this.level;
	}

	public int getFortuneAmount() {
		return (int) (((double) this.level) / 12) + 2;
	}

	public static boolean validFlowerStack(ItemStack stack) {
		if (stack != null) {
			Item item = stack.getItem();
			Block block = Block.getBlockFromItem(stack.getItem());
			String name = Utils.snakeCase(item.getItemStackDisplayName(stack));
			if ((item != null
					&& (
						xpMap.containsKey(name)
						|| name.equals("azure_bluet")
						|| name.equals("lilac")
						|| name.equals("peony")
					)
			) || (block != null && validFlowerBlock(block))) {
				return true;
			}
		}
		return false;
	}

	public static String getFlowerName(IBlockState state) {
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

	public static boolean validFlowerBlock(Block block) {
		return (block instanceof BlockFlower || block instanceof GlowFlower);
	}

	public static boolean validFlowerState(IBlockState state) {
		Block flower = state.getBlock();
		return validFlowerBlock(flower);
	}

	@Override
	public List<String> getToolTip() {
		List<String> tooltip = new ArrayList<String>();
		tooltip.add("§a" + Utils.formatPercent(this.getFortuneChance()) + "%§r chance to §a" + Utils.getFortuneString(this.getFortuneAmount()) + "§r flower drops.");
		tooltip.add("§a" + Utils.formatPercent(this.getSplosionChance()) + "%§r chance to cause a flowersplosion with radius §a" + this.getSplosionRadius() + "§r.");
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
			if (botany.validFlowerState(state)) {
				Block block = state.getBlock();
				/**
				 * DOUBLE Plants are coded weirdly, so currently fortune WON'T apply to them.
				 * EDIT: Won't apply to some of them...? Why you do dis.
				 */
				double random = Utils.rand.nextDouble();
				if (random < botany.getFortuneChance()) {
					List<ItemStack> drops = event.getDrops();
					// Let's not loop infinitely, that seems like a good idea.
					int dropSize = drops.size();
          for (int j = 0; j < botany.getFortuneAmount() - 1; j++) {
            for (int i = 0; i < dropSize; i++) {
              drops.add(drops.get(i).copy());
            }
          }
          Skills.playFortuneSound(player);
					if (PlayerPlacedBlocks.isNaturalBlock(event.getWorld(), event.getPos())) {
          	botany.addXp((EntityPlayerMP) player, 200); // and 200 xp!
					}
				}
			}
		}
	}

	public static void flowerSplosion(BlockEvent.PlaceEvent event) {
		EntityPlayer player = event.getPlayer();
		if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.BOTANY, EnumFacing.NORTH)) {
			SkillBotany botany = (SkillBotany) player.getCapability(Skills.BOTANY, EnumFacing.NORTH);
			if (Utils.rand.nextDouble() < botany.getSplosionChance()) {
				IBlockState placedState = event.getPlacedBlock();
				if (botany.validFlowerState(placedState)) {
					BlockPos placedPos = event.getPos();
					int radius = botany.getSplosionRadius();
					for (int i = -radius; i <= radius; i++) {
						for (int j = -radius; j <= radius; j++) {
							if (i != 0 || j != 0) {
								BlockPos airPos = new BlockPos(placedPos.getX() + i, placedPos.getY(), placedPos.getZ() + j);
								IBlockState airState = player.worldObj.getBlockState(airPos);
								if (airState != null) {
									Block airBlock = airState.getBlock();
									if (airBlock.isAir(airState, player.worldObj, airPos)) {
										BlockPos dirtPos = new BlockPos(airPos.getX(), airPos.getY() - 1, airPos.getZ());
										IBlockState dirtState = player.worldObj.getBlockState(dirtPos);
										if (dirtState != null) {
											Block dirtBlock = dirtState.getBlock();
											if (dirtBlock instanceof BlockDirt || dirtBlock instanceof BlockGrass || dirtBlock instanceof BlockFarmland) {
												player.worldObj.setBlockState(airPos, placedState);
												PlayerPlacedBlocks.addBlock(player.worldObj, airPos);
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
					ItemStack offStack = player.getHeldItemOffhand();
					if (botany.validFlowerStack(mainStack) || botany.validFlowerStack(offStack)) {
						DamageSource source = event.getSource();
						if (source.damageType == "mob" || source.damageType == "player") {
							Entity target = source.getEntity();
							target.attackEntityFrom(DamageSource.magic, (float) (event.getAmount() * 0.25));
						}
					}
				}
			}
		}
	}

	public static void seduceVillager(PlayerInteractEvent.EntityInteract event) {
		EntityPlayer player = event.getEntityPlayer();
		Entity targetEntity = event.getTarget();
		if (targetEntity instanceof EntityVillager) {
			EntityVillager villager = (EntityVillager) targetEntity;
			if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.BOTANY, EnumFacing.NORTH)) {
				SkillBotany botany = (SkillBotany) player.getCapability(Skills.BOTANY, EnumFacing.NORTH);
				if (botany.hasAbility(3)) {
					ItemStack mainStack = player.getHeldItemMainhand();
					if (botany.validFlowerStack(mainStack)) {
						villager.setIsWillingToMate(true);
						MerchantRecipeList buyingList = (MerchantRecipeList) Reflection.getPrivateField(villager, "buyingList");
						for (MerchantRecipe recipe : buyingList) {
							ItemStack first = recipe.getItemToBuy();
							if (first.stackSize > 4) {
								first.stackSize--;
							}
							if (recipe.hasSecondItemToBuy()) {
								ItemStack second = recipe.getSecondItemToBuy();
								if (second.stackSize > 4) {
									second.stackSize--;
								}
							}
						}
						SkrimPacketHandler.INSTANCE.sendTo(new SpawnHeartPacket(villager.posX, villager.posY, villager.posZ, villager.height, villager.width), (EntityPlayerMP) player);
						mainStack.stackSize--;
						if (mainStack.stackSize == 0) {
							player.inventory.deleteStack(mainStack);
							botany.addXp((EntityPlayerMP) player, 100);
						}
						event.setCanceled(true);
					}
				}
			}
		}
	}

	public static void verifyFlowers(ItemCraftedEvent event) {
		Item targetItem = event.crafting.getItem();
		if (targetItem != null && targetItem instanceof GlowFlowerVariants) {
			if (!Skills.canCraft(event.player, Skills.BOTANY, 25)) {
				Skills.replaceWithComponents(event);
			} else if (event.player.hasCapability(Skills.BOTANY, EnumFacing.NORTH)) {
				SkillBotany botany = (SkillBotany) event.player.getCapability(Skills.BOTANY, EnumFacing.NORTH);
				botany.addXp((EntityPlayerMP) event.player, 500);
			}
		} else if (targetItem != null && targetItem instanceof EnchantedFlowerVariants) {
			if (!Skills.canCraft(event.player, Skills.BOTANY, 100)) {
				Skills.replaceWithComponents(event);
			} else if (event.player.hasCapability(Skills.BOTANY, EnumFacing.NORTH)) {
				SkillBotany botany = (SkillBotany) event.player.getCapability(Skills.BOTANY, EnumFacing.NORTH);
				botany.addXp((EntityPlayerMP) event.player, 10000);
			}
		}
	}

}
