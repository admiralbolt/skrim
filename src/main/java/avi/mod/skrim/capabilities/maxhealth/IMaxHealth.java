package avi.mod.skrim.capabilities.maxhealth;

/**
 * A capability to provide a max health bonus to an entity.
 *
 * Remember! One heart = 2 max health.
 */
public interface IMaxHealth {

  float getBonusMaxHealth();

  void setBonusMaxHealth(float bonusMaxHealth);

  void addBonusMaxHealth(float healthToAdd);
}