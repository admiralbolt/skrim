package avi.mod.skrim.items.artifacts;

import avi.mod.skrim.Skrim;
import avi.mod.skrim.entities.passive.EntityPumpkow;
import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.items.items.ArtifactItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.List;

public class PumpkinStone extends ArtifactItem {

  public PumpkinStone() {
    super("pumpkin_stone");
    this.setMaxDamage(1);
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    tooltip.add("§4Used for making Pumpkows!§r");
    tooltip.add("§e\"What? Cow is evolving!\"§r");
  }

  @Mod.EventBusSubscriber(modid = Skrim.MOD_ID)
  public static class PumpkinStoneHandler {

    @SubscribeEvent
    public static void evolveCow(PlayerInteractEvent.EntityInteract event) {
      if (event.getItemStack().getItem() != SkrimItems.PUMPKIN_STONE) return;
      if (!(event.getTarget() instanceof EntityCow)) return;

      EntityCow cow = (EntityCow) event.getTarget();
      cow.setDead();
      if (event.getEntityPlayer().world.isRemote) {
        event.getEntityPlayer().world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, false, cow.posX, cow.posY + (cow.height / 2),
            cow.posZ, 1, 0.0D, 0.0D, 0, 0);
        return;
      }

      EntityCow entitycow = new EntityPumpkow(cow.world);
      entitycow.setLocationAndAngles(cow.posX, cow.posY, cow.posZ, cow.rotationYaw, cow.rotationPitch);
      entitycow.setHealth(cow.getHealth());
      entitycow.renderYawOffset = cow.renderYawOffset;

      if (cow.hasCustomName()) {
        entitycow.setCustomNameTag(cow.getCustomNameTag());
      }

      cow.world.spawnEntity(entitycow);
      event.getItemStack().damageItem(2, event.getEntityPlayer());
    }

  }


}
