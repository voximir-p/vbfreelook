package org.voximir.vbfreelook.config.enums;

import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.util.StringRepresentable;
import org.voximir.vbfreelook.utilities.ConfigEnum;
import org.voximir.vbfreelook.utilities.Transition;

public enum TransitionType implements Transition, ConfigEnum, NameableEnum, StringRepresentable {
    LINEAR {
        @Override
        public double apply(double t) {
            return t;
        }
    },
    EASE_IN_SINE {
        @Override
        public double apply(double t) {
            return 1 - Math.cos(HALF_PI * t);
        }
    },
    EASE_OUT_SINE {
        @Override
        public double apply(double t) {
            return Math.sin(HALF_PI * t);
        }
    },
    EASE_IN_OUT_SINE {
        @Override
        public double apply(double t) {
            return -(Math.cos(Math.PI * t) - 1) / 2;
        }
    },
    EASE_IN_QUAD {
        @Override
        public double apply(double t) {
            return t * t;
        }
    },
    EASE_OUT_QUAD {
        @Override
        public double apply(double t) {
            return 1 - (1 - t) * (1 - t);
        }
    },
    EASE_IN_OUT_QUAD {
        @Override
        public double apply(double t) {
            double p = 2 * (1 - t);
            return t < 0.5
                    ? 2 * (t * t)
                    : 1 - (p * p) / 2;
        }
    },
    EASE_IN_CUBIC {
        @Override
        public double apply(double t) {
            return t * t * t;
        }
    },
    EASE_OUT_CUBIC {
        @Override
        public double apply(double t) {
            double p = 1 - t;
            return 1 - (p * p * p);
        }
    },
    EASE_IN_OUT_CUBIC {
        @Override
        public double apply(double t) {
            double p = 2 * (1 - t);
            return t < 0.5
                    ? 4 * (t * t * t)
                    : 1 - (p * p * p) / 2;
        }
    },
    EASE_IN_EXP {
        @Override
        public double apply(double t) {
            return t <= 0.0
                    ? 0.0
                    : Math.pow(2.0, 10.0 * t - 10.0);
        }
    },
    EASE_OUT_EXP {
        @Override
        public double apply(double t) {
            return t >= 1.0
                    ? 1.0
                    : 1.0 - Math.pow(2.0, -10.0 * t);
        }
    },
    EASE_IN_OUT_EXP {
        @Override
        public double apply(double t) {
            if (t <= 0.0) return 0.0;
            if (t >= 1.0) return 1.0;
            return t < 0.5
                    ? Math.pow(2.0, 20.0 * t - 10.0) / 2.0
                    : (2.0 - Math.pow(2.0, -20.0 * t + 10.0)) / 2.0;
        }
    };

    private static final double HALF_PI = Math.PI / 2;

    public static final EnumCodec<TransitionType> CODEC =
            StringRepresentable.fromEnum(TransitionType::values);

    @Override
    public String getTranslationKey() {
        return "transition_type";
    }

    @Override
    public String getLocalizedName(String serializedName) {
        return serializedName.replace("_", ".").replace("in.out", "in_out");
    }
}
