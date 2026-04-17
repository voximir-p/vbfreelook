package org.voximir.vbfreelook;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.voximir.vbfreelook.input.InputHandler;
import org.voximir.vbfreelook.input.KeyCategories;

public class VBFreelook implements ClientModInitializer {
    public static final String MOD_ID = "vbfreelook";
    public static final Logger LOGGER = LoggerFactory.getLogger(VBFreelook.class);

    @Override
    public void onInitializeClient() {
        KeyCategories.registerCategories();
        InputHandler.registerKeys();
        InputHandler.registerEvents();

        LOGGER.info("Initialized");
    }
}
