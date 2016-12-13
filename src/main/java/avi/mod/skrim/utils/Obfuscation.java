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
	public static ObfuscatedField POTION_DURATION = new ObfuscatedField("duration", "field_76460_b");
	public static ObfuscatedField FISH_HOOK_CATCHABLE = new ObfuscatedField("ticksCatchable", "field_146045_ax");
	public static ObfuscatedField FISH_HOOK_CAUGHT_DELAY = new ObfuscatedField("ticksCaughtDelay", "field_146040_ay");
	public static ObfuscatedField FISH_HOOK_IN_GROUND = new ObfuscatedField("inGround", "field_146051_au");
	public static ObfuscatedField FISH_HOOK_ANGLER = new ObfuscatedField("angler", "field_146042_b");
	public static ObfuscatedField FISH_HOOK_TICKS_IN_GROUND = new ObfuscatedField("ticksInGround", "field_146049_av");
	public static ObfuscatedField FISH_HOOK_STATE = new ObfuscatedField("state", "this.field_190627_av");
	public static ObfuscatedField FISH_HOOK_RAYTRACE = new ObfuscatedField("func_190624_r", "func_190624_r");

	public static int getStackSize(ItemStack stack) {
		return stack.func_190916_E();
	}

	public static void setStackSize(ItemStack stack, int stackSize) {
		Reflection.hackValueTo(stack, stackSize, STACK_SIZE.getFieldNames());
	}

	public static boolean canBlockBePlaced(World world, Block block, BlockPos pos, boolean notsure, EnumFacing facing, @Nullable Entity entity) {
		return world.func_190527_a(block, pos, notsure, facing, entity);
	}
	
	/**
	 * Okay, I get that you don't want this to return null anymore,
	 * but WHY WOULD YOU WANT IT TO RETURN AIR!!??
	 */
	public static boolean isEmptyStack(ItemStack stack) {
		return stack == ItemStack.field_190927_a;
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
