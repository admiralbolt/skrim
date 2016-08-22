package avi.mod.skrim.client.gui;

import java.io.IOException;

import org.lwjgl.input.Mouse;

import avi.mod.skrim.Utils;
import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.ISkill;
import avi.mod.skrim.skills.Skills;

import net.minecraft.block.BlockTNT;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.brewing.PotionBrewEvent;

import net.minecraft.world.Explosion;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class SkillScreen extends GuiScreen {

  private GuiButton inventoryTab;
  private int left;
  private int top;
  /**
   * Current scroll position
   */
  private int scrollY = 0;
  /**
   * Max scroll position.
   */
  private int maxScroll = 1000;
  private int scrollBarWidth = 6;
  private int scrollBarHeight = 40;
  private int scrollPaddingLeft = 5;
  private int paddingTop = 5;
  private int paddingBottom = 5;
  private int paddingRight = 5;
  private double scrollMultiplier = 0.03;

  private int skillPaddingLeft = 5;
  private int skillHeight = 30;
  private int skillPaddingDesc = 5;
  private int levelTextLeft;

  private int levelBarHeight = 9;
  private int levelBarWidth = 176 - this.paddingRight - this.scrollBarWidth - this.scrollPaddingLeft - this.skillPaddingLeft - this.skillHeight - this.skillPaddingDesc;

  private int headerColor = 0xFF333333;
  private int levelBarColor = 0x8055dd55;
  private int levelBarTextColor = 0xFFFFFFFF;

  private int boundTop;
  private int boundBottom;

  private EntityPlayer player;


  public SkillScreen(int left, int top) {
    super();
    this.left = left;
    this.top = top;
    this.boundTop = this.top + 3;
    this.boundBottom = this.top + 176 - 3;
    this.levelTextLeft = this.left + 176 - 50;
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    this.drawDefaultBackground();
    this.mc.getTextureManager().bindTexture(new ResourceLocation("skrim", "textures/guis/skills/background.png"));
    this.drawTexturedModalRect(this.left, this.top, 0, 0, 176, 176);
    this.drawScrollBar();
    this.drawSkills(mouseX, mouseY);

    super.drawScreen(mouseX, mouseY, partialTicks);
  }

  public void drawScrollBar() {
	  int adjust = (int) Math.floor(((double) this.scrollY / this.maxScroll) * (176 - this.scrollBarHeight - this.paddingTop - this.paddingBottom - 10));
	  int left = this.left + this.scrollPaddingLeft;
	  int top = this.top + this.paddingTop + adjust;
	  int right = left + this.scrollBarWidth;
	  int bottom = top + this.scrollBarHeight;
	  this.drawRect(left, top, right, bottom, 0xAAAAAAAA);
  }

  public void drawSkills(int mouseX, int mouseY) {
    for (int i = 0; i < Skills.ALL_SKILLS.size(); i++) {
      this.drawSkill(Skills.ALL_SKILLS.get(i), i, mouseX, mouseY);
    }
  }

  public boolean shouldRender(int top, int bottom) {
    return (top > this.boundTop && this.boundBottom > bottom);
  }

  public boolean shouldRenderIcon(int top, int bottom) {
    return (bottom > this.boundTop && this.boundBottom > top);
  }

  public void drawSkill(Capability<? extends ISkill> capability, int index, int mouseX, int mouseY) {
	  Skill skill = (Skill) this.player.getCapability(capability, EnumFacing.NORTH);
	  int left = this.left + this.scrollPaddingLeft + this.scrollBarWidth + this.skillPaddingLeft;
	  int top = this.top + this.paddingTop - this.scrollY + index * this.skillHeight;

	  /**
	   * Draw header information.
	   */
	  int textLeft = left + this.skillPaddingDesc + this.skillHeight;
    if (this.shouldRender(top, top + 7)) {
      this.mc.fontRendererObj.drawString(skill.name, textLeft, top, this.headerColor);
      this.mc.fontRendererObj.drawString("Level " + skill.level, this.levelTextLeft, top, this.headerColor);
    }


	  /**
	   * Draw level up information
	   */
  int levelLeft = textLeft;
  int levelTop = top + this.skillHeight - this.levelBarHeight;
  int levelRight = levelLeft + (int) Math.floor(this.levelBarWidth * skill.getPercentToNext());
  int levelBottom = levelTop + this.levelBarHeight;
     if (shouldRender(levelTop, levelBottom)) {
  	  this.drawRect(levelLeft, levelTop, levelRight, levelBottom, this.levelBarColor);

  	  String levelText = skill.xp + " / " + skill.nextLevelTotal;
  	  int levelTextWidth = this.mc.fontRendererObj.getStringWidth(levelText);
  	  int levelTextLeft = levelLeft + (int) (this.levelBarWidth / 2) - (int) ((double) levelTextWidth / 2);

  	  this.mc.fontRendererObj.drawString(levelText, levelTextLeft, levelTop + 1, this.levelBarTextColor);
    }

    /**
     * Draw Skill Icon
     */
    this.mc.getTextureManager().bindTexture(skill.getIconTexture());
    // If we are overlapping on the top
    int dist;
    if ((top < this.boundTop) && ((top + this.skillHeight) > this.boundTop)) {
      int start = this.boundTop - top;
      dist = this.skillHeight - start;
      this.drawTexturedModalRect(left, this.boundTop, 0, start, this.skillHeight, dist);
    } else if (this.boundBottom > top && top + this.skillHeight > this.boundBottom) {
      dist = this.boundBottom - top;
      this.drawTexturedModalRect(left, top, 0, 0, this.skillHeight, dist);
    } else if (shouldRenderIcon(top, top + this.skillHeight)) {
      this.drawTexturedModalRect(left, top, 0, 0, this.skillHeight, this.skillHeight);
      if (Utils.isPointInRegion(left, top, left + this.skillHeight, top + this.skillHeight, mouseX, mouseY)) {
  		  this.drawHoveringText(skill.getToolTip(), mouseX, mouseY);
  	  }
    }

  }

  @Override
  public boolean doesGuiPauseGame() {
    return false;
  }

  @Override
  public void initGui() {
    this.inventoryTab = new GuiButton(1995, this.left, this.top - 20 - 1, 176, 20, "Inventory");
    this.buttonList.add(this.inventoryTab);
    this.player = Minecraft.getMinecraft().thePlayer;
  }

  @Override
  protected void actionPerformed(GuiButton button) throws IOException {
    if (button == this.inventoryTab) {
      this.mc.displayGuiScreen(new CustomGuiInventory(Minecraft.getMinecraft().thePlayer));
    }
  }

  @Override
  protected void keyTyped(char typedChar, int keyCode) throws IOException {
    if (this.mc.gameSettings.keyBindInventory.getKeyCode() == keyCode) {
      this.mc.displayGuiScreen(null);
      if (this.mc.currentScreen == null) {
        this.mc.setIngameFocus();
      }
    }
    super.keyTyped(typedChar, keyCode);
  }

  @Override
  public void handleMouseInput() throws IOException {
	  Mouse.getEventDWheel();
	  int i = Mouse.getEventDWheel();
	  if (i != 0) {
		  /**
		   * Scrolling DOWN is a negative number.
		   * Scrolling UP is a positive number.
		   * However, 0 is our lowest scroll y, so we can always subtract.
		   */
		  this.scrollY -= (int) Math.floor(this.scrollMultiplier * i);
		  if (this.scrollY < 0) {
			  this.scrollY = 0;
		  } else if (this.scrollY > this.maxScroll) {
			  this.scrollY = this.maxScroll;
		  }
	  }
	  super.handleMouseInput();
  }

}
