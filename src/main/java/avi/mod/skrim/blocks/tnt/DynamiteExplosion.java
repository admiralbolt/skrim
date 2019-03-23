package avi.mod.skrim.blocks.tnt;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class DynamiteExplosion extends CustomExplosion {

	public static float size = 5.0F;
	public static boolean flaming = false;
	public static boolean smoking = true;

	public DynamiteExplosion(World worldIn, Entity entityIn, double x, double y, double z) {
		super(worldIn, entityIn, x, y, z, size, flaming, smoking);
		this.dropChance = 1.0F;
	}

}
