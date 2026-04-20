package org.voximir.vbfreelook.utilities;

import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public interface ConfigEnum extends NameableEnum, StringRepresentable {
    String getTranslationKey();

    default String getLocalizedName(String serializedName) {
        return serializedName;
    }

    @Override
    default Component getDisplayName() {
        return TranslationKey.getEnum(getTranslationKey())
                .dot(getLocalizedName(getSerializedName()))
                .asComponent();
    }

    @Override
    default String getSerializedName() {
        return ((Enum<?>) this).name().toLowerCase(Locale.ROOT);
    }
}
