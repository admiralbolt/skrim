package avi.mod.skrim.client.gui;

import avi.mod.skrim.advancements.SkrimAdvancements;
import avi.mod.skrim.network.AdvancementPacket;
import avi.mod.skrim.network.SkrimPacketHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;

/**
 * Add the Skills button to the players inventory!
 */
public class CustomGuiInventory extends GuiInventory {

  private static final int SKILL_BUTTON_WIDTH = 176;
  private static final int SKILL_BUTTON_HEIGHT = 20;

  private GuiButton skillTab;
  private EntityPlayer player;

  public CustomGuiInventory(EntityPlayer player) {
    super(player);
    this.player = player;
  }

  @Override
  public void initGui() {
    super.initGui();
    this.skillTab = new GuiButton(1337, this.guiLeft, this.guiTop - SKILL_BUTTON_HEIGHT - 1, SKILL_BUTTON_WIDTH,
        SKILL_BUTTON_HEIGHT,
        "Skills");
    this.buttonList.add(this.skillTab);
  }

  @Override
  public void actionPerformed(GuiButton button) throws IOException {
    if (button == this.skillTab) {
      SkrimPacketHandler.INSTANCE.sendToServer(new AdvancementPacket(SkrimAdvancements.FOUND_SKILLS.name));
      this.mc.displayGuiScreen(new SkillScreen(this.guiLeft, this.guiTop));
      if (this.mc.currentScreen == null) this.mc.setIngameFocus();
    }
    super.actionPerformed(button);
  }

}
