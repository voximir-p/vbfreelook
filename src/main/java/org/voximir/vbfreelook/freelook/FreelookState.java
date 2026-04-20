package org.voximir.vbfreelook.freelook;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import org.voximir.vbfreelook.VBFreelook;
import org.voximir.vbfreelook.config.VBFreelookSettings;
import org.voximir.vbfreelook.config.enums.SwitchBackPerspective;

public class FreelookState {
    private static final long NANOS_PER_MILLISECOND = 1_000_000L;

    private static boolean active = false;
    private static long zoomOutStart;
    private static long lastPressed;
    private static CameraType lastPerspective = CameraType.FIRST_PERSON;

    public static void activate(Minecraft client) {
        if (active) return;

        var settings = VBFreelookSettings.getInstance();

        active = true;
        lastPerspective = client.options.getCameraType();
        if (lastPerspective == CameraType.FIRST_PERSON) {
            zoomOutStart = System.nanoTime();
        }

        if (settings.getShouldSwitchPerspective().get()) {
            client.options.setCameraType(settings
                    .getFreelookPerspective()
                    .get()
                    .asCameraType()
            );
        }

        VBFreelook.LOGGER.debug("Freelook activated");
    }

    public static void deactivate(Minecraft client) {
        if (!active) return;

        var settings = VBFreelookSettings.getInstance();

        active = false;
        if (settings.getShouldSwitchPerspective().get()) {
            switch (settings.getShouldSwitchBackPerspective().get()) {
                case ALWAYS -> SwitchBackPerspective.switchBackPerspective(client, lastPerspective);
                case IF_UNCHANGED -> {
                    var freelookPerspective = settings.getFreelookPerspective().get().asCameraType();
                    if (client.options.getCameraType() == freelookPerspective) {
                        SwitchBackPerspective.switchBackPerspective(client, lastPerspective);
                    }
                }
            }
        }

        VBFreelook.LOGGER.debug("Freelook deactivated");
    }

    public static void toggle(Minecraft client) {
        if (active) deactivate(client);
        else activate(client);
    }

    public static void handleKeyPressed(Minecraft client) {
        var settings = VBFreelookSettings.getInstance();
        var freelookKeyBehavior = settings.getFreelookKeyBehavior().get();
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
        var settings = VBFreelookSettings.getInstance();
        var freelookKeyBehavior = settings.getFreelookKeyBehavior().get();
        var smartThreshold = settings.getSmartThreshold().get();
        switch (freelookKeyBehavior) {
            case HOLD -> deactivate(client);
            case SMART -> {
                if (System.nanoTime() - lastPressed > smartThreshold * NANOS_PER_MILLISECOND) {
                    deactivate(client);
                } else {
                    lastPressed = 0;
                }
            }
        }
    }

    public static boolean isActive() {
        return active;
    }

    public static double getZoomingOutProgress() {
        if (!active) return 0.0f;

        var settings = VBFreelookSettings.getInstance();
        var elapsed = System.nanoTime() - zoomOutStart;
        var zoomOutTimeNanos = settings.getZoomOutTime().get() * NANOS_PER_MILLISECOND;
        return Math.min(1.0, (double) elapsed / zoomOutTimeNanos);
    }
}
