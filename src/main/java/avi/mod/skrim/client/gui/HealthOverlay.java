package avi.mod.skrim.client.gui;

import avi.mod.skrim.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.FoodStats;
import net.minecraft.util.math.MathHelper;

public class HealthOverlay extends Gui {
	
	private Minecraft mc;
	protected long healthUpdateCounter;
  protected int updateCounter;
  protected int playerHealth;
  protected int lastPlayerHealth;
  protected long lastSystemTime;
  
  public HealthOverlay(Minecraft mc) {
  	this.mc = mc;
  }

	public void render() {
		TextureManager manager = this.mc.getTextureManager();
		ScaledResolution scaledRes = new ScaledResolution(this.mc);
		EntityPlayer entityplayer = (EntityPlayer) this.mc.getRenderViewEntity();
		int health = MathHelper.ceiling_float_int(entityplayer.getHealth());
		boolean flag = this.healthUpdateCounter > (long) this.updateCounter && (this.healthUpdateCounter - (long) this.updateCounter) / 3L % 2L == 1L;		
		
		if (health < this.playerHealth && entityplayer.hurtResistantTime > 0) {
			this.lastSystemTime = Minecraft.getSystemTime();
			this.healthUpdateCounter = (long) (this.updateCounter + 20);
		} else if (health > this.playerHealth && entityplayer.hurtResistantTime > 0) {
			this.lastSystemTime = Minecraft.getSystemTime();
			this.healthUpdateCounter = (long) (this.updateCounter + 10);
		}

		if (Minecraft.getSystemTime() - this.lastSystemTime > 1000L) {
			this.playerHealth = health;
			this.lastPlayerHealth = health;
			this.lastSystemTime = Minecraft.getSystemTime();
		}
		
		this.playerHealth = health;
		int j = this.lastPlayerHealth;
		Utils.rand.setSeed((long) (this.updateCounter * 312871));
		FoodStats foodstats = entityplayer.getFoodStats();
		int k = foodstats.getFoodLevel();
		IAttributeInstance iattributeinstance = entityplayer.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
		int l = scaledRes.getScaledWidth() / 2 - 91;
		int i1 = scaledRes.getScaledWidth() / 2 + 91;
		int j1 = scaledRes.getScaledHeight() - 39;
		float maxHealth = (float) iattributeinstance.getAttributeValue();
		int absorptionAmount = MathHelper.ceiling_float_int(entityplayer.getAbsorptionAmount());
		int l1 = MathHelper.ceiling_float_int((maxHealth + (float) absorptionAmount) / 2.0F / 10.0F);
		int i2 = Math.max(10 - (l1 - 2), 3);
		int j3 = -1;

		if (entityplayer.isPotionActive(MobEffects.REGENERATION)) {
			j3 = this.updateCounter % MathHelper.ceiling_float_int(maxHealth + 5.0F);
		}
		
		boolean hardCore = entityplayer.worldObj.getWorldInfo().isHardcoreModeEnabled();
		int hardCoreOffset = (hardCore) ? 5 : 0;
				
		int absHeight = j1 - i2;
		int healthHeight = j1;
		int absIconFullX = 160;
		int absIconHalfX = 169;
		int borderMod = (flag) ? 1 : 0;
		
		// Draw absorption row, if you have any absorption hearts
		if (absorptionAmount > 0) {
			for (int absPos = 0; absPos < (absorptionAmount / 2.0F); absPos++) {
				int xCoord = l + absPos * 8;
				// draw the border
	      this.drawTexturedModalRect(xCoord, healthHeight, 16 + borderMod * 9, 9 * hardCoreOffset, 9, 9);
				if (absPos * 2 + 1 < absorptionAmount) {
					this.drawTexturedModalRect(xCoord, absHeight, absIconFullX, 9 * hardCoreOffset, 9, 9);
				} else if (absPos * 2 + 1 == absorptionAmount) {
					this.drawTexturedModalRect(xCoord, absHeight, absIconHalfX, 9 * hardCoreOffset, 9, 9);
				}
			}
		}
		
		int extraHealth = health - 20;
				
		// Draw health row
		for (int healthPos = 0; healthPos < 10; healthPos++) {
			// Logic largely depends on vanilla vs. custom icons
			// check for that first.
			int xCoord = l + healthPos * 8;
			
			// Draw the border
      this.drawTexturedModalRect(xCoord, healthHeight, 16 + borderMod * 9, 9 * hardCoreOffset, 9, 9);
			
			if (healthPos * 2 + 1 < maxHealth) {
				if (extraHealth > 0) {
					if (healthPos * 2 + 1 <= extraHealth || flag) {
						this.drawCustom(manager, entityplayer, flag, healthPos, xCoord, healthHeight, hardCore, extraHealth);
					} else {
						this.drawVanilla(entityplayer, flag, healthPos, xCoord, healthHeight, hardCoreOffset, health);
					}
				} else {
					this.drawVanilla(entityplayer, flag, healthPos, xCoord, healthHeight, hardCoreOffset, health);
				}
			} else if (healthPos * 2 + 1 == maxHealth) {
				this.drawVanilla(entityplayer, flag, healthPos, xCoord, healthHeight, hardCoreOffset, health);
			}			
		}
		
	}
	
