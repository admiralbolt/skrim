package avi.mod.skrim.items.artifacts;

import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.items.weapons.ArtifactSword;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.cooking.SkillCooking;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Chicken chicken chicken, which combo you pickin'?
 */
public class CanesSword extends ArtifactSword {

  private static double SWEEP_RANGE = 2.0D;

  public CanesSword() {
    super("raising_canes_fry_sword", SkrimItems.ARTIFACT_DEFAULT);
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    tooltip.add("§4Sweep attack ignites enemies.");
    tooltip.add("§4Deals 10x damage to chickens & fries them.§r");
    tooltip.add("§e\"Chicken chicken chicken, which combo you pickin'?\"");
  }

  @Override
  public void getSubItems(@Nonnull CreativeTabs tab, NonNullList<ItemStack> subItems) {
    ItemStack caneStack = new ItemStack(SkrimItems.CANES_SWORD);
    caneStack.addEnchantment(Enchantments.FIRE_ASPECT, 2);
    subItems.add(caneStack);
  }

  @Override
  public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
    if (!(attacker instanceof EntityPlayer)) return true;
    EntityPlayer player = (EntityPlayer) attacker;
    if (stack.getItem() != SkrimItems.CANES_SWORD || !canSweep(player, target)) return true;
    doFireSweep(player, target);
    return true;
  }

  /**
   * This is definitely stolen from some base game code, not sure where good luck future me.
   */
  private static boolean canSweep(EntityPlayer player, EntityLivingBase targetEntity) {
    boolean flag1 = player.isSprinting();
    boolean flag2 = player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder() && !player.isInWater()
        && !player.isPotionActive(MobEffects.BLINDNESS) && !player.isRiding() && targetEntity != null;

    double d0 = (double) (player.distanceWalkedModified - player.prevDistanceWalkedModified);

    return (!flag2 && !flag1 && player.onGround && d0 < (double) player.getAIMoveSpeed());
  }

  /**
   * Set everything in an aoe on fire!
   */
  private static void doFireSweep(EntityPlayer player, EntityLivingBase targetEntity) {
    for (EntityLivingBase entitylivingbase : player.world.getEntitiesWithinAABB(EntityLivingBase.class,
        targetEntity.getEntityBoundingBox().expand(SWEEP_RANGE, SWEEP_RANGE, SWEEP_RANGE).expand(-1 * SWEEP_RANGE, -1 * SWEEP_RANGE,
            -1 * SWEEP_RANGE))) {

      if (entitylivingbase == player || entitylivingbase == targetEntity || player.isOnSameTeam(entitylivingbase))
        continue;

      entitylivingbase.setFire(4);
    }
  }

  public static class CanesHandler {

    /**
     * Deal 10x damage to chickens, because fuck chickens.
     */
    public static void slayChicken(LivingHurtEvent event) {
      DamageSource source = event.getSource();
      Entity entity = source.getTrueSource();
      if (!(entity instanceof EntityPlayer) || !(event.getEntity() instanceof EntityChicken)) return;

      EntityPlayer player = (EntityPlayer) entity;
      Item sword = player.getHeldItem(EnumHand.MAIN_HAND).getItem();
      if (sword != SkrimItems.CANES_SWORD) return;

      event.setAmount(event.getAmount() * 10);
    }

    /**
     * Deep fries every chicken into a delicious golden treat every time.
     */
    public static void fryChicken(LivingDropsEvent event) {
      if (!(event.getEntity() instanceof EntityChicken)) return;

      DamageSource source = event.getSource();
      Entity sourceEntity = source.getTrueSource();
      if (!(sourceEntity instanceof EntityPlayer)) return;

      EntityPlayer player = (EntityPlayer) sourceEntity;
      Item sword = player.getHeldItem(EnumHand.MAIN_HAND).getItem();
      if (sword != SkrimItems.CANES_SWORD) return;

      SkillCooking cooking = Skills.getSkill(player, Skills.COOKING, SkillCooking.class);

      List<EntityItem> drops = event.getDrops();
      for (int i = 0; i < drops.size(); i++) {
        EntityItem item = drops.get(i);
        if (item.getName().equals("item.item.chickenCooked") || item.getName().equals("item.item.chickenRaw")) {
          ItemStack chickenStrips = new ItemStack(SkrimItems.CANES_CHICKEN, item.getItem().getCount());
          NBTTagCompound compound = new NBTTagCompound();
          NBTTagCompound customName = new NBTTagCompound();
          compound.setInteger("level", cooking.level);

          // Set a custom name based on who cooked it.
          customName.setString("Name", player.getName() + "'s " + chickenStrips.getDisplayName());
          compound.setTag("display", customName);
          chickenStrips.setTagCompound(compound);
          drops.set(i, new EntityItem(player.world, item.posX, item.posY, item.posZ, chickenStrips));
          if (player instanceof EntityPlayerMP) {
            cooking.addXp((EntityPlayerMP) player, SkillCooking.getXp("chickencooked"));
          }
        }
      }
    }

  }

}
