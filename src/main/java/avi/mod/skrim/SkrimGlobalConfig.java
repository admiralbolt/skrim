package avi.mod.skrim;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * Global configuration for the skrim mod!
 */
public class SkrimGlobalConfig {

  // Whether or not to enable miscellaneous debugging. It affects:
  //   * Whether or not bonemeal can be applied to weirwood trees & beanstalks.
  public static BooleanOption DEBUG = new BooleanOption(false);

  // Whether or not to require that blocks be naturally generated in order to get XP from them.
  public static BooleanOption ENFORCE_NATURAL = new BooleanOption(true);

  // Whether or not every hit criticals.
  public static BooleanOption ALWAYS_CRIT = new BooleanOption(false);

  // The global XP multiplier. This is applied multiplicatively with a players individual xp modifier.
  public static DoubleOption XP_MULTIPLIER = new DoubleOption(1.0);

  public static Map<String, ConfigOption> CONFIG_OPTIONS = ImmutableMap.of(
      "debug", DEBUG,
      "enforce_natural", ENFORCE_NATURAL,
      "always_crit", ALWAYS_CRIT,
      "xp_multiplier", XP_MULTIPLIER
  );


  /**
   * Helper class for config options. setValue() takes a String so that options can be easily set via the /skrimoption command.
   */
  public abstract static class ConfigOption<T> {

    public T value;

    public ConfigOption(T defaultValue) {
      this.value = defaultValue;
    }

    public abstract void setValue(String value);

  }

  // This section is for direct instantiations of the ConfigOption class. I think there's a way to avoid this by doing some reflection
  // black magic, but this is just cleaner and easier to understand.

  public static class BooleanOption extends ConfigOption<Boolean> {

    public BooleanOption(Boolean defaultValue) {
      super(defaultValue);
    }

    @Override
    public void setValue(String value) {
      this.value = Boolean.valueOf(value);
    }

  }

  public static class DoubleOption extends ConfigOption<Double> {

    public DoubleOption(Double defaultValue) {
      super(defaultValue);
    }

    @Override
    public void setValue(String value) {
      this.value = Double.parseDouble(value);
    }
  }

}
