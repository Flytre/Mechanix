package net.flytre.mechanix.util;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.flytre.mechanix.base.energy.EnergyItem;
import net.flytre.mechanix.base.fluid.FluidInventory;
import net.flytre.mechanix.block.cable.Cable;
import net.flytre.mechanix.block.cell.EnergyCell;
import net.flytre.mechanix.block.cell.EnergyCellEntity;
import net.flytre.mechanix.block.cell.EnergyCellScreenHandler;
import net.flytre.mechanix.block.fluid_pipe.FluidPipe;
import net.flytre.mechanix.block.fluid_pipe.FluidPipeBlockEntity;
import net.flytre.mechanix.block.furnace.PoweredFurnaceBlock;
import net.flytre.mechanix.block.furnace.PoweredFurnaceBlockEntity;
import net.flytre.mechanix.block.furnace.PoweredFurnaceScreenHandler;
import net.flytre.mechanix.block.generator.GeneratorBlock;
import net.flytre.mechanix.block.generator.GeneratorBlockEntity;
import net.flytre.mechanix.block.generator.GeneratorScreenHandler;
import net.flytre.mechanix.block.item_pipe.ItemPipe;
import net.flytre.mechanix.block.item_pipe.ItemPipeBlockEntity;
import net.flytre.mechanix.block.tank.FluidTank;
import net.flytre.mechanix.block.tank.FluidTankBlockEntity;
import net.flytre.mechanix.block.tank.FluidTankScreenHandler;
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

public class MachineRegistry {

    public static final Cable CABLE = new Cable(FabricBlockSettings.of(Material.METAL).hardness(0.9f));
    public static final Cable GILDED_CABLE = new Cable(FabricBlockSettings.of(Material.METAL).hardness(0.9f));
    public static final Cable VYSTERIUM_CABLE = new Cable(FabricBlockSettings.of(Material.METAL).hardness(0.9f));
    public static final Cable NEPTUNIUM_CABLE = new Cable(FabricBlockSettings.of(Material.METAL).hardness(0.9f));


    public static final Block ITEM_PIPE = new ItemPipe(FabricBlockSettings.of(Material.METAL).hardness(0.9f));
    public static BlockEntityType<ItemPipeBlockEntity> ITEM_PIPE_ENTITY;

    public static final FluidTank FLUID_TANK = new FluidTank(FabricBlockSettings.of(Material.METAL).nonOpaque().hardness(4.5f));
    public static final FluidTank GILDED_FLUID_TANK = new FluidTank(FabricBlockSettings.of(Material.METAL).nonOpaque().hardness(4.5f));
    public static final FluidTank VYSTERIUM_FLUID_TANK = new FluidTank(FabricBlockSettings.of(Material.METAL).nonOpaque().hardness(4.5f));
    public static final FluidTank NEPTUNIUM_FLUID_TANK = new FluidTank(FabricBlockSettings.of(Material.METAL).nonOpaque().hardness(4.5f));
    public static BlockEntityType<FluidTankBlockEntity> FLUID_TANK_ENTITY;
    public static ScreenHandlerType<FluidTankScreenHandler> FLUID_TANK_SCREEN_HANDLER;


    public static final FluidPipe FLUID_PIPE = new FluidPipe(FabricBlockSettings.of(Material.METAL).nonOpaque().hardness(0.9f));
    public static final FluidPipe GILDED_FLUID_PIPE = new FluidPipe(FabricBlockSettings.of(Material.METAL).nonOpaque().hardness(0.9f));
    public static final FluidPipe VYSTERIUM_FLUID_PIPE = new FluidPipe(FabricBlockSettings.of(Material.METAL).nonOpaque().hardness(0.9f));
    public static final FluidPipe NEPTUNIUM_FLUID_PIPE = new FluidPipe(FabricBlockSettings.of(Material.METAL).nonOpaque().hardness(0.9f));
    public static BlockEntityType<FluidPipeBlockEntity> FLUID_PIPE_ENTITY;


    public static final EnergyCell ENERGY_CELL = new EnergyCell(FabricBlockSettings.of(Material.METAL).hardness(4.5f));
    public static final EnergyCell GILDED_ENERGY_CELL = new EnergyCell(FabricBlockSettings.of(Material.METAL).hardness(4.5f));
    public static final EnergyCell VYSTERIUM_ENERGY_CELL = new EnergyCell(FabricBlockSettings.of(Material.METAL).hardness(4.5f));
    public static final EnergyCell NEPTUNIUM_ENERGY_CELL = new EnergyCell(FabricBlockSettings.of(Material.METAL).hardness(4.5f));
    public static BlockEntityType<EnergyCellEntity> ENERGY_CELL_ENTITY;
    public static ScreenHandlerType<EnergyCellScreenHandler> ENERGY_CELL_SCREEN_HANDLER;

