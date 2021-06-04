package net.flytre.mechanix;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.flytre.mechanix.api.fluid.FluidPackets;
import net.flytre.mechanix.api.fluid.FluidPacketsClient;
import net.flytre.mechanix.api.machine.MachineEntityRenderer;
import net.flytre.mechanix.block.alloyer.AlloyerScreen;
import net.flytre.mechanix.block.cell.EnergyCellRenderer;
import net.flytre.mechanix.api.client.SimpleEnergyScreen;
import net.flytre.mechanix.block.crusher.CrusherScreen;
import net.flytre.mechanix.block.fluid_pipe.FluidPipeRenderer;
import net.flytre.mechanix.block.fluid_pipe.FluidPipeScreen;
import net.flytre.mechanix.block.fluid_pipe.FluidPipeScreenHandler;
import net.flytre.mechanix.block.tank.FluidTankRenderer;
import net.flytre.mechanix.block.tank.FluidTankScreen;
import net.flytre.mechanix.util.BlockRegistry;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.server.network.ServerPlayerEntity;

//TODO: INCOMPLETE PORT OF CLASS
public class MechanixClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        FluidPacketsClient.init();

        BlockEntityRendererRegistry.INSTANCE.register(MachineRegistry.ENERGY_CELL_ENTITY, EnergyCellRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(MachineRegistry.FLUID_TANK_ENTITY, FluidTankRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(MachineRegistry.FLUID_PIPE_ENTITY, FluidPipeRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(MachineRegistry.THERMAL_GENERATOR.getEntityType(), MachineEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(MachineRegistry.ALLOYER.getEntityType(), MachineEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(MachineRegistry.CRUSHER.getEntityType(), MachineEntityRenderer::new);


        ScreenRegistry.register(MachineRegistry.SIMPLE_SCREEN_HANDLER, SimpleEnergyScreen::new);
        ScreenRegistry.register(MachineRegistry.FLUID_TANK_SCREEN_HANDLER, FluidTankScreen::new);
        ScreenRegistry.register(MachineRegistry.FLUID_PIPE_SCREEN_HANDLER, FluidPipeScreen::new);
        ScreenRegistry.register(MachineRegistry.THERMAL_GENERATOR.getHandlerType(), SimpleEnergyScreen::new);
        ScreenRegistry.register(MachineRegistry.ALLOYER.getHandlerType(), AlloyerScreen::new);
        ScreenRegistry.register(MachineRegistry.CRUSHER.getHandlerType(), CrusherScreen::new);


        BlockRenderLayerMap.INSTANCE.putBlock(MachineRegistry.FLUID_TANKS.getStandard(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MachineRegistry.FLUID_TANKS.getGilded(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MachineRegistry.FLUID_TANKS.getVysterium(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MachineRegistry.FLUID_TANKS.getNeptunium(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MachineRegistry.FLUID_PIPES.getStandard(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MachineRegistry.FLUID_PIPES.getGilded(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MachineRegistry.FLUID_PIPES.getVysterium(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MachineRegistry.FLUID_PIPES.getNeptunium(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.HARDENED_GLASS, RenderLayer.getTranslucent());

    }
}
