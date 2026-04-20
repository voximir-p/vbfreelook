package org.voximir.vbfreelook.utilities;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringRepresentable;
import org.voximir.vbfreelook.VBFreelook;

public class TranslationKey {
    private final TranslationKey parent;
    private final String part;
    private String cached;

    private TranslationKey(TranslationKey parent, String part) {
        this.parent = parent;
        this.part = part;
    }

    public static TranslationKey of(String... parts) {
        TranslationKey key = null;
        for (String part : parts) {
            key = new TranslationKey(key, part);
        }
        return key;
    }

    public TranslationKey dot(String part) {
        return new TranslationKey(this, part);
    }

    public <T extends StringRepresentable> TranslationKey dotEnum(T value) {
        return dot(value.getSerializedName());
    }

    private String build() {
        StringBuilder sb = new StringBuilder();
        buildInto(sb);
        return sb.toString();
    }

    private void buildInto(StringBuilder sb) {
        if (parent != null) {
            parent.buildInto(sb);
            sb.append('.');
        }
        sb.append(part);
    }

    public String asString() {
        if (cached == null) {
            if (parent != null && parent.cached != null) {
                cached = parent.cached + "." + part;
            } else {
                cached = build();
            }
        }
        return cached;
    }

    public MutableComponent asComponent() {
        return Component.translatable(asString());
    }

    public static TranslationKey getConfigFor(String key) {
        return TranslationKey.of("config", VBFreelook.MOD_ID, key);
    }

    public static TranslationKey getCategory(String key) {
        return TranslationKey.getConfigFor("category").dot(key);
    }

    public static TranslationKey getEnum(String key) {
        return TranslationKey.getConfigFor("enum").dot(key);
    }
}
