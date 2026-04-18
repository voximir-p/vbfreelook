package org.voximir.vbfreelook.config.enums;

import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.client.CameraType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum FreelookPerspective implements NameableEnum, StringRepresentable {
    THIRD_PERSON,
    FIRST_PERSON,
    SECOND_PERSON;

    public static final EnumCodec<FreelookPerspective> CODEC =
            StringRepresentable.fromEnum(FreelookPerspective::values);

    @Override
    public Component getDisplayName() {
        return Component.translatable("config.vbfreelook.enum.freelook_perspective." + this.name().toLowerCase());
    }

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase();
    }

    public CameraType asCameraType() {
        return switch (this) {
            case FIRST_PERSON -> CameraType.FIRST_PERSON;
            case SECOND_PERSON -> CameraType.THIRD_PERSON_FRONT;
            case THIRD_PERSON -> CameraType.THIRD_PERSON_BACK;
        };
    }
}
