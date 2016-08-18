package avi.mod.skrim.client.gui;

import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class SkillScreen extends GuiScreen {

  private GuiButton inventoryTab;
  private int left;
  private int top;

  public SkillScreen(int left, int top) {
    super();
    this.left = left;
    this.top = top;
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    this.drawDefaultBackground();
    this.mc.getTextureManager().bindTexture(new ResourceLocation("skrim", "textures/guis/skill_background.png"));
    this.drawTexturedModalRect(this.left, this.top, 0, 0, 176, 176);
    super.drawScreen(mouseX, mouseY, partialTicks);
  }

  @Override
  public boolean doesGuiPauseGame() {
    return false;
  }

  @Override
  public void initGui() {
    this.inventoryTab = new GuiButton(1995, this.left, this.top - 20 - 1, 176, 20, "Inventory");
    this.buttonList.add(this.inventoryTab);
  }

  @Override
  protected void actionPerformed(GuiButton button) throws IOException {
    if (button == this.inventoryTab) {
      this.mc.displayGuiScreen(new CustomGuiInventory(Minecraft.getMinecraft().thePlayer));
    }
  }

  @Override
  protected void keyTyped(char typedChar, int keyCode) throws IOException {
    if (this.mc.gameSettings.keyBindInventory.getKeyCode() == keyCode) {
      this.mc.displayGuiScreen(null);
      if (this.mc.currentScreen == null) {
        this.mc.setIngameFocus();
      }
    }
    super.keyTyped(typedChar, keyCode);
  }

}
