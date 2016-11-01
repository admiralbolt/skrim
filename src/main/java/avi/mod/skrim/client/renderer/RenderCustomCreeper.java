package avi.mod.skrim.client.renderer;

import avi.mod.skrim.entities.monster.CustomCreeper;
import net.minecraft.client.renderer.entity.RenderCreeper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.ResourceLocation;

public class RenderCustomCreeper extends RenderCreeper {
	
	private static final ResourceLocation NAPALM_CREEPER = new ResourceLocation("skrim:textures/entities/napalm_creeper.png");
	private static final ResourceLocation BIO_CREEPER = new ResourceLocation("skrim:textures/entities/bio_creeper.png");

	public RenderCustomCreeper(RenderManager renderManagerIn) {
		super(renderManagerIn);
		System.out.println("constructing renderer");
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityCreeper entity) {
		if (entity instanceof CustomCreeper) {
			CustomCreeper creeper = (CustomCreeper) entity;
			return creeper.getResourceLocation();
		} else {
			return null;
		}
	}

}