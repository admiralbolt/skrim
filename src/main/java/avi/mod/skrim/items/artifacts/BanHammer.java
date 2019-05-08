package avi.mod.skrim.items.artifacts;

import avi.mod.skrim.init.SkrimSoundEvents;
import avi.mod.skrim.items.items.ArtifactItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.command.ICommandManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nullable;
import java.util.List;

public class BanHammer extends ArtifactItem {

  public BanHammer() {
    super("ban_hammer");
    this.setMaxDamage(1);
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    tooltip.add("§4Bans any player hit.");
    tooltip.add("§4Only one use, choose wisely.");
    tooltip.add("§e\"Once you tire of the nail, feel free to join me in my art.\"");
  }


  @Override
  public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
    if (!(target instanceof EntityPlayer)) return true;
    if (attacker.world.isRemote) return true;

    MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
    ICommandManager cm = server.getCommandManager();
    cm.executeCommand(server, "/ban " + target.getName());
    attacker.world.playSound(null, attacker.getPosition(), SkrimSoundEvents.BAN_HAMMER, SoundCategory.PLAYERS, 10000f, 1.0f);
    stack.damageItem(2, attacker);

    return true;
  }

}
