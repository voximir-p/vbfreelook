package org.voximir.vbfreelook.config.enums;

import net.minecraft.util.StringRepresentable;
import org.voximir.vbfreelook.utilities.ConfigEnum;

public enum ShouldSwitchBackPerspective implements ConfigEnum {
    ALWAYS,
    IF_UNCHANGED,
    NEVER;

    public static final EnumCodec<ShouldSwitchBackPerspective> CODEC =
            StringRepresentable.fromEnum(ShouldSwitchBackPerspective::values);

    @Override
    public String getTranslationKey() {
        return "should_switch_back_perspective";
    }
}
