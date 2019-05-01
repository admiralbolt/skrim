package avi.mod.skrim.client.renderer.entity;

import avi.mod.skrim.entities.items.EntityKingOfRedLions;
import net.minecraft.client.model.IMultipassModel;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class RenderKingOfRedLions extends Render<EntityKingOfRedLions> {

  private static final ResourceLocation BOAT_TEXTURE = new ResourceLocation("skrim:textures/entities/king_of_red_lions.png");
  /**
   * instance of ModelBoat for rendering
   */
  protected ModelBase modelBoat = new SkrimModelBoat();

  public RenderKingOfRedLions(RenderManager renderManagerIn) {
    super(renderManagerIn);
    this.shadowSize = 0.5F;
  }

  /**
   * Renders the desired {@code T} type Entity.
   */
  public void doRender(EntityKingOfRedLions entity, double x, double y, double z, float entityYaw, float partialTicks) {
    GlStateManager.pushMatrix();
    this.setupTranslation(x, y, z);
    this.setupRotation(entity, entityYaw, partialTicks);
    this.bindEntityTexture(entity);

    if (this.renderOutlines) {
      GlStateManager.enableColorMaterial();
      GlStateManager.enableOutlineMode(this.getTeamColor(entity));
    }

    this.modelBoat.render(entity, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

    if (this.renderOutlines) {
      GlStateManager.disableOutlineMode();
      GlStateManager.disableColorMaterial();
    }

    GlStateManager.popMatrix();
    super.doRender(entity, x, y, z, entityYaw, partialTicks);
  }

  public void setupRotation(EntityKingOfRedLions p_188311_1_, float p_188311_2_, float p_188311_3_) {
    GlStateManager.rotate(180.0F - p_188311_2_, 0.0F, 1.0F, 0.0F);
    float f = (float) p_188311_1_.getTimeSinceHit() - p_188311_3_;
    float f1 = p_188311_1_.getDamageTaken() - p_188311_3_;

    if (f1 < 0.0F) {
      f1 = 0.0F;
    }

    if (f > 0.0F) {
      GlStateManager.rotate(MathHelper.sin(f) * f * f1 / 10.0F * (float) p_188311_1_.getForwardDirection(), 1.0F, 0.0F, 0.0F);
    }

    GlStateManager.scale(-1.0F, -1.0F, 1.0F);
  }

  public void setupTranslation(double p_188309_1_, double p_188309_3_, double p_188309_5_) {
    GlStateManager.translate((float) p_188309_1_, (float) p_188309_3_ + 0.375F, (float) p_188309_5_);
  }

  protected ResourceLocation getEntityTexture(@Nonnull EntityKingOfRedLions entity) {
    return BOAT_TEXTURE;
  }

  public boolean isMultipass() {
    return true;
  }

  public void renderMultipass(EntityKingOfRedLions p_188300_1_, double p_188300_2_, double p_188300_4_, double p_188300_6_,
                              float p_188300_8_,
                              float p_188300_9_) {
    GlStateManager.pushMatrix();
    this.setupTranslation(p_188300_2_, p_188300_4_, p_188300_6_);
    this.setupRotation(p_188300_1_, p_188300_8_, p_188300_9_);
    this.bindEntityTexture(p_188300_1_);
    ((IMultipassModel) this.modelBoat).renderMultipass(p_188300_1_, p_188300_9_, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
    GlStateManager.popMatrix();
  }

  @SideOnly(Side.CLIENT)
  public static class SkrimModelBoat extends ModelBase implements IMultipassModel {
    public ModelRenderer[] boatSides = new ModelRenderer[5];
    public ModelRenderer[] paddles = new ModelRenderer[2];
    public ModelRenderer noWater;
    private final int patchList = GLAllocation.generateDisplayLists(1);

    public SkrimModelBoat() {
      this.boatSides[0] = (new ModelRenderer(this, 0, 0)).setTextureSize(128, 64);
      this.boatSides[1] = (new ModelRenderer(this, 0, 19)).setTextureSize(128, 64);
      this.boatSides[2] = (new ModelRenderer(this, 0, 27)).setTextureSize(128, 64);
      this.boatSides[3] = (new ModelRenderer(this, 0, 35)).setTextureSize(128, 64);
      this.boatSides[4] = (new ModelRenderer(this, 0, 43)).setTextureSize(128, 64);
      int i = 32;
      int j = 6;
      int k = 20;
      int l = 4;
      int i1 = 28;
      this.boatSides[0].addBox(-14.0F, -9.0F, -3.0F, 28, 16, 3, 0.0F);
      this.boatSides[0].setRotationPoint(0.0F, 3.0F, 1.0F);
      this.boatSides[1].addBox(-13.0F, -7.0F, -1.0F, 18, 6, 2, 0.0F);
      this.boatSides[1].setRotationPoint(-15.0F, 4.0F, 4.0F);
      this.boatSides[2].addBox(-8.0F, -7.0F, -1.0F, 16, 6, 2, 0.0F);
      this.boatSides[2].setRotationPoint(15.0F, 4.0F, 0.0F);
      this.boatSides[3].addBox(-14.0F, -7.0F, -1.0F, 28, 6, 2, 0.0F);
      this.boatSides[3].setRotationPoint(0.0F, 4.0F, -9.0F);
      this.boatSides[4].addBox(-14.0F, -7.0F, -1.0F, 28, 6, 2, 0.0F);
      this.boatSides[4].setRotationPoint(0.0F, 4.0F, 9.0F);
      this.boatSides[0].rotateAngleX = ((float) Math.PI / 2F);
      this.boatSides[1].rotateAngleY = ((float) Math.PI * 3F / 2F);
      this.boatSides[2].rotateAngleY = ((float) Math.PI / 2F);
      this.boatSides[3].rotateAngleY = (float) Math.PI;
      this.paddles[0] = this.makePaddle(true);
      this.paddles[0].setRotationPoint(3.0F, -5.0F, 9.0F);
      this.paddles[1] = this.makePaddle(false);
      this.paddles[1].setRotationPoint(3.0F, -5.0F, -9.0F);
      this.paddles[1].rotateAngleY = (float) Math.PI;
      this.paddles[0].rotateAngleZ = 0.19634955F;
      this.paddles[1].rotateAngleZ = 0.19634955F;
      this.noWater = (new ModelRenderer(this, 0, 0)).setTextureSize(128, 64);
      this.noWater.addBox(-14.0F, -9.0F, -3.0F, 28, 16, 3, 0.0F);
      this.noWater.setRotationPoint(0.0F, -3.0F, 1.0F);
      this.noWater.rotateAngleX = ((float) Math.PI / 2F);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch,
                       float scale) {
      GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
      EntityBoat entityboat = (EntityBoat) entityIn;
      this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);

      for (int i = 0; i < 5; ++i) {
        this.boatSides[i].render(scale);
      }
    }

    public void renderMultipass(Entity p_187054_1_, float p_187054_2_, float p_187054_3_, float p_187054_4_, float p_187054_5_,
                                float p_187054_6_, float scale) {
      GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.colorMask(false, false, false, false);
      this.noWater.render(scale);
      GlStateManager.colorMask(true, true, true, true);
    }

    protected ModelRenderer makePaddle(boolean p_187056_1_) {
      ModelRenderer modelrenderer = (new ModelRenderer(this, 62, p_187056_1_ ? 0 : 20)).setTextureSize(128, 64);
      int i = 20;
      int j = 7;
      int k = 6;
      float f = -5.0F;
      modelrenderer.addBox(-1.0F, 0.0F, -5.0F, 2, 2, 18);
      modelrenderer.addBox(p_187056_1_ ? -1.001F : 0.001F, -3.0F, 8.0F, 1, 6, 7);
      return modelrenderer;
    }
  }
}
