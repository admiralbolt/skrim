package avi.mod.skrim.items.artifacts;

import java.util.List;

import avi.mod.skrim.items.ModItems;
import avi.mod.skrim.network.SetBlockPacket;
import avi.mod.skrim.network.SkrimPacketHandler;
import avi.mod.skrim.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

public class CeruleanSandals extends ArtifactArmor {

	public CeruleanSandals() {
		super("cerulean_sandals", EntityEquipmentSlot.FEET);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean par4) {
		tooltip.add("§4Walk on water.§r");
		tooltip.add("§e\"Jesus baby.\"");
	}

	public static class Handler {

		public static void walkOnWater(LivingUpdateEvent event) {
			Entity entity = event.getEntity();
			if (entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entity;
				if (player.world.isRemote && Utils.isWearingArmor(player, ModItems.CERULEAN_SANDALS)) {
					BlockPos playerLocation = new BlockPos(player.posX, player.posY, player.posZ);
					IBlockState onState = player.world.getBlockState(playerLocation.add(0, -1, 0));
					if (onState.getBlock() == Blocks.WATER || onState.getBlock() == Blocks.FLOWING_WATER) {
						SkrimPacketHandler.INSTANCE.sendToServer(new SetBlockPacket(Block.getStateId(Blocks.ICE.getDefaultState()), player.posX,
								player.posY - 1, player.posZ));
						if (player.motionY <= 0) {
							player.motionY = 0;
						}
					}
				}
			}
		}

	}

}