    public static final Block GENERATOR = new GeneratorBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f));
    public static BlockEntityType<GeneratorBlockEntity> GENERATOR_ENTITY;
    public static ScreenHandlerType<GeneratorScreenHandler> GENERATOR_SCREEN_HANDLER;

    public static final Block POWERED_FURNACE = new PoweredFurnaceBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f));
    public static BlockEntityType<PoweredFurnaceBlockEntity> POWERED_FURNACE_ENTITY;
    public static ScreenHandlerType<PoweredFurnaceScreenHandler> POWERED_FURNACE_SCREEN_HANDLER;


    public static void init() {
        registerCable(CABLE,"cable");
        registerCable(GILDED_CABLE,"gilded_cable");
        registerCable(VYSTERIUM_CABLE,"vysterium_cable");
        registerCable(NEPTUNIUM_CABLE,"neptunium_cable");

        Registry.register(Registry.BLOCK, new Identifier("mechanix", "item_pipe"), ITEM_PIPE);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "item_pipe"), new BlockItem(ITEM_PIPE, new Item.Settings().group(MiscRegistry.TAB)));
        ITEM_PIPE_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "mechanix:item_pipe", BlockEntityType.Builder.create(ItemPipeBlockEntity::new, ITEM_PIPE).build(null));


        registerTank(FLUID_TANK,"tank");
        registerTank(GILDED_FLUID_TANK,"gilded_tank");
        registerTank(VYSTERIUM_FLUID_TANK,"vysterium_tank");
        registerTank(NEPTUNIUM_FLUID_TANK,"neptunium_tank");
        FLUID_TANK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "mechanix:tank", BlockEntityType.Builder.create(FluidTankBlockEntity::new, FLUID_TANK, GILDED_FLUID_TANK, VYSTERIUM_FLUID_TANK, NEPTUNIUM_FLUID_TANK).build(null));
        FLUID_TANK_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(new Identifier("mechanix:tank"), FluidTankScreenHandler::new);


        registerFluidPipe(FLUID_PIPE,"fluid_pipe");
        registerFluidPipe(GILDED_FLUID_PIPE,"gilded_fluid_pipe");
        registerFluidPipe(VYSTERIUM_FLUID_PIPE,"vysterium_fluid_pipe");
        registerFluidPipe(NEPTUNIUM_FLUID_PIPE,"neptunium_fluid_pipe");
        FLUID_PIPE_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "mechanix:fluid_pipe", BlockEntityType.Builder.create(FluidPipeBlockEntity::new, FLUID_PIPE, GILDED_FLUID_PIPE, VYSTERIUM_FLUID_PIPE, NEPTUNIUM_FLUID_PIPE).build(null));


        registerEnergyCell(ENERGY_CELL,"energy_cell");
        registerEnergyCell(GILDED_ENERGY_CELL,"gilded_energy_cell");
        registerEnergyCell(VYSTERIUM_ENERGY_CELL,"vysterium_energy_cell");
        registerEnergyCell(NEPTUNIUM_ENERGY_CELL,"neptunium_energy_cell");
        ENERGY_CELL_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "mechanix:energy_cell", BlockEntityType.Builder.create(EnergyCellEntity::new, ENERGY_CELL,GILDED_ENERGY_CELL,VYSTERIUM_ENERGY_CELL,NEPTUNIUM_ENERGY_CELL).build(null));
        ENERGY_CELL_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(new Identifier("mechanix:energy_cell"), EnergyCellScreenHandler::new);

        Registry.register(Registry.BLOCK, new Identifier("mechanix", "generator"), GENERATOR);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "generator"), new EnergyItem(GENERATOR, new Item.Settings().group(MiscRegistry.TAB)));
        GENERATOR_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "mechanix:generator", BlockEntityType.Builder.create(GeneratorBlockEntity::new, GENERATOR).build(null));
        GENERATOR_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(new Identifier("mechanix:generator"), GeneratorScreenHandler::new);

        Registry.register(Registry.BLOCK, new Identifier("mechanix", "furnace"), POWERED_FURNACE);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "furnace"), new EnergyItem(POWERED_FURNACE, new Item.Settings().group(MiscRegistry.TAB)));
        POWERED_FURNACE_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "mechanix:furnace", BlockEntityType.Builder.create(PoweredFurnaceBlockEntity::new, POWERED_FURNACE).build(null));
        POWERED_FURNACE_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(new Identifier("mechanix:furnace"), PoweredFurnaceScreenHandler::new);

    }


    private static void registerCable(Cable cable, String id) {
        Registry.register(Registry.BLOCK, new Identifier("mechanix", id), cable);
        Registry.register(Registry.ITEM, new Identifier("mechanix", id), new BlockItem(cable, new Item.Settings().group(MiscRegistry.TAB)));
    }

    private static void registerTank(FluidTank tank, String id) {
        Registry.register(Registry.BLOCK, new Identifier("mechanix", id), tank);
        Registry.register(Registry.ITEM, new Identifier("mechanix", id), new BlockItem(tank, new Item.Settings().group(MiscRegistry.TAB)) {
            @Override
            public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
                FluidInventory.toToolTip(stack,tooltip);
                super.appendTooltip(stack, world, tooltip, context);
            }
        });

    }

    private static void registerFluidPipe(FluidPipe pipe, String id) {
        Registry.register(Registry.BLOCK, new Identifier("mechanix", id), pipe);
        Registry.register(Registry.ITEM, new Identifier("mechanix", id), new BlockItem(pipe, new Item.Settings().group(MiscRegistry.TAB)));
    }

    private static void registerEnergyCell(EnergyCell cell, String id) {
        Registry.register(Registry.BLOCK, new Identifier("mechanix", id), cell);
        Registry.register(Registry.ITEM, new Identifier("mechanix", id), new EnergyItem(cell, new Item.Settings().group(MiscRegistry.TAB)));
    }
}
