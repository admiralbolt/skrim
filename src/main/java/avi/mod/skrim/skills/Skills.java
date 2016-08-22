package avi.mod.skrim.skills;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.capabilities.Capability;
import avi.mod.skrim.skills.ISkill;
import avi.mod.skrim.skills.mining.ISkillMining;
import avi.mod.skrim.skills.mining.MiningProvider;

public class Skills {

  public static final Capability<ISkillMining> MINING = MiningProvider.MINING;

  public static final List<Capability<? extends ISkill>> ALL_SKILLS;
  static {
    ALL_SKILLS = new ArrayList<Capability<? extends ISkill>>();
    ALL_SKILLS.add(MINING);
  }

}
