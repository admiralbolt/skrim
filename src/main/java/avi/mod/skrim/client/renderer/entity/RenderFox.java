package avi.mod.skrim.client.renderer.entity;

import avi.mod.skrim.client.model.ModelFox;
import avi.mod.skrim.entities.passive.EntityFox;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderFox extends RenderLiving<EntityFox> {
	private static final ResourceLocation FOX_TEXTURES = new ResourceLocation("skrim:textures/entities/fox.png");
	private static final ResourceLocation TAMED_FOX_TEXTURES = new ResourceLocation("skrim:textures/entities/fox.png");
	private static final ResourceLocation ANRGY_FOX_TEXTURES = new ResourceLocation("skrim:textures/entities/fox.png");

	public RenderFox(RenderManager p_i47187_1_) {
		super(p_i47187_1_, new ModelFox(), 0.5F);
	}

	/**
	 * Defines what float the third param in setRotationAngles of ModelBase is
	 */
	protected float handleRotationFloat(EntityFox livingBase, float partialTicks) {
		return livingBase.getTailRotation();
	}

	/**
	 * Renders the desired {@code T} type Entity.
	 */
	public void doRender(EntityFox entity, double x, double y, double z, float entityYaw, float partialTicks) {
		if (entity.isWolfWet()) {
			float f = entity.getBrightness(partialTicks) * entity.getShadingWhileWet(partialTicks);
			GlStateManager.color(f, f, f);
		}
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called
	 * unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(EntityFox entity) {
		return entity.isTamed() ? TAMED_FOX_TEXTURES : (entity.isAngry() ? ANRGY_FOX_TEXTURES : FOX_TEXTURES);
	}
}
