package org.voximir.vbfreelook.config;

import com.mojang.serialization.Codec;
import dev.isxander.yacl3.config.v3.ConfigEntry;
import dev.isxander.yacl3.config.v3.JsonFileCodecConfig;
import net.fabricmc.loader.api.FabricLoader;

public class VBFreelookSettings extends JsonFileCodecConfig<VBFreelookSettings> {
    private static final VBFreelookSettings INSTANCE = new VBFreelookSettings();

    static {
        if (!INSTANCE.loadFromFile())
            INSTANCE.saveToFile();

        if (INSTANCE.firstLaunch.get()) {
            INSTANCE.firstLaunch.set(false);
            INSTANCE.saveToFile();
        }
    }

    private final ConfigEntry<FreelookKeyBehavior> freelookKeyBehavior = register("freelook_key_behavior", FreelookKeyBehavior.SMART, FreelookKeyBehavior.CODEC);
    private final ConfigEntry<FreelookPerspective> freelookPerspective = register("freelook_perspective", FreelookPerspective.THIRD_PERSON, FreelookPerspective.CODEC);
    private final ConfigEntry<Integer> smartThreshold = register("smart_threshold", 150, Codec.INT);
    private final ConfigEntry<Boolean> firstLaunch = register("first_launch", true, Codec.BOOL);

    public VBFreelookSettings() {
        super(FabricLoader.getInstance().getConfigDir().resolve("vbfreelook.json"));
    }

    public static VBFreelookSettings getInstance() {
        return INSTANCE;
    }

    public ConfigEntry<FreelookKeyBehavior> getFreelookKeyBehavior() {
        return freelookKeyBehavior;
    }

    public ConfigEntry<FreelookPerspective> getFreelookPerspective() {
        return freelookPerspective;
    }

    public ConfigEntry<Integer> getSmartThreshold() {
        return smartThreshold;
    }
}
