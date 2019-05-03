package avi.mod.skrim.items.artifacts;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.items.ItemBase;
import avi.mod.skrim.items.SkrimItems;
import com.google.common.collect.ImmutableSet;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class EssenceAegis extends ItemShield implements ItemBase {

  private static final String NAME = "essence_aegis";

  public EssenceAegis() {
    super();
    this.setMaxDamage(1500);
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
  @Nonnull
  public String getItemStackDisplayName(ItemStack stack) {
    return "Essence Aegis";
  }

  @Override
  public String getTexturePath() {
    return "items";
  }

  @Override
  public int getItemEnchantability() {
    return 0;
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    tooltip.add("§4Take half damage from all non-physical sources.§r");
    tooltip.add("§e\"If you're killing a Shaper, you'd better go prepared.\"§r");
  }

  @Mod.EventBusSubscriber(modid = Skrim.MOD_ID)
  public static class EssenceAegisHandler {

    private static final Set<String> RESISTANCES = ImmutableSet.of(
        "inFire", "lightningBolt", "onFire", "lava", "hotFloor", "magic", "wither", "dragonBreath", "indirectMagic"
    );

    @SubscribeEvent
    public static void magicResistance(LivingHurtEvent event) {
      if (!(event.getEntity() instanceof EntityPlayer)) return;

      EntityPlayer player = (EntityPlayer) event.getEntity();
      if (player.getHeldItemOffhand().getItem() != SkrimItems.ESSENCE_AEGIS) return;

      DamageSource source = event.getSource();

      if (!RESISTANCES.contains(source.getDamageType())) return;

      event.setAmount(event.getAmount() / 2);
      player.getHeldItemOffhand().damageItem(1, player);
    }

  }


}
