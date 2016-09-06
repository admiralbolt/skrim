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
import net.minecraft.util.ResourceLocation;
import avi.mod.skrim.Utils;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillAbility;
import avi.mod.skrim.skills.SkillStorage;

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

	public int getXp(String blockName) {
		return (xpMap.containsKey(blockName)) ? xpMap.get(blockName) : 0;
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
			|| (flower instanceof BlockDoublePlant && !(flowerName.equals("double_tallgrass")) && !(flowerName.equals("large_fern"))	)
		);
	}

	@Override
	public List<String> getToolTip() {
		DecimalFormat fmt = new DecimalFormat("0.0");
		List<String> tooltip = new ArrayList<String>();
		tooltip.add("§a" + fmt.format(this.getFortuneChance() * 100) + "%§r chance to §a" + Utils.getFortuneString(this.getFortuneAmount()) + "§r flower drops.");
		return tooltip;
	}

}
