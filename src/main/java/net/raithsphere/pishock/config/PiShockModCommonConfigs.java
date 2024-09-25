package net.raithsphere.pishock.config;
import net.minecraftforge.common.ForgeConfigSpec;

public class PiShockModCommonConfigs {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final Configurable<Byte> MODE;
    public static final Configurable<String> USERNAME;
    public static final Configurable<String> CODE;
    public static final Configurable<String> API_KEY;
    public static final Configurable<Integer> COOLDOWN;
    public static final Configurable<Boolean> TRIGGER;
    public static final Configurable<Double> DEATH_INTENSITY;

    static{
        BUILDER.push("Configs for PiShock");

        // CONFIG DEFINATIONS
        BUILDER.comment("Configuration PiShock");
        MODE = new Configurable<>(BUILDER.define("Mode", (byte) 0), (byte)0);
        USERNAME = new Configurable<>(BUILDER.define("Username", "RaithSphere"), "RaithSphere");
        CODE = new Configurable<>(BUILDER.define("Code", "26EDFE2FC0F"), "26EDFE2FC0F");
        TRIGGER = new Configurable<>(BUILDER.define("TriggerOnDeath", true), true);
        API_KEY = new Configurable<>(BUILDER.define("ApiKey", "29cf665b-99c6-48dc-a408-598221ec36db"), "29cf665b-99c6-48dc-a408-598221ec36db");
        COOLDOWN = new Configurable<>(BUILDER.defineInRange("Cooldown", 2, 2, 60, Integer.class), 2);
        DEATH_INTENSITY = new Configurable<>(BUILDER.defineInRange("DeathIntensity", 80d, 1d, 100d, Double.class), 80d);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
    public static class Configurable<T> {
        ForgeConfigSpec.ConfigValue<T> config;
        private T val;

        public Configurable(ForgeConfigSpec.ConfigValue<T> config, T def) {
            this.config = config;
            this.val = def;
        }

        public void set(T t) {
            this.val = t;
        }

        public T get() {
            return this.val;
        }

        public void save() {
            if (this.config.get() != this.get())
                this.config.set(this.val);
        }
    }
}
