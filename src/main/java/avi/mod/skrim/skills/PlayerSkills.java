package avi.mod.skrim.skills;

import avi.mod.skrim.skills.botany.BotanyProvider;
import avi.mod.skrim.skills.cooking.CookingProvider;
import avi.mod.skrim.skills.digging.DiggingProvider;
import avi.mod.skrim.skills.fishing.FishingProvider;
import avi.mod.skrim.skills.farming.FarmingProvider;
import avi.mod.skrim.skills.mining.MiningProvider;
import avi.mod.skrim.skills.smelting.SmeltingProvider;
import avi.mod.skrim.skills.woodcutting.WoodcuttingProvider;

public class PlayerSkills {

  public static void register() {
    MiningProvider.register();
    WoodcuttingProvider.register();
    DiggingProvider.register();
    FarmingProvider.register();
    BotanyProvider.register();
    CookingProvider.register();
    SmeltingProvider.register();
    FishingProvider.register();
  }

}
