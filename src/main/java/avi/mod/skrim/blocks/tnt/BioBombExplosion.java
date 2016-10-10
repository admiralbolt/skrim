package avi.mod.skrim.blocks.tnt;

import avi.mod.skrim.utils.Utils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BioBombExplosion extends CustomExplosion {

	public static float size = 8.0F;
	public static boolean flaming = false;
	public static boolean smoking = true;
	private World world;

	public BioBombExplosion(World worldIn, Entity entityIn, double x, double y, double z) {
		super(worldIn, entityIn, x, y, z, size, flaming, smoking);
		this.world = worldIn;
	}

	@Override
	public void doExplosionA() {
		super.doExplosionA();
		this.clearAffectedBlockPositions();
	}

	@Override
	public void doExplosionB(boolean particles) {
		super.doExplosionB(particles);
	}

}
