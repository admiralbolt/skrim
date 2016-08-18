package avi.mod.skrim.handlers;

import java.lang.reflect.Field;
import java.util.List;

import avi.mod.skrim.client.gui.CustomGuiInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GuiEventHandler {

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onInventoryOpen(GuiOpenEvent event) {
		if (event.getGui() instanceof GuiInventory) {
      event.setGui(new CustomGuiInventory(Minecraft.getMinecraft().thePlayer));
    }
  }


}
