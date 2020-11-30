package net.flytre.mechanix;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.flytre.mechanix.block.cell.EnergyCellRenderer;
import net.flytre.mechanix.block.cell.EnergyCellScreen;
import net.flytre.mechanix.block.fluid_pipe.FluidPipeRenderer;
import net.flytre.mechanix.block.foundry.FoundryScreen;
import net.flytre.mechanix.block.furnace.PoweredFurnaceScreen;
import net.flytre.mechanix.block.generator.GeneratorScreen;
import net.flytre.mechanix.block.hydrator.HydratorScreen;
import net.flytre.mechanix.block.item_pipe.ItemPipeScreen;
import net.flytre.mechanix.block.tank.FluidTankRenderer;
import net.flytre.mechanix.block.tank.FluidTankScreen;
import net.flytre.mechanix.util.FluidRegistry;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockRenderView;

import java.util.function.Function;


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
        ScreenRegistry.register(MachineRegistry.HYDRATOR.getHandlerType(), HydratorScreen::new);
        ScreenRegistry.register(MachineRegistry.FOUNDRY.getHandlerType(), FoundryScreen::new);

        BlockRenderLayerMap.INSTANCE.putBlock(MachineRegistry.FLUID_TANKS.getStandard(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MachineRegistry.FLUID_TANKS.getGilded(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MachineRegistry.FLUID_TANKS.getVysterium(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MachineRegistry.FLUID_TANKS.getNeptunium(), RenderLayer.getCutout());


        BlockRenderLayerMap.INSTANCE.putBlock(MachineRegistry.FLUID_PIPES.getStandard(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MachineRegistry.FLUID_PIPES.getGilded(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MachineRegistry.FLUID_PIPES.getVysterium(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MachineRegistry.FLUID_PIPES.getNeptunium(), RenderLayer.getCutout());

        setupFluidRendering(FluidRegistry.STILL_PERLIUM, FluidRegistry.FLOWING_PERLIUM, new Identifier("mechanix", "molten_perlium"), -1);

    }


    public static void setupFluidRendering(final Fluid still, final Fluid flowing, final Identifier textureFluidId, final int color)
    {
        final Identifier stillSpriteId = new Identifier(textureFluidId.getNamespace(), "block/" + textureFluidId.getPath() + "_still");
        final Identifier flowingSpriteId = new Identifier(textureFluidId.getNamespace(), "block/" + textureFluidId.getPath() + "_flow");

        // If they're not already present, add the sprites to the block atlas
        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) ->
        {
            registry.register(stillSpriteId);
            registry.register(flowingSpriteId);
        });

        final Identifier fluidId = Registry.FLUID.getId(still);
        final Identifier listenerId = new Identifier(fluidId.getNamespace(), fluidId.getPath() + "_reload_listener");

        final Sprite[] fluidSprites = { null, null };

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener()
        {
            @Override
            public Identifier getFabricId()
            {
                return listenerId;
            }

            /**
             * Get the sprites from the block atlas when resources are reloaded
             */
            @Override
            public void apply(ResourceManager resourceManager)
            {
                final Function<Identifier, Sprite> atlas = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
                fluidSprites[0] = atlas.apply(stillSpriteId);
                fluidSprites[1] = atlas.apply(flowingSpriteId);
            }
        });

        // The FluidRenderer gets the sprites and color from a FluidRenderHandler during rendering
        final FluidRenderHandler renderHandler = new FluidRenderHandler()
        {
            @Override
            public Sprite[] getFluidSprites(BlockRenderView view, BlockPos pos, FluidState state)
            {
                return fluidSprites;
            }

            @Override
            public int getFluidColor(BlockRenderView view, BlockPos pos, FluidState state)
            {
                return color;
            }
        };

        FluidRenderHandlerRegistry.INSTANCE.register(still, renderHandler);
        FluidRenderHandlerRegistry.INSTANCE.register(flowing, renderHandler);
    }

}
