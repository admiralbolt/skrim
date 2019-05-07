package avi.mod.skrim.items.artifacts;

import avi.mod.skrim.items.items.ArtifactItem;
import avi.mod.skrim.utils.Utils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGeneratorBonusChest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Chance to generate a chest with a high probability of containing artifacts. Also spawns a bunch of enemies.
 */
public class LamentConfigurum extends ArtifactItem {

  public LamentConfigurum() {
    super("lament_configurum");
    this.setMaxDamage(5);
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    tooltip.add("§4Chance at spawning a chest, only works at night.§r");
    tooltip.add("§e\"Who knows what mysterious wonders it contains?\"§r");
  }

  @Override
  @Nonnull
  public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand hand) {
    ItemStack itemStackIn = playerIn.getHeldItem(hand);

    if (worldIn.isRemote || worldIn.isDaytime()) return new ActionResult<>(EnumActionResult.FAIL, itemStackIn);

    itemStackIn.damageItem(1, playerIn);
    if (Math.random() >= (0.6 + 0.1 * playerIn.getLuck())) return new ActionResult<>(EnumActionResult.FAIL, itemStackIn);

    WorldGeneratorBonusChest generator = new WorldGeneratorBonusChest();
    // The actual chest generation seems a little flaky, so in the interest of not being a complete asshole, we won't spawn enemies
    // unless a chest spawns.
    if (!generator.generate(worldIn, Utils.rand, playerIn.getPosition())) return new ActionResult<>(EnumActionResult.FAIL, itemStackIn);

    EntityZombie zombie = null;
    for (int i = 0; i < Utils.randInt(3, 5); i++) {
      zombie = new EntityZombie(worldIn);
      zombie.setPosition(playerIn.posX + Utils.randInt(-2, 2), playerIn.posY, playerIn.posZ + Utils.randInt(-2, 2));
      worldIn.spawnEntity(zombie);
    }

    EntitySkeleton skeleton = null;
    for (int i = 0; i < Utils.randInt(2, 4); i++) {
      skeleton = new EntitySkeleton(worldIn);
      skeleton.setPosition(playerIn.posX + Utils.randInt(-2, 2), playerIn.posY, playerIn.posZ + Utils.randInt(-2, 2));
      worldIn.spawnEntity(skeleton);
    }

    EntityWitherSkeleton witherSkeleton = null;
    for (int i = 0; i < Utils.randInt(1, 3); i++) {
      witherSkeleton = new EntityWitherSkeleton(worldIn);
      witherSkeleton.setPosition(playerIn.posX + Utils.randInt(-2, 2), playerIn.posY, playerIn.posZ + Utils.randInt(-2, 2));
      worldIn.spawnEntity(witherSkeleton);
    }

    return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
  }

}
