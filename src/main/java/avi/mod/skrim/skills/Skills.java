package avi.mod.skrim.skills;

import net.minecraftforge.common.capabilities.Capability;
import avi.mod.skrim.skills.mining.ISkillMining;
import avi.mod.skrim.skills.mining.MiningProvider;

public class Skills {

  public static final Capability<ISkillMining> MINING = MiningProvider.MINING;

}
