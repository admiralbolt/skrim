package avi.mod.skrim.skills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import avi.mod.skrim.skills.*;
import avi.mod.skrim.skills.blacksmithing.BlacksmithingProvider;
import avi.mod.skrim.skills.blacksmithing.ISkillBlacksmithing;
import avi.mod.skrim.skills.botany.BotanyProvider;
import avi.mod.skrim.skills.botany.ISkillBotany;
import avi.mod.skrim.skills.cooking.CookingProvider;
import avi.mod.skrim.skills.cooking.ISkillCooking;
import avi.mod.skrim.skills.defense.DefenseProvider;
import avi.mod.skrim.skills.defense.ISkillDefense;
import avi.mod.skrim.skills.demolition.DemolitionProvider;
import avi.mod.skrim.skills.demolition.ISkillDemolition;
import avi.mod.skrim.skills.digging.ISkillDigging;
import avi.mod.skrim.skills.digging.DiggingProvider;
import avi.mod.skrim.skills.farming.FarmingProvider;
import avi.mod.skrim.skills.farming.ISkillFarming;
import avi.mod.skrim.skills.fishing.FishingProvider;
import avi.mod.skrim.skills.fishing.ISkillFishing;
import avi.mod.skrim.skills.melee.ISkillMelee;
import avi.mod.skrim.skills.melee.MeleeProvider;
import avi.mod.skrim.skills.mining.ISkillMining;
import avi.mod.skrim.skills.mining.MiningProvider;
import avi.mod.skrim.skills.ranged.ISkillRanged;
import avi.mod.skrim.skills.ranged.RangedProvider;
import avi.mod.skrim.skills.woodcutting.ISkillWoodcutting;
import avi.mod.skrim.skills.woodcutting.WoodcuttingProvider;

public class Skills {

	public static Capability<ISkillBlacksmithing> BLACKSMITHING = BlacksmithingProvider.BLACKSMITHING;
	public static Capability<ISkillBotany> BOTANY = BotanyProvider.BOTANY;
	public static Capability<ISkillCooking> COOKING = CookingProvider.COOKING;
	public static Capability<ISkillDefense> DEFENSE = DefenseProvider.DEFENSE;
	public static Capability<ISkillDemolition> DEMOLITION = DemolitionProvider.DEMOLITION;
  public static Capability<ISkillDigging> DIGGING = DiggingProvider.DIGGING;
  public static Capability<ISkillFarming> FARMING = FarmingProvider.FARMING;
  public static Capability<ISkillFishing> FISHING = FishingProvider.FISHING;
  public static Capability<ISkillMelee> MELEE = MeleeProvider.MELEE;
  public static Capability<ISkillMining> MINING = MiningProvider.MINING;
  public static Capability<ISkillRanged> RANGED = RangedProvider.RANGED;
  public static Capability<ISkillWoodcutting> WOODCUTTING = WoodcuttingProvider.WOODCUTTING;

  public static Map<String, Capability<? extends ISkill>> skillMap = new HashMap<String, Capability<? extends ISkill>>();
  public static List<Capability<? extends ISkill>> ALL_SKILLS = new ArrayList<Capability<? extends ISkill>>();

