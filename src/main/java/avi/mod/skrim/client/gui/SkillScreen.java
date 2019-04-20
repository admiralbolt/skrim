package avi.mod.skrim.client.gui;

import avi.mod.skrim.client.gui.GuiUtils.Icon;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillAbility;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Skill UI!
 */
public class SkillScreen extends GuiScreen {

  //======== Scroll Bar ========//
  private static final int MAX_SCROLL = 435;
  private static final int SCROLL_BAR_WIDTH = 6;
  private static final int SCROLL_BAR_HEIGHT = 40;
  private static final int SCROLL_PADDING_LEFT = 5;
  private static final double SCROLL_SPEED = 0.03;

  //======== Global ========//
  private static final int HEADER_COLOR = 0xFF333333;
  private static final int PADDING_TOP = 5;
  private static final int PADDING_BOTTOM = 5;
  private static final int PADDING_RIGHT = 5;

  //======== Skills ========//
  private static final int TITLE_HEIGHT = 30;
  private static final int TITLE_PADDING_LEFT = 5;
  private static final int DIVIDER_PADDING = 2;
  private static final int DIVIDER_COLOR = 0xFFAAAAAA;
  private static final int SKILL_PADDING_LEFT = 5;
  private static final int SKILL_HEIGHT = 39;
  private static final int SKILL_ICON_SIZE = 32;
  private static final int SKILL_PADDING_DESC = 5;
  private static final int SKILL_PADDING_TOP = 10 + DIVIDER_PADDING;
  private static final int SKILL_HEADER_HEIGHT = 11;

  //======== Abilities ========//
  private static final int ABILITY_ICON_SIZE = 16;
  private static final int ABILITY_ICON_PADDING = 10;

  //========Level Up Bar========//
  private static final int LEVEL_BAR_HEIGHT = 9;
  private static final int LEVEL_BAR_WIDTH =
      176 - PADDING_RIGHT - SCROLL_BAR_WIDTH - SCROLL_PADDING_LEFT - SKILL_PADDING_LEFT;
  private static final int LEVEL_BAR_COLOR = 0x8055dd55;
  private static final int LEVEL_BAR_TEXT_COLOR = 0xFFFFFFFF;

  private GuiButton inventoryTab;
  private int left;
  private int top;
  private int scrollY = 0;
  private int levelTextLeft;
  private int boundTop;
  private int boundBottom;

  private EntityPlayer player;

  public SkillScreen(int left, int top) {
    super();
    this.left = left;
    this.top = top;
    this.boundTop = this.top + 3;
    this.boundBottom = this.top + 165 - 3;
    this.levelTextLeft = this.left + 176 - 55;
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    this.drawDefaultBackground();
    this.mc.getTextureManager().bindTexture(new ResourceLocation("skrim", "textures/guis/skills/background.png"));
    this.drawTexturedModalRect(this.left, this.top, 0, 0, 176, 176);
    super.drawScreen(mouseX, mouseY, partialTicks);
    this.drawScrollBar();
    this.drawTitleBar();
    this.drawSkills(mouseX, mouseY);
  }

  private void drawScrollBar() {
    // How far down the scroll bar is actually scrolled.
    int adjust =
        (int) Math.floor(((double) this.scrollY / MAX_SCROLL) * (176 - SCROLL_BAR_HEIGHT - PADDING_TOP - PADDING_BOTTOM - 10));
    int left = this.left + SCROLL_PADDING_LEFT;
    int top = this.top + PADDING_TOP + adjust;
    int right = left + SCROLL_BAR_WIDTH;
    int bottom = top + SCROLL_BAR_HEIGHT;
    drawRect(left, top, right, bottom, 0xAAAAAAAA);
  }

