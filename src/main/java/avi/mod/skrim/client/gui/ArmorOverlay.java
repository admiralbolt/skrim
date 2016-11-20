package avi.mod.skrim.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

public class ArmorOverlay extends Gui {
	
	private Minecraft mc;
	
	public ArmorOverlay(Minecraft mc) {
		this.mc = mc;
	}

	public void render() {
		TextureManager manager = this.mc.getTextureManager();
		EntityPlayer entityplayer = (EntityPlayer) this.mc.getRenderViewEntity();
    IAttributeInstance iattributeinstance = entityplayer.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
		int absorptionAmount = MathHelper.ceiling_float_int(entityplayer.getAbsorptionAmount());
		ScaledResolution scaledRes = new ScaledResolution(this.mc);
		int l = scaledRes.getScaledWidth() / 2 - 91;
    int i1 = scaledRes.getScaledWidth() / 2 + 91;
    int j1 = scaledRes.getScaledHeight() - 39;
    float f = (float) iattributeinstance.getAttributeValue();
    int k1 = MathHelper.ceiling_float_int(entityplayer.getAbsorptionAmount());

    int i2 = 10;
    int yCoord = j1 - i2 - 10 + 9;
    
        
    int k2 = j1 - 10;
    int l2 = k1;
    int armor = entityplayer.getTotalArmorValue();
    int extraArmor = armor - 20;
    int j3 = -1;
        
    for (int iconPos = 0; iconPos < 10; iconPos++) {
			if (armor > 0) {
				int xCoord = l + iconPos * 8;
				int checkVal = iconPos * 2 + 1;
				
				if (checkVal < armor) {
					if (extraArmor > 0) {
						if (checkVal < extraArmor) {
							this.drawCustomArmorIcon(manager, xCoord, yCoord, true);
						} else if (checkVal == extraArmor) {
							this.drawCustomArmorIcon(manager, xCoord, yCoord, false);
						} else {
							this.drawTexturedModalRect(xCoord, yCoord, 34, 9, 9, 9);
						}
					} else {
						this.drawTexturedModalRect(xCoord, yCoord, 34, 9, 9, 9);
					}
				} else if (checkVal == armor) {
					this.drawTexturedModalRect(xCoord, yCoord, 25, 9, 9, 9);
				} else if (checkVal > armor) {
					this.drawTexturedModalRect(xCoord, yCoord, 16, 9, 9, 9);
				}
				
			}
		}
	}
	
	public void drawCustomArmorIcon(TextureManager manager, int xCoord, int yCoord, boolean fullIcon) {
		manager.bindTexture(GuiUtils.CUSTOM_ICONS);
		GuiUtils.Icon drawIcon = (fullIcon) ? GuiUtils.EXTRA_ARMOR_FULL : GuiUtils.EXTRA_ARMOR_HALF;
		this.drawTexturedModalRect(xCoord, yCoord, drawIcon.getX(), drawIcon.getY(), drawIcon.getWidth(), drawIcon.getHeight());
		manager.bindTexture(GuiIngame.ICONS);
	}

}
