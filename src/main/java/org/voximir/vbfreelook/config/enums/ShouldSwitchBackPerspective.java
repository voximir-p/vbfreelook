package org.voximir.vbfreelook.config.enums;

import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum ShouldSwitchBackPerspective implements NameableEnum, StringRepresentable {
    ALWAYS,
    IF_UNCHANGED,
    NEVER;

    public static final EnumCodec<ShouldSwitchBackPerspective> CODEC =
            StringRepresentable.fromEnum(ShouldSwitchBackPerspective::values);

    @Override
    public Component getDisplayName() {
        return Component.translatable("config.vbfreelook.enum.should_switch_back_perspective." + getSerializedName());
    }

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase();
    }
}
