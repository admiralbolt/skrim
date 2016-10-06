package avi.mod.skrim.blocks.tnt;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class NapalmExplosion extends CustomExplosion {

	public static float size = 12.0F;
	public static boolean flaming = true;
	public static boolean smoking = true;
	private World world;

	public NapalmExplosion(World worldIn, Entity entityIn, double x, double y, double z) {
		super(worldIn, entityIn, x, y, z, size, flaming, smoking);
		this.world = worldIn;
	}
	
	@Override
	public void doExplosionA() {
		super.doExplosionA();
		System.out.println("setting block pos: " + this.getPos());
		this.world.setBlockState(this.getPos(), Blocks.LAVA.getDefaultState());
		this.world.setBlockState(this.getPos().add(0, 1, 0), Blocks.LAVA.getDefaultState());
	}
	
	@Override
	public void doExplosionB(boolean particles) {
		super.doExplosionB(particles);
		System.out.println("setting block pos: " + this.getPos());
		this.world.setBlockState(this.getPos(), Blocks.LAVA.getDefaultState());
		this.world.setBlockState(this.getPos().add(0, 1, 0), Blocks.LAVA.getDefaultState());
	}

}
