package org.voximir.vbfreelook.config;

import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum FreelookKeyBehavior implements NameableEnum, StringRepresentable {
    SMART,
    HOLD,
    TOGGLE;

    public static final StringRepresentable.EnumCodec<FreelookKeyBehavior> CODEC =
            StringRepresentable.fromEnum(FreelookKeyBehavior::values);

    @Override
    public Component getDisplayName() {
        return Component.translatable("config.vbfreelook.enum.freelook_key_behavior." + this.name().toLowerCase());
    }

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase();
    }
}