	public void drawCustom(TextureManager manager, EntityPlayer entityplayer, boolean updateHealth, int healthPos, int xCoord, int yCoord, boolean hardCore, int extraHealth) {
		// I know I created all those icons specifically for this,
		// but mapping them individual would suck, so lets just do
		// some math instead.
		int iconX = 0;
		if (entityplayer.isPotionActive(MobEffects.POISON)) {
			iconX += 36;
		} else if (entityplayer.isPotionActive(MobEffects.WITHER)) {
			iconX += 72;
		}
		int hardCoreOffset = (hardCore) ? 1 : 0;
		int iconY = 9 + 9 * hardCoreOffset;
		
		manager.bindTexture(GuiUtils.CUSTOM_ICONS);
		
		if (updateHealth) {
			if (healthPos * 2 + 1 < this.lastPlayerHealth) {
				this.drawTexturedModalRect(xCoord, yCoord, iconX + 18, iconY, 9, 9);
			} else if (healthPos * 2 + 1 == this.lastPlayerHealth) {
				this.drawTexturedModalRect(xCoord, yCoord, iconX + 27, iconY, 9, 9);
			}
		}
		
		if (healthPos * 2 + 1 < extraHealth) {
			this.drawTexturedModalRect(xCoord, yCoord, iconX, iconY, 9, 9);
		} else if (healthPos * 2 + 1 == extraHealth) {
			this.drawTexturedModalRect(xCoord, yCoord, iconX + 9, iconY, 9, 9);
		}
		
		manager.bindTexture(GuiIngame.ICONS);
	}
	
	public void drawVanilla(EntityPlayer entityplayer, boolean updateHealth, int healthPos, int xCoord, int yCoord, int hardCoreOffset, int maxHealth) {
		int iconX = 16 + 36;
		if (entityplayer.isPotionActive(MobEffects.POISON)) {
			iconX += 36;
		} else if (entityplayer.isPotionActive(MobEffects.WITHER)) {
			iconX += 72;
		}
		
		if (updateHealth) {
			if (healthPos * 2 + 1 < this.lastPlayerHealth) {
				this.drawTexturedModalRect(xCoord, yCoord, iconX + 18, 9 * hardCoreOffset, 9, 9);
			}

			if (healthPos * 2 + 1 == this.lastPlayerHealth) {
				this.drawTexturedModalRect(xCoord, yCoord, iconX + 27, 9 * hardCoreOffset, 9, 9);
			}
		}
		
		if (healthPos * 2 + 1 < maxHealth) {
			this.drawTexturedModalRect(xCoord, yCoord, iconX, 9 * hardCoreOffset, 9, 9);
		}

		if (healthPos * 2 + 1 == maxHealth) {
			this.drawTexturedModalRect(xCoord, yCoord, iconX + 9, 9 * hardCoreOffset, 9, 9);
		}
	}
	
  public void updateTick() {
  	this.updateCounter++;
  }

}
