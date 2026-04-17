package org.voximir.vbfreelook.input;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.voximir.vbfreelook.VBFreelook;

public class KeyCategories {
    public static KeyMapping.Category VBFREELOOK;

    public static void registerCategories() {
        VBFREELOOK = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(VBFreelook.MOD_ID, VBFreelook.MOD_ID));
    }
}
