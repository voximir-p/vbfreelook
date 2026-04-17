package org.voximir.vbfreelook.freelook;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import org.voximir.vbfreelook.VBFreelook;
import org.voximir.vbfreelook.config.VBFreelookSettings;

public class FreelookState {
    private static final long NANOS_PER_MILLISECOND = 1_000_000L;

    private static boolean active = false;
    private static long lastPressed;
    private static CameraType lastPerspective = CameraType.FIRST_PERSON;

    public static void activate(Minecraft client) {
        if (active) return;

        active = true;
        lastPerspective = client.options.getCameraType();

        if (VBFreelookSettings.getInstance().getSwitchPerspective().get()) {
            client.options.setCameraType(VBFreelookSettings.getInstance()
                    .getFreelookPerspective()
                    .get()
                    .asCameraType()
            );
        }

        VBFreelook.LOGGER.debug("Freelook activated");
    }

    public static void deactivate(Minecraft client) {
        if (!active) return;

        active = false;

        if (VBFreelookSettings.getInstance().getSwitchPerspective().get()) {
            client.options.setCameraType(lastPerspective);
        }

        VBFreelook.LOGGER.debug("Freelook deactivated");
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
                if (System.nanoTime() - lastPressed > smartThreshold * NANOS_PER_MILLISECOND)
                    deactivate(client);
            }
        }
    }

    public static boolean isActive() {
        return active;
    }
}
