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
	private String title;
	
	public MegaChestGui(IInventory playerInventory, MegaChestTileEntity entity) {
		super(new MegaChestContainerTileEntity(playerInventory, entity));
		
		this.playerInventory = playerInventory;
		this.entity = entity;
				
		
		this.xSize = 428;
		this.ySize = 256;
		this.title = GuiUtils.getRandomChestText();
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
	    this.fontRenderer.drawString(this.title, 89 - this.fontRenderer.getStringWidth(this.title) / 2, 6, 4210752);
	    this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 9, 108, 4210752);
	}

}
