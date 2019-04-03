package avi.mod.skrim.handlers;

import avi.mod.skrim.client.gui.ArmorOverlay;
import avi.mod.skrim.client.gui.CriticalAscensionOverlay;
import avi.mod.skrim.client.gui.CustomGuiInventory;
import avi.mod.skrim.client.gui.HealthOverlay;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.ranged.SkillRanged;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiEventHandler {

  @SideOnly(Side.CLIENT)
  private static ArmorOverlay ARMOR_OVERLAY = new ArmorOverlay(Minecraft.getMinecraft());

  @SideOnly(Side.CLIENT)
  private static HealthOverlay HEALTH_OVERLAY = new HealthOverlay(Minecraft.getMinecraft());

  @SideOnly(Side.CLIENT)
  private static CriticalAscensionOverlay CRITICAL_ASCENSION_OVERLAY = new CriticalAscensionOverlay(Minecraft.getMinecraft());

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onInventoryOpen(GuiOpenEvent event) {
    if (event.getGui() instanceof GuiInventory) {
      event.setGui(new CustomGuiInventory(Minecraft.getMinecraft().player));
    }
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void onRenderPre(RenderGameOverlayEvent.Pre event) {
    if (event.getType() == ElementType.ARMOR) {
      event.setCanceled(true);
      ARMOR_OVERLAY.render();
    } else if (event.getType() == ElementType.HEALTH) {
      event.setCanceled(true);
      HEALTH_OVERLAY.render();
      HEALTH_OVERLAY.updateTick();
    }
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void onRenderPost(RenderGameOverlayEvent.Post event) {
    if (event.getType() == ElementType.FOOD) {
      EntityPlayerSP player = Minecraft.getMinecraft().player;
      if (player.hasCapability(Skills.RANGED, EnumFacing.NORTH)) {
        SkillRanged ranged = (SkillRanged) player.getCapability(Skills.RANGED, EnumFacing.NORTH);
        if (ranged.hasAbility(4)) {
          CRITICAL_ASCENSION_OVERLAY.render(ranged.getStacks());
        }
      }
    }
  }


}
