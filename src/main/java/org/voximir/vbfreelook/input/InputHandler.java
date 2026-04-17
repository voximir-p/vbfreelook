package org.voximir.vbfreelook.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.voximir.vbfreelook.config.SettingsGuiFactory;
import org.voximir.vbfreelook.freelook.FreelookState;

public class InputHandler {
    public static KeyMapping FREELOOK_KEY;
    public static KeyMapping SETTINGS_KEY;

    private static boolean wasFreelookKeyDown = false;

    public static void registerKeys() {
        FREELOOK_KEY = registerKey("freelook", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, KeyCategories.VBFREELOOK);
        SETTINGS_KEY = registerKey("settings", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F12, KeyCategories.VBFREELOOK);
    }

    public static void registerEvents() {
        ClientTickEvents.END_CLIENT_TICK.register(InputHandler::handleFreelookKey);
        ClientTickEvents.END_CLIENT_TICK.register(InputHandler::handleSettingsKey);
    }

    private static void handleFreelookKey(Minecraft client) {
        if (client.player == null) return;

        boolean isFreelookKeyDown = FREELOOK_KEY.isDown();

        // Only react to key state changes
        if (isFreelookKeyDown == wasFreelookKeyDown) return;

        wasFreelookKeyDown = isFreelookKeyDown;

        if (isFreelookKeyDown)
            FreelookState.handleKeyPressed(client);
        else
            FreelookState.handleKeyReleased(client);
    }

    private static void handleSettingsKey(Minecraft client) {
        while (SETTINGS_KEY.consumeClick()) {
            client.setScreen(SettingsGuiFactory.createSettingsGui(client.screen));
        }
    }

    private static KeyMapping registerKey(String id, InputConstants.Type type, int code, KeyMapping.Category category) {
        return KeyMappingHelper.registerKeyMapping(new KeyMapping("key.vbfreelook." + id, type, code, category));
    }
}
