package net.flytre.mechanix.util;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.flytre.mechanix.block.cable.Cable;
import net.flytre.mechanix.block.cell.EnergyCell;
import net.flytre.mechanix.block.cell.EnergyCellEntity;
import net.flytre.mechanix.block.cell.EnergyCellScreenHandler;
import net.flytre.mechanix.block.furnace.PoweredFurnaceBlock;
import net.flytre.mechanix.block.furnace.PoweredFurnaceBlockEntity;
import net.flytre.mechanix.block.furnace.PoweredFurnaceScreenHandler;
import net.flytre.mechanix.block.generator.GeneratorBlock;
import net.flytre.mechanix.block.generator.GeneratorBlockEntity;
import net.flytre.mechanix.block.generator.GeneratorScreenHandler;
import net.flytre.mechanix.block.item_pipe.ItemPipe;
import net.flytre.mechanix.block.item_pipe.ItemPipeBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class MachineRegistry {

    public static final Block CABLE = new Cable(FabricBlockSettings.of(Material.METAL));

    public static final Block ITEM_PIPE = new ItemPipe(FabricBlockSettings.of(Material.METAL));
    public static BlockEntityType<ItemPipeBlockEntity> ITEM_PIPE_ENTITY;


    public static final Block ENERGY_CELL = new EnergyCell(FabricBlockSettings.of(Material.METAL));
    public static BlockEntityType<EnergyCellEntity> ENERGY_CELL_ENTITY;
    public static ScreenHandlerType<EnergyCellScreenHandler> ENERGY_CELL_SCREEN_HANDLER;

    public static final Block GENERATOR = new GeneratorBlock(FabricBlockSettings.of(Material.METAL));
    public static BlockEntityType<GeneratorBlockEntity> GENERATOR_ENTITY;
    public static ScreenHandlerType<GeneratorScreenHandler> GENERATOR_SCREEN_HANDLER;

    public static final Block POWERED_FURNACE = new PoweredFurnaceBlock(FabricBlockSettings.of(Material.METAL));
    public static BlockEntityType<PoweredFurnaceBlockEntity> POWERED_FURNACE_ENTITY;
    public static ScreenHandlerType<PoweredFurnaceScreenHandler> POWERED_FURNACE_SCREEN_HANDLER;


    public static void init() {
        Registry.register(Registry.BLOCK, new Identifier("mechanix", "cable"), CABLE);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "cable"), new BlockItem(CABLE, new Item.Settings().group(MiscRegistry.TAB)));


        Registry.register(Registry.BLOCK, new Identifier("mechanix", "item_pipe"), ITEM_PIPE);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "item_pipe"), new BlockItem(ITEM_PIPE, new Item.Settings().group(MiscRegistry.TAB)));
        ITEM_PIPE_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "mechanix:item_pipe", BlockEntityType.Builder.create(ItemPipeBlockEntity::new, ITEM_PIPE).build(null));


        Registry.register(Registry.BLOCK, new Identifier("mechanix", "energy_cell"), ENERGY_CELL);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "energy_cell"), new BlockItem(ENERGY_CELL, new Item.Settings().group(MiscRegistry.TAB)));
        ENERGY_CELL_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "mechanix:energy_cell", BlockEntityType.Builder.create(EnergyCellEntity::new, ENERGY_CELL).build(null));
        ENERGY_CELL_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(new Identifier("mechanix:energy_cell"), EnergyCellScreenHandler::new);

        Registry.register(Registry.BLOCK, new Identifier("mechanix", "generator"), GENERATOR);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "generator"), new BlockItem(GENERATOR, new Item.Settings().group(MiscRegistry.TAB)));
        GENERATOR_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "mechanix:generator", BlockEntityType.Builder.create(GeneratorBlockEntity::new, GENERATOR).build(null));
        GENERATOR_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(new Identifier("mechanix:generator"), GeneratorScreenHandler::new);

        Registry.register(Registry.BLOCK, new Identifier("mechanix", "furnace"), POWERED_FURNACE);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "furnace"), new BlockItem(POWERED_FURNACE, new Item.Settings().group(MiscRegistry.TAB)));
        POWERED_FURNACE_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "mechanix:furnace", BlockEntityType.Builder.create(PoweredFurnaceBlockEntity::new, POWERED_FURNACE).build(null));
        POWERED_FURNACE_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(new Identifier("mechanix:furnace"), PoweredFurnaceScreenHandler::new);

    }
}
