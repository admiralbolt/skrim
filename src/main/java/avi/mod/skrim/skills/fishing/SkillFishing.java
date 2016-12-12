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
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import avi.mod.skrim.items.ModItems;
import avi.mod.skrim.skills.RandomTreasure;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillAbility;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.utils.Obfuscation;
import avi.mod.skrim.utils.Reflection;
import avi.mod.skrim.utils.Utils;

public class SkillFishing extends Skill implements ISkillFishing {

	public static SkillStorage<ISkillFishing> skillStorage = new SkillStorage<ISkillFishing>();

	public static SkillAbility BATMAN = new SkillAbility(
		"Batman",
		25,
		"na na na na na na na na",
		"Your fishing rod can now be used as a grappling hook."
	);

	public static SkillAbility TRIPLE_HOOK = new SkillAbility(
		"Triple Hook",
		50,
		"Triple the hooks, triple the pleasure.",
		"You now catch §a3x" + SkillAbility.descColor + " as many items."
	);

	public static SkillAbility BOUNTIFUL_CATCH = new SkillAbility(
		"Bountiful Catch",
		75,
		"On that E-X-P grind.",
		"Catching a fish provides an additional§a 9-24" + SkillAbility.descColor + " xp."
	);

	public static SkillAbility FLING = new SkillAbility(
		"Fling",
		100,
		"Sometimes I don't know my own strength.",
		"Launch hooked entities into the air."
	);

	public SkillFishing() {
		this(1, 0);
	}

	public SkillFishing(int level, int currentXp) {
		super("Fishing", level, currentXp);
		this.iconTexture = new ResourceLocation("skrim", "textures/guis/skills/fishing.png");
		this.addAbilities(BATMAN, TRIPLE_HOOK, BOUNTIFUL_CATCH, FLING);
	}

	public double getTreasureChance() {
		return 0.01 * this.level;
	}

	public double getDelayReduction() {
		return 0.0075 * this.level;
	}

	@Override
	public List<String> getToolTip() {
		List<String> tooltip = new ArrayList<String>();
		tooltip.add("§a" + Utils.formatPercentTwo(this.getDelayReduction()) + "%§r reduced fishing time.");
		tooltip.add("§a" + Utils.formatPercent(this.getTreasureChance()) + "%§r chance to fish additional treasure.");
		return tooltip;
	}

}
