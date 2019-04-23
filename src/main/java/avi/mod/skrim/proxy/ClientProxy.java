package avi.mod.skrim.proxy;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.advancements.SkrimAdvancements;
import avi.mod.skrim.capabilities.SkrimCapabilities;
import avi.mod.skrim.client.renderer.CustomRenderers;
import avi.mod.skrim.entities.SkrimEntities;
import avi.mod.skrim.handlers.EventHandler;
import avi.mod.skrim.handlers.GuiEventHandler;
import avi.mod.skrim.handlers.LoadSkillsHandler;
import avi.mod.skrim.handlers.SkrimEntitySpawnHandler;
import avi.mod.skrim.handlers.GuiHandler;
import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.network.SkrimPacketHandler;
import avi.mod.skrim.world.loot.CustomLootTables;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.NetworkRegistry;
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
    CustomRenderers.register();
  }

  /**
   * Note that block & item registration happens as part of a subscription to a forge event. The registration of
   * blocks & items happens in SkrimBlocks & SkrimItems respectively.
   */
  @Override
  public void init() {
    CustomLootTables.registerLootTables();
    SkrimCapabilities.registerCapabilities();
    SkrimEntities.register();
    SkrimEntitySpawnHandler.init();
    SkrimPacketHandler.registerPackets();

    for (SkrimAdvancements.CustomAdvancement advancement : SkrimAdvancements.ADVANCEMENTS_BY_NAME.values()) {
      CriteriaTriggers.register(advancement.trigger);
    }

    // Hook up all event handlers, this allows them to use Subscribe to Events
    MinecraftForge.EVENT_BUS.register(new LoadSkillsHandler());
    MinecraftForge.EVENT_BUS.register(new EventHandler());
    MinecraftForge.EVENT_BUS.register(new GuiEventHandler());
    NetworkRegistry.INSTANCE.registerGuiHandler(Skrim.instance, new GuiHandler());
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
