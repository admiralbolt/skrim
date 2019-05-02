package avi.mod.skrim.utils;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.lang.reflect.Modifier;

/**
 * Really just an extended Utils/ReflectionUtils specifically for dealing with
 * obfuscation issues.
 */
public class Obfuscation {

  public static ObfuscatedField FISH_HOOK_CATCHABLE = new ObfuscatedField("ticksCatchable", "field_146045_ax");
  public static ObfuscatedField FISH_HOOK_CAUGHT_DELAY = new ObfuscatedField("ticksCaughtDelay", "field_146040_ay");
  public static ObfuscatedField FISH_HOOK_IN_GROUND = new ObfuscatedField("inGround", "field_146051_au");
  public static ObfuscatedField FISH_HOOK_LUCK = new ObfuscatedField("luck", "field_191518_aw");
  public static ObfuscatedField FISH_HOOK_LURE_SPEED = new ObfuscatedField("lureSpeed", "field_191519_ax");

  public static ObfuscatedField HORSE_JUMP_ATTRIBUTE = new ObfuscatedField("JUMP_STRENGTH", "field_110271_bv");
  public static ObfuscatedField EXPLOSION_SIZE = new ObfuscatedField("explosionSize", "field_77280_f");
  public static ObfuscatedField ENTITY_FIRE = new ObfuscatedField("fire", "field_190534_ay");
  public static ObfuscatedField VILLAGER_BUY_LIST = new ObfuscatedField("buyingList", "field_70963_i");
  public static ObfuscatedField CURRENT_ITEM = new ObfuscatedField("itemStack", "field_70457_g");

  public static ObfuscatedField BOAT_FORWARD_DOWN = new ObfuscatedField("forwardInputDown", "field_184461_aB");
  public static ObfuscatedField BOAT_LEFT_DOWN = new ObfuscatedField("leftInputDown", "field_184480_az");
  public static ObfuscatedField BOAT_RIGHT_DOWN = new ObfuscatedField("rightInputDown", "field_184459_aA");




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

    public String obName;
    public String deobName;

    private ObfuscatedField(String deobName, String obName) {
      this.deobName = deobName;
      this.obName = obName;
    }

    public String[] getFieldNames() {
      return new String[]{this.obName, this.deobName};
    }

    public void hackValueTo(Object instance, Object value) {
      ReflectionUtils.hackValueTo(instance, value, this.obName, this.deobName);
    }

    public Object getValue(Object instance) {
      return ReflectionUtils.getPrivateField(instance, this.obName, this.deobName);
    }

  }

}
