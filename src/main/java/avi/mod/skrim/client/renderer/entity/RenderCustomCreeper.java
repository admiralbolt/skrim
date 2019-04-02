package avi.mod.skrim.client.renderer.entity;

import avi.mod.skrim.entities.monster.CustomCreeper;
import net.minecraft.client.renderer.entity.RenderCreeper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.ResourceLocation;

public class RenderCustomCreeper extends RenderCreeper {

	public RenderCustomCreeper(RenderManager renderManagerIn) {
		super(renderManagerIn);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityCreeper entity) {
		if (!(entity instanceof CustomCreeper)) return null;
		return ((CustomCreeper) entity).getResourceLocation();
	}

}