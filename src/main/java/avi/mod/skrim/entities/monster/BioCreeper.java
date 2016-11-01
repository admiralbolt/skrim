package avi.mod.skrim.entities.monster;

import avi.mod.skrim.blocks.tnt.BioBombExplosion;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BioCreeper extends CustomCreeper {

	public BioCreeper(World worldIn) {
		super(worldIn, "textures/entities/bio_creeper.png");
	}
	
	@Override
	public Explosion getExplosion() {
		boolean flag = this.worldObj.getGameRules().getBoolean("mobGriefing");
		float f = this.getPowered() ? 2.0F : 1.0F;
		BioBombExplosion explosion = new BioBombExplosion(this.worldObj, this, this.posX, this.posY, this.posZ, BioBombExplosion.DEFAULT_SIZE * f);
		return explosion;
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
	}

}