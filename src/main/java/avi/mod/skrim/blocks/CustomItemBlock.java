package avi.mod.skrim.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CustomItemBlock extends ItemBlock {

  private boolean magicEffect;
  private EnumRarity rarity;

  public CustomItemBlock(Block block) {
    this(block, false, EnumRarity.COMMON);
  }

  public CustomItemBlock(Block block, boolean magicEffect, EnumRarity rarity) {
    super(block);
    this.magicEffect = magicEffect;
    this.rarity = rarity;
  }

  @Override
  public boolean hasEffect(ItemStack stack) {
    return this.magicEffect;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public EnumRarity getRarity(ItemStack stack) {
    return this.rarity;
  }

}
