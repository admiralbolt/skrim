package avi.mod.skrim.skills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraftforge.common.capabilities.Capability;
import avi.mod.skrim.skills.*;
import avi.mod.skrim.skills.blacksmithing.BlacksmithingProvider;
import avi.mod.skrim.skills.blacksmithing.ISkillBlacksmithing;
import avi.mod.skrim.skills.botany.BotanyProvider;
import avi.mod.skrim.skills.botany.ISkillBotany;
import avi.mod.skrim.skills.cooking.CookingProvider;
import avi.mod.skrim.skills.cooking.ISkillCooking;
import avi.mod.skrim.skills.defense.DefenseProvider;
import avi.mod.skrim.skills.defense.ISkillDefense;
import avi.mod.skrim.skills.demolition.DemolitionProvider;
import avi.mod.skrim.skills.demolition.ISkillDemolition;
import avi.mod.skrim.skills.digging.ISkillDigging;
import avi.mod.skrim.skills.digging.DiggingProvider;
import avi.mod.skrim.skills.farming.FarmingProvider;
import avi.mod.skrim.skills.farming.ISkillFarming;
import avi.mod.skrim.skills.fishing.FishingProvider;
import avi.mod.skrim.skills.fishing.ISkillFishing;
import avi.mod.skrim.skills.melee.ISkillMelee;
import avi.mod.skrim.skills.melee.MeleeProvider;
import avi.mod.skrim.skills.mining.ISkillMining;
import avi.mod.skrim.skills.mining.MiningProvider;
import avi.mod.skrim.skills.ranged.ISkillRanged;
import avi.mod.skrim.skills.ranged.RangedProvider;
import avi.mod.skrim.skills.woodcutting.ISkillWoodcutting;
import avi.mod.skrim.skills.woodcutting.WoodcuttingProvider;

public class Skills {

	public static Capability<ISkillBlacksmithing> BLACKSMITHING = BlacksmithingProvider.BLACKSMITHING;
	public static Capability<ISkillBotany> BOTANY = BotanyProvider.BOTANY;
	public static Capability<ISkillCooking> COOKING = CookingProvider.COOKING;
	public static Capability<ISkillDefense> DEFENSE = DefenseProvider.DEFENSE;
	public static Capability<ISkillDemolition> DEMOLITION = DemolitionProvider.DEMOLITION;
  public static Capability<ISkillDigging> DIGGING = DiggingProvider.DIGGING;
  public static Capability<ISkillFarming> FARMING = FarmingProvider.FARMING;
  public static Capability<ISkillFishing> FISHING = FishingProvider.FISHING;
  public static Capability<ISkillMelee> MELEE = MeleeProvider.MELEE;
  public static Capability<ISkillMining> MINING = MiningProvider.MINING;
  public static Capability<ISkillRanged> RANGED = RangedProvider.RANGED;
  public static Capability<ISkillWoodcutting> WOODCUTTING = WoodcuttingProvider.WOODCUTTING;

  public static Map<String, Capability<? extends ISkill>> skillMap = new HashMap<String, Capability<? extends ISkill>>();
  public static List<Capability<? extends ISkill>> ALL_SKILLS = new ArrayList<Capability<? extends ISkill>>();

	public static void register() {
    MiningProvider.register();
    WoodcuttingProvider.register();
    DiggingProvider.register();
    FarmingProvider.register();
    BotanyProvider.register();
    CookingProvider.register();
    BlacksmithingProvider.register();
    FishingProvider.register();
    DemolitionProvider.register();
    MeleeProvider.register();
    RangedProvider.register();
    DefenseProvider.register();

    /**
     * This functionality should really be handled in the providers themselves.
     */

    BLACKSMITHING = BlacksmithingProvider.BLACKSMITHING;
  	BOTANY = BotanyProvider.BOTANY;
  	COOKING = CookingProvider.COOKING;
  	DEFENSE = DefenseProvider.DEFENSE;
  	DEMOLITION = DemolitionProvider.DEMOLITION;
    DIGGING = DiggingProvider.DIGGING;
    FARMING = FarmingProvider.FARMING;
    FISHING = FishingProvider.FISHING;
    MELEE = MeleeProvider.MELEE;
    MINING = MiningProvider.MINING;
    RANGED = RangedProvider.RANGED;
    WOODCUTTING = WoodcuttingProvider.WOODCUTTING;

    ALL_SKILLS.add(BLACKSMITHING);
    ALL_SKILLS.add(BOTANY);
    ALL_SKILLS.add(COOKING);
    ALL_SKILLS.add(DEFENSE);
    ALL_SKILLS.add(DEMOLITION);
    ALL_SKILLS.add(DIGGING);
    ALL_SKILLS.add(FARMING);
    ALL_SKILLS.add(FISHING);
    ALL_SKILLS.add(MELEE);
    ALL_SKILLS.add(MINING);
    ALL_SKILLS.add(RANGED);
    ALL_SKILLS.add(WOODCUTTING);

    skillMap.put("blacksmithing", BLACKSMITHING);
		skillMap.put("botany", BOTANY);
		skillMap.put("cooking", COOKING);
		skillMap.put("defense", DEFENSE);
		skillMap.put("demolition", DEMOLITION);
		skillMap.put("digging", DIGGING);
		skillMap.put("farming", FARMING);
		skillMap.put("fishing", FISHING);
		skillMap.put("melee", MELEE);
		skillMap.put("mining", MINING);
		skillMap.put("ranged", RANGED);
		skillMap.put("woodcutting", WOODCUTTING);
  }

}
