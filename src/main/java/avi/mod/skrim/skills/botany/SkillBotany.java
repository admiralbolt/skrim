package avi.mod.skrim.skills.botany;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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
		xpMap.put("syringa", 40); // lila
		xpMap.put("rose_bush", 40);
		xpMap.put("paeonia", 40); // peony
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

	private int getXp(String blockName) {
		return (xpMap.containsKey(blockName)) ? xpMap.get(blockName) : 0;
	}

	private double getFortuneChance() {
		return 0.01 * this.level;
	}

	private int getFortuneAmount() {
		return (int) (((double) this.level) / 12) + 2;
	}

	private String getFlowerName(IBlockState state) {
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

	private boolean validFlower(IBlockState state) {
		Block flower = state.getBlock();
		String flowerName = this.getFlowerName(state);
		return (flower instanceof BlockFlower
			|| (flower instanceof BlockDoublePlant && !(flowerName.equals("double_tallgrass")) && !(flowerName.equals("large_fern"))	)
		);
	}

	@Override
	public List<String> getToolTip() {
		DecimalFormat fmt = new DecimalFormat("0.0");
		List<String> tooltip = new ArrayList<String>();
		tooltip.add("§a" + (this.getFortuneChance() * 100) + "%§r chance to §a" + Utils.getFortuneString(this.getFortuneAmount()) + "§r flower drops.");
		return tooltip;
	}

	@SubscribeEvent
	public void onBlockBreak(BlockEvent.BreakEvent event) {
		IBlockState state = event.getState();
		this.xp += this.getXp(this.getFlowerName(state));
		this.levelUp();
	}

	@SubscribeEvent
	public void onHarvestFlower(BlockEvent.HarvestDropsEvent event) {
		IBlockState state = event.getState();
		if (this.validFlower(state)) {
			EntityPlayer player = event.getHarvester();
			Block block = state.getBlock();
			/**
			 * DOUBLE Plants are coded weirdly, so currently fortune WON'T apply to them.
			 * EDIT: Won't apply to some of them...? Why you do dis.
			 */
			if (player != null && player.hasCapability(Skills.BOTANY, EnumFacing.NORTH)) {
				SkillBotany botany = (SkillBotany) player.getCapability(Skills.BOTANY, EnumFacing.NORTH);
				double random = Math.random();
				if (random < botany.getFortuneChance()) {
					List<ItemStack> drops = event.getDrops();
					// Let's not loop infinitely, that seems like a good idea.
					int dropSize = drops.size();
          for (int j = 0; j < this.getFortuneAmount() - 1; j++) {
            for (int i = 0; i < dropSize; i++) {
              drops.add(drops.get(i).copy());
            }
          }
          this.xp += 100; // And 100 xp!
          this.levelUp();
				}
			}
		}
	}

	// @SubscribeEvent
	// public void asdf(ItemSmeltedEvent event) {
	// 	System.out.println(event.player);
	// 	System.out.println(event.toString());
	// 	System.out.println(event.getResult());
	// 	System.out.println("FUCK YEAH");
	// 	System.out.println(event.smelting);
	// 	System.out.println(event.hashCode());
	// }

}
