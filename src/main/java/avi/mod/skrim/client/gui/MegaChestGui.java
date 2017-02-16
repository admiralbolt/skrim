package avi.mod.skrim.client.gui;

import java.io.IOException;

import avi.mod.skrim.inventory.MegaChestContainer;
import avi.mod.skrim.network.AchievementPacket;
import avi.mod.skrim.network.SkrimPacketHandler;
import avi.mod.skrim.network.SortChestPacket;
import avi.mod.skrim.stats.SkrimAchievements;
import avi.mod.skrim.tileentity.MegaChestTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class MegaChestGui extends GuiContainer {

	private IInventory playerInventory;
	private MegaChestTileEntity entity;
	private String title;
	private GuiButton sortButton;

	public MegaChestGui(IInventory playerInventory, MegaChestTileEntity entity) {
		super(new MegaChestContainer(playerInventory, entity));

		this.playerInventory = playerInventory;
		this.entity = entity;

		this.xSize = 428;
		this.ySize = 256;
		this.title = GuiUtils.getRandomChestText();
	}
	
	@Override
	public void initGui() {
		super.initGui();
		sortButton = new GuiButton(1501, this.guiLeft + 7, this.guiTop + 9 * 19 + 14, 120, 20, "Sort");
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

		int nwX = 256;
		int nwY = 256;
		int neX = 172;
		int neY = 256;

		int left = this.guiLeft;
		int top = this.guiTop;

		this.mc.getTextureManager().bindTexture(new ResourceLocation("skrim:textures/guis/containers/mega_chest_nw.png"));
		this.drawTexturedModalRect(left, top, 0, 0, nwX, nwY);

		this.mc.getTextureManager().bindTexture(new ResourceLocation("skrim:textures/guis/containers/mega_chest_ne.png"));
		this.drawTexturedModalRect(left + nwX, top, 0, 0, neX, neY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.fontRenderer.drawString(this.title, 69 - this.fontRenderer.getStringWidth(this.title) / 2, 9 * 19 + 1, 4210752);
	}

}
