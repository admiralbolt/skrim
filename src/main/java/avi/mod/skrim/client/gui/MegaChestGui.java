package avi.mod.skrim.client.gui;

import avi.mod.skrim.inventory.MegaChestContainer;
import avi.mod.skrim.network.SkrimPacketHandler;
import avi.mod.skrim.network.SortChestPacket;
import avi.mod.skrim.tileentity.MegaChestTileEntity;
import avi.mod.skrim.utils.Utils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import java.io.IOException;

/**
 * The UI for the MegaChest!
 * <p>
 * Take a look at GuiChest.java for where this stuff comes from.
 */
public class MegaChestGui extends GuiContainer {

  // The megachest UI is loaded from two different texture files (since it's so large).
  // These values correspond to the height / width of the first & second texture files respectively.
  private static final int NW_X = 256;
  private static final int NW_Y = 256;
  private static final int NE_X = 172;
  private static final int NE_Y = 256;

  private static final int PANEL_WIDTH = 120;

  private IInventory playerInventory;
  private MegaChestTileEntity entity;
  private String title;
  private GuiButton sortButton;

  private static String[] CHEST_TITLES = {
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

  public MegaChestGui(IInventory playerInventory, MegaChestTileEntity entity) {
    super(new MegaChestContainer(playerInventory, entity));

    this.playerInventory = playerInventory;
    this.entity = entity;

    this.xSize = 428;
    this.ySize = 256;
    this.title = CHEST_TITLES[Utils.rand.nextInt(CHEST_TITLES.length)];
  }

  @Override
  public void initGui() {
    super.initGui();
    sortButton = new GuiButton(1501, this.guiLeft + 7, this.guiTop + 9 * 19 + 28, PANEL_WIDTH, 20, "Sort");
    this.buttonList.add(sortButton);
  }

  @Override
  public void actionPerformed(GuiButton button) throws IOException {
    if (button == this.sortButton) {
      BlockPos pos = entity.getPos();
      SkrimPacketHandler.INSTANCE.sendToServer(new SortChestPacket(pos.getX(), pos.getY(), pos.getZ()));
    } else {
      super.actionPerformed(button);
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

    int left = this.guiLeft;
    int top = this.guiTop;

    this.mc.getTextureManager().bindTexture(new ResourceLocation("skrim:textures/guis/containers/mega_chest_nw.png"));
    this.drawTexturedModalRect(left, top, 0, 0, NW_X, NW_Y);

    this.mc.getTextureManager().bindTexture(new ResourceLocation("skrim:textures/guis/containers/mega_chest_ne.png"));
    this.drawTexturedModalRect(left + NW_X, top, 0, 0, NE_X, NE_Y);
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    int i = 0;
    for (String line : this.fontRenderer.listFormattedStringToWidth(this.title, PANEL_WIDTH)) {
      this.fontRenderer.drawString(line, 69 - this.fontRenderer.getStringWidth(line) / 2, 9 * 19 + 1 + i * 9, 4210752);
      i++;
    }
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    this.drawDefaultBackground();
    super.drawScreen(mouseX, mouseY, partialTicks);
    this.renderHoveredToolTip(mouseX, mouseY);
  }

}
