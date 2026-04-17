package org.voximir.vbfreelook.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.config.v3.ConfigEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.BiFunction;
import java.util.function.Function;

public class SettingsGuiFactory {
    public static Screen createSettingsGui(Screen parent) {
        return new SettingsGuiFactory().createGui(parent);
    }

    public Screen createGui(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(getCKey("title"))
                .save(VBFreelookSettings.getInstance()::saveToFile)
                .category(createVBFreelookCategory())
                .build()
                .generateScreen(parent);
    }

    private ConfigCategory createVBFreelookCategory() {
        String category = getKey("category.vbfreelook");

        Option<FreelookKeyBehaviour> freelookKeyBehaviourOption = registerOption(
                category + ".behaviour.option.freelook_key_behaviour",
                VBFreelookSettings.getInstance().getFreelookKeyBehaviour(),
                option -> EnumControllerBuilder.create(option).enumClass(FreelookKeyBehaviour.class),
                new OptionFlag[0],
                (value, key) -> OptionDescription.createBuilder().text(
                        Component.translatable(key + ".description")
                                .append(Component.translatable(key + ".description." + value.name().toLowerCase()))
                ));

        OptionGroup behaviour = OptionGroup.createBuilder()
                .name(Component.translatable(category + ".behaviour"))
                .option(freelookKeyBehaviourOption)
                .option(registerOption(
                        category + ".behaviour.option.freelook_perspective",
                        VBFreelookSettings.getInstance().getFreelookPerspective(),
                        option -> EnumControllerBuilder.create(option).enumClass(FreelookPerspective.class),
                        new OptionFlag[0],
                        null
                ))
                .build();

        Option<Integer> smartThresholdOption = registerOption(
                category + ".smart_mode.option.smart_threshold",
                VBFreelookSettings.getInstance().getSmartThreshold(),
                option -> IntegerSliderControllerBuilder.create(option)
                        .range(1, 500)
                        .step(10)
                        .formatValue(ms -> Component.literal(String.format("%d ms", ms))),
                new OptionFlag[0],
                null
        );

        smartThresholdOption.setAvailable(freelookKeyBehaviourOption.pendingValue() == FreelookKeyBehaviour.SMART);
        freelookKeyBehaviourOption.addEventListener((option, event) -> {
            if (event == OptionEventListener.Event.INITIAL || event == OptionEventListener.Event.STATE_CHANGE)
                smartThresholdOption.setAvailable(option.pendingValue() == FreelookKeyBehaviour.SMART);
        });

        OptionGroup smartMode = OptionGroup.createBuilder()
                .name(Component.translatable(category + ".smart_mode"))
                .collapsed(true)
                .option(smartThresholdOption)
                .build();

        return ConfigCategory.createBuilder()
                .name(Component.translatable(category))
                .group(behaviour)
                .group(smartMode)
                .build();
    }

    private <T> Option<T> registerOption(
            String translationKey,
            ConfigEntry<T> entry,
            Function<Option<T>, ControllerBuilder<T>> controllerFactory,
            OptionFlag[] flags,
            BiFunction<T, String,  OptionDescription.Builder> descriptionBuilderFactory
    ) {
        if (descriptionBuilderFactory == null) {
            descriptionBuilderFactory = (value, key) -> OptionDescription.createBuilder()
                    .text(Component.translatable(key + ".description"));
        }

        BiFunction<T, String, OptionDescription.Builder> finalDescriptionBuilderFactory = descriptionBuilderFactory;
        Option.Builder<T> builder = Option.<T>createBuilder()
                .name(Component.translatable(translationKey))
                .description(value -> finalDescriptionBuilderFactory.apply(value, translationKey).build())
                .binding(entry.defaultValue(), entry::get, entry::set)
                .controller(controllerFactory);

        if (flags.length > 0) {
            builder.flag(flags);
        }

        return builder.build();
    }

    private static String getKey(String key) {
        return "config.vbfreelook." + key;
    }

    private static String getKey(String category, String key) {
        return "config.vbfreelook." + category + "." + key;
    }

    private static Component getCKey(String key) {
        return Component.translatable(getKey(key));
    }

    private static Component getCKey(String category, String key) {
        return Component.translatable(getKey(category, key));
    }
}
