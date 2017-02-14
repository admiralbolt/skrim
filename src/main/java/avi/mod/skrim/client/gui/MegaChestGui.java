package avi.mod.skrim.client.gui;

import avi.mod.skrim.tileentity.MegaChestContainerTileEntity;
import avi.mod.skrim.tileentity.MegaChestTileEntity;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class MegaChestGui extends GuiContainer {
	
	private IInventory playerInventory;
	private MegaChestTileEntity entity;
	
	public MegaChestGui(IInventory playerInventory, MegaChestTileEntity entity) {
		super(new MegaChestContainerTileEntity(playerInventory, entity));
		
		this.playerInventory = playerInventory;
		this.entity = entity;
		
		this.xSize = 176;
		this.ySize = 166;
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
	    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
	    this.mc.getTextureManager().bindTexture(new ResourceLocation("skrim:textures/guis/containers/fucked_up_container.png"));
	    this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		System.out.println("this.entity.getDisplayName();: " + this.entity.getDisplayName());
	    // String s = this.entity.getDisplayName().getUnformattedText();
		String s = "TEST";
	    this.fontRenderer.drawString(s, 88 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);            //#404040
	    this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, 72, 4210752);      //#404040
	}

}
