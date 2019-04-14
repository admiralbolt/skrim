package avi.mod.skrim.blocks.tnt;

import avi.mod.skrim.utils.Utils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class NapalmExplosion extends CustomExplosion {

	public static final float DEFAULT_SIZE = 12.0F;
	private static final boolean FLAMING = true;
	private static final boolean DAMAGES_TERRAIN = true;

	private World world;

	public NapalmExplosion(World worldIn, Entity entityIn, double x, double y, double z, @Nullable Float size) {
		this(worldIn, entityIn, x, y, z, (size == null) ? DEFAULT_SIZE : size, DAMAGES_TERRAIN);
	}

	public NapalmExplosion(World worldIn, Entity entityIn, double x, double y, double z, @Nullable Float size, boolean damagesTerrain) {
		super(worldIn, entityIn, x, y, z, (size == null) ? DEFAULT_SIZE : size, FLAMING, damagesTerrain);
		this.world = worldIn;
	}

	@Override
	public void doExplosionA() {
		super.doExplosionA();
		if (!this.damagesTerrain) return;
		this.world.setBlockState(this.getPos(), Blocks.LAVA.getDefaultState());
		this.world.setBlockState(this.getPos().add(0, 1, 0), Blocks.LAVA.getDefaultState());
	}

	@Override
	public void doExplosionB(boolean particles) {
		super.doExplosionB(particles);
		if (!this.damagesTerrain) return;
		for (BlockPos pos : this.affectedBlockPositions) {
			IBlockState blockState = world.getBlockState(pos);
			if (blockState.getMaterial() != Material.AIR) {
				if (Utils.rand.nextDouble() < 0.25) {
					this.world.setBlockState(pos, Blocks.LAVA.getDefaultState());
				}
			}
		}
	}

}
