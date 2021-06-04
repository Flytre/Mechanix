package net.flytre.mechanix.util;


import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.flytre.mechanix.api.energy.EnergyDisplayItem;
import net.flytre.mechanix.api.fluid.FluidInventory;
import net.flytre.mechanix.api.fluid.Fraction;
import net.flytre.mechanix.api.machine.MachineBlock;
import net.flytre.mechanix.api.machine.MachineList;
import net.flytre.mechanix.api.machine.MachineType;
import net.flytre.mechanix.block.alloyer.AlloyerEntity;
import net.flytre.mechanix.block.alloyer.AlloyerHandler;
import net.flytre.mechanix.block.cable.Cable;
import net.flytre.mechanix.block.cell.EnergyCell;
import net.flytre.mechanix.block.cell.EnergyCellEntity;
import net.flytre.mechanix.api.energy.SimpleEnergyScreenHandler;
import net.flytre.mechanix.block.crusher.CrusherEntity;
import net.flytre.mechanix.block.crusher.CrusherHandler;
import net.flytre.mechanix.block.fluid_pipe.FluidPipe;
import net.flytre.mechanix.block.fluid_pipe.FluidPipeEntity;
import net.flytre.mechanix.block.fluid_pipe.FluidPipeScreenHandler;
import net.flytre.mechanix.block.solar_panel.SolarPanelBlock;
import net.flytre.mechanix.block.solar_panel.SolarPanelEntity;
import net.flytre.mechanix.block.tank.FluidTank;
import net.flytre.mechanix.block.tank.FluidTankEntity;
import net.flytre.mechanix.block.tank.FluidTankScreenHandler;
import net.flytre.mechanix.block.thermal_generator.ThermalEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

//TODO: INCOMPLETE PORT OF CLASS
public class MachineRegistry {

    public static ScreenHandlerType<SimpleEnergyScreenHandler> SIMPLE_SCREEN_HANDLER;


    public static MachineList<Cable> CABLES;


    public static MachineList<EnergyCell> ENERGY_CELLS;
    public static BlockEntityType<EnergyCellEntity> ENERGY_CELL_ENTITY;

    public static MachineList<SolarPanelBlock> SOLAR_PANELS;
    public static BlockEntityType<SolarPanelEntity> SOLAR_PANEL_ENTITY;


    public static MachineList<FluidTank> FLUID_TANKS;
    public static BlockEntityType<FluidTankEntity> FLUID_TANK_ENTITY;
    public static ScreenHandlerType<FluidTankScreenHandler> FLUID_TANK_SCREEN_HANDLER;

    public static MachineList<FluidPipe> FLUID_PIPES;
    public static BlockEntityType<FluidPipeEntity> FLUID_PIPE_ENTITY;
    public static ScreenHandlerType<FluidPipeScreenHandler> FLUID_PIPE_SCREEN_HANDLER;

    public static MachineType<MachineBlock, ThermalEntity, SimpleEnergyScreenHandler> THERMAL_GENERATOR;
    public static MachineType<MachineBlock, AlloyerEntity, AlloyerHandler> ALLOYER;
    public static MachineType<MachineBlock, CrusherEntity, CrusherHandler> CRUSHER;


    public static void init() {
        SIMPLE_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(new Identifier("mechanix:simple_energy_screen_handler"), SimpleEnergyScreenHandler::new);


        CABLES = registerTier(
                (tier) -> new Cable(FabricBlockSettings.of(Material.METAL).hardness(0.9f), tier.equals("") ? 50 : tier.startsWith("g") ? 150 : tier.startsWith("v") ? 500 : 2000),
                "cable",
                (cable) -> new BlockItem(cable, new Item.Settings().group(MiscRegistry.TAB))
        );


        ENERGY_CELLS = registerTier(
                () -> new EnergyCell(FabricBlockSettings.of(Material.METAL).hardness(4.5f)),
                "energy_cell",
                (cell) -> new EnergyDisplayItem(cell, new Item.Settings().group(MiscRegistry.TAB))
        );
        ENERGY_CELL_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "mechanix:energy_cell", BlockEntityType.Builder.create(EnergyCellEntity::new, ENERGY_CELLS.toArray(new EnergyCell[4])).build(null));


