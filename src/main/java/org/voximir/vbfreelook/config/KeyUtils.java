package org.voximir.vbfreelook.config;

import org.voximir.vbfreelook.VBFreelook;

public class KeyUtils {
    public static String getKey(String key) {
        return "config." + VBFreelook.MOD_ID + "." + key;
    }

    public static String getCategory(String category) {
        return getKey("category." + category);
    }

    public static String getGroup(String category, String group) {
        return category + "." + group;
    }

    public static String getOption(String group, String option) {
        return group + "." + option;
    }
}
