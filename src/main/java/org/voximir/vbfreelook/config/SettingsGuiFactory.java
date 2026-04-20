package org.voximir.vbfreelook.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v3.ConfigEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.voximir.vbfreelook.config.enums.*;
import org.voximir.vbfreelook.utilities.TranslationKey;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class SettingsGuiFactory {
    private SettingsGuiFactory() {
    }

    public static Screen createSettingsGui(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(TranslationKey.getConfigFor("title").asComponent())
                .save(VBFreelookSettings.getInstance()::saveToFile)
                .category(Categories.createBehaviorCategory())
                .category(Categories.createControlsCategory())
                .build()
                .generateScreen(parent);
    }

    private static <T> Option<T> registerOption(
            TranslationKey translationKey,
            ConfigEntry<T> entry,
            Function<Option<T>, ControllerBuilder<T>> controllerFactory,
            OptionFlag[] flags,
            BiFunction<T, TranslationKey, OptionDescription.Builder> descriptionBuilderFactory
    ) {
        var descriptionKey = translationKey.dot("description");
        if (descriptionBuilderFactory == null) {
            descriptionBuilderFactory = (ignoredValue, key) -> OptionDescription.createBuilder()
                    .text(key.asComponent());
        }

        var finalDescriptionBuilderFactory = descriptionBuilderFactory;
        Option.Builder<T> builder = Option.<T>createBuilder()
                .name(translationKey.asComponent())
                .description(value -> finalDescriptionBuilderFactory.apply(value, descriptionKey).build())
                .binding(entry.defaultValue(), entry::get, entry::set)
                .controller(controllerFactory);

        if (flags.length > 0) {
            builder.flag(flags);
        }

        return builder.build();
    }

    private static <T> void bindDependentsAvailability(
            Option<T> sourceOption,
            List<Option<?>> dependentOptions,
            Function<T, Boolean> availabilityPredicate
    ) {
        for (var dependent : dependentOptions) {
            dependent.setAvailable(availabilityPredicate.apply(sourceOption.pendingValue()));
        }

        sourceOption.addEventListener((option, event) -> {
            if (event == OptionEventListener.Event.INITIAL || event == OptionEventListener.Event.STATE_CHANGE) {
                for (var dependent : dependentOptions) {
                    dependent.setAvailable(availabilityPredicate.apply(option.pendingValue()));
                }
            }
        });
    }

    private static class Categories {
        private static ConfigCategory createBehaviorCategory() {
            TranslationKey category = TranslationKey.getCategory("behavior");

            class Groups {
                static Option<Boolean> shouldSwitchPerspectiveOption;
                static Option<FreelookPerspective> freelookPerspectiveOption;

                OptionGroup createPerspectiveGroup() {
                    TranslationKey group = category.dot("perspective");

                    shouldSwitchPerspectiveOption = registerOption(
                            group.dot("should_switch_perspective"),
                            VBFreelookSettings.getInstance().getShouldSwitchPerspective(),
                            TickBoxControllerBuilder::create,
                            new OptionFlag[0],
                            null
                    );

                    var shouldSwitchBackPerspectiveOption = registerOption(
                            group.dot("should_switch_back_perspective"),
                            VBFreelookSettings.getInstance().getShouldSwitchBackPerspective(),
                            option -> EnumControllerBuilder.create(option).enumClass(ShouldSwitchBackPerspective.class),
                            new OptionFlag[0],
                            (value, descKey) -> OptionDescription.createBuilder()
                                    .text(descKey.asComponent()
                                            .append(descKey.dotEnum(value).asComponent()))
                    );

                    freelookPerspectiveOption = registerOption(
                            group.dot("freelook_perspective"),
                            VBFreelookSettings.getInstance().getFreelookPerspective(),
                            option -> EnumControllerBuilder.create(option).enumClass(FreelookPerspective.class),
                            new OptionFlag[0],
                            null
                    );

                    var switchBackPerspectiveOption = registerOption(
                            group.dot("switch_back_perspective"),
                            VBFreelookSettings.getInstance().getSwitchBackPerspective(),
                            option -> EnumControllerBuilder.create(option).enumClass(SwitchBackPerspective.class),
                            new OptionFlag[0],
                            (value, key) -> {
                                var txt = key.asComponent();
                                if (value == SwitchBackPerspective.ORIGINAL) {
                                    txt.append(key.dotEnum(value).asComponent());
                                }
                                return OptionDescription.createBuilder().text(txt);
                            }
                    );

                    bindDependentsAvailability(
                            shouldSwitchBackPerspectiveOption,
                            List.of(switchBackPerspectiveOption),
                            val -> val != ShouldSwitchBackPerspective.NEVER
                    );

                    bindDependentsAvailability(
                            shouldSwitchPerspectiveOption,
                            List.of(shouldSwitchBackPerspectiveOption,
                                    freelookPerspectiveOption,
                                    switchBackPerspectiveOption),
                            Boolean::booleanValue
                    );

                    return OptionGroup.createBuilder()
                            .name(group.asComponent())
                            .option(shouldSwitchPerspectiveOption)
                            .option(shouldSwitchBackPerspectiveOption)
                            .option(freelookPerspectiveOption)
                            .option(switchBackPerspectiveOption)
                            .build();
                }

                OptionGroup createTransitionGroup() {
                    TranslationKey group = category.dot("transition");

                    var zoomOutTimeOption = registerOption(
                            group.dot("zoom_out_time"),
                            VBFreelookSettings.getInstance().getZoomOutTime(),
                            option -> IntegerSliderControllerBuilder.create(option)
                                    .range(0, 5000)
                                    .step(100)
                                    .formatValue(ms -> Component.literal(String.format("%.1f secs", ms / 1000.0f))),
                            new OptionFlag[0],
                            null
                    );

                    var zoomOutTransitionOption = registerOption(
                            group.dot("zoom_out_transition"),
                            VBFreelookSettings.getInstance().getZoomOutTransition(),
                            option -> EnumControllerBuilder.create(option).enumClass(TransitionType.class),
                            new OptionFlag[0],
                            null
                    );

                    bindDependentsAvailability(
                            shouldSwitchPerspectiveOption,
                            List.of(zoomOutTimeOption,
                                    zoomOutTransitionOption),
                            Boolean::booleanValue
                    );

                    bindDependentsAvailability(
                            freelookPerspectiveOption,
                            List.of(zoomOutTimeOption,
                                    zoomOutTransitionOption),
                            val -> val != FreelookPerspective.FIRST_PERSON
                    );

                    bindDependentsAvailability(
                            zoomOutTimeOption,
                            List.of(zoomOutTransitionOption),
                            val -> val > 0
                    );

                    return OptionGroup.createBuilder()
                            .name(group.asComponent())
                            .option(zoomOutTimeOption)
                            .option(zoomOutTransitionOption)
                            .build();
                }

                OptionGroup createOtherGroup() {
                    TranslationKey group = category.dot("other");

                    var cameraNoClipOption = registerOption(
                            group.dot("camera_no_clip"),
                            VBFreelookSettings.getInstance().getCameraNoClip(),
                            TickBoxControllerBuilder::create,
                            new OptionFlag[0],
                            null
                    );

                    return OptionGroup.createBuilder()
                            .name(group.asComponent())
                            .option(cameraNoClipOption)
                            .build();
                }
            }

            Groups groups = new Groups();

            return ConfigCategory.createBuilder()
                    .name(category.asComponent())
                    .group(groups.createPerspectiveGroup())
                    .group(groups.createTransitionGroup())
                    .group(groups.createOtherGroup())
                    .build();
        }

        private static ConfigCategory createControlsCategory() {
            TranslationKey category = TranslationKey.getCategory("controls");

            class Groups {
                OptionGroup createBasicGroup() {
                    TranslationKey group = category.dot("basic");

                    var freelookKeyBehaviorOption = registerOption(
                            group.dot("freelook_key_behavior"),
                            VBFreelookSettings.getInstance().getFreelookKeyBehavior(),
                            option -> EnumControllerBuilder.create(option).enumClass(FreelookKeyBehavior.class),
                            new OptionFlag[0],
                            (value, key) -> OptionDescription.createBuilder()
                                    .text(key.asComponent()
                                            .append(key.dotEnum(value).asComponent()))
                    );

                    var smartThresholdOption = registerOption(
                            group.dot("smart_threshold"),
                            VBFreelookSettings.getInstance().getSmartThreshold(),
                            option -> IntegerSliderControllerBuilder.create(option)
                                    .range(0, 500)
                                    .step(10)
                                    .formatValue(ms -> Component.literal(String.format("%d ms", ms))),
                            new OptionFlag[0],
                            null
                    );

                    bindDependentsAvailability(
                            freelookKeyBehaviorOption,
                            List.of(smartThresholdOption),
                            val -> val == FreelookKeyBehavior.SMART
                    );

                    return OptionGroup.createBuilder()
                            .name(group.asComponent())
                            .option(freelookKeyBehaviorOption)
                            .option(smartThresholdOption)
                            .build();
                }
            }

            Groups groups = new Groups();

            return ConfigCategory.createBuilder()
                    .name(category.asComponent())
                    .group(groups.createBasicGroup())
                    .build();
        }
    }
}
