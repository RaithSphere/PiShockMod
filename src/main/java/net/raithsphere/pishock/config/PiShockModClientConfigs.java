package net.raithsphere.pishock.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class PiShockModClientConfigs {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    static {
        BUILDER.push("Configs for PiShock");

        // CONFIG DEFINATIONS

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
