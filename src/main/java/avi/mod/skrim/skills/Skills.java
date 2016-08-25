package avi.mod.skrim.skills;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.capabilities.Capability;
import avi.mod.skrim.skills.ISkill;
import avi.mod.skrim.skills.botany.BotanyProvider;
import avi.mod.skrim.skills.botany.ISkillBotany;
import avi.mod.skrim.skills.cooking.CookingProvider;
import avi.mod.skrim.skills.cooking.ISkillCooking;
import avi.mod.skrim.skills.digging.ISkillDigging;
import avi.mod.skrim.skills.digging.DiggingProvider;
import avi.mod.skrim.skills.farming.FarmingProvider;
import avi.mod.skrim.skills.farming.ISkillFarming;
import avi.mod.skrim.skills.fishing.FishingProvider;
import avi.mod.skrim.skills.fishing.ISkillFishing;
import avi.mod.skrim.skills.mining.ISkillMining;
import avi.mod.skrim.skills.mining.MiningProvider;
import avi.mod.skrim.skills.smelting.ISkillSmelting;
import avi.mod.skrim.skills.smelting.SmeltingProvider;
import avi.mod.skrim.skills.woodcutting.ISkillWoodcutting;
import avi.mod.skrim.skills.woodcutting.WoodcuttingProvider;

public class Skills {

  public static final Capability<ISkillDigging> DIGGING = DiggingProvider.DIGGING;
  public static final Capability<ISkillMining> MINING = MiningProvider.MINING;
  public static final Capability<ISkillWoodcutting> WOODCUTTING = WoodcuttingProvider.WOODCUTTING;
  public static final Capability<ISkillFarming> FARMING = FarmingProvider.FARMING;
  public static final Capability<ISkillBotany> BOTANY = BotanyProvider.BOTANY;
  public static final Capability<ISkillCooking> COOKING = CookingProvider.COOKING;
  public static final Capability<ISkillSmelting> SMELTING = SmeltingProvider.SMELTING;
  public static final Capability<ISkillFishing> FISHING = FishingProvider.FISHING;

  public static final List<Capability<? extends ISkill>> ALL_SKILLS;
  static {
    ALL_SKILLS = new ArrayList<Capability<? extends ISkill>>();
    ALL_SKILLS.add(MINING);
    ALL_SKILLS.add(WOODCUTTING);
    ALL_SKILLS.add(DIGGING);
    ALL_SKILLS.add(FARMING);
    ALL_SKILLS.add(BOTANY);
    ALL_SKILLS.add(COOKING);
    ALL_SKILLS.add(SMELTING);
    ALL_SKILLS.add(FISHING);
  }

}
