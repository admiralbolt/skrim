package avi.mod.skrim.utils;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Really just an extended Utils/Reflection specifically for dealing with
 * obfuscation issues
 * 
 * @author aknecht
 *
 */
public class Obfuscation {

	public static ObfuscatedField STACK_ITEM = new ObfuscatedField("item", "field_151002_e");
	public static ObfuscatedField STACK_SIZE = new ObfuscatedField("stackSize", "field_77994_a");

	public static int getStackSize(ItemStack stack) {
		return stack.func_190916_E();
	}

	public static void setStackSize(ItemStack stack, int stackSize) {
		Reflection.hackValueTo(stack, stackSize, STACK_SIZE.getFieldNames());
	}

	public static boolean canBlockBePlaced(World world, Block block, BlockPos pos, boolean notsure, EnumFacing facing, @Nullable Entity entity) {
		return world.func_190527_a(block, pos, notsure, facing, entity);
	}

	public static class ObfuscatedField {

		public String clientName;
		public String deobfuscatedName;

		public ObfuscatedField(String clientName, String deobfuscatedName) {
			this.clientName = clientName;
			this.deobfuscatedName = deobfuscatedName;
		}

		public String[] getFieldNames() {
			return new String[] { this.clientName, this.deobfuscatedName };
		}

	}

}
