package avi.mod.skrim.items.artifacts;

import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.items.items.ArtifactItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Staff of fireballs!
 */
public class FireStaff extends ArtifactItem {

  private static final int FIREBALL_RADIUS = 3;
  private static final double FIREBALL_SPEED = 0.5;
  private static final double VERTICAL_OFFSET = 1;


  public FireStaff() {
    super("fire_staff");
    this.setMaxDamage(512);
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    tooltip.add("§4\"Grants immunity to fire while held.\"§r");
    tooltip.add("§4\"Also shoots fireballs.\"§r");
    tooltip.add("§e\"A wizard's best friend.\"§r");
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand hand) {
    ItemStack itemStackIn = playerIn.getHeldItem(hand);
    if (worldIn.isRemote) return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);

    Vec3d vec3d = playerIn.getLookVec();
    worldIn.playEvent(null, 1016, new BlockPos(playerIn), 0);
    EntityLargeFireball entitylargefireball = new EntityLargeFireball(worldIn, playerIn, 0, 0, 0);
    entitylargefireball.explosionPower = FIREBALL_RADIUS;
    entitylargefireball.posX = playerIn.posX + vec3d.x * 2;
    entitylargefireball.posY = playerIn.posY + vec3d.y * 2 + VERTICAL_OFFSET;
    entitylargefireball.posZ = playerIn.posZ + vec3d.z * 2;
    entitylargefireball.accelerationX = vec3d.x * FIREBALL_SPEED;
    entitylargefireball.accelerationY = vec3d.y * FIREBALL_SPEED;
    entitylargefireball.accelerationZ = vec3d.z * FIREBALL_SPEED;
    worldIn.spawnEntity(entitylargefireball);
    itemStackIn.damageItem(1, playerIn);
    return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
  }

  public static class FireStaffHandler {

    // Immunity to fire, not explosions. An unlucky wizard is probably gonna commit sudoku on accident.
    public static void fireImmunity(LivingHurtEvent event) {
      Entity entity = event.getEntity();
      if (!(entity instanceof EntityPlayer)) return;
      EntityPlayer player = (EntityPlayer) entity;
      if (player.getHeldItemMainhand().getItem() != SkrimItems.FIRE_STAFF && player.getHeldItemOffhand().getItem() != SkrimItems.FIRE_STAFF)
        return;

      if (!event.getSource().isFireDamage()) return;

      event.setAmount(0);
      player.extinguish();
      event.setCanceled(true);
    }
  }
}
