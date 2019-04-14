package avi.mod.skrim.items.artifacts;

import avi.mod.skrim.items.ModItems;
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
 * Run real fast but gives you blindness and nausea.
 */
public class BlindingBoots extends ArtifactArmor {

  public BlindingBoots() {
    super("boots_of_blinding_speed", EntityEquipmentSlot.FEET);
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    tooltip.add("§4Gain absurdly enhanced speed.§r");
    tooltip.add("§4Gain blindness and nausea.§r");
    tooltip.add("§e\"Move fast and break shit.\"");
  }

  public static class BlindingBootsHandler {

    public static List<Potion> effects = new ArrayList<Potion>();
    public static Map<Potion, Integer> effectStrength = new HashMap<Potion, Integer>();

    static {
      effects.add(MobEffects.NAUSEA);
      effectStrength.put(MobEffects.NAUSEA, 0);

      effects.add(MobEffects.BLINDNESS);
      effectStrength.put(MobEffects.BLINDNESS, 0);

      effects.add(MobEffects.SPEED);
      effectStrength.put(MobEffects.SPEED, 10);
    }

    public static void goFast(LivingUpdateEvent event) {
      Entity entity = event.getEntity();
      if (!(entity instanceof EntityPlayer)) return;
      EntityPlayer player = (EntityPlayer) entity;
      if (player.world.isRemote || !Utils.isWearingArmor(player, ModItems.BLINDING_BOOTS) || player.world.getTotalWorldTime() % 40L != 0L)
        return;
      for (Potion potion : effects) {
        PotionEffect newEffect = new PotionEffect(potion, 100, effectStrength.get(potion), false, false);
        Utils.addOrCombineEffect(player, newEffect);
      }
    }
  }

}