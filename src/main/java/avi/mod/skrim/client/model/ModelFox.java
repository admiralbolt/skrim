package avi.mod.skrim.client.model;

import avi.mod.skrim.entities.passive.EntityFox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelFox extends ModelBase {
	/** main box for the fox head */
	public ModelRenderer foxHeadMain;
	/** The fox's body */
	public ModelRenderer foxBody;
	/** Wolf'se first leg */
	public ModelRenderer foxLeg1;
	/** Wolf's second leg */
	public ModelRenderer foxLeg2;
	/** Wolf's third leg */
	public ModelRenderer foxLeg3;
	/** Wolf's fourth leg */
	public ModelRenderer foxLeg4;
	/** The fox's tail */
	ModelRenderer foxTail;
	/** The fox's mane */
	ModelRenderer foxMane;

	public ModelFox() {
		float f = 0.0F;
		float f1 = 13.5F;
		this.foxHeadMain = new ModelRenderer(this, 0, 0);
		this.foxHeadMain.addBox(-2.0F, -3.0F, -2.0F, 6, 6, 4, 0.0F);
		this.foxHeadMain.setRotationPoint(-1.0F, 13.5F, -7.0F);
		this.foxBody = new ModelRenderer(this, 18, 14);
		this.foxBody.addBox(-3.0F, -2.0F, -3.0F, 6, 9, 6, 0.0F);
		this.foxBody.setRotationPoint(0.0F, 14.0F, 2.0F);
		this.foxMane = new ModelRenderer(this, 21, 0);
		this.foxMane.addBox(-3.0F, -3.0F, -3.0F, 8, 6, 7, 0.0F);
		this.foxMane.setRotationPoint(-1.0F, 14.0F, 2.0F);
		this.foxLeg1 = new ModelRenderer(this, 0, 18);
		this.foxLeg1.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
		this.foxLeg1.setRotationPoint(-2.5F, 16.0F, 7.0F);
		this.foxLeg2 = new ModelRenderer(this, 0, 18);
		this.foxLeg2.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
		this.foxLeg2.setRotationPoint(0.5F, 16.0F, 7.0F);
		this.foxLeg3 = new ModelRenderer(this, 0, 18);
		this.foxLeg3.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
		this.foxLeg3.setRotationPoint(-2.5F, 16.0F, -4.0F);
		this.foxLeg4 = new ModelRenderer(this, 0, 18);
		this.foxLeg4.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
		this.foxLeg4.setRotationPoint(0.5F, 16.0F, -4.0F);
		this.foxTail = new ModelRenderer(this, 9, 18);
		this.foxTail.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
		this.foxTail.setRotationPoint(-1.0F, 12.0F, 8.0F);
		this.foxHeadMain.setTextureOffset(16, 14).addBox(-2.0F, -5.0F, 0.0F, 2, 2, 1, 0.0F);
		this.foxHeadMain.setTextureOffset(16, 14).addBox(2.0F, -5.0F, 0.0F, 2, 2, 1, 0.0F);
		this.foxHeadMain.setTextureOffset(0, 10).addBox(-0.5F, 0.0F, -5.0F, 3, 3, 4, 0.0F);
	}

	/**
	 * Sets the models various rotation angles then renders the model.
	 */
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
		this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);

		if (this.isChild) {
			float f = 2.0F;
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, 5.0F * scale, 2.0F * scale);
			this.foxHeadMain.renderWithRotation(scale);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
			GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
			this.foxBody.render(scale);
			this.foxLeg1.render(scale);
			this.foxLeg2.render(scale);
			this.foxLeg3.render(scale);
			this.foxLeg4.render(scale);
			this.foxTail.renderWithRotation(scale);
			this.foxMane.render(scale);
			GlStateManager.popMatrix();
		} else {
			this.foxHeadMain.renderWithRotation(scale);
			this.foxBody.render(scale);
			this.foxLeg1.render(scale);
			this.foxLeg2.render(scale);
			this.foxLeg3.render(scale);
			this.foxLeg4.render(scale);
			this.foxTail.renderWithRotation(scale);
			this.foxMane.render(scale);
		}
	}

	/**
	 * Used for easily adding entity-dependent animations. The second and third
	 * float params here are the same second and third as in the
	 * setRotationAngles method.
	 */
	public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float p_78086_2_, float p_78086_3_, float partialTickTime) {
		EntityFox entityfox = (EntityFox) entitylivingbaseIn;

		if (entityfox.isAngry()) {
			this.foxTail.rotateAngleY = 0.0F;
		} else {
			this.foxTail.rotateAngleY = MathHelper.cos(p_78086_2_ * 0.6662F) * 1.4F * p_78086_3_;
		}

		if (entityfox.isSitting()) {
			this.foxMane.setRotationPoint(-1.0F, 16.0F, -3.0F);
			this.foxMane.rotateAngleX = ((float) Math.PI * 2F / 5F);
			this.foxMane.rotateAngleY = 0.0F;
			this.foxBody.setRotationPoint(0.0F, 18.0F, 0.0F);
			this.foxBody.rotateAngleX = ((float) Math.PI / 4F);
			this.foxTail.setRotationPoint(-1.0F, 21.0F, 6.0F);
			this.foxLeg1.setRotationPoint(-2.5F, 22.0F, 2.0F);
			this.foxLeg1.rotateAngleX = ((float) Math.PI * 3F / 2F);
			this.foxLeg2.setRotationPoint(0.5F, 22.0F, 2.0F);
			this.foxLeg2.rotateAngleX = ((float) Math.PI * 3F / 2F);
			this.foxLeg3.rotateAngleX = 5.811947F;
			this.foxLeg3.setRotationPoint(-2.49F, 17.0F, -4.0F);
			this.foxLeg4.rotateAngleX = 5.811947F;
			this.foxLeg4.setRotationPoint(0.51F, 17.0F, -4.0F);
		} else {
			this.foxBody.setRotationPoint(0.0F, 14.0F, 2.0F);
			this.foxBody.rotateAngleX = ((float) Math.PI / 2F);
			this.foxMane.setRotationPoint(-1.0F, 14.0F, -3.0F);
			this.foxMane.rotateAngleX = this.foxBody.rotateAngleX;
			this.foxTail.setRotationPoint(-1.0F, 12.0F, 8.0F);
			this.foxLeg1.setRotationPoint(-2.5F, 16.0F, 7.0F);
			this.foxLeg2.setRotationPoint(0.5F, 16.0F, 7.0F);
			this.foxLeg3.setRotationPoint(-2.5F, 16.0F, -4.0F);
			this.foxLeg4.setRotationPoint(0.5F, 16.0F, -4.0F);
			this.foxLeg1.rotateAngleX = MathHelper.cos(p_78086_2_ * 0.6662F) * 1.4F * p_78086_3_;
			this.foxLeg2.rotateAngleX = MathHelper.cos(p_78086_2_ * 0.6662F + (float) Math.PI) * 1.4F * p_78086_3_;
			this.foxLeg3.rotateAngleX = MathHelper.cos(p_78086_2_ * 0.6662F + (float) Math.PI) * 1.4F * p_78086_3_;
			this.foxLeg4.rotateAngleX = MathHelper.cos(p_78086_2_ * 0.6662F) * 1.4F * p_78086_3_;
		}

		this.foxHeadMain.rotateAngleZ = entityfox.getInterestedAngle(partialTickTime) + entityfox.getShakeAngle(partialTickTime, 0.0F);
		this.foxMane.rotateAngleZ = entityfox.getShakeAngle(partialTickTime, -0.08F);
		this.foxBody.rotateAngleZ = entityfox.getShakeAngle(partialTickTime, -0.16F);
		this.foxTail.rotateAngleZ = entityfox.getShakeAngle(partialTickTime, -0.2F);
	}

	/**
	 * Sets the model's various rotation angles. For bipeds, par1 and par2 are
	 * used for animating the movement of arms and legs, where par1 represents
	 * the time(so that arms and legs swing back and forth) and par2 represents
	 * how "far" arms and legs can swing at most.
	 */
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor,
			Entity entityIn) {
		super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
		this.foxHeadMain.rotateAngleX = headPitch * 0.017453292F;
		this.foxHeadMain.rotateAngleY = netHeadYaw * 0.017453292F;
		this.foxTail.rotateAngleX = ageInTicks;
	}
}
