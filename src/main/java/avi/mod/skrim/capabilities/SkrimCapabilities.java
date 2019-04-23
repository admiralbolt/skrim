package avi.mod.skrim.capabilities;

import avi.mod.skrim.capabilities.maxhealth.CapabilityMaxHealth;
import avi.mod.skrim.skills.Skills;

public class SkrimCapabilities {

  public static void registerCapabilities() {
    Skills.register();
    CapabilityMaxHealth.register();
  }

}
