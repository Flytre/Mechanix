package net.flytre.mechanix;

import net.fabricmc.api.ModInitializer;
import net.flytre.mechanix.api.fluid.FluidPackets;
import net.flytre.mechanix.util.*;

//TODO: INCOMPLETE PORT OF CLASS
public class Mechanix implements ModInitializer {
    @Override
    public void onInitialize() {
        System.out.println("Loading Mechanix...");
        MiscRegistry.init();
        MachineRegistry.init();
        ItemRegistry.init();
        BlockRegistry.init();
        RecipeRegistry.init();
        FluidPackets.initServer();
        Packets.serverPacketReceived();
    }
}
