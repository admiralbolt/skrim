package avi.mod.skrim.entities.monster;

import avi.mod.skrim.blocks.tnt.BioBombExplosion;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

/**
 * It's like a creeper, except its explosion doesn't affect blocks.
 */
public class BioCreeper extends CustomCreeper {

	public BioCreeper(World worldIn) {
		super(worldIn, "textures/entities/bio_creeper.png");
	}
	
	@Override
	public Explosion getExplosion() {
		float f = this.getPowered() ? 2.0F : 1.0F;
		return new BioBombExplosion(this.world, this, this.posX, this.posY, this.posZ, BioBombExplosion.DEFAULT_SIZE * f);
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
	}

}