package avi.mod.skrim.proxy;

import avi.mod.skrim.client.renderer.SkrimRenderers;
import avi.mod.skrim.handlers.GuiEventHandler;
import avi.mod.skrim.handlers.SkrimEntitySpawnHandler;
import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.items.items.SkrimPotion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

/**
 * Most of the interesting registration stuff goes here. Not entirely sure what the point of ServerProxy is yet, but
 * I'll keep it around just in case.
 */
public class ClientProxy implements IProxy {


  private final Minecraft MINECRAFT = Minecraft.getMinecraft();

  @Override
  public void preInit() {
    SkrimRenderers.register();
  }

  /**
   * Note that block & item registration happens as part of a subscription to a forge event. The registration of
   * blocks & items happens in SkrimBlocks & SkrimItems respectively.
   */
  @Override
  public void init() {
    SkrimEntitySpawnHandler.init();
    MinecraftForge.EVENT_BUS.register(new GuiEventHandler());
    MINECRAFT.getItemColors().registerItemColorHandler(new SkrimPotion.ColorHandler(), SkrimItems.SKRIM_POTION);
    MINECRAFT.getItemColors().registerItemColorHandler(new SkrimPotion.ColorHandler(), SkrimItems.SPLASH_SKRIM_POTION);
    MINECRAFT.getItemColors().registerItemColorHandler(new SkrimPotion.ColorHandler(), SkrimItems.LINGERING_SKRIM_POTION);
    MINECRAFT.getItemColors().registerItemColorHandler(new SkrimPotion.ColorHandler(), SkrimItems.SKRIM_TIPPED_ARROW);
  }

  @Override
  public void postInit() {
    SkrimItems.modifyBaseItems();
  }

  @Override
  public void doClientRightClick() {
    KeyBinding.onTick(MINECRAFT.gameSettings.keyBindUseItem.getKeyCode());
  }

  @Nullable
  @Override
  public EntityPlayer getClientPlayer() {
    return MINECRAFT.player;
  }

  @Nullable
  @Override
  public World getClientWorld() {
    return MINECRAFT.world;
  }

  @Override
  public IThreadListener getThreadListener(MessageContext context) {
    if (context.side.isClient()) return MINECRAFT;
    return context.getServerHandler().player.getServer();
  }

  @Override
  public EntityPlayer getPlayer(MessageContext context) {
    return null;
  }

}
