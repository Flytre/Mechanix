package net.flytre.mechanix;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.flytre.mechanix.block.cell.EnergyCellRenderer;
import net.flytre.mechanix.block.cell.EnergyCellScreen;
import net.flytre.mechanix.block.furnace.PoweredFurnaceScreen;
import net.flytre.mechanix.block.generator.GeneratorScreen;
import net.flytre.mechanix.util.MachineRegistry;


public class MechanixClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(MachineRegistry.ENERGY_CELL_ENTITY, EnergyCellRenderer::new);
        ScreenRegistry.register(MachineRegistry.ENERGY_CELL_SCREEN_HANDLER, EnergyCellScreen::new);
        ScreenRegistry.register(MachineRegistry.GENERATOR_SCREEN_HANDLER, GeneratorScreen::new);
        ScreenRegistry.register(MachineRegistry.POWERED_FURNACE_SCREEN_HANDLER, PoweredFurnaceScreen::new);

    }
}