        SOLAR_PANELS = registerTier(() -> new SolarPanelBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f)),
                "solar_panel",
                (panel) -> new BlockItem(panel, new Item.Settings().group(MiscRegistry.TAB)));
        SOLAR_PANEL_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "mechanix:solar_panel", BlockEntityType.Builder.create(SolarPanelEntity::new, SOLAR_PANELS.toArray(new SolarPanelBlock[4])).build(null));


        FLUID_TANKS = registerTier(
                (tier) -> new FluidTank(FabricBlockSettings.of(Material.METAL).nonOpaque().hardness(4.5f).luminance((state) -> state.get(FluidTank.LIGHT_LEVEL)),
                        tier.equals("") ? new Fraction(16,1) : tier.startsWith("g") ? new Fraction(64,1) : tier.startsWith("v") ? new Fraction(256,1) : new Fraction(1024,1)),
                "tank",
                (tank) -> new BlockItem(tank, new Item.Settings().group(MiscRegistry.TAB)) {
                    @Override
                    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
                        FluidInventory.toToolTip(stack, tooltip);
                        super.appendTooltip(stack, world, tooltip, context);
                    }
                });
        FLUID_TANK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "mechanix:tank", BlockEntityType.Builder.create(FluidTankEntity::new, FLUID_TANKS.toArray(new FluidTank[4])).build(null));
        FLUID_TANK_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(new Identifier("mechanix:tank"), FluidTankScreenHandler::new);

        FLUID_PIPES = registerTier(
                (tier) -> new FluidPipe(FabricBlockSettings.of(Material.METAL).nonOpaque().hardness(0.9f),
                        tier.equals("") ? 50 : tier.startsWith("g") ? 150 : tier.startsWith("v") ? 500 : 1500),
                "fluid_pipe",
                (pipe) -> new BlockItem(pipe, new Item.Settings().group(MiscRegistry.TAB))
        );
        FLUID_PIPE_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "mechanix:fluid_pipe", BlockEntityType.Builder.create(FluidPipeEntity::new, FLUID_PIPES.toArray(new FluidPipe[4])).build(null));
        FLUID_PIPE_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(new Identifier("mechanix:fluid_pipe"), FluidPipeScreenHandler::new);


        THERMAL_GENERATOR = new MachineType<>(
                new MachineBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f), ThermalEntity::new),
                "thermal_generator",
                IconMaker.STANDARD,
                ThermalEntity::new,
                SimpleEnergyScreenHandler::new
        );

        ALLOYER = new MachineType<>(
                new MachineBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f), AlloyerEntity::new),
                "alloyer",
                IconMaker.STANDARD,
                AlloyerEntity::new,
                AlloyerHandler::new
        );
        CRUSHER = new MachineType<>(
                new MachineBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f), CrusherEntity::new),
                "crusher",
                IconMaker.STANDARD,
                CrusherEntity::new,
                CrusherHandler::new
        );
    }


    private static <T extends Block> MachineList<T> registerTier(Function<String, T> maker, String id, IconMaker<T> creator) {
        MachineList<T> result = new MachineList<>();

        String[] tiers = new String[]{"", "gilded", "vysterium", "neptunium"};

        for (String tier : tiers) {
            T blk = maker.apply(tier);
            result.add(blk);
            registerBlock(blk, tier + (tier.length() > 0 ? "_" : "") + id, creator);
        }
        return result;
    }

    private static <T extends Block> MachineList<T> registerTier(Supplier<T> maker, String id, IconMaker<T> creator) {
        return registerTier(s -> maker.get(), id, creator);
    }

    public static <T extends Block> void registerBlock(T block, String id, IconMaker<T> creator) {
        Registry.register(Registry.BLOCK, new Identifier("mechanix", id), block);
        Registry.register(Registry.ITEM, new Identifier("mechanix", id), creator.create(block));
    }

}
