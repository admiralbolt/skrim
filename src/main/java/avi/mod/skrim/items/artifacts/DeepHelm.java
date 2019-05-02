package avi.mod.skrim.items.artifacts;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.items.armor.ArtifactArmor;
import avi.mod.skrim.utils.Utils;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class DeepHelm extends ArtifactArmor {

  public DeepHelm() {
    super("deep_helm", EntityEquipmentSlot.HEAD);
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    tooltip.add("§4Reduces speed on land, grants water breathing, and night vision.§r");
    tooltip.add("§e\"Our pride was a veil over our eyes.\"");
  }

  @Override
  public void getSubItems(@Nonnull CreativeTabs tab, NonNullList<ItemStack> subItems) {
    ItemStack deepHelm = new ItemStack(SkrimItems.DEEP_HELM);
    deepHelm.addEnchantment(Enchantments.DEPTH_STRIDER, 10);
    subItems.add(deepHelm);
  }

  @Mod.EventBusSubscriber(modid = Skrim.MOD_ID)
  public static class DeepHelmEventHandler {

    private static final long CHECK_TIME = 40L;
    private static final int DURATION = 50;

    private static final Map<Potion, Integer> EFFECTS = ImmutableMap.of(MobEffects.WATER_BREATHING, 1,
        MobEffects.NIGHT_VISION, 1, MobEffects.SPEED, 3);

    @SubscribeEvent
    public static void breathWater(LivingEvent.LivingUpdateEvent event) {
      Entity entity = event.getEntity();
      if (!(entity instanceof EntityPlayer)) return;

      EntityPlayer player = (EntityPlayer) entity;
      if (player.world.isRemote || !player.isInWater() || !Utils.isWearingArmor(player, SkrimItems.DEEP_HELM) || player.world.getTotalWorldTime() % CHECK_TIME != 0L)
        return;

      for (Map.Entry<Potion, Integer> effect : EFFECTS.entrySet()) {
        PotionEffect newEffect = new PotionEffect(effect.getKey(), (effect.getKey() == MobEffects.NIGHT_VISION) ? 250 : DURATION, effect.getValue(), false, false);
        Utils.addOrCombineEffect(player, newEffect);
      }

    }

  }


}
