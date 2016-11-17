package avi.mod.skrim.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiUtils {
	
	@SideOnly(Side.CLIENT)
	public static ArmorOverlay ARMOR_OVERLAY = new ArmorOverlay(Minecraft.getMinecraft());
	
	@SideOnly(Side.CLIENT)
	public static HealthOverlay HEALTH_OVERLAY = new HealthOverlay(Minecraft.getMinecraft());
	
	@SideOnly(Side.CLIENT)
	public static CriticalAscensionOverlay CRITICAL_ASCENSION_OVERLAY = new CriticalAscensionOverlay(Minecraft.getMinecraft());
	
	public static ResourceLocation CUSTOM_ICONS = new ResourceLocation("skrim:textures/guis/overlays/custom_icons.png");
	
	// First row
	public static Icon EXTRA_ARMOR_HALF = new Icon("extra_armor_half", 0, 0, 9, 9);
	public static Icon EXTRA_ARMOR_FULL = new Icon("extra_armo_full", 9, 0, 9, 9);
	
	// Second row
	public static Icon EXTRA_HEART_FULL = new Icon("extra_heart_full", 0, 9, 9, 9);
	public static Icon EXTRA_HEART_HALF = new Icon("extra_heart_half", 9, 9, 9, 9);
	public static Icon EXTRA_HEART_FULL_UPDATE = new Icon("extra_heart_full_update", 18, 9, 9, 9);
	public static Icon EXTRA_HEART_HALF_UPDATE = new Icon("extra_heart_half_update", 27, 9, 9, 9);
	public static Icon EXTRA_HEART_POISON_FULL = new Icon("extra_heart_poison_full", 36, 9, 9, 9);
	public static Icon EXTRA_HEART_POISON_HALF = new Icon("extra_heart_poison_half", 45, 9, 9, 9);
	public static Icon EXTRA_HEART_POISON_FULL_UPDATE = new Icon("extra_heart_poison_full_update", 54, 9, 9, 9);
	public static Icon EXTRA_HEART_POISON_HALF_UPDATE = new Icon("extra_heart_poison_half_update", 63, 9, 9, 9);
	public static Icon EXTRA_HEART_WITHER_FULL = new Icon("extra_heart_wither_full", 72, 9, 9, 9);
	public static Icon EXTRA_HEART_WITHER_HALF = new Icon("extra_heart_wither_half", 81, 9, 9, 9);
	public static Icon EXTRA_HEART_WITHER_FULL_UPDATE = new Icon("extra_heart_wither_full_update", 90, 9, 9, 9);
	public static Icon EXTRA_HEART_WITHER_HALF_UPDATE = new Icon("extra_heart_wither_half_update", 99, 9, 9, 9);
	
	// Third row
	public static Icon EXTRA_HEART_HARDCORE_FULL = new Icon("extra_heart_hardcore_full", 0, 18, 9, 9);
	public static Icon EXTRA_HEART_HARDCORE_HALF = new Icon("extra_heart_hardcore_half", 9, 18, 9, 9);
	public static Icon EXTRA_HEART_HARDCORE_FULL_UPDATE = new Icon("extra_heart_hardcore_full_update", 18, 18, 9, 9);
	public static Icon EXTRA_HEART_HARDCORE_HALF_UPDATE = new Icon("extra_heart_hardcore_half_update", 27, 18, 9, 9);
	public static Icon EXTRA_HEART_HARDCORE_POISON_FULL = new Icon("extra_heart_hardcore_poison_full", 36, 18, 9, 9);
	public static Icon EXTRA_HEART_HARDCORE_POISON_HALF = new Icon("extra_heart_hardcore_poison_half", 45, 18, 9, 9);
	public static Icon EXTRA_HEART_HARDCORE_POISON_FULL_UPDATE = new Icon("extra_heart_hardcore_poison_full_update", 54, 18, 9, 9);
	public static Icon EXTRA_HEART_HARDCORE_POISON_HALF_UPDATE = new Icon("extra_heart_hardcore_poison_half_update", 63, 18, 9, 9);
	public static Icon EXTRA_HEART_HARDCORE_WITHER_FULL = new Icon("extra_heart_hardcore_wither_full", 72, 18, 9, 9);
	public static Icon EXTRA_HEART_HARDCORE_WITHER_HALF = new Icon("extra_heart_hardcore_wither_half", 81, 18, 9, 9);
	public static Icon EXTRA_HEART_HARDCORE_WITHER_FULL_UPDATE = new Icon("extra_heart_hardcore_wither_full_update", 90, 18, 9, 9);
	public static Icon EXTRA_HEART_HARDCORE_WITHER_HALF_UPDATE = new Icon("extra_heart_hardcore_wither_half_update", 99, 18, 9, 9);
	
	
	public static Icon ACCURACY = new Icon("accuracy", 108, 0, 16, 16);
	
	public static void drawIcon(Gui gui, int xCoord, int yCoord, Icon icon) {
		gui.drawTexturedModalRect(xCoord, yCoord, icon.getX(), icon.getY(), icon.getWidth(), icon.getHeight());
	}
	
	/**
	 * Helper class to keep track of 10 billion icons.
	 */
	public static class Icon {
		
		private String name;
		private int xStart;
		private int yStart;
		private int width;
		private int height;
		
		public Icon(String name, int xStart, int yStart, int width, int height) {
			this.name = name;
			this.xStart = xStart;
			this.yStart = yStart;
			this.width = width;
			this.height = height;
		}
		
		public int getX() {
			return this.xStart;
		}
		
		public int getY() {
			return this.yStart;
		}
		
		public int getWidth() {
			return this.width;
		}
		
		public int getHeight() {
			return this.height;
		}
		
	}

}
