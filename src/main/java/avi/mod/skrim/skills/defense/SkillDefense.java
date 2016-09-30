package avi.mod.skrim.skills.defense;

import java.util.ArrayList;
import java.util.List;

import avi.mod.skrim.network.LevelUpPacket;
import avi.mod.skrim.network.SkillPacket;
import avi.mod.skrim.network.SkrimPacketHandler;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillAbility;
import avi.mod.skrim.skills.SkillStorage;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.utils.Reflection;
import avi.mod.skrim.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class SkillDefense extends Skill implements ISkillDefense {

	public static SkillStorage<ISkillDefense> skillStorage = new SkillStorage<ISkillDefense>();
	public int ticks = 0;
	public boolean canRegen = true;
	private static double healthPercent = 0.3;
	private static int regenLength = 15 * 20;

	public SkillDefense() {
		this(1, 0);
	}

	public static SkillAbility riteOfPassage = new SkillAbility(
		"Rite of Passage",
		25,
		"It's a reference to a magic card, so you probably missed it.",
		"Falling below 30% health activates a period of regeneration.",
		"You must fully heal before regeneration will activate again."
	);


	public SkillDefense(int level, int currentXp) {
		super("Defense", level, currentXp);
		this.iconTexture = new ResourceLocation("skrim", "textures/guis/skills/defense.png");
		this.addAbilities(riteOfPassage);
	}

	public double getDamageReduction() {
		return this.level * 0.005;
	}

	public int getExtraArmor() {
		return (int) (this.level / 5);
	}

	public int getXp(float amount) {
		return (int) (amount * 10);
	}

	@Override
	public void addXp(EntityPlayerMP player, int xp) {
    if (xp > 0) {
      this.xp += xp;
      this.levelUp(player);
    }
  }

	@Override
	public void levelUp(EntityPlayerMP player) {
    if (this.canLevelUp()) {
      this.level++;
      SkrimPacketHandler.INSTANCE.sendTo(new LevelUpPacket(this.name, this.level), player);
			IAttributeInstance armor = player.getEntityAttribute(SharedMonsterAttributes.ARMOR);
			Reflection.hackAttributeTo(armor, "maximumValue", 20.0 + this.getExtraArmor());
		}
    SkrimPacketHandler.INSTANCE.sendTo(new SkillPacket(this.name, this.level, this.xp), player);
  }

	@Override
	public List<String> getToolTip() {
		List<String> tooltip = new ArrayList<String>();
		tooltip.add("Take §a" + Utils.formatPercent(this.getDamageReduction()) + "%§r less damage from mob and players.");
		tooltip.add("Gain an additional §a" + this.getExtraArmor() + "§r max armor.");
		return tooltip;
	}

	public static void applyDefense(LivingHurtEvent event) {
		DamageSource source = event.getSource();
		Entity entity = event.getEntity();
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			Long l = player.worldObj.getTotalWorldTime();
			if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.DEFENSE, EnumFacing.NORTH)) {
				if (source.damageType == "mob" || source.damageType == "player") {
					SkillDefense defense = (SkillDefense) player.getCapability(Skills.DEFENSE, EnumFacing.NORTH);
					defense.addXp((EntityPlayerMP) player, defense.getXp(event.getAmount()));
					event.setAmount(event.getAmount() - (float) (defense.getDamageReduction() * event.getAmount()));
				}
			}
		}
	}

	public static void riteOfPassage(LivingUpdateEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if (!player.worldObj.isRemote) {
				if (player != null && player.hasCapability(Skills.DEFENSE, EnumFacing.NORTH)) {
					SkillDefense defense = (SkillDefense) player.getCapability(Skills.DEFENSE, EnumFacing.NORTH);
					if (defense.hasAbility(1)) {
						if (defense.canRegen && player.getHealth() <= (float) (defense.healthPercent * player.getMaxHealth())) {
							player.addPotionEffect(new PotionEffect(Potion.getPotionById(10), regenLength));
							defense.canRegen = false;
						} else if (!defense.canRegen && player.getHealth() == player.getMaxHealth()) {
							defense.canRegen = true;
						}
					}
				}
			}
		}
	}

	public static void renderArmor(RenderGameOverlayEvent.Pre event) {
		if (event.getType() == ElementType.ARMOR) {
			 // event.setCanceled(true);
			 // new ArmorOverlay(Minecraft.getMinecraft());
		}
	}

}
