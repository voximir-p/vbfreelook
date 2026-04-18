package org.voximir.vbfreelook.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v3.ConfigEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.voximir.vbfreelook.config.enums.FreelookKeyBehavior;
import org.voximir.vbfreelook.config.enums.FreelookPerspective;
import org.voximir.vbfreelook.config.enums.ShouldSwitchBackPerspective;
import org.voximir.vbfreelook.config.enums.SwitchBackPerspective;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class SettingsGuiFactory {
    public static Screen createSettingsGui(Screen parent) {
        return new SettingsGuiFactory().createGui(parent);
    }

    private Screen createGui(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(Component.translatable(KeyUtils.getConfig("title")))
                .save(VBFreelookSettings.getInstance()::saveToFile)
                .category(Categories.createBehaviorCategory())
                .category(Categories.createControlsCategory())
                .build()
                .generateScreen(parent);
    }

    private static <T> Option<T> registerOption(
            String translationKey,
            ConfigEntry<T> entry,
            Function<Option<T>, ControllerBuilder<T>> controllerFactory,
            OptionFlag[] flags,
            BiFunction<T, String, OptionDescription.Builder> descriptionBuildFactory
    ) {
        var descriptionKey = KeyUtils.join(translationKey, "description");
        if (descriptionBuildFactory == null) {
            descriptionBuildFactory = (ignoredValue, key) -> OptionDescription.createBuilder()
                    .text(Component.translatable(key));
        }

        var finalDescriptionFactory = descriptionBuildFactory;
        Option.Builder<T> builder = Option.<T>createBuilder()
                .name(Component.translatable(translationKey))
                .description(value -> finalDescriptionFactory.apply(value, descriptionKey).build())
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
        for (var dependant : dependentOptions) {
            dependant.setAvailable(availabilityPredicate.apply(sourceOption.pendingValue()));
        }

        sourceOption.addEventListener((option, event) -> {
            if (event == OptionEventListener.Event.INITIAL || event == OptionEventListener.Event.STATE_CHANGE) {
                for (var dependant : dependentOptions) {
                    dependant.setAvailable(availabilityPredicate.apply(option.pendingValue()));
                }
            }
        });
    }

    private static class Categories {
        private static ConfigCategory createBehaviorCategory() {
            String category = KeyUtils.getCategory("behavior");

            class Groups {
                private static Option<Boolean> shouldSwitchPerspectiveOption;
                private static Option<FreelookPerspective> freelookPerspectiveOption;

                private OptionGroup createPerspectiveGroup() {
                    String group = KeyUtils.join(category, "perspective");

                    shouldSwitchPerspectiveOption = registerOption(
                            KeyUtils.join(group, "should_switch_perspective"),
                            VBFreelookSettings.getInstance().getShouldSwitchPerspective(),
                            TickBoxControllerBuilder::create,
                            new OptionFlag[0],
                            null
                    );

                    freelookPerspectiveOption = registerOption(
                            KeyUtils.join(group, "freelook_perspective"),
                            VBFreelookSettings.getInstance().getFreelookPerspective(),
                            option -> EnumControllerBuilder.create(option).enumClass(FreelookPerspective.class),
                            new OptionFlag[0],
                            null
                    );

                    var shouldSwitchBackPerspectiveOption = registerOption(
                            KeyUtils.join(group, "should_switch_back_perspective"),
                            VBFreelookSettings.getInstance().getShouldSwitchBackPerspective(),
                            option -> EnumControllerBuilder.create(option).enumClass(ShouldSwitchBackPerspective.class),
                            new OptionFlag[0],
                            (value, key) -> OptionDescription.createBuilder()
                                    .text(Component.translatable(key)
                                            .append(Component.translatable(KeyUtils.join(key, value.name().toLowerCase()))))
                    );

                    var switchBackPerspectiveOption = registerOption(
                            KeyUtils.join(group, "switch_back_perspective"),
                            VBFreelookSettings.getInstance().getSwitchBackPerspective(),
                            option -> EnumControllerBuilder.create(option).enumClass(SwitchBackPerspective.class),
                            new OptionFlag[0],
                            (value, key) -> {
                                var txt = Component.translatable(key);
                                if (value == SwitchBackPerspective.ORIGINAL) {
                                    txt.append(Component.translatable(KeyUtils.join(key, value.name().toLowerCase())));
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
                            List.of(freelookPerspectiveOption,
                                    shouldSwitchBackPerspectiveOption,
                                    switchBackPerspectiveOption),
                            Boolean::booleanValue
                    );

                    return OptionGroup.createBuilder()
                            .name(Component.translatable(group))
                            .option(shouldSwitchPerspectiveOption)
                            .option(freelookPerspectiveOption)
                            .option(shouldSwitchBackPerspectiveOption)
                            .option(switchBackPerspectiveOption)
                            .build();
                }

                private OptionGroup createTransitionGroup() {
                    String group = KeyUtils.join(category, "transition");

                    var zoomOutTimeOption = registerOption(
                            KeyUtils.join(group, "zoom_out_time"),
                            VBFreelookSettings.getInstance().getZoomOutTime(),
                            option -> IntegerSliderControllerBuilder.create(option)
                                    .range(0, 2000)
                                    .step(10)
                                    .formatValue(ms -> Component.literal(String.format("%d ms", ms))),
                            new OptionFlag[0],
                            null
                    );

                    bindDependentsAvailability(
                            shouldSwitchPerspectiveOption,
                            List.of(zoomOutTimeOption),
                            Boolean::booleanValue
                    );

                    bindDependentsAvailability(
                            freelookPerspectiveOption,
                            List.of(zoomOutTimeOption),
                            val -> val != FreelookPerspective.FIRST_PERSON
                    );

                    return OptionGroup.createBuilder()
                            .name(Component.translatable(group))
                            .option(zoomOutTimeOption)
                            .build();
                }
            }

            Groups groups = new Groups();

            return ConfigCategory.createBuilder()
                    .name(Component.translatable(category))
                    .group(groups.createPerspectiveGroup())
                    .group(groups.createTransitionGroup())
                    .build();
        }

        private static ConfigCategory createControlsCategory() {
            String category = KeyUtils.getCategory("controls");

            class Groups {
                private OptionGroup createBasicGroup() {
                    String group = KeyUtils.join(category, "basic");

                    var freelookKeyBehaviorOption = registerOption(
                            KeyUtils.join(group, "freelook_key_behavior"),
                            VBFreelookSettings.getInstance().getFreelookKeyBehavior(),
                            option -> EnumControllerBuilder.create(option).enumClass(FreelookKeyBehavior.class),
                            new OptionFlag[0],
                            (value, key) -> OptionDescription.createBuilder()
                                    .text(Component.translatable(key)
                                            .append(Component.translatable(KeyUtils.join(key, value.name().toLowerCase()))))
                    );

                    var smartThresholdOption = registerOption(
                            KeyUtils.join(group, "smart_threshold"),
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
                            .name(Component.translatable(group))
                            .option(freelookKeyBehaviorOption)
                            .option(smartThresholdOption)
                            .build();
                }
            }

            Groups groups = new Groups();

            return ConfigCategory.createBuilder()
                    .name(Component.translatable(category))
                    .group(groups.createBasicGroup())
                    .build();
        }
    }
}
