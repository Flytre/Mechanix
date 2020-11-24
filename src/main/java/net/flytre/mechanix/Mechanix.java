package net.flytre.mechanix;

import net.fabricmc.api.ModInitializer;
import net.flytre.mechanix.util.MachineRegistry;
import net.flytre.mechanix.util.MiscRegistry;
import net.flytre.mechanix.util.Packets;

public class Mechanix implements ModInitializer {
	@Override
	public void onInitialize() {
		System.out.println("Loading Mechanix...");
		MiscRegistry.init();
		MachineRegistry.init();
		Packets.serverPacketRecieved();
	}
}