	public static void register() {
    MiningProvider.register();
    WoodcuttingProvider.register();
    DiggingProvider.register();
    FarmingProvider.register();
    BotanyProvider.register();
    CookingProvider.register();
    BlacksmithingProvider.register();
    FishingProvider.register();
    DemolitionProvider.register();
    MeleeProvider.register();
    RangedProvider.register();
    DefenseProvider.register();

    /**
     * This functionality should really be handled in the providers themselves.
     */

    BLACKSMITHING = BlacksmithingProvider.BLACKSMITHING;
  	BOTANY = BotanyProvider.BOTANY;
  	COOKING = CookingProvider.COOKING;
  	DEFENSE = DefenseProvider.DEFENSE;
  	DEMOLITION = DemolitionProvider.DEMOLITION;
    DIGGING = DiggingProvider.DIGGING;
    FARMING = FarmingProvider.FARMING;
    FISHING = FishingProvider.FISHING;
    MELEE = MeleeProvider.MELEE;
    MINING = MiningProvider.MINING;
    RANGED = RangedProvider.RANGED;
    WOODCUTTING = WoodcuttingProvider.WOODCUTTING;

    ALL_SKILLS.add(BLACKSMITHING);
    ALL_SKILLS.add(BOTANY);
    ALL_SKILLS.add(COOKING);
    ALL_SKILLS.add(DEFENSE);
    ALL_SKILLS.add(DEMOLITION);
    ALL_SKILLS.add(DIGGING);
    ALL_SKILLS.add(FARMING);
    ALL_SKILLS.add(FISHING);
    ALL_SKILLS.add(MELEE);
    ALL_SKILLS.add(MINING);
    ALL_SKILLS.add(RANGED);
    ALL_SKILLS.add(WOODCUTTING);

    skillMap.put("blacksmithing", BLACKSMITHING);
		skillMap.put("botany", BOTANY);
		skillMap.put("cooking", COOKING);
		skillMap.put("defense", DEFENSE);
		skillMap.put("demolition", DEMOLITION);
		skillMap.put("digging", DIGGING);
		skillMap.put("farming", FARMING);
		skillMap.put("fishing", FISHING);
		skillMap.put("melee", MELEE);
		skillMap.put("mining", MINING);
		skillMap.put("ranged", RANGED);
		skillMap.put("woodcutting", WOODCUTTING);
  }

	public static boolean canCraft(EntityPlayer player, Capability<? extends ISkill> cap, int level) {
		if (player != null && player.hasCapability(cap, EnumFacing.NORTH)) {
			Skill skill = (Skill) player.getCapability(cap, EnumFacing.NORTH);
			if (skill.level >= level) {
				return true;
			}
		}
		return false;
	}

	public static void destroyComponents(ItemCraftedEvent event) {
		if (event.player.inventory != null) {
			event.player.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, (float) (Math.random() - Math.random()) * 0.2F);
			event.crafting.stackSize = 0;
		}
	}

	public static void replaceWithComponents(ItemCraftedEvent event) {
		if (event.player.inventory != null) {
			Item targetItem = event.crafting.getItem();
			event.player.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, (float) (Math.random() - Math.random()) * 0.2F);
			event.crafting.stackSize = 1;
			ItemStack slot;
			Item slotItem;
			ItemStack addStack;
			boolean first = true;
			for (int i = 0; i < event.craftMatrix.getSizeInventory(); i++) {
				slot = event.craftMatrix.getStackInSlot(i);
				if (slot != null) {
					slotItem = slot.getItem();
					if (slotItem != null) {
						addStack = new ItemStack(slotItem, 1, slot.getMetadata());
						/**
						 * I honestly have no clue why this is happening.  It's
						 * recognizing the item, but can't find it's model.  If you
						 * drop then re-pick it up everything works.  Point is, we don't ever
						 * want to put glowstone in the hands of the player after crafting.
						 */
						if (first && slotItem != Items.GLOWSTONE_DUST) {
							event.crafting.setItem(slotItem);
							event.crafting.setItemDamage(slot.getMetadata());
							first = false;
						} else {
							event.player.inventory.addItemStackToInventory(addStack);
						}
					}
				}
			}
			/**
			 * Iterate through slots and destroy all items :/
			 */
			ItemStack inventoryAt;
			Item itemAt;
			for (int i = 0; i < event.player.inventory.getSizeInventory(); i++) {
				inventoryAt = event.player.inventory.getStackInSlot(i);
				if (inventoryAt != null) {
					itemAt = inventoryAt.getItem();
					if (itemAt != null && itemAt == targetItem) {
						event.player.inventory.removeStackFromSlot(i);
					}
				}
			}
		}
	}

	public static int getTotalSkillLevels(EntityPlayer player) {
		int totalLevels = 0;
		for (Capability<? extends ISkill> cap : ALL_SKILLS) {
			if (player.hasCapability(cap, EnumFacing.NORTH)) {
				Skill skill = (Skill) player.getCapability(cap, EnumFacing.NORTH);
				totalLevels += skill.level;
			}
 		}
		return totalLevels;
	}

	public static double getTotalXpBonus(EntityPlayer player) {
		return (0.01 * player.experienceLevel) + (0.001 * getTotalSkillLevels(player));
	}

}
