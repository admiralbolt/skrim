package avi.mod.skrim.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import avi.mod.skrim.skills.Skills;
import avi.mod.skrim.skills.defense.SkillDefense;
import avi.mod.skrim.utils.Reflection;

public class ArmorOverlay extends Gui {

	public ArmorOverlay(Minecraft mc) {
		EntityPlayer entityplayer = (EntityPlayer) mc.getRenderViewEntity();
    IAttributeInstance iattributeinstance = entityplayer.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
    IAttributeInstance armor = entityplayer.getEntityAttribute(SharedMonsterAttributes.ARMOR);
		ScaledResolution scaledRes = new ScaledResolution(mc);
		int l = scaledRes.getScaledWidth() / 2 - 91;
    int i1 = scaledRes.getScaledWidth() / 2 + 91;
    int j1 = scaledRes.getScaledHeight() - 39;
    float f = (float) iattributeinstance.getAttributeValue();
    int k1 = MathHelper.ceiling_float_int(entityplayer.getAbsorptionAmount());
    int l1 = MathHelper.ceiling_float_int((f + (float)k1) / 2.0F / 10.0F);

    int i2 = Math.max(10 - (l1 - 2), 3);
    int j2 = j1 - (l1 - 1) * i2 - 10;
    int k2 = j1 - 10;
    int l2 = k1;
    int i3 = entityplayer.getTotalArmorValue();
    int j3 = -1;
    /**
     * field_111136_b
     */
    double maxAttributeArmor = (double) Reflection.getAttributeValue(armor, "field_111120_a", "maximumValue");
    int maxArmor = (int) Math.ceil(Math.max(maxAttributeArmor, 20.0) / 2);

		for (int k3 = 0; k3 < maxArmor; ++k3) {
			if (i3 > 0) {
				int l3 = l + k3 * 8;

				if (k3 * 2 + 1 < i3) {
					this.drawTexturedModalRect(l3, j2, 34, 9, 9, 9);
				}

				if (k3 * 2 + 1 == i3) {
					this.drawTexturedModalRect(l3, j2, 25, 9, 9, 9);
				}

				if (k3 * 2 + 1 > i3) {
					this.drawTexturedModalRect(l3, j2, 16, 9, 9, 9);
				}
			}
		}
	}

}
