package org.voximir.vbfreelook.config;

import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum FreelookKeyBehaviour implements NameableEnum, StringRepresentable {
    SMART,
    HOLD,
    TOGGLE;

    public static final StringRepresentable.EnumCodec<FreelookKeyBehaviour> CODEC =
            StringRepresentable.fromEnum(FreelookKeyBehaviour::values);

    @Override
    public Component getDisplayName() {
        return Component.translatable("config.vbfreelook.enum.freelook_key_behaviour." + this.name().toLowerCase());
    }

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase();
    }
}
