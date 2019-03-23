package avi.mod.skrim.entities.monster;

import avi.mod.skrim.blocks.tnt.CustomTNTPrimed;
import avi.mod.skrim.blocks.tnt.NapalmExplosion;
import avi.mod.skrim.utils.Reflection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class NapalmCreeper extends CustomCreeper {

	public NapalmCreeper(World worldIn) {
		super(worldIn, "textures/entities/napalm_creeper.png");
	}
	
	@Override
	public Explosion getExplosion() {
		boolean flag = this.world.getGameRules().getBoolean("mobGriefing");
		float f = this.getPowered() ? 2.0F : 1.0F;
		NapalmExplosion explosion = new NapalmExplosion(this.world, this, this.posX, this.posY, this.posZ, NapalmExplosion.DEFAULT_SIZE * f);
		return explosion;
	}

}
