package avi.mod.skrim.skills.demolition;

import avi.mod.skrim.blocks.SkrimBlocks;
import avi.mod.skrim.blocks.tnt.CustomExplosion;
import avi.mod.skrim.entities.monster.BioCreeper;
import avi.mod.skrim.entities.monster.NapalmCreeper;
import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillAbility;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.utils.Obfuscation;
import avi.mod.skrim.utils.Utils;
import com.google.common.collect.Sets;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.*;

public class SkillDemolition extends Skill implements ISkillDemolition {

  public static SkillStorage<ISkillDemolition> skillStorage = new SkillStorage<>();
  private static Map<BlockPos, EntityPlayer> VALID_GO_BOOM = new HashMap<>();

  private static SkillAbility DYNAMITE = new SkillAbility("demolition", "Dynamite", 25, "Boom goes the dynamite.",
      "Grants you the ability to craft dynamite with tnt & a pickaxe.", "Dynamite has a larger blast radius and a 100% chance to drop " +
      "blocks.");
  private static SkillAbility BIOBOMB = new SkillAbility("demolition", "Bio-Bomb", 50, "A whole new meaning for c'mon BB.",
      "Grants you the ability to craft Bio-Bomb with... Stuff...", "Bio-bombs have twice the blast radius of tnt, and don't affect blocks" +
      ".");
  private static SkillAbility NAPALM = new SkillAbility("demolition", "Napalm", 75, "Handle with care.",
      "Grants you the ability to craft Napalm with... Stuff...", "Napalm has triple the blast radius of tnt, starts fires, and creates " +
      "lava spawns.");
  private static SkillAbility BADONKADONK = new SkillAbility("demolition", "Badonkadonk", 100, "Gut full of dynamite and booty like POW.",
      "Grants you the ability to craft a rocket launcher.");

  private static final Set<Item> EXPLOSIVES = Sets.newHashSet(new ItemStack(Blocks.TNT).getItem(),
      new ItemStack(SkrimBlocks.DYNAMITE).getItem(), new ItemStack(SkrimBlocks.BIOBOMB).getItem(),
      new ItemStack(SkrimBlocks.NAPALM).getItem(), new ItemStack(SkrimBlocks.FAT_BOY).getItem());


  public SkillDemolition() {
    this(1, 0);
  }

  public SkillDemolition(int level, int currentXp) {
    super("Demolition", level, currentXp);
    this.addAbilities(DYNAMITE, BIOBOMB, NAPALM, BADONKADONK);
  }

  private double getResistance() {
    return this.level * 0.01;
  }

  public double getExtraPower() {
    return this.level * 0.01;
  }

  @Override
  public List<String> getToolTip() {
    List<String> tooltip = new ArrayList<String>();
    tooltip.add("Passively gain §a" + Utils.formatPercent(this.getResistance()) + "%§r explosive resistance.");
    tooltip.add("Your explosions are §a" + Utils.formatPercent(this.getExtraPower()) + "%§r larger.");
    return tooltip;
  }

  public static void beforeGoBoom(final ExplosionEvent.Start event) {
    Explosion boom = event.getExplosion();
    final BlockPos location = new BlockPos(boom.getPosition());
    if (!VALID_GO_BOOM.containsKey(location)) return;

    EntityPlayer player = VALID_GO_BOOM.get(location);
    SkillDemolition demolition = Skills.getSkill(player, Skills.DEMOLITION, SkillDemolition.class);

    if (boom instanceof CustomExplosion) {
      CustomExplosion customBoom = (CustomExplosion) boom;
      customBoom.setExplosionSize((float) (customBoom.getExplosionSize() * (1 + demolition.getExtraPower())));
    } else {
      Obfuscation.EXPLOSION_SIZE.hackValueTo(boom, 4.0 * 1 + demolition.getExtraPower());
    }
  }

  public static void onGoBoom(final ExplosionEvent.Detonate event) {
    Explosion boom = event.getExplosion();
    final BlockPos location = new BlockPos(boom.getPosition());
    if (!VALID_GO_BOOM.containsKey(location)) return;

    EntityPlayer player = VALID_GO_BOOM.get(location);
    VALID_GO_BOOM.remove(location);
    SkillDemolition demolition = Skills.getSkill(player, Skills.DEMOLITION, SkillDemolition.class);
    demolition.addXp((EntityPlayerMP) player, 7000);
  }

  public static void onTntPlaced(BlockEvent.PlaceEvent event) {
    IBlockState state = event.getPlacedBlock();
    EntityPlayer player = event.getPlayer();
    if (state == null || player == null) return;

    if (!(state.getBlock() instanceof BlockTNT)) return;
    VALID_GO_BOOM.put(event.getPos(), player);
  }

  public static void reduceExplosion(LivingHurtEvent event) {
    Entity entity = event.getEntity();
    if (!(entity instanceof EntityPlayer) || !event.getSource().isExplosion()) return;

    EntityPlayer player = (EntityPlayer) entity;
    SkillDemolition demolition = Skills.getSkill(player, Skills.DEMOLITION, SkillDemolition.class);
    event.setAmount(event.getAmount() - (float) (event.getAmount() * demolition.getResistance()));
  }

  public static void onKillCreeper(LivingDeathEvent event) {
    Entity sourceEntity = event.getSource().getTrueSource();
    if (!(sourceEntity instanceof EntityPlayer)) return;

    Entity targetEntity = event.getEntity();
    if (!(targetEntity instanceof EntityCreeper)) return;

    EntityPlayer player = (EntityPlayer) sourceEntity;
    SkillDemolition demolition = Skills.getSkill(player, Skills.DEMOLITION, SkillDemolition.class);

    demolition.addXp((EntityPlayerMP) player, (targetEntity instanceof NapalmCreeper) ? 2500 : (targetEntity instanceof BioCreeper) ?
        1000 : 350);
  }

  public static void verifyExplosives(PlayerEvent.ItemCraftedEvent event) {
    Item targetItem = event.crafting.getItem();
    Item dynamite = new ItemStack(SkrimBlocks.DYNAMITE).getItem();
    Item biobomb = new ItemStack(SkrimBlocks.BIOBOMB).getItem();
    Item napalm = new ItemStack(SkrimBlocks.NAPALM).getItem();

    if (targetItem == dynamite) {
      if (!Skills.canCraft(event.player, Skills.DEMOLITION, 25)) {
        Skills.replaceWithComponents(event);
        event.player.world.createExplosion(null, event.player.posX, event.player.posY, event.player.posZ, 4.0F, true);
      }
    } else if (targetItem == biobomb) {
      if (!Skills.canCraft(event.player, Skills.DEMOLITION, 50)) {
        Skills.replaceWithComponents(event);
        event.player.world.createExplosion(null, event.player.posX, event.player.posY, event.player.posZ, 8.0F, true);
      }
    } else if (targetItem == napalm) {
      if (!Skills.canCraft(event.player, Skills.DEMOLITION, 75)) {
        Skills.replaceWithComponents(event);
        event.player.world.createExplosion(null, event.player.posX, event.player.posY, event.player.posZ, 12.0F, true);
      }
    } else if (targetItem == SkrimItems.ROCKET_LAUNCHER) {
      if (!Skills.canCraft(event.player, Skills.DEMOLITION, 100)) {
        Skills.replaceWithComponents(event);
      }
    }
  }

  public static boolean isExplosive(ItemStack stack) {
    if (stack == null) return false;
    return EXPLOSIVES.contains(stack.getItem());
  }

}
