package avi.mod.skrim.blocks.plants;

import avi.mod.skrim.blocks.BlockBase;
import avi.mod.skrim.blocks.SkrimBlocks;
import avi.mod.skrim.items.SkrimItems;
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

import java.util.Random;

/**
 * A weirwood tree allows one way teleportation if you have 100 woodcutting.
 * <p>
 * First, a home tree is set by using a weirwood totem. This allows teleporting from any weirwood tree back to your
 * home tree. Right clicking any tree without a totem in hand will teleport you to your home tree.
 */
public class WeirwoodWood extends BlockBase {

  public WeirwoodWood() {
    super(Material.WOOD, "weirwood_wood");
    this.setHardness(2.0F);
    this.setSoundType(SoundType.WOOD);
  }

  /**
   * Set the home teleport location for a particular player to the target tree. Right clicking another weirwood tree
   * will teleport the player to their home tree.
   */
  private static void setTeleportLocation(World worldIn, BlockPos pos, EntityPlayer playerIn, ItemStack heldItem,
                                          float hitX, float hitZ) {
    if (WeirwoodCoords.addCoord(playerIn, pos)) {
      Obfuscation.setStackSize(heldItem, Obfuscation.getStackSize(heldItem) - 1);
      if (Obfuscation.getStackSize(heldItem) == 0) {
        playerIn.inventory.deleteStack(heldItem);
      }
      heldItem.damageItem(heldItem.getMaxDamage(), playerIn);

      double posX = pos.getX() + hitX;
      double posZ = pos.getZ() + hitZ;
      double posY = pos.getY();

      // Find the top-most point on the weirwood tree from the hit position.
      // We move up from the hit position looking for either the first leaves around the trunk we can find OR if we
      // run out of weirwood-wood blocks.
      A:
      for (int q = 1; q <= 4; q++) {
        if (!(worldIn.getBlockState(new BlockPos(pos.getX(), posY + 1, pos.getZ())) == SkrimBlocks.WEIRWOOD_WOOD.getDefaultState()))
          break;

        // Check around the trunk for leaves, and break if we find any.
        for (int i = -1; i <= 1; i++) {
          for (int j = -1; j <= 1; j++) {
            if (worldIn.getBlockState(new BlockPos(pos.getX() + i, posY + 1, pos.getZ() + j)) == SkrimBlocks.WEIRWOOD_LEAF.getDefaultState()) {
              break A;
            }
          }
        }
        posY += 1;
      }

      // Spawn fireworks in a 3 block radius around the tree.
      double d0 = Utils.rand.nextGaussian() * 0.03D;
      double d1 = Utils.rand.nextGaussian() * 0.03D;
      double d2 = Utils.rand.nextGaussian() * 0.03D;
      for (int i = -3; i <= 3; i++) {
        for (int j = -3; j <= 3; j++) {
          worldIn.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK,
              posX + i + Utils.rand.nextDouble() / 2, posY - Utils.rand.nextDouble() / 2,
              posZ + j + Utils.rand.nextDouble() / 2, d0, d1, d2);
        }
      }
    }
  }

  @Override
  public int quantityDropped(IBlockState state, int fortune, Random random) {
    return 0;
  }

  public static BlockPos getBottomOfTree(World worldIn, BlockPos pos) {
    while (worldIn.getBlockState(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ())) == SkrimBlocks.WEIRWOOD_WOOD.getDefaultState()) {
      pos = pos.add(0, -1, 0);
    }
    return pos;
  }

  @Override
  public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                  EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    System.out.println("\n\nIsRemote: " + worldIn.isRemote);
    ItemStack heldItem = playerIn.getHeldItem(hand);
    // Normalize the activated position to the bottom of the tree.
    pos = getBottomOfTree(worldIn, pos);
    System.out.println("Normalized position: " + pos);
    if (!WeirwoodCoords.validCoord(playerIn, pos)) return false;
    System.out.println("iiiiits valid");
    if (!playerIn.hasCapability(Skills.WOODCUTTING, EnumFacing.NORTH)) return false;
    SkillWoodcutting woodcutting = (SkillWoodcutting) playerIn.getCapability(Skills.WOODCUTTING, EnumFacing.NORTH);
    if (!woodcutting.hasAbility(4)) return false;

    System.out.println("Highly suspicious.");

    // Set the home base tree if the player is holding a weirwood totem.
    if (heldItem.getItem() == SkrimItems.WEIRWOOD_TOTEM) {
      setTeleportLocation(worldIn, pos, playerIn, heldItem, hitX, hitZ);
      return true;
    }

    // Otherwise teleport the player to their home base tree.
    if (worldIn.isRemote) return false;

    System.out.println("Do some portin");

    BlockPos teleportLoc = WeirwoodCoords.getCoord(playerIn);
    // Make sure we don't teleport a player to the tree they are standing at.
    if (teleportLoc == null || (teleportLoc.getX() == pos.getX() && teleportLoc.getZ() == pos.getZ())) return false;

    MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
    ICommandManager cm = server.getCommandManager();
    int xMod = (Utils.rand.nextBoolean()) ? 1 : -1;
    int zMod = (Utils.rand.nextBoolean()) ? 1 : -1;
    cm.executeCommand(server,
        "/tp " + playerIn.getName() + " " + (teleportLoc.getX() + xMod) + " " + teleportLoc.getY() + " " + (teleportLoc.getZ() + zMod));
    worldIn.playSound(null, pos, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.BLOCKS, 1.0F, 0.5F);
    worldIn.playSound(null, teleportLoc, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.BLOCKS, 1.0F,
        0.5F);
    return true;
  }

}
