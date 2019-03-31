package avi.mod.skrim.client.gui;

import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.utils.Utils;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class GuiUtils {

  public static ResourceLocation CUSTOM_ICONS = new ResourceLocation("skrim:textures/guis/overlays/custom_icons.png");
  public static ResourceLocation ABILITY_ICONS = new ResourceLocation("skrim:textures/guis/skills/skill_abilities.png");
  public static ResourceLocation SKILL_ICONS = new ResourceLocation("skrim:textures/guis/skills/skills.png");
  // First row
  public static Icon EXTRA_ARMOR_HALF = new Icon("extra_armor_half", 0, 0, 9, 9);
  public static Icon EXTRA_ARMOR_FULL = new Icon("extra_armo_full", 9, 0, 9, 9);
  public static Icon ACCURACY = new Icon("accuracy", 108, 0, 16, 16);

  private static String[] RANDOM_CHEST_TITLES = {
      "Mo chest mo problems",
      "Mega Chest",
      "Chest O'Mega",
      "Slightly larger than average chest",
      "Why do you keep feeding me garbage?",
      "Cobblestone Hotel",
      "Big Ass-Chest",
      "Just a normal chest, move along",
      "M E G A C H E S T"
  };

  // Tracks the location of skill & ability icons in their respective textures.
  private static Map<String, Integer> SKILL_ABILITY_X = new HashMap<>();
  private static Map<String, Integer> SKILL_ABILITY_Y = new HashMap<>();
  private static Map<String, Integer> SKILL_X = new HashMap<>();
  private static Map<String, Integer> SKILL_Y = new HashMap<>();

  /**
   * Looking at the textures makes this easier to understand:
   *   textures/guis/skills/skill_abilities.png
   *   textures/guis/skills/skills.png
   * Each skill has 4 abilities that are 16x16 icons. So, each skill has a 16x64 block of pixels corresponding to its
   * abilities. Since the texture is 256 pixels wide, we can fit 4 sets of skill abilities per row.
   *
   * Each skill icon is 32x32, so we can fit 8 skills per row.
   */
  static {
    int i = 0;
    for (String skillName : Skills.ALPHABETICAL_SKILLS) {
      SKILL_ABILITY_X.put(skillName, (i % 4) * 64);
      SKILL_ABILITY_Y.put(skillName, (i / 4) * 16);
      SKILL_X.put(skillName, (i % 8) * 32);
      SKILL_Y.put(skillName, (i / 8) * 32);
      i++;
    }
  }

  /**
   * Gets the correct location of an ability icon based on skill & level.
   * <p>
   * textures/guis/skills/skill_abilities.png contains all the icons for the skill abilities. Texture file is a grid
   * of 16x16 icons with the grayscale versions being 3 rows (48 pixels) lower than the full color version.
   */
  public static Icon getAbilityIcon(String skillName, int level, boolean unlocked) {
    return new Icon(skillName + "_" + level, SKILL_ABILITY_X.get(skillName) + (level - 1) * 16,
        SKILL_ABILITY_Y.get(skillName) + ((unlocked) ? 0 : 48), 16, 16);
  }

  /**
   * Gets the correct location of a skill icon.
   * <p>
   * textures/guis/skills/skills.png contains all the icons for the skills themselves. Texture file is a grid of
   * 32x32 icons.
   */
  public static Icon getSkillIcon(String skillName) {
    return new Icon(skillName + "_icon", SKILL_X.get(skillName), SKILL_Y.get(skillName), 32, 32);
  }

  /**
   * Helper method for drawing icons with slightly less parameters.
   */
  public static void drawIcon(Gui gui, int xCoord, int yCoord, Icon icon) {
    gui.drawTexturedModalRect(xCoord, yCoord, icon.getX(), icon.getY(), icon.getWidth(), icon.getHeight());
  }

  /**
   * Draws an icon with the given bounds. Used to correctly draw icons when the skills UI is partially scrolled.
   */
  public static void drawIconWithBounds(Gui gui, int xCoord, int yCoord, Icon icon, int boundTop, int boundBottom) {
    if (yCoord < boundTop && (yCoord + icon.getHeight() > boundTop)) {
      int start = boundTop - yCoord;
      int dist = icon.getHeight() - start;
      gui.drawTexturedModalRect(xCoord, boundTop, icon.getX(), icon.getY() + start, icon.getWidth(), dist);
    } else if (boundBottom > yCoord && (yCoord + icon.getHeight() > boundBottom)) {
      int dist = boundBottom - yCoord;
      gui.drawTexturedModalRect(xCoord, yCoord, icon.getX(), icon.getY(), icon.getWidth(), dist);
    } else if ((yCoord + icon.getHeight()) > boundTop && boundBottom > yCoord) {
      gui.drawTexturedModalRect(xCoord, yCoord, icon.getX(), icon.getY(), icon.getWidth(), icon.getHeight());
    }
  }

  public static String getRandomChestText() {
    int aynRandom = Utils.rand.nextInt(RANDOM_CHEST_TITLES.length);
    return RANDOM_CHEST_TITLES[aynRandom];
  }

  /**
   * Helper class to keep track of ~10 billion icons.
   */
  public static class Icon {

    private String name;
    private int xStart;
    private int yStart;
    private int width;
    private int height;

    public Icon(String name, int xStart, int yStart, int width, int height) {
      this.name = name;
      this.xStart = xStart;
      this.yStart = yStart;
      this.width = width;
      this.height = height;
    }

    public String toString() {
      return "(" + this.name + ")[" + this.xStart + ", " + this.yStart + ", " + this.width + ", " + this.height + "]";
    }

    public int getX() {
      return this.xStart;
    }

    public int getY() {
      return this.yStart;
    }

    public int getWidth() {
      return this.width;
    }

    public int getHeight() {
      return this.height;
    }

  }

}
