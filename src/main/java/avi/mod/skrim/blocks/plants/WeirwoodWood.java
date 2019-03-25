package avi.mod.skrim.blocks.plants;

import java.util.Random;

import avi.mod.skrim.blocks.BlockBase;
import avi.mod.skrim.blocks.ModBlocks;
import avi.mod.skrim.items.ModItems;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.woodcutting.SkillWoodcutting;
import avi.mod.skrim.utils.Obfuscation;
import avi.mod.skrim.utils.Utils;
import avi.mod.skrim.world.WeirwoodCoords;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.ICommandManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class WeirwoodWood extends BlockBase {

	public WeirwoodWood() {
		super(Material.WOOD, "weirwood_wood");
		this.setHardness(2.0F);
        this.setSoundType(SoundType.WOOD);
	}

	@Override
	public int quantityDropped(IBlockState state, int fortune, Random random) {
		return 0;
	}
	
	@Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack heldItem = playerIn.getHeldItem(hand);
		// Check and make sure there are at least two natural weirwood blocks directly above/below the activated block
		if (WeirwoodCoords.validCoord(playerIn, pos)) {
			// make sure that heldItem is not null AND is the totem.
			if (playerIn.hasCapability(Skills.WOODCUTTING, EnumFacing.NORTH)) {
				SkillWoodcutting woodcutting = (SkillWoodcutting) playerIn.getCapability(Skills.WOODCUTTING, EnumFacing.NORTH);
				if (woodcutting.hasAbility(4)) {
					if (heldItem != null && heldItem.getItem() == ModItems.WEIRWOOD_TOTEM) {
						if (WeirwoodCoords.addCoord(playerIn, pos)) {
							Obfuscation.setStackSize(heldItem, Obfuscation.getStackSize(heldItem) - 1);
							if (Obfuscation.getStackSize(heldItem) == 0) {
								playerIn.inventory.deleteStack(heldItem);
							}
							heldItem.damageItem(heldItem.getMaxDamage(), playerIn);
							// SPAWN PARTICLES AND SHIT
							double d0 = Utils.rand.nextGaussian() * 0.03D;
							double d1 = Utils.rand.nextGaussian() * 0.03D;
							double d2 = Utils.rand.nextGaussian() * 0.03D;
							double posX = pos.getX() + hitX;
							double posZ = pos.getZ() + hitZ;
							double posY = pos.getY();
							A: for (int q = 1; q <= 4; q++) {
								if (worldIn.getBlockState(new BlockPos(pos.getX(), posY + 1, pos.getZ())) == ModBlocks.WEIRWOOD_WOOD.getDefaultState()) {
									for (int i = -1; i <= 1; i++) {
										for (int j = -1; j <= 1; j++) {
											if (worldIn.getBlockState(new BlockPos(pos.getX() + i, posY + 1, pos.getZ() + j)) == ModBlocks.WEIRWOOD_LEAF.getDefaultState()) {
												break A;
											}
										}
									}
									posY += 1;
								} else {
									break A;
								}
							}
							for (int i = -3; i <= 3; i++) {
								for (int j = -3; j <= 3; j++) {
									worldIn.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, (double) posX + i + Utils.rand.nextDouble()/2, (double) posY - Utils.rand.nextDouble()/2, (double) posZ + j + Utils.rand.nextDouble()/2, d0, d1, d2);
								}
							}
							return true;
						}
					} else if (heldItem.isEmpty()) {
						// TELEPORT N' SHIT
						if (!worldIn.isRemote) {
							BlockPos teleportLoc = WeirwoodCoords.getCoord(playerIn);
							if (teleportLoc != null) {
								if (teleportLoc.getX() != pos.getX() || teleportLoc.getZ() != pos.getZ()) {
									MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
									ICommandManager cm = server.getCommandManager();
									int xMod = (Utils.rand.nextBoolean()) ? 1 : -1;
									int zMod = (Utils.rand.nextBoolean()) ? 1 : -1;
									cm.executeCommand(server, "/tp " + playerIn.getName() + " " + (teleportLoc.getX() + xMod) + " " + teleportLoc.getY() + " " + (teleportLoc.getZ() + zMod));
									worldIn.playSound((EntityPlayer) null, pos, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.BLOCKS, 1.0F, 0.5F);
									worldIn.playSound((EntityPlayer) null, teleportLoc, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.BLOCKS, 1.0F, 0.5F);
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

}
