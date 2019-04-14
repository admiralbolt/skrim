package avi.mod.skrim.blocks;

import avi.mod.skrim.items.SkrimItems;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ArtifactItemBlock extends CustomItemBlock {


  protected String name;

  public ArtifactItemBlock(Block block) {
    super(block, true, SkrimItems.ARTIFACT_RARITY);
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

  }

}
