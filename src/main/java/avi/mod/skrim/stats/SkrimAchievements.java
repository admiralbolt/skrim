package avi.mod.skrim.stats;

import avi.mod.skrim.items.ModItems;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

public class SkrimAchievements {
	
	public static AchievementPage SKRIM_ACHIEVEMENTS;
	
	public static Achievement FOUND_SKILLS = new Achievement(
		"achievement.found_skills",
		"found_skills",
		0,
		0,
		ModItems.ARUARIAN_DANCE,
		(Achievement) null
	);
	
	public static Achievement ARCHAEOLOGY = new Achievement(
		"achievement.archaelogy",
		"archaeology",
		2,
		1,
		ModItems.BLINDING_BOOTS,
		FOUND_SKILLS
	);
	
	
	public static void register() {
		ARCHAEOLOGY.registerStat();
		SKRIM_ACHIEVEMENTS = new AchievementPage(
			"Skrim Achievements",
			new Achievement[] {
				FOUND_SKILLS, ARCHAEOLOGY	
			}
		);
		AchievementPage.registerAchievementPage(SKRIM_ACHIEVEMENTS);
	}
	
	public static Achievement getAchievmentById(String achievmentId) {
		for (Achievement achievement : SKRIM_ACHIEVEMENTS.getAchievements()) {
			if (achievement.statId.equals(achievmentId)) {
				return achievement;
			}
		}
		return null;
	}

}
