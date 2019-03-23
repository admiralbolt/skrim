package avi.mod.skrim.entities.monster;

import avi.mod.skrim.utils.Reflection;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class CustomCreeper extends EntityCreeper {

	private ResourceLocation customResource;

	public CustomCreeper(World worldIn, String resourcePath) {
		super(worldIn);
		this.customResource = new ResourceLocation("skrim:" + resourcePath);
	}

	public ResourceLocation getResourceLocation() {
		return this.customResource;
	}

	/**
	 * Fuck you minecraft for not making explode() public. I wouldn't have to do any of this if you would have let me override the explode method.
	 * 
	 * Wouldn't you want to be able to call explode from elsewhere anyway? Don't you want to be able to set off creepers from other sources?
	 */
	@Override
	public void onUpdate() {
		if (this.isEntityAlive()) {
			Integer timeSinceIgnited = (Integer) Reflection.getSuperSuperPrivateField(this, "timeSinceIgnited", "field_70833_d");
			Integer fuseTime = (Integer) Reflection.getSuperSuperPrivateField(this, "fuseTime", "field_82225_f");
			int i = this.getCreeperState();
			if ((timeSinceIgnited + i) >= fuseTime) {
				this.explode();
			}
		}

		super.onUpdate();
	}

	public void explode() {
		if (!this.world.isRemote) {
			this.dead = true;
			Explosion explosion = this.getExplosion();
			explosion.doExplosionA();
			explosion.doExplosionB(true);
			this.setDead();
		}
	}

	// Override this
	public Explosion getExplosion() {
		return null;
	}

}
