package avi.mod.skrim.client.renderer;

import avi.mod.skrim.blocks.tnt.CustomTNTPrimed;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class TNTRenderFactory implements IRenderFactory<CustomTNTPrimed> {

	@Override
	public Render<? super CustomTNTPrimed> createRenderFor(RenderManager manager) {
		// TODO Auto-generated method stub
		return new EntityCustomTNTPrimedRenderer(manager);
	}

}
