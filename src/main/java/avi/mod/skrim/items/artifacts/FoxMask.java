package avi.mod.skrim.items.artifacts;

import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.items.armor.ArtifactArmor;
import avi.mod.skrim.utils.Utils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Faux Squad!
 */
public class FoxMask extends ArtifactArmor {

  public FoxMask() {
    super("fox_mask", EntityEquipmentSlot.HEAD);
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    tooltip.add("§4Increase jump height, speed, and grants night vision.§r");
    tooltip.add("§4Sneaking grants invisibility.§r");
    tooltip.add("§e\"I'll wear my guy fox mask.\"");
  }

  @Override
  public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
    Entity entity = entityIn;
    if (!(entity instanceof EntityPlayer)) return;

    EntityPlayer player = (EntityPlayer) entity;
    if (player.world.isRemote || !Utils.isWearingArmor(player, SkrimItems.FOX_MASK)) return;

    for (Potion potion : new Potion[]{MobEffects.SPEED, MobEffects.JUMP_BOOST}) {
      PotionEffect newEffect = new PotionEffect(potion, 10, 1, true,
              false);
      Utils.addOrCombineEffect(player, newEffect);
    }

    if (!player.isSneaking()) return;
    PotionEffect newEffect = new PotionEffect(MobEffects.INVISIBILITY, 10, 0, true, false);
    Utils.addOrCombineEffect(player, newEffect);
  }

}
