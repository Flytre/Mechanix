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
import net.flytre.mechanix.api.machine.MachineBlockEntityRenderer;
import net.flytre.mechanix.block.alloyer.AlloyerScreen;
import net.flytre.mechanix.block.cell.EnergyCellRenderer;
import net.flytre.mechanix.block.cell.EnergyCellScreen;
import net.flytre.mechanix.block.centrifuge.CentrifugeScreen;
import net.flytre.mechanix.block.crafter.CrafterScreen;
import net.flytre.mechanix.block.crusher.CrusherScreen;
import net.flytre.mechanix.block.distiller.DistillerScreen;
import net.flytre.mechanix.block.enchanter.EnchanterScreen;
import net.flytre.mechanix.block.fluid_pipe.FluidPipeRenderer;
import net.flytre.mechanix.block.foundry.FoundryScreen;
import net.flytre.mechanix.block.furnace.PoweredFurnaceScreen;
import net.flytre.mechanix.block.generator.GeneratorScreen;
import net.flytre.mechanix.block.hydrator.HydratorScreen;
import net.flytre.mechanix.block.hydroponator.HydroponatorScreen;
import net.flytre.mechanix.block.item_pipe.ItemPipeScreen;
import net.flytre.mechanix.block.liquifier.LiquifierScreen;
import net.flytre.mechanix.block.pressurizer.PressurizerScreen;
import net.flytre.mechanix.block.quarry.QuarryScreen;
import net.flytre.mechanix.block.sawmill.SawmillScreen;
import net.flytre.mechanix.block.solar_panel.SolarPanelScreen;
import net.flytre.mechanix.block.tank.FluidTankRenderer;
import net.flytre.mechanix.block.tank.FluidTankScreen;
import net.flytre.mechanix.block.thermal_generator.ThermalGenScreen;
import net.flytre.mechanix.block.xp_bank.XpBankScreen;
import net.flytre.mechanix.util.BlockRegistry;
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
        BlockEntityRendererRegistry.INSTANCE.register(MachineRegistry.ALLOYER.getEntityType(), MachineBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(MachineRegistry.CENTRIFUGE.getEntityType(), MachineBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(MachineRegistry.CRAFTER.getEntityType(), MachineBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(MachineRegistry.CRUSHER.getEntityType(), MachineBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(MachineRegistry.DISTILLER.getEntityType(), MachineBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(MachineRegistry.ENERGY_CELL_ENTITY, EnergyCellRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(MachineRegistry.FLUID_PIPE_ENTITY, FluidPipeRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(MachineRegistry.FLUID_TANK_ENTITY, FluidTankRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(MachineRegistry.FOUNDRY.getEntityType(), MachineBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(MachineRegistry.GENERATOR.getEntityType(), MachineBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(MachineRegistry.HYDRATOR.getEntityType(), MachineBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(MachineRegistry.HYDROPONATOR.getEntityType(), MachineBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(MachineRegistry.LIQUIFIER.getEntityType(), MachineBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(MachineRegistry.POWERED_FURNACE.getEntityType(), MachineBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(MachineRegistry.PRESSURIZER.getEntityType(), MachineBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(MachineRegistry.QUARRY.getEntityType(), MachineBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(MachineRegistry.SAWMILL.getEntityType(), MachineBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(MachineRegistry.THERMAL_GENERATOR.getEntityType(), MachineBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(MachineRegistry.XP_BANK.getEntityType(), MachineBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(MachineRegistry.ENCHANTER.getEntityType(), MachineBlockEntityRenderer::new);


        ScreenRegistry.register(MachineRegistry.ALLOYER.getHandlerType(), AlloyerScreen::new);
        ScreenRegistry.register(MachineRegistry.CENTRIFUGE.getHandlerType(), CentrifugeScreen::new);
        ScreenRegistry.register(MachineRegistry.CRAFTER.getHandlerType(), CrafterScreen::new);
        ScreenRegistry.register(MachineRegistry.CRUSHER.getHandlerType(), CrusherScreen::new);
        ScreenRegistry.register(MachineRegistry.DISTILLER.getHandlerType(), DistillerScreen::new);
        ScreenRegistry.register(MachineRegistry.ENERGY_CELL_SCREEN_HANDLER, EnergyCellScreen::new);
        ScreenRegistry.register(MachineRegistry.FLUID_TANK_SCREEN_HANDLER, FluidTankScreen::new);
        ScreenRegistry.register(MachineRegistry.FOUNDRY.getHandlerType(), FoundryScreen::new);
        ScreenRegistry.register(MachineRegistry.GENERATOR.getHandlerType(), GeneratorScreen::new);
        ScreenRegistry.register(MachineRegistry.HYDRATOR.getHandlerType(), HydratorScreen::new);
        ScreenRegistry.register(MachineRegistry.HYDROPONATOR.getHandlerType(), HydroponatorScreen::new);
        ScreenRegistry.register(MachineRegistry.ITEM_PIPE_SCREEN_HANDLER, ItemPipeScreen::new);
        ScreenRegistry.register(MachineRegistry.LIQUIFIER.getHandlerType(), LiquifierScreen::new);
        ScreenRegistry.register(MachineRegistry.POWERED_FURNACE.getHandlerType(), PoweredFurnaceScreen::new);
        ScreenRegistry.register(MachineRegistry.PRESSURIZER.getHandlerType(), PressurizerScreen::new);
        ScreenRegistry.register(MachineRegistry.QUARRY.getHandlerType(), QuarryScreen::new);
        ScreenRegistry.register(MachineRegistry.SAWMILL.getHandlerType(), SawmillScreen::new);
        ScreenRegistry.register(MachineRegistry.SOLAR_PANEL_HANDLER, SolarPanelScreen::new);
        ScreenRegistry.register(MachineRegistry.THERMAL_GENERATOR.getHandlerType(), ThermalGenScreen::new);
        ScreenRegistry.register(MachineRegistry.XP_BANK.getHandlerType(), XpBankScreen::new);
        ScreenRegistry.register(MachineRegistry.ENCHANTER.getHandlerType(), EnchanterScreen::new);



        BlockRenderLayerMap.INSTANCE.putBlock(MachineRegistry.FLUID_TANKS.getStandard(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MachineRegistry.FLUID_TANKS.getGilded(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MachineRegistry.FLUID_TANKS.getVysterium(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MachineRegistry.FLUID_TANKS.getNeptunium(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.HARDENED_GLASS, RenderLayer.getTranslucent());


        BlockRenderLayerMap.INSTANCE.putBlock(MachineRegistry.FLUID_PIPES.getStandard(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MachineRegistry.FLUID_PIPES.getGilded(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MachineRegistry.FLUID_PIPES.getVysterium(), RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(MachineRegistry.FLUID_PIPES.getNeptunium(), RenderLayer.getCutout());

        setupFluidRendering(FluidRegistry.PERLIUM.getStill(), FluidRegistry.PERLIUM.getFlowing(), new Identifier("mechanix", "molten_perlium"), -1);
        setupFluidRendering(FluidRegistry.GOLD.getStill(), FluidRegistry.GOLD.getFlowing(), new Identifier("mechanix", "molten_gold"), -1);
        setupFluidRendering(FluidRegistry.IRON.getStill(), FluidRegistry.IRON.getFlowing(), new Identifier("mechanix", "molten_iron"), -1);
        setupFluidRendering(FluidRegistry.BRONZE.getStill(), FluidRegistry.BRONZE.getFlowing(), new Identifier("mechanix", "molten_bronze"), -1);
        setupFluidRendering(FluidRegistry.COPPER.getStill(), FluidRegistry.COPPER.getFlowing(), new Identifier("mechanix", "molten_copper"), -1);
        setupFluidRendering(FluidRegistry.ELECTRUM.getStill(), FluidRegistry.ELECTRUM.getFlowing(), new Identifier("mechanix", "molten_electrum"), -1);
        setupFluidRendering(FluidRegistry.INVAR.getStill(), FluidRegistry.INVAR.getFlowing(), new Identifier("mechanix", "molten_invar"), -1);
        setupFluidRendering(FluidRegistry.LEAD.getStill(), FluidRegistry.LEAD.getFlowing(), new Identifier("mechanix", "molten_lead"), -1);
        setupFluidRendering(FluidRegistry.NICKEL.getStill(), FluidRegistry.NICKEL.getFlowing(), new Identifier("mechanix", "molten_nickel"), -1);
        setupFluidRendering(FluidRegistry.PLATINUM.getStill(), FluidRegistry.PLATINUM.getFlowing(), new Identifier("mechanix", "molten_platinum"), -1);
        setupFluidRendering(FluidRegistry.SILVER.getStill(), FluidRegistry.SILVER.getFlowing(), new Identifier("mechanix", "molten_silver"), -1);
        setupFluidRendering(FluidRegistry.TIN.getStill(), FluidRegistry.TIN.getFlowing(), new Identifier("mechanix", "molten_tin"), -1);
        setupFluidRendering(FluidRegistry.RAW_GOLD.getStill(), FluidRegistry.RAW_GOLD.getFlowing(), new Identifier("mechanix", "raw_gold"), -1);
        setupFluidRendering(FluidRegistry.RAW_PERLIUM.getStill(), FluidRegistry.RAW_PERLIUM.getFlowing(), new Identifier("mechanix", "raw_perlium"), -1);
        setupFluidRendering(FluidRegistry.RAW_IRON.getStill(), FluidRegistry.RAW_IRON.getFlowing(), new Identifier("mechanix", "raw_iron"), -1);
        setupFluidRendering(FluidRegistry.RAW_COPPER.getStill(), FluidRegistry.RAW_COPPER.getFlowing(), new Identifier("mechanix", "raw_copper"), -1);
        setupFluidRendering(FluidRegistry.RAW_LEAD.getStill(), FluidRegistry.RAW_LEAD.getFlowing(), new Identifier("mechanix", "raw_lead"), -1);
        setupFluidRendering(FluidRegistry.RAW_NICKEL.getStill(), FluidRegistry.RAW_NICKEL.getFlowing(), new Identifier("mechanix", "raw_nickel"), -1);
        setupFluidRendering(FluidRegistry.RAW_PLATINUM.getStill(), FluidRegistry.RAW_PLATINUM.getFlowing(), new Identifier("mechanix", "raw_platinum"), -1);
        setupFluidRendering(FluidRegistry.RAW_SILVER.getStill(), FluidRegistry.RAW_SILVER.getFlowing(), new Identifier("mechanix", "raw_silver"), -1);
        setupFluidRendering(FluidRegistry.RAW_TIN.getStill(), FluidRegistry.RAW_TIN.getFlowing(), new Identifier("mechanix", "raw_tin"), -1);
        setupFluidRendering(FluidRegistry.LIQUID_XP.getStill(), FluidRegistry.LIQUID_XP.getFlowing(), new Identifier("mechanix", "liquid_xp"), -1);

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
