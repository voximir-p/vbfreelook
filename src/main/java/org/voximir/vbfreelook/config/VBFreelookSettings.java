package org.voximir.vbfreelook.config;

import com.mojang.serialization.Codec;
import dev.isxander.yacl3.config.v3.ConfigEntry;
import dev.isxander.yacl3.config.v3.JsonFileCodecConfig;
import net.fabricmc.loader.api.FabricLoader;
import org.voximir.vbfreelook.VBFreelook;
import org.voximir.vbfreelook.config.enums.*;

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

    final Behavior behavior = new Behavior();
    final Controls controls = new Controls();

    class Behavior {
        final Perspective perspective = new Perspective();
        final Transitions transitions = new Transitions();
        final Other other = new Other();

        class Perspective {
            private final ConfigEntry<Boolean> shouldSwitchPerspective = register(
                    "should_switch_perspective",
                    true,
                    Codec.BOOL
            );
            private final ConfigEntry<ShouldSwitchBackPerspective> shouldSwitchBackPerspective = register(
                    "should_switch_back_perspective",
                    ShouldSwitchBackPerspective.ALWAYS,
                    ShouldSwitchBackPerspective.CODEC
            );
            private final ConfigEntry<FreelookPerspective> freelookPerspective = register(
                    "freelook_perspective",
                    FreelookPerspective.THIRD_PERSON,
                    FreelookPerspective.CODEC
            );
            private final ConfigEntry<SwitchBackPerspective> switchBackPerspective = register(
                    "switch_back_perspective",
                    SwitchBackPerspective.ORIGINAL,
                    SwitchBackPerspective.CODEC
            );
        }

        class Transitions {
            private final ConfigEntry<Integer> zoomOutTime = register(
                    "zoom_out_time",
                    1000,
                    Codec.INT
            );
            private final ConfigEntry<TransitionType> zoomOutTransition = register(
                    "zoom_out_transition",
                    TransitionType.EASE_OUT_EXP,
                    TransitionType.CODEC
            );
        }

        class Other {
            private final ConfigEntry<Boolean> cameraNoClip = register(
                    "camera_no_clip",
                    false,
                    Codec.BOOL
            );
        }
    }

    class Controls {
        private final ConfigEntry<FreelookKeyBehavior> freelookKeyBehavior = register(
                "freelook_key_behavior",
                FreelookKeyBehavior.SMART,
                FreelookKeyBehavior.CODEC
        );
        private final ConfigEntry<Integer> smartThreshold = register(
                "smart_threshold",
                150,
                Codec.INT
        );
    }

    public VBFreelookSettings() {
        super(FabricLoader.getInstance().getConfigDir().resolve(VBFreelook.MOD_ID + ".json"));
    }

    public static VBFreelookSettings getInstance() {
        return INSTANCE;
    }

    public static ConfigEntry<Boolean> getShouldSwitchPerspective() {
        return INSTANCE.behavior.perspective.shouldSwitchPerspective;
    }

    public static ConfigEntry<FreelookPerspective> getFreelookPerspective() {
        return INSTANCE.behavior.perspective.freelookPerspective;
    }

    public static ConfigEntry<ShouldSwitchBackPerspective> getShouldSwitchBackPerspective() {
        return INSTANCE.behavior.perspective.shouldSwitchBackPerspective;
    }

    public static ConfigEntry<SwitchBackPerspective> getSwitchBackPerspective() {
        return INSTANCE.behavior.perspective.switchBackPerspective;
    }

    public static ConfigEntry<Integer> getZoomOutTime() {
        return INSTANCE.behavior.transitions.zoomOutTime;
    }

    public static ConfigEntry<TransitionType> getZoomOutTransition() {
        return INSTANCE.behavior.transitions.zoomOutTransition;
    }

    public static ConfigEntry<Boolean> getCameraNoClip() {
        return INSTANCE.behavior.other.cameraNoClip;
    }

    public static ConfigEntry<FreelookKeyBehavior> getFreelookKeyBehavior() {
        return INSTANCE.controls.freelookKeyBehavior;
    }

    public static ConfigEntry<Integer> getSmartThreshold() {
        return INSTANCE.controls.smartThreshold;
    }
}
