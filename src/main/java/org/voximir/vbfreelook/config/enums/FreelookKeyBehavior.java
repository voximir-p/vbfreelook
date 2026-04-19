package org.voximir.vbfreelook.config.enums;

import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum FreelookKeyBehavior implements NameableEnum, StringRepresentable {
    SMART,
    HOLD,
    TOGGLE;

    public static final StringRepresentable.EnumCodec<FreelookKeyBehavior> CODEC =
            StringRepresentable.fromEnum(FreelookKeyBehavior::values);

    @Override
    public Component getDisplayName() {
        return Component.translatable("config.vbfreelook.enum.freelook_key_behavior." + getSerializedName());
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
