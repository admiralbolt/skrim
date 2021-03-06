package avi.mod.skrim;

import avi.mod.skrim.proxy.ServerProxy;
import net.minecraftforge.common.config.Configuration;

public class Config {

  private static final String CATEGORY_GENERAL = "general";
  private static final String CATEGORY_DIMENSIONS = "dimensions";

  public static boolean isThisAGoodTutorial = true;
  public static String yourRealName = "Steve";
  public static int dimensionId = 100;

  public static void readConfig() {
    Configuration cfg = ServerProxy.config;
    try {
      cfg.load();
      initGeneralConfig(cfg);
      initDimensionConfig(cfg);
    } catch (Exception e1) {
      e1.printStackTrace();
    } finally {
      if (cfg.hasChanged()) {
        cfg.save();
      }
    }
  }

  private static void initGeneralConfig(Configuration cfg) {
    cfg.addCustomCategoryComment(CATEGORY_GENERAL, "General configuration");
    isThisAGoodTutorial = cfg.getBoolean("goodTutorial", CATEGORY_GENERAL, isThisAGoodTutorial, "Set to false if you don't like this tutorial");
    yourRealName = cfg.getString("realName", CATEGORY_GENERAL, yourRealName, "Set your real name here");
  }

  private static void initDimensionConfig(Configuration cfg) {
    cfg.addCustomCategoryComment(CATEGORY_DIMENSIONS, "Dimension configuration");
    dimensionId = cfg.getInt("dimensionId", CATEGORY_DIMENSIONS, dimensionId, -1000, 1000, "The Id to use for the dimension");
  }

}
