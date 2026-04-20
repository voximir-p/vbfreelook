package org.voximir.vbfreelook.config.enums;

import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.util.StringRepresentable;
import org.voximir.vbfreelook.utilities.ConfigEnum;

public enum FreelookKeyBehavior implements ConfigEnum, NameableEnum, StringRepresentable {
    SMART,
    HOLD,
    TOGGLE;

    public static final StringRepresentable.EnumCodec<FreelookKeyBehavior> CODEC =
            StringRepresentable.fromEnum(FreelookKeyBehavior::values);

    @Override
    public String getTranslationKey() {
        return "freelook_key_behavior";
    }
}
