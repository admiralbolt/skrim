package avi.mod.skrim.skills.brewing;

import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillAbility;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.utils.Utils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

/**
 * Most of the logic for this skill is in other places, see:
 * - {@link SkrimPotionRecipes}
 * - {@link SkrimPotionUtils}
 * - {@link PotionModifier}
 * - {@link avi.mod.skrim.items.items.SkrimPotion}
 * - {@link avi.mod.skrim.tileentity.SkrimBrewingStandEntity}
 */
public class SkillBrewing extends Skill implements ISkillBrewing {

  public static SkillStorage<ISkillBrewing> STORAGE = new SkillStorage<>();

  public static Map<Item, Integer> INGREDIENT_XP = ImmutableMap.<Item, Integer>builder()
       // Primary effects are worth the most experience.
      .put(Items.GHAST_TEAR, 425)
      .put(Items.RABBIT_FOOT, 400)
      .put(Items.MAGMA_CREAM, 350)
      .put(Items.BLAZE_POWDER, 325)
      .put(Items.SPECKLED_MELON, 300)
      .put(Items.GOLDEN_CARROT, 300)
      .put(Items.FERMENTED_SPIDER_EYE, 300)
      .put(Items.FISH, 275)
      .put(Items.SPIDER_EYE, 250)
      .put(Items.SUGAR, 200)
      // Potion type modifiers.
      .put(Items.DRAGON_BREATH, 200)
      .put(Items.GUNPOWDER, 100)
      // Worth less since they can be applied multiple times. Redstone can be applied twice as much as glowstone.
      .put(Items.GLOWSTONE_DUST, 120)
      .put(Items.REDSTONE, 60)
      .build();

  private static SkillAbility DOUBLE_BUBBLE = new SkillAbility("brewing", "Double Bubble", 25,
      "Fire burn and caldron bubble.",
      "Add an additional effect to potions you create.");

  private static SkillAbility TWO_PLACEHOLDER = new SkillAbility("brewing", "Placeholder", 50,
      "I'm impressed that you made it to level 50 before I made the ability for it.", "No description.");

  private static SkillAbility THREE_PLACEHOLDER = new SkillAbility("brewing", "Placeholder", 75,
      "Okay, what the actual fuck.", "No description.");

  private static SkillAbility CHEMIST = new SkillAbility("brewing", "Chemist", 100,
      "Morrowind levels of broken.",
      "Add any number of additional effects to potions you create.");

  public SkillBrewing() {
    this(1, 0);
  }

  public SkillBrewing(int level, int xp) {
    super("Brewing", level, xp);
    this.addAbilities(DOUBLE_BUBBLE, TWO_PLACEHOLDER, THREE_PLACEHOLDER, CHEMIST);
  }

  @Override
  public List<String> getToolTip() {
    return ImmutableList.of(
        "Potions take §a" + Utils.formatPercent(this.brewSpeed()) + "%§r less time to brew.",
        "You can apply §a" + this.totalModifiers() + "§r levels of modification to your potions.");
  }

  public int totalModifiers() {
    return 2 + this.level / 15;
  }

  public double brewSpeed() {
    return Math.min(this.level * 0.005, 1.00);
  }



}