  private void drawTitleBar() {
    int textLeft = this.left + SCROLL_PADDING_LEFT + SCROLL_BAR_WIDTH + TITLE_PADDING_LEFT;
    int titleTop = this.top + PADDING_TOP - this.scrollY;
    if (shouldRender(titleTop, titleTop + 7)) {
      this.mc.fontRenderer.drawString("Total Skill Level: " + Skills.getTotalSkillLevels(this.player), textLeft,
          titleTop, HEADER_COLOR);
    }
    if (shouldRender(titleTop + 12, titleTop + 19)) {
      this.mc.fontRenderer.drawString("Total Experience Boost: " + Utils.formatPercent(Skills.getTotalXpBonus(this.player)) + "%", textLeft,
          titleTop + 12, HEADER_COLOR);
    }
    if (shouldRender(titleTop + 23, titleTop + 25)) {
      this.drawHorizontalLine(textLeft, textLeft + LEVEL_BAR_WIDTH, titleTop + 24, DIVIDER_COLOR);
    }
  }

  private void drawSkills(int mouseX, int mouseY) {
    List<Integer> topValues = new ArrayList<>();
    List<Skill> skills = new ArrayList<>();
    for (int i = 0; i < Skills.ALL_SKILLS.size(); i++) {
      skills.add((Skill) this.player.getCapability(Skills.ALL_SKILLS.get(i), EnumFacing.NORTH));
      int top = this.top + TITLE_HEIGHT + PADDING_TOP - this.scrollY + i * SKILL_HEIGHT;
      if (i > 0) {
        top += SKILL_PADDING_TOP * i;
      }
      topValues.add(top);
    }
    int left = this.left + SCROLL_PADDING_LEFT + SCROLL_BAR_WIDTH + SKILL_PADDING_LEFT;

    // The z-index of gui elements can't be controlled, instead the order in which they are written determines the
    // z-index. Priority is given to later elements, so things like hover text need to be drawn last.
    for (int q = 0; q <= 5; q++) {
      for (int i = 0; i < skills.size(); i++) {
        Skill skill = skills.get(i);
        int top = topValues.get(i);
        if (q == 0) {
          this.drawSkillHeader(skill, left, top);
        } else if (q == 1) {
          this.drawSkillLevelUp(skill, left, top);
        } else if (q == 2) {
          this.drawSkillIcon(skill, left, top);
        } else if (q == 3) {
          this.drawAbilityIcons(skill, left, top);
        } else if (q == 4) {
          this.drawAbilityHoverText(skill, left, top, mouseX, mouseY);
        } else {
          this.drawSkillHoverText(skill, left, top, mouseX, mouseY);
        }
      }
    }
  }

  /**
   * Draws the title of the skill!
   */
  private void drawSkillHeader(Skill skill, int left, int top) {
    int textLeft = left + SKILL_PADDING_DESC + SKILL_ICON_SIZE;

    if (!this.shouldRender(top, top + 7)) return;
    this.mc.fontRenderer.drawString(skill.name, textLeft, top, HEADER_COLOR);
    this.mc.fontRenderer.drawString("Level " + skill.level, this.levelTextLeft, top, HEADER_COLOR);
  }

  /**
   * Draw the experience / level up bar beneath each skill.
   */
  private void drawSkillLevelUp(Skill skill, int left, int top) {
    int levelTop = top + SKILL_ICON_SIZE + DIVIDER_PADDING;
    int levelRight = left + (int) Math.floor(LEVEL_BAR_WIDTH * skill.getPercentToNext());
    int levelBottom = levelTop + LEVEL_BAR_HEIGHT;

    if (!shouldRender(levelTop, levelBottom)) return;
    drawRect(left, levelTop, levelRight, levelBottom, LEVEL_BAR_COLOR);
    String levelText = skill.getIntXp() + " / " + skill.getNextLevelTotal();
    int levelTextWidth = this.mc.fontRenderer.getStringWidth(levelText);
    int levelTextLeft = left + (LEVEL_BAR_WIDTH / 2) - (int) ((double) levelTextWidth / 2);
    this.mc.fontRenderer.drawString(levelText, levelTextLeft, levelTop + 1, LEVEL_BAR_TEXT_COLOR);
  }

  /**
   * Draw a skill at the target spot. Respects the bound of the skills window so no overflowing will occur.
   */
  private void drawSkillIcon(Skill skill, int left, int top) {
    this.mc.getTextureManager().bindTexture(GuiUtils.SKILL_ICONS);
    GuiUtils.drawIconWithBounds(this, left, top, skill.getIcon(), this.boundTop, this.boundBottom);
  }

