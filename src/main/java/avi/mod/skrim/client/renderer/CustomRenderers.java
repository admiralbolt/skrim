package avi.mod.skrim.client.renderer;

import avi.mod.skrim.blocks.tnt.CustomTNTPrimed;
import avi.mod.skrim.client.renderer.entity.RenderCustomCreeper;
import avi.mod.skrim.client.renderer.entity.RenderFox;
import avi.mod.skrim.client.renderer.entity.RenderKingOfRedLions;
import avi.mod.skrim.client.renderer.entity.RenderMegaChicken;
import avi.mod.skrim.client.renderer.projectiles.RenderDeathArrow;
import avi.mod.skrim.client.renderer.tileentity.MegaChestRenderer;
import avi.mod.skrim.entities.items.EntityKingOfRedLions;
import avi.mod.skrim.entities.monster.BioCreeper;
import avi.mod.skrim.entities.monster.GigaChicken;
import avi.mod.skrim.entities.monster.MegaChicken;
import avi.mod.skrim.entities.monster.NapalmCreeper;
import avi.mod.skrim.entities.passive.EntityFox;
import avi.mod.skrim.entities.projectile.Rocket;
import avi.mod.skrim.items.artifacts.DeathArrow;
import avi.mod.skrim.tileentity.EnchantedFlowerTileEntity;
import avi.mod.skrim.tileentity.MegaChestTileEntity;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CustomRenderers {

  @SideOnly(Side.CLIENT)
  public static void register() {
    ClientRegistry.bindTileEntitySpecialRenderer(EnchantedFlowerTileEntity.class,
        new TileEntityEnchantedFlowerRenderer());
    ClientRegistry.bindTileEntitySpecialRenderer(MegaChestTileEntity.class, new MegaChestRenderer());

    RenderingRegistry.registerEntityRenderingHandler(CustomTNTPrimed.class, EntityCustomTNTPrimedRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(NapalmCreeper.class, RenderCustomCreeper::new);
    RenderingRegistry.registerEntityRenderingHandler(BioCreeper.class, RenderCustomCreeper::new);
    RenderingRegistry.registerEntityRenderingHandler(Rocket.class, RenderRocket::new);
    RenderingRegistry.registerEntityRenderingHandler(MegaChicken.class, RenderMegaChicken::new);
    RenderingRegistry.registerEntityRenderingHandler(GigaChicken.class, RenderMegaChicken::new);
    RenderingRegistry.registerEntityRenderingHandler(EntityFox.class, RenderFox::new);
    RenderingRegistry.registerEntityRenderingHandler(DeathArrow.EntityDeathArrow.class, RenderDeathArrow::new);
    RenderingRegistry.registerEntityRenderingHandler(EntityKingOfRedLions.class, RenderKingOfRedLions::new);
  }

}
