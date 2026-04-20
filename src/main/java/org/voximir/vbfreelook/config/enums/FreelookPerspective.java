package org.voximir.vbfreelook.config.enums;

import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.client.CameraType;
import net.minecraft.util.StringRepresentable;
import org.voximir.vbfreelook.utilities.ConfigEnum;

public enum FreelookPerspective implements ConfigEnum, NameableEnum, StringRepresentable {
    THIRD_PERSON,
    FIRST_PERSON,
    SECOND_PERSON;

    public static final EnumCodec<FreelookPerspective> CODEC =
            StringRepresentable.fromEnum(FreelookPerspective::values);

    @Override
    public String getTranslationKey() {
        return "freelook_perspective";
    }

    public CameraType asCameraType() {
        return switch (this) {
            case FIRST_PERSON -> CameraType.FIRST_PERSON;
            case SECOND_PERSON -> CameraType.THIRD_PERSON_FRONT;
            case THIRD_PERSON -> CameraType.THIRD_PERSON_BACK;
        };
    }
}
