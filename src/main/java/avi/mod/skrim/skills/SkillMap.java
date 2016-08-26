package avi.mod.skrim.skills;

import net.minecraftforge.common.capabilities.Capability;

public class SkillMap {
	
	public int skillNum;
	public String skillName;
	public Capability<? extends ISkill> cap;
	
	public SkillMap(int skillNum, String skillName, Capability<? extends ISkill> cap) {
		this.skillNum = skillNum;
		this.skillName = skillName;
		this.cap = cap;
	}

}
