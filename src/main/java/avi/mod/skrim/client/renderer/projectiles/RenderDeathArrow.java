package avi.mod.skrim.client.renderer.projectiles;

import avi.mod.skrim.items.artifacts.DeathArrow;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class RenderDeathArrow extends RenderArrow<DeathArrow.EntityDeathArrow> {

  private static final ResourceLocation RES_ARROW = new ResourceLocation("skrim", "textures/entities/projectiles/death_arrow.png");

  public RenderDeathArrow(RenderManager manager) {
    super(manager);
  }

  @Nullable
  @Override
  protected ResourceLocation getEntityTexture(@Nonnull DeathArrow.EntityDeathArrow entity) {
    return RES_ARROW;
  }

}

