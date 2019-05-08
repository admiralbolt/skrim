package avi.mod.skrim.items.artifacts;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.items.tools.ArtifactHoe;
import avi.mod.skrim.utils.Utils;
import com.google.common.collect.Multimap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class WretchedScythe extends ArtifactHoe {

  // Increased damage per soul harvested.
  private static final float SCALING = 0.01f;

  public WretchedScythe() {
    super("wretched_scythe", SkrimItems.ARTIFACT_DEFAULT);
    this.setMaxDamage(1200);
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    tooltip.add("§4Gains power as more enemies are killed.§r");
    tooltip.add("§4Souls harvested: " + stack.getTagCompound().getInteger("souls") + ".§r");
    tooltip.add("§4Deals " + Utils.formatPercentTwo(stack.getTagCompound().getInteger("souls") * SCALING) + "% more damage.§r");
    tooltip.add("§e\"Listen closely, and you can hear the whispers of the damned.\"§r");
  }

  @Override
  public void getSubItems(@Nonnull CreativeTabs tab, NonNullList<ItemStack> subItems) {
    ItemStack scythe = new ItemStack(SkrimItems.WRETCHED_SCYTHE);
    NBTTagCompound compound = new NBTTagCompound();
    compound.setInteger("souls", 0);
    scythe.setTagCompound(compound);
    subItems.add(scythe);
  }

  @Override
  @Nonnull
  public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
    Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);

    if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
      multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 2.0D
          , 0));
      multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier",
          5.0, 0));
    }

    return multimap;
  }


  @Mod.EventBusSubscriber(modid = Skrim.MOD_ID)
  public static class WretchedScytheHandler {

    @SubscribeEvent
    public static void addSouls(LivingDeathEvent event) {
      Entity sourceEntity = event.getSource().getTrueSource();
      if (!(sourceEntity instanceof EntityPlayer)) return;

      EntityPlayer player = (EntityPlayer) sourceEntity;
      if (player.getHeldItemMainhand().getItem() != SkrimItems.WRETCHED_SCYTHE) return;

      ItemStack scythe = player.getHeldItemMainhand();
      NBTTagCompound compound = scythe.getTagCompound();

      compound.setInteger("souls", compound.getInteger("souls") + 1);
      scythe.setTagCompound(compound);
    }

    @SubscribeEvent
    public static void doMoreDamage(LivingHurtEvent event) {
      Entity sourceEntity = event.getSource().getTrueSource();
      if (!(sourceEntity instanceof EntityPlayer)) return;

      EntityPlayer player = (EntityPlayer) sourceEntity;
      if (player.getHeldItemMainhand().getItem() != SkrimItems.WRETCHED_SCYTHE) return;

      ItemStack scythe = player.getHeldItemMainhand();
      NBTTagCompound compound = scythe.getTagCompound();
      event.setAmount(event.getAmount() * (1 + SCALING * compound.getInteger("souls")));
    }

  }
}
