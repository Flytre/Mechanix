package net.flytre.mechanix;

import net.fabricmc.api.ModInitializer;
import net.flytre.mechanix.util.*;

public class Mechanix implements ModInitializer {
	@Override
	public void onInitialize() {
		System.out.println("Loading Mechanix...");
		FluidRegistry.init();
		MiscRegistry.init();
		MachineRegistry.init();
		ItemRegistery.init();
		BlockRegistry.init();
		RecipeRegistry.init();
		Packets.serverPacketRecieved();
	}
}
