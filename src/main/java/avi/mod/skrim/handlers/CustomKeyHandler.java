package avi.mod.skrim.handlers;

import avi.mod.skrim.client.gui.SkillScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;


public class CustomKeyHandler {

  @SubscribeEvent
  public void onKeyInput(InputEvent.KeyInputEvent event) {
    Minecraft mc = Minecraft.getMinecraft();
    GameSettings settings = mc.gameSettings;
  }

}
