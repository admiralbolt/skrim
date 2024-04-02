package avi.mod.skrim.items.artifacts;

import avi.mod.skrim.items.items.ArtifactItem;
import avi.mod.skrim.utils.Utils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class LeafClover extends ArtifactItem {

  public LeafClover() {
    super("leaf_clover");
    this.setMaxDamage(3000);
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    tooltip.add("§e\"Luck is on your side.\"§r");
  }

  @Override
  public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
    Entity entity = entityIn;
    if (!(entity instanceof EntityPlayer)) return;

    EntityPlayer player = (EntityPlayer) entity;
    if (player.world.isRemote) return;

    PotionEffect luck = new PotionEffect(MobEffects.LUCK, 10, 5);
    Utils.addOrCombineEffect(player, luck);
  }

}
