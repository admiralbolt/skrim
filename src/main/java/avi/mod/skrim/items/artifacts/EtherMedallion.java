package avi.mod.skrim.items.artifacts;

import avi.mod.skrim.items.items.ArtifactItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Lightning and the thunder.
 */
public class EtherMedallion extends ArtifactItem {

  private static final int VERTICAL_REACH = 50;
  private static final int RADIUS = 12;
  private static final int BUFFER = 4;

  public EtherMedallion() {
    super("ether_medallion");
    this.setMaxDamage(100);
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    tooltip.add("§4\"Call down a lightning storm.\"§r");
    tooltip.add("§e\"Billions of volts of goodness.\"§r");
  }

  @Override
  @Nonnull
  public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand hand) {
    ItemStack itemStackIn = playerIn.getHeldItem(hand);

    if (worldIn.isRemote) return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);

    BlockPos pos = playerIn.getPosition();
    int x = pos.getX();
    int y = pos.getY();
    int z = pos.getZ();

    for (int i = x - RADIUS; i <= x + RADIUS; i++) {
      for (int j = z - RADIUS; j <= z + RADIUS; j++) {
        if (Math.abs(j - z) <= BUFFER && Math.abs(i - x) <= BUFFER) continue;
        BlockPos topBlock = worldIn.getTopSolidOrLiquidBlock(new BlockPos(i, y, j));
        worldIn.addWeatherEffect(new EntityLightningBolt(worldIn, topBlock.getX(), topBlock.getY(), topBlock.getZ(), false));
      }
    }
    AxisAlignedBB bound = new AxisAlignedBB(x - RADIUS, y - RADIUS, z - RADIUS, x + RADIUS, y + VERTICAL_REACH, z + RADIUS);
    for (EntityLivingBase entity : worldIn.getEntitiesWithinAABB(EntityLivingBase.class, bound)) {
      if (entity == playerIn) continue;
      if (entity instanceof EntityDragon) {
        DamageSource source = DamageSource.causeThornsDamage(playerIn);
        entity.attackEntityFrom(source, 160.0F);
      } else {
        entity.attackEntityFrom(DamageSource.LIGHTNING_BOLT, 20.0F);
      }
    }
    itemStackIn.damageItem(1, playerIn);
    return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
  }

}
