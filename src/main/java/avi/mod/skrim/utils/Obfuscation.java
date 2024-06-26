package avi.mod.skrim.utils;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Really just an extended Utils/ReflectionUtils specifically for dealing with
 * obfuscation issues.
 *
 * FYI, you can find names of obfuscated fields by searching through some logs in the build folder.
 * grep -rnw build/ -e "FIELD_NAME"
 *
 * .//createSrgToMcp/output.srg:16423:FD: net/minecraft/entity/EntityLivingBase/field_184628_bn net/minecraft/entity/EntityLivingBase/activeItemStackUseCount
 * .//createMcpToSrg/output.tsrg:16414:    activeItemStackUseCount field_184628_bn
 */
public class Obfuscation {

  public static ObfuscatedField FISH_HOOK_CATCHABLE = new ObfuscatedField("ticksCatchable", "field_146045_ax");
  public static ObfuscatedField FISH_HOOK_CAUGHT_DELAY = new ObfuscatedField("ticksCaughtDelay", "field_146040_ay");
  public static ObfuscatedField FISH_HOOK_IN_GROUND = new ObfuscatedField("inGround", "field_146051_au");
  public static ObfuscatedField FISH_HOOK_LUCK = new ObfuscatedField("luck", "field_191518_aw");
  public static ObfuscatedField FISH_HOOK_LURE_SPEED = new ObfuscatedField("lureSpeed", "field_191519_ax");

  public static ObfuscatedField HORSE_JUMP_ATTRIBUTE = new ObfuscatedField("JUMP_STRENGTH", "field_110271_bv");
  public static ObfuscatedField EXPLOSION_SIZE = new ObfuscatedField("size", "field_77280_f");
  public static ObfuscatedField ENTITY_FIRE = new ObfuscatedField("fire", "field_190534_ay");
  public static ObfuscatedField VILLAGER_BUY_LIST = new ObfuscatedField("buyingList", "field_70963_i");
  public static ObfuscatedField CURRENT_ITEM = new ObfuscatedField("itemStack", "field_70457_g");

  public static ObfuscatedField BOAT_FORWARD_DOWN = new ObfuscatedField("forwardInputDown", "field_184461_aB");
  public static ObfuscatedField BOAT_LEFT_DOWN = new ObfuscatedField("leftInputDown", "field_184480_az");
  public static ObfuscatedField BOAT_RIGHT_DOWN = new ObfuscatedField("rightInputDown", "field_184459_aA");

  public static ObfuscatedField POTION_EFFECT_DURATION = new ObfuscatedField("duration", "field_76460_b");
  public static ObfuscatedField POTION_EFFECT_AMPLIFIER = new ObfuscatedField("amplifier", "field_76461_c");

  public static ObfuscatedField ACTIVE_STACK_COUNT = new ObfuscatedField("activeItemStackUseCount", "field_184628_bn");

  // Getting age property from crops!
  public static ObfuscatedField CROP_AGE = new ObfuscatedField("AGE", "BEETROOT_AGE", "field_176486_a", "field_176488_a", "field_185531_a", "field_176501_a");


  public static boolean canBlockBePlaced(World world, Block block, BlockPos pos, boolean notsure, EnumFacing facing,
                                         @Nullable Entity entity) {
    return world.mayPlace(block, pos, notsure, facing, entity);
  }

  /**
   * A helper class for managing obfuscated fields. We preserve a mapping between
   * the real NAME and the obfuscated NAME, that way using reflection and setting
   * values works on both the dev environment (de-obfuscated) and the prod
   * environment (obfuscated).
   */
  public static class ObfuscatedField {

    public String[] names;

    private ObfuscatedField(String... names) {
      this.names = names;
    }

    public String[] getFieldNames() {
      return this.names;
    }

    public void hackValueTo(Object instance, Object value) {
      ReflectionUtils.hackValueTo(instance, value, this.names);
    }

    public Object getValue(Object instance) {
      return ReflectionUtils.getPrivateField(instance, this.names);
    }

  }

}
