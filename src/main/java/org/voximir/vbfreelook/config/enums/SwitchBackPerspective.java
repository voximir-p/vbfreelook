package org.voximir.vbfreelook.config.enums;

import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import org.voximir.vbfreelook.config.VBFreelookSettings;

public enum SwitchBackPerspective implements NameableEnum, StringRepresentable {
    ORIGINAL,
    FIRST_PERSON,
    THIRD_PERSON,
    SECOND_PERSON;

    public static final EnumCodec<SwitchBackPerspective> CODEC =
            StringRepresentable.fromEnum(SwitchBackPerspective::values);

    @Override
    public Component getDisplayName() {
        return Component.translatable("config.vbfreelook.enum.switch_back_perspective." + getSerializedName());
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase();
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
