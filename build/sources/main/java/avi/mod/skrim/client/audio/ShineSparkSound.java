package avi.mod.skrim.client.audio;

import avi.mod.skrim.init.SkrimSoundEvents;
import avi.mod.skrim.items.artifacts.PowerSuitChestplate;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.cooking.SkillCooking;
import avi.mod.skrim.utils.MathUtils;
import avi.mod.skrim.utils.Utils;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ShineSparkSound extends MovingSound {

	private EntityPlayer player;

	private static float MAX_PITCH = 1.0F;
	private static float MAX_VOLUME = 0.35F;

	public ShineSparkSound(EntityPlayer player) {
		super(SkrimSoundEvents.SHINESPARK_LOOP, SoundCategory.PLAYERS);
		this.player = player;
		this.repeat = true;
		this.repeatDelay = 0;
		this.volume = MAX_VOLUME;
		this.pitch = MAX_PITCH;
	}

	@Override
	public void update() {
		ItemStack armorStack = Utils.getArmor(this.player, EntityEquipmentSlot.CHEST);
		Item armor = armorStack.getItem();
		if (armor instanceof PowerSuitChestplate) {
			PowerSuitChestplate chozoChest = (PowerSuitChestplate) armor;
			System.out.println("shine.isspark: " + chozoChest.spark);
			if (chozoChest.spark) {
				this.xPosF = (float) this.player.posX;
				this.yPosF = (float) this.player.posY;
				this.zPosF = (float) this.player.posZ;
			} else {
				System.out.println("donePlaying..?");
				this.repeat = false;
				this.donePlaying = true;
			}
		}
	}

}