  /**
   * Draw the skill description text if the skill is hovered.
   */
  private void drawSkillHoverText(Skill skill, int left, int top, int mouseX, int mouseY) {
    if (!Utils.isPointInRegion(left, top, left + SKILL_ICON_SIZE, top + SKILL_ICON_SIZE, mouseX, mouseY)) return;
    this.drawHoveringText(skill.getToolTip(), mouseX, mouseY);
  }

  /**
   * Draw the ability icons for a given skill.
   */
  private void drawAbilityIcons(Skill skill, int left, int top) {
    int startLeft = left + SKILL_PADDING_DESC + SKILL_ICON_SIZE;
    int abilityTop = top + SKILL_HEADER_HEIGHT;
    this.mc.getTextureManager().bindTexture(GuiUtils.ABILITY_ICONS);

    for (int i = 1; i <= 4; i++) {
      int abilityLeft = startLeft + (i - 1) * (ABILITY_ICON_SIZE + ABILITY_ICON_PADDING);
      Icon icon = skill.getAbilityIcon(i);
      GuiUtils.drawIconWithBounds(this, abilityLeft, abilityTop, icon, this.boundTop, this.boundBottom);
    }
  }

  /**
   * Draw the ability description text if the ability is hovered.
   * <p>
   * The bounding box for each ability is created and tested individually for the given skill.
   */
  private void drawAbilityHoverText(Skill skill, int left, int top, int mouseX, int mouseY) {
    int startLeft = left + SKILL_PADDING_DESC + SKILL_ICON_SIZE;
    int abilityTop = top + SKILL_HEADER_HEIGHT;
    int abilityBottom = abilityTop + ABILITY_ICON_SIZE;

    for (int i = 1; i <= 4; i++) {
      int abilityLeft = startLeft + (i - 1) * (ABILITY_ICON_SIZE + ABILITY_ICON_PADDING);
      int abilityRight = abilityLeft + ABILITY_ICON_SIZE;
      if (!Utils.isPointInRegion(abilityLeft, abilityTop, abilityRight, abilityBottom, mouseX, mouseY)) continue;
      this.drawHoveringText(SkillAbility.getAbilityTooltip(skill.getAbility(i), skill.hasAbility(i)), mouseX, mouseY);
    }
  }

  private boolean shouldRender(int top, int bottom) {
    return (top > this.boundTop && this.boundBottom > bottom);
  }

  @Override
  public boolean doesGuiPauseGame() {
    return false;
  }

  @Override
  public void initGui() {
    this.inventoryTab = new GuiButton(1995, this.left, this.top - 20 - 1, 176, 20, "Inventory");
    this.buttonList.add(this.inventoryTab);
    this.player = Minecraft.getMinecraft().player;
  }

  /**
   * Handles button clicks, in this case there's only the inventory button.
   */
  @Override
  protected void actionPerformed(GuiButton button) {
    if (button == this.inventoryTab) {
      this.mc.displayGuiScreen(new CustomGuiInventory(Minecraft.getMinecraft().player));
    }
  }

  /**
   * Swap back to the inventory screen if the inventory button is pressed.
   */
  @Override
  protected void keyTyped(char typedChar, int keyCode) throws IOException {
    if (this.mc.gameSettings.keyBindInventory.getKeyCode() == keyCode) {
      this.mc.displayGuiScreen(null);
      if (this.mc.currentScreen == null) this.mc.setIngameFocus();
    }
    super.keyTyped(typedChar, keyCode);
  }

  @Override
  public void handleMouseInput() throws IOException {
    int i = Mouse.getEventDWheel();
    if (i != 0) {
      // Scrolling DOWN is a negative number. Scrolling UP is a positive number. We scroll based on the scroll speed,
      // and then keep the resulting scroll position in bounds.
      this.scrollY -= (int) Math.floor(SCROLL_SPEED * i);
      this.scrollY = Math.max(Math.min(this.scrollY, MAX_SCROLL), 0);
    }
    super.handleMouseInput();
  }

}
