package avi.mod.skrim.client.gui;

import avi.mod.skrim.skills.Skill;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Map;

public class GuiUtils {

  private static ResourceLocation DEFAULT_SKILL_ICON = new ResourceLocation("skrim:textures/guis/skills/default_skill.png");
  private static ResourceLocation DEFAULT_ABILITY_ICON = new ResourceLocation("skrim:textures/guis/skills/default_ability.png");
  public static ResourceLocation CUSTOM_ICONS = new ResourceLocation("skrim:textures/guis/overlays/custom_icons.png");

  // First row
  public static Icon EXTRA_ARMOR_HALF = new Icon("extra_armor_half", 0, 0, 9, 9);
  public static Icon EXTRA_ARMOR_FULL = new Icon("extra_armor_full", 9, 0, 9, 9);
  public static Icon ACCURACY = new Icon("accuracy", 108, 0, 16, 16);

  // Look at the textures in textures/guis/skills/ for an explanation of these numbers.
  private static Icon SKILL_ICON = new Icon("skill", 0, 0, 32, 32);
  private static Icon SKILL_ICON_DISABLED = new Icon("skill_disabled", 32, 0, 32, 32);
  private static Icon ABILITY_ICON_1 = new Icon("ability_1", 0, 32, 16, 16);
  private static Icon ABILITY_ICON_1_LOCKED = new Icon("ability_1_locked", 16, 32, 16, 16);
  private static Icon ABILITY_ICON_1_DISABLED = new Icon("ability_1_disabled", 32, 32, 16, 16);
  private static Icon ABILITY_ICON_2 = new Icon("ability_2", 0, 48, 16, 16);
  private static Icon ABILITY_ICON_2_LOCKED = new Icon("ability_2_locked", 16, 48, 16, 16);
  private static Icon ABILITY_ICON_2_DISABLED = new Icon("ability_2_disabled", 32, 48, 16, 16);
  private static Icon ABILITY_ICON_3 = new Icon("ability_3", 0, 64, 16, 16);
  private static Icon ABILITY_ICON_3_LOCKED = new Icon("ability_3_locked", 16, 64, 16, 16);
  private static Icon ABILITY_ICON_3_DISABLED = new Icon("ability_3_disabled", 32, 64, 16, 16);
  private static Icon ABILITY_ICON_4 = new Icon("ability_4", 0, 80, 16, 16);
  private static Icon ABILITY_ICON_4_LOCKED = new Icon("ability_4_locked", 16, 80, 16, 16);
  private static Icon ABILITY_ICON_4_DISABLED = new Icon("ability_4_disabled", 32, 80, 16, 16);

  private static Map<Integer, Triple<Icon, Icon, Icon>> ABILITY_ICON_MAP = ImmutableMap.<Integer, Triple<Icon, Icon, Icon>>builder()
      .put(1, Triple.of(ABILITY_ICON_1, ABILITY_ICON_1_LOCKED, ABILITY_ICON_1_DISABLED))
      .put(2, Triple.of(ABILITY_ICON_2, ABILITY_ICON_2_LOCKED, ABILITY_ICON_2_DISABLED))
      .put(3, Triple.of(ABILITY_ICON_3, ABILITY_ICON_3_LOCKED, ABILITY_ICON_3_DISABLED))
      .put(4, Triple.of(ABILITY_ICON_4, ABILITY_ICON_4_LOCKED, ABILITY_ICON_4_DISABLED))
      .build();

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

  public static void drawSkillIconWithBounds(Gui gui, Skill skill, int xCoord, int yCoord, int boundTop, int boundBottom) {
    drawIconWithBounds(gui, xCoord, yCoord, SKILL_ICON, boundTop, boundBottom);
  }

  public static void drawAbilityIconWithBounds(Gui gui, Skill skill, int i, int xCoord, int yCoord, int boundTop, int boundBottom) {
    Triple<Icon, Icon, Icon> icons = ABILITY_ICON_MAP.get(i);
    Icon icon = skill.hasAbility(i) ? skill.abilityEnabled(i) ? icons.getLeft() : icons.getRight() : icons.getMiddle();
    drawIconWithBounds(gui, xCoord, yCoord, icon, boundTop, boundBottom);
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
