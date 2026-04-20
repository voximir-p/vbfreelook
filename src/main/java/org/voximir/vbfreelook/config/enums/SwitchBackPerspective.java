package org.voximir.vbfreelook.config.enums;

import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.util.StringRepresentable;
import org.voximir.vbfreelook.config.VBFreelookSettings;
import org.voximir.vbfreelook.utilities.ConfigEnum;

public enum SwitchBackPerspective implements ConfigEnum, NameableEnum, StringRepresentable {
    ORIGINAL,
    FIRST_PERSON,
    THIRD_PERSON,
    SECOND_PERSON;

    public static final EnumCodec<SwitchBackPerspective> CODEC =
            StringRepresentable.fromEnum(SwitchBackPerspective::values);

    @Override
    public String getTranslationKey() {
        return "switch_back_perspective";
    }

    public CameraType asCameraType() {
        return switch (this) {
            case ORIGINAL -> null; // Handled separately in FreelookState
            case FIRST_PERSON -> CameraType.FIRST_PERSON;
            case SECOND_PERSON -> CameraType.THIRD_PERSON_FRONT;
            case THIRD_PERSON -> CameraType.THIRD_PERSON_BACK;
        };
    }

    public static void switchBackPerspective(Minecraft client, CameraType lastPerspective) {
        var switchBackPerspective = VBFreelookSettings.getInstance().getSwitchBackPerspective().get().asCameraType();
        if (switchBackPerspective == null) {
            client.options.setCameraType(lastPerspective);
        } else {
            client.options.setCameraType(switchBackPerspective);
        }
    }
}
