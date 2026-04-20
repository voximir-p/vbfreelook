package org.voximir.vbfreelook.input;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.voximir.vbfreelook.VBFreelook;

public final class KeyCategories {
    public static KeyMapping.Category VBFREELOOK_CATEGORY;

    private KeyCategories() {
    }

    public static void registerCategories() {
        VBFREELOOK_CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(VBFreelook.MOD_ID, VBFreelook.MOD_ID));
    }
}
