package avi.mod.skrim.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.TextureManager;

/**
 * Renders the overlay for the level 100 ranged skill. It's a reference to a skill from borderlands 2 that does the
 * same thing.
 */
public class CriticalAscensionOverlay extends Gui {

  private Minecraft mc;

  public CriticalAscensionOverlay(Minecraft mc) {
    this.mc = mc;
  }

  public void render(int stacks) {
    TextureManager manager = this.mc.getTextureManager();
    ScaledResolution scaledRes = new ScaledResolution(this.mc);
    int xCoord = scaledRes.getScaledWidth() / 2 + 91 + 9 - 10 * 8;
    int yCoord = scaledRes.getScaledHeight() - 39 - GuiUtils.ACCURACY.getHeight();

    manager.bindTexture(GuiUtils.CUSTOM_ICONS);
    GuiUtils.drawIcon(this, xCoord, yCoord, GuiUtils.ACCURACY);
    this.mc.fontRenderer.drawString(String.valueOf(stacks), xCoord + GuiUtils.ACCURACY.getWidth() + 5,
				yCoord + (GuiUtils.ACCURACY.getHeight() / 2), 0xFF000000);
    manager.bindTexture(GuiIngame.ICONS);
  }

}
