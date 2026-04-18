package org.voximir.vbfreelook.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v3.ConfigEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.voximir.vbfreelook.VBFreelook;
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

    public Screen createGui(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(Component.translatable(getKey("title")))
                .save(VBFreelookSettings.getInstance()::saveToFile)
                .category(createBehaviorCategory())
                .category(createControlsCategory())
                .build()
                .generateScreen(parent);
    }

    private ConfigCategory createBehaviorCategory() {
        String category = getKey("category.behavior");

        Option<Boolean> shouldSwitchPerspectiveOption = registerOption(
                category + ".perspective.option.should_switch_perspective",
                VBFreelookSettings.getInstance().getShouldSwitchPerspective(),
                TickBoxControllerBuilder::create,
                new OptionFlag[0],
                null
        );

        Option<FreelookPerspective> freelookPerspectiveOption = registerOption(
                category + ".perspective.option.freelook_perspective",
                VBFreelookSettings.getInstance().getFreelookPerspective(),
                option -> EnumControllerBuilder.create(option).enumClass(FreelookPerspective.class),
                new OptionFlag[0],
                null
        );

        Option<ShouldSwitchBackPerspective> shouldSwitchBackPerspectiveOption = registerOption(
                category + ".perspective.option.should_switch_back_perspective",
                VBFreelookSettings.getInstance().getShouldSwitchBackPerspective(),
                option -> EnumControllerBuilder.create(option).enumClass(ShouldSwitchBackPerspective.class),
                new OptionFlag[0],
                (value, key) -> OptionDescription.createBuilder()
                        .text(Component.translatable(key + ".description")
                                .append(Component.translatable(key + ".description." + value.name().toLowerCase()))
                        ).build()
        );

        Option<SwitchBackPerspective> switchBackPerspectiveOption = registerOption(
                category + ".perspective.option.switch_back_perspective",
                VBFreelookSettings.getInstance().getSwitchBackPerspective(),
                option -> EnumControllerBuilder.create(option).enumClass(SwitchBackPerspective.class),
                new OptionFlag[0],
                (value, key) -> {
                    var txt = Component.translatable(key + ".description");
                    if (value == SwitchBackPerspective.ORIGINAL) {
                        txt.append(Component.translatable(key + ".description." + value.name().toLowerCase()));
                    }
                    return OptionDescription.createBuilder().text(txt).build();
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

        OptionGroup perspective = OptionGroup.createBuilder()
                .name(Component.translatable(category + ".perspective"))
                .option(shouldSwitchPerspectiveOption)
                .option(freelookPerspectiveOption)
                .option(shouldSwitchBackPerspectiveOption)
                .option(switchBackPerspectiveOption)
                .build();

        return ConfigCategory.createBuilder()
                .name(Component.translatable(category))
                .group(perspective)
                .build();
    }

    private ConfigCategory createControlsCategory() {
        String category = getKey("category.controls");

        Option<FreelookKeyBehavior> freelookKeyBehaviorOption = registerOption(
                category + ".option.freelook_key_behavior",
                VBFreelookSettings.getInstance().getFreelookKeyBehavior(),
                option -> EnumControllerBuilder.create(option).enumClass(FreelookKeyBehavior.class),
                new OptionFlag[0],
                (value, key) -> OptionDescription.createBuilder()
                        .text(Component.translatable(key + ".description")
                                .append(Component.translatable(key + ".description." + value.name().toLowerCase()))
                        ).build()
        );

        Option<Integer> smartThresholdOption = registerOption(
                category + ".option.smart_threshold",
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

        return ConfigCategory.createBuilder()
                .name(Component.translatable(category))
                .option(freelookKeyBehaviorOption)
                .option(smartThresholdOption)
                .build();
    }

    private static String getKey(String key) {
        return "config." + VBFreelook.MOD_ID + "." + key;
    }

    private <T> Option<T> registerOption(
            String translationKey,
            ConfigEntry<T> entry,
            Function<Option<T>, ControllerBuilder<T>> controllerFactory,
            OptionFlag[] flags,
            BiFunction<T, String, OptionDescription> descriptionFactory
    ) {
        if (descriptionFactory == null) {
            descriptionFactory = (ignoredValue, key) -> OptionDescription.createBuilder()
                    .text(Component.translatable(key + ".description"))
                    .build();
        }

        var finalDescriptionFactory = descriptionFactory;
        Option.Builder<T> builder = Option.<T>createBuilder()
                .name(Component.translatable(translationKey))
                .description(value -> finalDescriptionFactory.apply(value, translationKey))
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
}
