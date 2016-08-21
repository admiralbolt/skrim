package avi.mod.skrim.skills.mining;

import avi.mod.skrim.skills.Skill;
import avi.mod.skrim.skills.SkillStorage;
import net.minecraft.block.BlockOre;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SkillMining extends Skill implements ISkillMining {

	public static SkillStorage<ISkillMining> skillStorage = new SkillStorage<ISkillMining>();

  public SkillMining() {
    super("Mining");
		System.out.println("Constructing SkillMining();");
  }

  public SkillMining(int level, int currentXp) {
    super("Mining", level, currentXp);
		System.out.println("Constructing SkillMining(" + level + "," + currentXp + ");");
  }

  @SubscribeEvent
	public void onBlockBreak(BlockEvent.BreakEvent event) {
		System.out.println("Block Break registered in capability!!!");
		EntityPlayer player = event.getPlayer();
    // PlayerSkills skills = PlayerSkills.get(player);
    World world = player.worldObj;
    this.xp += 500;
    this.levelUp();

    if (!world.isRemote && event.getState().getBlock() instanceof BlockOre) {
      this.xp += 500;
      if (this.canLevelUp()) {
        this.levelUp();
				System.out.println("leveling up!");
      }
      // skills.syncWithClient();
    }
  }

}
