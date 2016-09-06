package avi.mod.skrim.items;

import avi.mod.skrim.items.CustomFishHook;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderFish;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderCustomFish implements IRenderFactory<CustomFishHook> {

    @Override
    public Render<? super CustomFishHook> createRenderFor(RenderManager manager) {
        return new RenderFish(manager);
    }

}