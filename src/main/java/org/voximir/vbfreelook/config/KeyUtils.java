package org.voximir.vbfreelook.config;

import org.voximir.vbfreelook.VBFreelook;

public class KeyUtils {
    public static String join(String... args) {
        return String.join(".", args);
    }

    public static String getConfig(String key) {
        return join("config", VBFreelook.MOD_ID, key);
    }

    public static String getCategory(String category) {
        return join(getConfig("category"), category);
    }
}
