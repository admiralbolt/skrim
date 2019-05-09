package avi.mod.skrim.client.renderer.entity;

import avi.mod.skrim.entities.passive.EntityWatermoolon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelCow;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderWatermoolon extends RenderLiving<EntityWatermoolon> {

  private static final ResourceLocation TEXTURE = new ResourceLocation("skrim:textures/entities/watermoolon.png");

  public RenderWatermoolon(RenderManager p_i47200_1_) {
    super(p_i47200_1_, new ModelCow(), 0.7F);
    this.addLayer(new LayerWatermoolonPumpkin(this));
  }

  public ModelCow getMainModel() {
    return (ModelCow) super.getMainModel();
  }

  /**
   * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
   */
  protected ResourceLocation getEntityTexture(EntityWatermoolon entity) {
    return TEXTURE;
  }

  @SideOnly(Side.CLIENT)
  public static class LayerWatermoolonPumpkin implements LayerRenderer<EntityWatermoolon> {

    private static final float SCALE = 0.3F;
    private final RenderWatermoolon pumpkowRenderer;

    public LayerWatermoolonPumpkin(RenderWatermoolon pumpkowRendererIn) {
      this.pumpkowRenderer = pumpkowRendererIn;
    }

    public void doRenderLayer(EntityWatermoolon entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks,
                              float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      if (!entitylivingbaseIn.isChild() && !entitylivingbaseIn.isInvisible()) {
        BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        this.pumpkowRenderer.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.enableCull();
        GlStateManager.cullFace(GlStateManager.CullFace.FRONT);
        GlStateManager.pushMatrix();
        GlStateManager.scale(SCALE, -SCALE, SCALE);
        GlStateManager.translate(0.2F, 0.0F, 0.5F);
        GlStateManager.rotate(23.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.pushMatrix();
        GlStateManager.translate(-1.2F, -0.5F, 1.2F);
        blockrendererdispatcher.renderBlockBrightness(Blocks.MELON_BLOCK.getDefaultState(), 1.0F);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.1F, 0.0F, -0.6F);
        GlStateManager.rotate(142.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(-0.5F, -0.5F, 0.5F);
        blockrendererdispatcher.renderBlockBrightness(Blocks.MELON_BLOCK.getDefaultState(), 1.0F);
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();

        this.pumpkowRenderer.getMainModel().head.postRender(0.0625F);
        GlStateManager.scale(SCALE, -SCALE, SCALE);
        GlStateManager.translate(0.0F, 1.2F, -0.5F);
        GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(-0.5F, -0.5F, 0.5F);
        blockrendererdispatcher.renderBlockBrightness(Blocks.MELON_BLOCK.getDefaultState(), 1.0F);
        GlStateManager.popMatrix();
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        GlStateManager.disableCull();
      }
    }

    public boolean shouldCombineTextures() {
      return true;
    }
  }

}
