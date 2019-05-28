package avi.mod.skrim.items.artifacts;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.init.SkrimSoundEvents;
import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.items.weapons.ArtifactSword;
import avi.mod.skrim.utils.Utils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;
import java.util.Random;

public class Casey extends ArtifactSword {

  public Casey() {
    super("casey", SkrimItems.ARTIFACT_DEFAULT);
    this.setMaxDamage(1000);
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    tooltip.add("§4Buh-bye now.§r");
    tooltip.add("§e\"And now the air is shattered by the force of Casey's blow.\"§r");
  }

  @Override
  public void getSubItems(@Nonnull CreativeTabs tab, NonNullList<ItemStack> subItems) {
    ItemStack pick = new ItemStack(SkrimItems.CASEY);
    pick.addEnchantment(Enchantments.KNOCKBACK, 50);
    subItems.add(pick);
  }

  @Mod.EventBusSubscriber(modid = Skrim.MOD_ID)
  public static class CaseyHandler {

    @SubscribeEvent
    public static void homeRun(LivingHurtEvent event) {
      Entity entity = event.getSource().getTrueSource();
      if (!(entity instanceof EntityPlayer)) return;

      EntityPlayer player = (EntityPlayer) entity;
      if (player.getHeldItemMainhand().getItem() != SkrimItems.CASEY) return;

      player.world.playSound(null, player.getPosition(), SkrimSoundEvents.HOME_RUN, SoundCategory.PLAYERS, 150.0F, 1.0F);
    }

    /**
     * The minecraft knockback event except without the hard cap on the vertical vector so we get a true "home run."
     * Knockback code adapted from {@link EntityLivingBase#knockBack()}
     * @param event
     */
    @SubscribeEvent
    public static void blastOff(LivingKnockBackEvent event) {
      Entity entity = event.getAttacker();
      if (!(entity instanceof EntityPlayer)) return;

      EntityPlayer player = (EntityPlayer) entity;
      Item sword = player.getHeldItem(EnumHand.MAIN_HAND).getItem();
      if (sword != SkrimItems.CASEY) return;

      // Intercept knockback event and cancel it so that we can apply custom knockback.
      event.setCanceled(true);

      EntityLivingBase target = event.getEntityLiving();

      if (Utils.rand.nextDouble() < target.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getAttributeValue()) return;
      
      float strength = event.getStrength(); 
      double xRatio = event.getRatioX(); 
      double zRatio = event.getRatioZ();
          
      target.isAirBorne = true;
      float f = MathHelper.sqrt(xRatio * xRatio + zRatio * zRatio);
      target.motionX /= 2.0;
      target.motionZ /= 2.0;
      target.motionY /= 2.0;
      target.motionX -= (xRatio * strength) / f;
      target.motionZ -= (zRatio * strength) / f;
      target.motionY += strength / 5.0;
    }
  }
}
