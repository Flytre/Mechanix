package net.flytre.mechanix;

import net.fabricmc.api.ModInitializer;
import net.flytre.mechanix.api.fluid.screen.FluidPackets;
import net.flytre.mechanix.api.upgrade.UpgradePackets;
import net.flytre.mechanix.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Mechanix implements ModInitializer {

    public static final Logger LOGGER = LogManager.getFormatterLogger("Mechanix");

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Mechanix");
        FluidRegistry.init();
        MiscRegistry.init();
        MachineRegistry.init();
        ItemRegistry.init();
        BlockRegistry.init();
        RecipeRegistry.init();
        FluidPackets.initServer();
        UpgradePackets.initServer();
        Packets.serverPacketReceived();
        LOGGER.info("Mechanix initialization finished.");
    }
}
