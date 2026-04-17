package org.voximir.vbfreelook.config;

import com.mojang.serialization.Codec;
import dev.isxander.yacl3.config.v3.ConfigEntry;
import dev.isxander.yacl3.config.v3.JsonFileCodecConfig;
import net.fabricmc.loader.api.FabricLoader;
import org.voximir.vbfreelook.VBFreelook;
import org.voximir.vbfreelook.config.enums.FreelookKeyBehavior;
import org.voximir.vbfreelook.config.enums.FreelookPerspective;

public class VBFreelookSettings extends JsonFileCodecConfig<VBFreelookSettings> {
    private static final VBFreelookSettings INSTANCE = new VBFreelookSettings();

    private static final int SCHEMA_VERSION = 1;
    private final ConfigEntry<Integer> schemaVersion = register("schema_version", SCHEMA_VERSION, Codec.INT);

    static {
        if (!INSTANCE.loadFromFile()) {
            INSTANCE.saveToFile();
        } else {
            // noinspection StatementWithEmptyBody
            if (INSTANCE.schemaVersion.get() < 1) {
                // Handle migration; not used yet
            }
        }
    }

    Behavior behavior = new Behavior();
    Controls controls = new Controls();

    class Behavior {
        private final ConfigEntry<Boolean> switchPerspective = register("switch_perspective", true, Codec.BOOL);
        private final ConfigEntry<FreelookPerspective> freelookPerspective = register("freelook_perspective", FreelookPerspective.THIRD_PERSON, FreelookPerspective.CODEC);
    }

    class Controls {
        private final ConfigEntry<FreelookKeyBehavior> freelookKeyBehavior = register("freelook_key_behavior", FreelookKeyBehavior.SMART, FreelookKeyBehavior.CODEC);
        private final ConfigEntry<Integer> smartThreshold = register("smart_threshold", 150, Codec.INT);
    }

    public VBFreelookSettings() {
        super(FabricLoader.getInstance().getConfigDir().resolve(VBFreelook.MOD_ID + ".json"));
    }

    public static VBFreelookSettings getInstance() {
        return INSTANCE;
    }

    public ConfigEntry<Boolean> getSwitchPerspective() {
        return behavior.switchPerspective;
    }

    public ConfigEntry<FreelookPerspective> getFreelookPerspective() {
        return behavior.freelookPerspective;
    }

    public ConfigEntry<FreelookKeyBehavior> getFreelookKeyBehavior() {
        return controls.freelookKeyBehavior;
    }

    public ConfigEntry<Integer> getSmartThreshold() {
        return controls.smartThreshold;
    }
}
