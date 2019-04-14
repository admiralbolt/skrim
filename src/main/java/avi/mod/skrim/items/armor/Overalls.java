package avi.mod.skrim.items.armor;

import avi.mod.skrim.items.SkrimItems;
import avi.mod.skrim.network.SkrimPacketHandler;
import avi.mod.skrim.network.skillpackets.ApplyBonemealPacket;
import avi.mod.skrim.skills.farming.SkillFarming;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemHoe;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/**
 * Maybe the saddest piece of armor in this whole mod.
 */
public class Overalls extends CustomArmor {

  private static ItemArmor.ArmorMaterial OVERALLS_ARMOR = EnumHelper.addArmorMaterial("overalls", "skrim:overalls", 10
      , new int[]{1, 3, 2, 1}, 15, null, 0.0F);


  public Overalls() {
    super("overalls", OVERALLS_ARMOR, 3, EntityEquipmentSlot.CHEST);
  }

  public static void applyOveralls(PlayerInteractEvent.RightClickBlock event) {
    EntityPlayer player = event.getEntityPlayer();
    if (!player.world.isRemote) return;

    // Make sure the player is actually wearing overalls.
    InventoryPlayer inventory = player.inventory;
    if (inventory == null) return;
    Item armor = inventory.armorInventory.get(2).getItem();
    if (armor != SkrimItems.OVERALLS) return;

    // Make sure they got themselves a hoe.
    Item mainItem = player.getHeldItemMainhand().getItem();
    if (!(mainItem instanceof ItemHoe)) return;

    // Finally make sure that the right-clicked block is actually a crop.
    RayTraceResult result = player.rayTrace(5.0D, 1.0F);
    if (result == null || result.typeOfHit != RayTraceResult.Type.BLOCK) return;

    BlockPos targetPos = result.getBlockPos();
    IBlockState targetState = player.world.getBlockState(targetPos);
    if (!SkillFarming.validCrop(targetState)) return;


    SkrimPacketHandler.INSTANCE.sendToServer(new ApplyBonemealPacket(targetPos.getX(), targetPos.getY(), targetPos.getZ()));
  }

}