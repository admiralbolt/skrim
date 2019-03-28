package avi.mod.skrim.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.EntityPlayer;

/**
 * This is an overlay that draws blue armor icons if you have more than 20 armor. The blue armor icons are drawn
 * starting from the left-most part of the bar overwriting the gray armor icons. This replaces the default armor
 * rendering, so logic for armor <= 20 should remain the same.
 * <p>
 * This is mostly copied from GuiIngame.java, specifically from the renderPlayerStats()
 * method. Since this is mostly copied code, I'm not really positive how it works, so
 * if this breaks, good fuckin' luck.
 */
public class ArmorOverlay extends Gui {

  private Minecraft mc;

  public ArmorOverlay(Minecraft mc) {
    this.mc = mc;
  }

  public void render() {
    TextureManager manager = this.mc.getTextureManager();
    EntityPlayer entityplayer = (EntityPlayer) this.mc.getRenderViewEntity();
    ScaledResolution scaledRes = new ScaledResolution(this.mc);
    // I have no clue what's going on with this math here.
    int l = scaledRes.getScaledWidth() / 2 - 91;
    int j1 = scaledRes.getScaledHeight() - 39;
    int i2 = 10;
    int yCoord = j1 - i2 - 10 + 9;


    int armor = entityplayer.getTotalArmorValue();
    if (armor <= 0) return;

    int extraArmor = armor - 20;

    /**
     * This logic is a *little* complicated but we basically iterate through each icon position and we need to decide
     * which type of icon to draw: full blue, half blue, full gray, half gray, or empty armor. For example, if we want
     * to render someone with 24 armor (aka full obsidian) we want to render two full blue armor icons, then 8 full
     * gray armor icons. This is done through some maths, in particular the checkVal (iconPos * 2 + 1) is useful in
     * determining which type of icon should be rendered.
     */
    for (int iconPos = 0; iconPos < 10; iconPos++) {
      int xCoord = l + iconPos * 8;
      int checkVal = iconPos * 2 + 1;

      if (checkVal < armor) {
        if (extraArmor > 0 && checkVal <= extraArmor) {
          this.drawCustomArmorIcon(manager, xCoord, yCoord, checkVal < extraArmor);
          continue;
        }
        this.drawArmorIcon(xCoord, yCoord, ArmorIconType.FULL);
        continue;
      }

      if (checkVal == armor) {
        this.drawArmorIcon(xCoord, yCoord, ArmorIconType.HALF);
      } else {
        this.drawArmorIcon(xCoord, yCoord, ArmorIconType.EMPTY);
      }

    }
  }

  /**
   * Helper function to draw the blue armor icons. See guis/overlays/custom_icons.png
   */
  private void drawCustomArmorIcon(TextureManager manager, int xCoord, int yCoord, boolean fullIcon) {
    manager.bindTexture(GuiUtils.CUSTOM_ICONS);
    GuiUtils.Icon drawIcon = (fullIcon) ? GuiUtils.EXTRA_ARMOR_FULL : GuiUtils.EXTRA_ARMOR_HALF;
    this.drawTexturedModalRect(xCoord, yCoord, drawIcon.getX(), drawIcon.getY(), drawIcon.getWidth(),
        drawIcon.getHeight());
    manager.bindTexture(GuiIngame.ICONS);
  }

  /**
   * Draws a normal armor icon. The texture locations are hard-coded to their correct values for full, half, and
   * empty armor icons.
   */
  private void drawArmorIcon(int xCoord, int yCoord, ArmorIconType iconType) {
    if (iconType == ArmorIconType.FULL) {
      this.drawTexturedModalRect(xCoord, yCoord, 34, 9, 9, 9);
    } else if (iconType == ArmorIconType.HALF) {
      this.drawTexturedModalRect(xCoord, yCoord, 25, 9, 9, 9);
    } else {
      this.drawTexturedModalRect(xCoord, yCoord, 16, 9, 9, 9);
    }
  }

  private enum ArmorIconType {
    FULL,
    HALF,
    EMPTY
  }

}
