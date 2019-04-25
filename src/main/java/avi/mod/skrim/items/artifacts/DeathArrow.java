package avi.mod.skrim.items.artifacts;

import avi.mod.skrim.items.ItemBase;
import avi.mod.skrim.items.SkrimItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class DeathArrow extends ItemArrow implements ItemBase {

  private static final String NAME = "death_arrow";

  public DeathArrow() {
    this.setUnlocalizedName(NAME);
    this.setRegistryName(NAME);
  }

  @Override
  public boolean hasEffect(ItemStack stack) {
    return true;
  }

  @Override
  @Nonnull
  @SideOnly(Side.CLIENT)
  public EnumRarity getRarity(ItemStack stack) {
    return SkrimItems.ARTIFACT_RARITY;
  }

  @Override
  public String getTexturePath() {
    return "items";
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    tooltip.add("§4Instantly kills any entity hit.§r");
    tooltip.add("§e\"Let my black arrow be the answer to your violation.\"§r");
  }

  @Nonnull
  public EntityArrow createArrow(@Nonnull World worldIn, @Nonnull ItemStack stack, EntityLivingBase shooter) {
    return new EntityDeathArrow(worldIn, shooter);
  }

  public static class EntityDeathArrow extends EntityArrow {

    public EntityDeathArrow(World worldIn) {
      super(worldIn);
    }

    public EntityDeathArrow(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
    }

    public EntityDeathArrow(World worldIn, EntityLivingBase shooter) {
      super(worldIn, shooter);
    }

    @Override
    protected void arrowHit(EntityLivingBase living) {
      System.out.println("hit entity: " + living);
      living.onKillCommand();
    }

    @Override
    @Nonnull
    protected ItemStack getArrowStack() {
      return new ItemStack(SkrimItems.DEATH_ARROW);
    }

    @Override
    protected void onHit(RayTraceResult raytraceResultIn) {
      super.onHit(raytraceResultIn);
      Entity entity = raytraceResultIn.entityHit;
      if (!(entity instanceof MultiPartEntityPart)) return;

      System.out.println("hit a dragon part");

      MultiPartEntityPart dragonPart = (MultiPartEntityPart) entity;
      if (!(dragonPart.parent instanceof EntityDragon)) return;

      System.out.println("converted to a draogn...");

      EntityDragon dragon = (EntityDragon) dragonPart.parent;
      dragon.attackEntityFrom(DamageSource.causeThornsDamage(this.shootingEntity), 1000);
    }
  }


}
