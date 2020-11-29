package net.flytre.mechanix;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.flytre.mechanix.block.cell.EnergyCellRenderer;
import net.flytre.mechanix.block.cell.EnergyCellScreen;
import net.flytre.mechanix.block.fluid_pipe.FluidPipeRenderer;
import net.flytre.mechanix.block.furnace.PoweredFurnaceScreen;
import net.flytre.mechanix.block.generator.GeneratorScreen;
import net.flytre.mechanix.block.item_pipe.ItemPipeScreen;
import net.flytre.mechanix.block.tank.FluidTankRenderer;
import net.flytre.mechanix.block.tank.FluidTankScreen;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.client.render.RenderLayer;


public class MechanixClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(MachineRegistry.ENERGY_CELL_ENTITY, EnergyCellRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(MachineRegistry.FLUID_TANK_ENTITY, FluidTankRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(MachineRegistry.FLUID_PIPE_ENTITY, FluidPipeRenderer::new);


        ScreenRegistry.register(MachineRegistry.ENERGY_CELL_SCREEN_HANDLER, EnergyCellScreen::new);
        ScreenRegistry.register(MachineRegistry.GENERATOR.getHandlerType(), GeneratorScreen::new);
        ScreenRegistry.register(MachineRegistry.POWERED_FURNACE.getHandlerType(), PoweredFurnaceScreen::new);
        ScreenRegistry.register(MachineRegistry.FLUID_TANK_SCREEN_HANDLER, FluidTankScreen::new);
        ScreenRegistry.register(MachineRegistry.ITEM_PIPE_SCREEN_HANDLER, ItemPipeScreen::new);


        BlockRenderLayerMap.INSTANCE.putBlock(MachineRegistry.FLUID_TANKS.getStandard(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MachineRegistry.FLUID_TANKS.getGilded(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MachineRegistry.FLUID_TANKS.getVysterium(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MachineRegistry.FLUID_TANKS.getNeptunium(), RenderLayer.getCutout());


        BlockRenderLayerMap.INSTANCE.putBlock(MachineRegistry.FLUID_PIPES.getStandard(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MachineRegistry.FLUID_PIPES.getGilded(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MachineRegistry.FLUID_PIPES.getVysterium(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MachineRegistry.FLUID_PIPES.getNeptunium(), RenderLayer.getCutout());


    }
}
