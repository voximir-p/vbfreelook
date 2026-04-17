package org.voximir.vbfreelook.freelook;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import org.voximir.vbfreelook.VBFreelook;
import org.voximir.vbfreelook.config.VBFreelookSettings;

public class FreelookState {
    private static boolean active = false;
    private static long lastPressed;
    private static CameraType lastPerspective;

    public static void activate(Minecraft client) {
        active = true;
        lastPerspective = client.options.getCameraType();

        var freelookPerspective = VBFreelookSettings.getInstance().getFreelookPerspective().get();
        client.options.setCameraType(freelookPerspective.asCameraType());
        VBFreelook.LOGGER.info("Freelook activated");
    }

    public static void deactivate(Minecraft client) {
        active = false;

        client.options.setCameraType(lastPerspective);
        VBFreelook.LOGGER.info("Freelook deactivated");
    }

    public static void toggle(Minecraft client) {
        if (active) deactivate(client);
        else activate(client);
    }

    public static void handleKeyPressed(Minecraft client) {
        var freelookKeyBehavior = VBFreelookSettings.getInstance().getFreelookKeyBehavior().get();
        switch (freelookKeyBehavior) {
            case HOLD -> activate(client);
            case TOGGLE -> toggle(client);
            case SMART -> {
                if (!active) {
                    toggle(client);
                    lastPressed = System.nanoTime();
                }
            }
        }
    }

    public static void handleKeyReleased(Minecraft client) {
        var freelookKeyBehavior = VBFreelookSettings.getInstance().getFreelookKeyBehavior().get();
        var smartThreshold = VBFreelookSettings.getInstance().getSmartThreshold().get();
        switch (freelookKeyBehavior) {
            case HOLD -> deactivate(client);
            case SMART -> {
                if (System.nanoTime() - lastPressed > smartThreshold * 1_000_000L)
                    deactivate(client);
            }
        }
    }

    public static boolean isActive() {
        return active;
    }
}
