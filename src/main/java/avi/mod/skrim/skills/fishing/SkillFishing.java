package avi.mod.skrim.skills.fishing;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import net.minecraft.command.ICommandManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import avi.mod.skrim.Utils;
import avi.mod.skrim.skills.RandomTreasure;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillAbility;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;

public class SkillFishing extends Skill implements ISkillFishing {

	public static SkillStorage<ISkillFishing> skillStorage = new SkillStorage<ISkillFishing>();
	private static Random xpRand = new Random();

	public boolean canCatch = false;
	public static Map<String, Integer> xpMap;
	static {
		xpMap = new HashMap<String, Integer>();
		String[] validItems = {
			"item.item.fish.cod.raw",
			"item.item.fish.salmon.raw",
			"item.item.fish.clownfish.raw",
			"item.item.fish.pufferfish.raw",
			"item.item.bow",
			"item.item.enchantedbook",
			"item.item.fishingrod",
			"item.item.nametag",
			"item.item.saddle",
			"item.item.waterlily",
			"item.item.bowl",
			"item.item.leather",
			"item.item.bootscloth",
			"item.item.rottenflesh",
			"item.item.stick",
			"item.item.string",
			"item.item.potion",
			"item.item.bone",
			"item.item.dyepowder.black",
			"item.tile.tripwiresource"
		};
		/**
		 * This may be unnecessary, but we want to keep this way so we can
		 * easily change experience in the future.
		 */
		for (String itemName : validItems) {
			xpMap.put(itemName, 100);
		}
	}

	public static SkillAbility batman = new SkillAbility(
		"Batman",
		25,
		"na na na na na na na na",
		"Your fishing rod can now be used as a grappling hook."
	);

	public SkillFishing() {
		this(1, 0);
	}

	public SkillFishing(int level, int currentXp) {
		super("Fishing", level, currentXp);
		this.iconTexture = new ResourceLocation("skrim", "textures/guis/skills/fishing.png");
		this.addAbilities(batman);
	}

	public int getXp(String blockName) {
		return (xpMap.containsKey(blockName)) ? xpMap.get(blockName) : 0;
	}

	public double getTreasureChance() {
		return 0.01 * this.level;
	}

	public double getDelayReduction() {
		return 0.0075 * this.level;
	}

	public boolean isValidFish(EntityItem item) {
		return this.canCatch && this.xpMap.containsKey(Utils.snakeCase(item.getName()));
	}

	public int randomXPOrb() {
		int min = this.getMinXP();
		int max = this.getMaxXP();
		return xpRand.nextInt(max - min) + min;
	}

	private int getMinXP() {
		return (int) (this.level * 0.5) + 1;
	}

	private int getMaxXP() {
		return (int) (this.level * 1.1) + 2;
	}

	@Override
	public List<String> getToolTip() {
		DecimalFormat fmt = new DecimalFormat("0.0");
		List<String> tooltip = new ArrayList<String>();
		tooltip.add("§a" + fmt.format(this.getDelayReduction() * 100) + "%§r reduced fishing time.");
		tooltip.add("§a" + fmt.format(this.getTreasureChance() * 100) + "%§r chance to fish additional treasure.");
		tooltip.add("Fishing provides an additional §a" + this.getMinXP() + "§r-§a" + this.getMaxXP() + "§r xp.");
		return tooltip;
	}

}
