package avi.mod.skrim.entities.monster;

import avi.mod.skrim.blocks.tnt.NapalmExplosion;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class NapalmCreeper extends CustomCreeper {

	public NapalmCreeper(World worldIn) {
		super(worldIn, "textures/entities/napalm_creeper.png");
	}
	
	@Override
	public Explosion getExplosion() {
		float f = this.getPowered() ? 2.0F : 1.0F;
		return new NapalmExplosion(this.world, this, this.posX, this.posY, this.posZ, NapalmExplosion.DEFAULT_SIZE * f, this.world.getGameRules().getBoolean("mobGriefing"));
	}

}
