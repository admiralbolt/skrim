package avi.mod.skrim.blocks;

import avi.mod.skrim.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ArtifactItemBlock extends CustomItemBlock {


  protected String name;

  public ArtifactItemBlock(Block block) {
    super(block, true, ModItems.ARTIFACT_RARITY);
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

  }

}
