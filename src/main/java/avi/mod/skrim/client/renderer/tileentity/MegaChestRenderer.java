package avi.mod.skrim.client.renderer.tileentity;

import java.util.Calendar;

import avi.mod.skrim.tileentity.MegaChestTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MegaChestRenderer extends TileEntitySpecialRenderer<MegaChestTileEntity> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("skrim", "textures/entities/chests/mega_chest.png");

	private final ModelChest simpleChest = new ModelChest();

	public MegaChestRenderer() {
	}

	@Override
	public void render(MegaChestTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		GlStateManager.enableDepth();
		GlStateManager.depthFunc(515);
		GlStateManager.depthMask(true);
		int facing = 0;

		if (te.hasWorld()) {
			Block block = te.getBlockType();
			facing = te.getBlockMetadata();
		}

		ModelChest modelchest;
		modelchest = this.simpleChest;

		if (destroyStage >= 0) {
			System.out.println("destroy stage: " + destroyStage);
			this.bindTexture(DESTROY_STAGES[destroyStage]);
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.scale(4.0F, 4.0F, 1.0F);
			GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
			GlStateManager.matrixMode(5888);
		} else {
			this.bindTexture(TEXTURE);
		}

		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();

		if (destroyStage < 0) {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		}

		GlStateManager.translate((float) x, (float) y + 1.0F, (float) z + 1.0F);
		GlStateManager.scale(1.0F, -1.0F, -1.0F);
		GlStateManager.translate(0.5F, 0.5F, 0.5F);
		

		int rotationDegrees = 0;
		
		if (facing == 2) {
			rotationDegrees = 0;
		} else if (facing == 3) {
			rotationDegrees = 180;
		} else if (facing == 4) {
			rotationDegrees = -90;
		} else if (facing == 5) {
			rotationDegrees = 90;
		}
		
		
        GlStateManager.rotate((float) rotationDegrees, 0.0F, 1.0F, 0.0F);

		GlStateManager.translate(-0.5F, -0.5F, -0.5F);
		float f = te.prevLidAngle + (te.lidAngle - te.prevLidAngle) * partialTicks;

		f = 1.0F - f;
		f = 1.0F - f * f * f;
		modelchest.chestLid.rotateAngleX = -(f * ((float) Math.PI / 2F));
		modelchest.renderAll();
		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		if (destroyStage >= 0) {
			GlStateManager.matrixMode(5890);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
		}
	}
}