package avi.mod.skrim.skills.ranged;

import avi.mod.skrim.init.SkrimSoundEvents;
import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.network.SkrimPacketHandler;
import avi.mod.skrim.network.skillpackets.CriticalAscensionPacket;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillAbility;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

import java.util.ArrayList;
import java.util.List;

public class SkillRanged extends Skill implements ISkillRanged {

  private static final int MAX_STACKS = 999;
  private static final int GLOW_DURATION = 120;


  public static SkillStorage<ISkillRanged> skillStorage = new SkillStorage<>();


  private static SkillAbility SNEAK_ATTACK = new SkillAbility("ranged", "Sneak Attack", 25, "Surprise Motherfucker.",
      "Deal 25% extra damage to enemies while crouching & undetected.");

  private static SkillAbility FAERIE_FIRE = new SkillAbility("ranged", "Faerie Fire", 50, "Light that shit up.",
      "Your arrows automatically light up the enemy.");

  private static SkillAbility GREAT_BOW = new SkillAbility("ranged", "Great Bow", 75, "gr8 bow m8, I r8 8/8.",
      "Gain the ability to craft a powerful great bow.");

  private static SkillAbility CRITICAL_ASCENSION = new SkillAbility("ranged", "Critical Ascension", 100, "Boom, Headshot.",
      "Killing an enemy with a bow grants a stack of accuracy.", "Taking damage removes 10 stacks of accuracy.", "Dying removes all " +
      "stacks " +
      "of accuracy.",
      "Each stack of accuracy grants §a+0.5%" + SkillAbility.DESC_COLOR + " ranged damage.");

  private int accuracyStacks = 0;

  public SkillRanged() {
    this(1, 0);
  }

  public SkillRanged(int level, int currentXp) {
    super("Ranged", level, currentXp);
    this.addAbilities(SNEAK_ATTACK, FAERIE_FIRE, GREAT_BOW, CRITICAL_ASCENSION);
  }

  private double getExtraDamage() {
    return this.level * 0.0075 + this.accuracyStacks * 0.005;
  }

  public float getChargeReduction() {
    return this.level * 0.005f;
  }

  public int getStacks() {
    return this.accuracyStacks;
  }

  public void setStacks(int stacks) {
    this.accuracyStacks = stacks;
    this.verifyStacks();
  }

  private void addStacks(int add) {
    this.accuracyStacks += add;
    this.verifyStacks();
  }

  private void verifyStacks() {
    if (this.accuracyStacks < 0) {
      this.accuracyStacks = 0;
    } else if (this.accuracyStacks > MAX_STACKS) {
      this.accuracyStacks = MAX_STACKS;
    }
  }

  @Override
  public List<String> getToolTip() {
    List<String> tooltip = new ArrayList<>();
    tooltip.add("Ranged attacks deal §a" + Utils.formatPercentTwo(this.getExtraDamage()) + "%§r extra damage.");
    tooltip.add("Bows take §a" + Utils.formatPercentTwo(this.getChargeReduction()) + "%§r less time to charge.");
    return tooltip;
  }

  public static void applyRanged(LivingHurtEvent event) {
    DamageSource source = event.getSource();
    if (!source.isProjectile()) return;

    Entity entity = source.getTrueSource();
    if (!(entity instanceof EntityPlayer)) return;

    EntityPlayer player = (EntityPlayer) entity;
    SkillRanged ranged = Skills.getSkill(player, Skills.RANGED, SkillRanged.class);
    event.setAmount(event.getAmount() + (float) (ranged.getExtraDamage() * event.getAmount()));
    EntityLivingBase targetEntity = event.getEntityLiving();


    if (ranged.hasAbility(1)) {
      if (player.isSneaking() && targetEntity.getAttackingEntity() != player) {
        event.setAmount((float) (event.getAmount() * 1.25));
        player.world.playSound(null, player.getPosition(), SkrimSoundEvents.SNEAK_ATTACK, player.getSoundCategory(),
            0.2F,
            1.0F);
      }
      if (ranged.hasAbility(2)) {
        targetEntity.addPotionEffect(new PotionEffect(MobEffects.GLOWING, GLOW_DURATION, 0, true, false));
      }
    }
    ranged.addXp((EntityPlayerMP) player, (int) event.getAmount() * 10);
  }

  public static void handleKill(LivingDeathEvent event) {
    DamageSource source = event.getSource();
    if (!source.isProjectile()) return;

    Entity entity = source.getTrueSource();
    if (!(entity instanceof EntityPlayer)) return;

    EntityPlayer player = (EntityPlayer) entity;
    SkillRanged ranged = Skills.getSkill(player, Skills.RANGED, SkillRanged.class);

    ranged.addXp((EntityPlayerMP) player, Skills.entityKillXp(event.getEntity()));

    // We only want to give stacks when a player kills a non-passive entity.
    if (ranged.hasAbility(4) && Skills.entityKillXp(event.getEntity()) > 0) {
      ranged.addStacks(1);
      SkrimPacketHandler.INSTANCE.sendTo(new CriticalAscensionPacket(ranged.getStacks()), (EntityPlayerMP) player);
    }
  }

  public static void removeAscensionStacks(LivingHurtEvent event) {
    Entity entity = event.getEntity();
    if (!(entity instanceof EntityPlayer)) return;

    if (event.getAmount() <= 0) return;

    EntityPlayer player = (EntityPlayer) entity;
    SkillRanged ranged = Skills.getSkill(player, Skills.RANGED, SkillRanged.class);
    ranged.addStacks(-10);
    SkrimPacketHandler.INSTANCE.sendTo(new CriticalAscensionPacket(ranged.getStacks()), (EntityPlayerMP) player);
  }

  public static void verifyItems(ItemCraftedEvent event) {
    if (event.player.world.isRemote) return;

    Item targetItem = event.crafting.getItem();
    if (targetItem == SkrimItems.GREAT_BOW) {
      if (!Skills.canCraft(event.player, Skills.RANGED, 75)) {
        Skills.replaceWithComponents(event);
        return;
      }
      SkillRanged ranged = Skills.getSkill(event.player, Skills.RANGED, SkillRanged.class);
      ranged.addXp((EntityPlayerMP) event.player, 500);
    }
  }

}
