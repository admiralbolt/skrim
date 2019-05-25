package avi.mod.skrim.advancements;

import avi.mod.skrim.Skrim;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;

/**
 * All custom advancements will be registered here. The advancements that don't need a custom trigger can be done purely
 * in json. Look at the advancements/ folder under resources. Even advancements that need a custom trigger will still
 * need a json file defined there. Make sure the NAME matches exactly what you register the advancement as.
 * <p>
 * Also take a look at: https://github.com/skylinerw/guides/blob/master/java/advancements.md, great guide with a bunch
 * of info about advancements.
 */
public class SkrimAdvancements {

  public static final HashMap<String, CustomAdvancement> ADVANCEMENTS_BY_NAME = new HashMap<>();

  // Granted when the player finds the skills tab. See CustomGuiInventory.java.
  public static CustomAdvancement FOUND_SKILLS = registerAdvancement("found_skills");

  // Granted when the player gets any skill to level 25, 50, 75, and 100 respectively.
  public static CustomAdvancement DING_APPRENTICE = registerAdvancement("ding_apprentice");
  public static CustomAdvancement DING_JOURNEYMAN = registerAdvancement("ding_journeyman");
  public static CustomAdvancement DING_EXPERT = registerAdvancement("ding_expert");
  public static CustomAdvancement DING_MASTER = registerAdvancement("ding_master");

  // Recipes granted when a player hits an appropriate level.
  public static CustomAdvancement HAND_SAW = registerAdvancement("woodcutting_25");
  public static CustomAdvancement LEAF_ARMOR = registerAdvancement("woodcutting_75");

  private static CustomAdvancement registerAdvancement(String name) {
    CustomAdvancement advancement = new CustomAdvancement(name);
    ADVANCEMENTS_BY_NAME.put(name, advancement);
    return advancement;
  }

  public static void register() {
    for (SkrimAdvancements.CustomAdvancement advancement : SkrimAdvancements.ADVANCEMENTS_BY_NAME.values()) {
      CriteriaTriggers.register(advancement.trigger);
    }
  }


  /**
   * A class for holding advancements. Advancements can be granted to a player by calling the grant() function.
   */
  public static class CustomAdvancement {

    public String name;
    public CustomTrigger trigger;

    private CustomAdvancement(String name) {
      this.name = name;
      this.trigger = new CustomTrigger(new ResourceLocation(Skrim.MOD_ID, name));
    }

    public void grant(EntityPlayerMP player) {
      this.trigger.trigger(player);
    }

  }

}
