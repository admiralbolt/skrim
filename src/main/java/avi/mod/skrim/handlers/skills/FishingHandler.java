package avi.mod.skrim.handlers.skills;

import java.util.Timer;
import java.util.TimerTask;

import net.minecraft.command.ICommandManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import avi.mod.skrim.Utils;
import avi.mod.skrim.skills.RandomTreasure;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.fishing.SkillFishing;

public class FishingHandler {

  @SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onFishEvent(PlayerInteractEvent event) {
		ItemStack stack = event.getItemStack();
		if (stack != null) {
			Item item = stack.getItem();
			if (item != null && item == Items.FISHING_ROD) {
				EntityPlayer player = event.getEntityPlayer();
				if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.FISHING, EnumFacing.NORTH)) {
					Entity entity = event.getEntity();
					EntityFishHook fishHook = player.fishEntity;
					if (fishHook != null) {
						if (!fishHook.isAirBorne) {
							final SkillFishing fishing = (SkillFishing) player.getCapability(Skills.FISHING, EnumFacing.NORTH);
							if (fishHook.onGround) {
								if (fishing.canGrapple()) {
									MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
									ICommandManager cm = server.getCommandManager();
									BlockPos pos = fishHook.getPosition();
									cm.executeCommand(server, "/tp " + player.getName() + " " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
								}
							} else if (fishHook.isInWater()) {
								fishing.canCatch = true;
								new Timer().schedule(
									new TimerTask() {
										@Override
										public void run() {
											fishing.canCatch = false;
										}
									}, 900
								);
							}
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onItemPickup(EntityItemPickupEvent event) {
		EntityPlayer player = event.getEntityPlayer();
		if (player != null && player instanceof EntityPlayerMP && player.hasCapability(Skills.FISHING, EnumFacing.NORTH)) {
			SkillFishing fishing = (SkillFishing) player.getCapability(Skills.FISHING, EnumFacing.NORTH);
			EntityItem item = event.getItem();
			if (fishing.isValidFish(item)) {
				double random = Math.random();
				fishing.canCatch = false;
				fishing.xp += fishing.getXp(Utils.snakeCase(event.getItem().getName()));
				player.worldObj.spawnEntityInWorld(new EntityXPOrb(player.worldObj, player.posX, player.posY, player.posZ, fishing.randomXPOrb()));
				if (random < fishing.getTreasureChance()) {
					player.worldObj.spawnEntityInWorld(new EntityItem(player.worldObj, player.posX, player.posY, player.posZ, RandomTreasure.generate()));
					fishing.xp += 50; // And an xp bonus!
				}
				fishing.levelUp((EntityPlayerMP) player);
			}
		}
	}

}
