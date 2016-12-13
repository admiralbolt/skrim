package avi.mod.skrim.client.renderer;

import avi.mod.skrim.entities.monster.MegaChicken;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelChicken;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderChicken;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderMegaChicken extends RenderLiving<MegaChicken> {

	private static final ResourceLocation CHICKEN_TEXTURES = new ResourceLocation("textures/entity/chicken.png");

	public RenderMegaChicken(RenderManager manager) {
		super(manager, new ModelChicken(), 0.3F);
	}

	protected float handleRotationFloat(EntityChicken livingBase, float partialTicks) {
		float f = livingBase.oFlap + (livingBase.wingRotation - livingBase.oFlap) * partialTicks;
		float f1 = livingBase.oFlapSpeed + (livingBase.destPos - livingBase.oFlapSpeed) * partialTicks;
		return (MathHelper.sin(f) + 1.0F) * f1;
	}

	@Override
	protected void preRenderCallback(MegaChicken entitylivingbaseIn, float partialTickTime) {
		GlStateManager.scale(MegaChicken.SIZE_MULT, MegaChicken.SIZE_MULT, MegaChicken.SIZE_MULT);
	}

	@Override
	protected ResourceLocation getEntityTexture(MegaChicken entity) {
		return CHICKEN_TEXTURES;
	}

}
