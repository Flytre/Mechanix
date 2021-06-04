package net.flytre.mechanix.util;


import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.flytre.mechanix.api.energy.EnergyDisplayItem;
import net.flytre.mechanix.api.energy.SimpleEnergyScreenHandler;
import net.flytre.mechanix.api.fluid.FluidInventory;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.api.machine.MachineBlock;
import net.flytre.mechanix.api.machine.MachineList;
import net.flytre.mechanix.api.machine.MachineType;
import net.flytre.mechanix.block.alloyer.AlloyerEntity;
import net.flytre.mechanix.block.alloyer.AlloyerHandler;
import net.flytre.mechanix.block.cable.Cable;
import net.flytre.mechanix.block.cell.EnergyCell;
import net.flytre.mechanix.block.cell.EnergyCellEntity;
import net.flytre.mechanix.block.centrifuge.CentrifugeEntity;
import net.flytre.mechanix.block.centrifuge.CentrifugeHandler;
import net.flytre.mechanix.block.crafter.CrafterEntity;
import net.flytre.mechanix.block.crafter.CrafterHandler;
import net.flytre.mechanix.block.crusher.CrusherEntity;
import net.flytre.mechanix.block.crusher.CrusherHandler;
import net.flytre.mechanix.block.disenchanter.DisenchanterEntity;
import net.flytre.mechanix.block.disenchanter.DisenchanterHandler;
import net.flytre.mechanix.block.distiller.DistillerEntity;
import net.flytre.mechanix.block.distiller.DistillerHandler;
import net.flytre.mechanix.block.enchanter.EnchanterEntity;
import net.flytre.mechanix.block.enchanter.EnchanterHandler;
import net.flytre.mechanix.block.fluid_pipe.FluidPipe;
import net.flytre.mechanix.block.fluid_pipe.FluidPipeEntity;
import net.flytre.mechanix.block.fluid_pipe.FluidPipeScreenHandler;
import net.flytre.mechanix.block.foundry.FoundryEntity;
import net.flytre.mechanix.block.foundry.FoundryHandler;
import net.flytre.mechanix.block.furnace.FurnaceTypeMachineBlock;
import net.flytre.mechanix.block.furnace.PoweredFurnaceEntity;
import net.flytre.mechanix.block.furnace.PoweredFurnaceHandler;
import net.flytre.mechanix.block.generator.GeneratorEntity;
import net.flytre.mechanix.block.generator.GeneratorHandler;
import net.flytre.mechanix.block.hydrator.HydratorEntity;
import net.flytre.mechanix.block.hydrator.HydratorHandler;
import net.flytre.mechanix.block.hydroponator.HydroponatorEntity;
import net.flytre.mechanix.block.hydroponator.HydroponatorHandler;
import net.flytre.mechanix.block.item_collector.ItemCollectorEntity;
import net.flytre.mechanix.block.item_collector.ItemCollectorHandler;
import net.flytre.mechanix.block.liquifier.LiquifierEntity;
import net.flytre.mechanix.block.liquifier.LiquifierHandler;
import net.flytre.mechanix.block.pressurizer.PressurizerEntity;
import net.flytre.mechanix.block.pressurizer.PressurizerHandler;
import net.flytre.mechanix.block.quarry.QuarryEntity;
import net.flytre.mechanix.block.quarry.QuarryHandler;
import net.flytre.mechanix.block.sawmill.SawmillEntity;
import net.flytre.mechanix.block.sawmill.SawmillHandler;
import net.flytre.mechanix.block.solar_panel.SolarPanelBlock;
import net.flytre.mechanix.block.solar_panel.SolarPanelEntity;
import net.flytre.mechanix.block.tank.FluidTank;
import net.flytre.mechanix.block.tank.FluidTankEntity;
import net.flytre.mechanix.block.tank.FluidTankScreenHandler;
import net.flytre.mechanix.block.thermal_generator.ThermalEntity;
import net.flytre.mechanix.block.transmuter.TransmuterEntity;
import net.flytre.mechanix.block.transmuter.TransmuterHandler;
import net.flytre.mechanix.block.xp_bank.BankEntity;
import net.flytre.mechanix.block.xp_bank.BankHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
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
    public static MachineType<MachineBlock, HydratorEntity, HydratorHandler> HYDRATOR;
    public static MachineType<FurnaceTypeMachineBlock, PoweredFurnaceEntity, PoweredFurnaceHandler> POWERED_FURNACE;
    public static MachineType<FurnaceTypeMachineBlock, GeneratorEntity, GeneratorHandler> GENERATOR;
    public static MachineType<MachineBlock, SawmillEntity, SawmillHandler> SAWMILL;
    public static MachineType<MachineBlock, LiquifierEntity, LiquifierHandler> LIQUIFIER;
    public static MachineType<MachineBlock, FoundryEntity, FoundryHandler> FOUNDRY;
    public static MachineType<MachineBlock, DistillerEntity, DistillerHandler> DISTILLER;
    public static MachineType<MachineBlock, EnchanterEntity, EnchanterHandler> ENCHANTER;
    public static MachineType<MachineBlock, DisenchanterEntity, DisenchanterHandler> DISENCHANTER;
    public static MachineType<MachineBlock, PressurizerEntity, PressurizerHandler> PRESSURIZER;
    public static MachineType<MachineBlock, CentrifugeEntity, CentrifugeHandler> CENTRIFUGE;
    public static MachineType<MachineBlock, HydroponatorEntity, HydroponatorHandler> HYDROPONATOR;
    public static MachineType<MachineBlock, CrafterEntity, CrafterHandler> CRAFTER;
    public static MachineType<MachineBlock, ItemCollectorEntity, ItemCollectorHandler> ITEM_COLLECTOR;
    public static MachineType<MachineBlock, QuarryEntity, QuarryHandler> QUARRY;
    public static MachineType<MachineBlock, BankEntity, BankHandler> XP_BANK;
    public static MachineType<MachineBlock, TransmuterEntity, TransmuterHandler> TRANSMUTER;


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
                        (tier.equals("") ? 16 : tier.startsWith("g") ? 64 : tier.startsWith("v") ? 256 : 1024) * FluidStack.UNITS_PER_BUCKET),
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
                        tier.equals("") ? 0.05f : tier.startsWith("g") ? 0.15f : tier.startsWith("v") ? 0.5f : 1.5f),
                "fluid_pipe",
                (pipe) -> new BlockItem(pipe, new Item.Settings().group(MiscRegistry.TAB))
        );
        FLUID_PIPE_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "mechanix:fluid_pipe", BlockEntityType.Builder.create(FluidPipeEntity::new, FLUID_PIPES.toArray(new FluidPipe[4])).build(null));
        FLUID_PIPE_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(new Identifier("mechanix:fluid_pipe"), FluidPipeScreenHandler::new);


        THERMAL_GENERATOR = new MachineType<>(
                new MachineBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f), ThermalEntity::new),
                "thermal_generator",
                IconMaker.ENERGY,
                ThermalEntity::new,
                SimpleEnergyScreenHandler::new
        );

        ALLOYER = new MachineType<>(
                new MachineBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f).luminance(b -> b.get(MachineBlock.ACTIVATED) ? 11 : 0), AlloyerEntity::new),
                "alloyer",
                IconMaker.ENERGY,
                AlloyerEntity::new,
                AlloyerHandler::new
        );
        CRUSHER = new MachineType<>(
                new MachineBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f).luminance(b -> b.get(MachineBlock.ACTIVATED) ? 11 : 0), CrusherEntity::new),
                "crusher",
                IconMaker.ENERGY,
                CrusherEntity::new,
                CrusherHandler::new
        );

        HYDRATOR = new MachineType<>(
                new MachineBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f), HydratorEntity::new),
                "hydrator",
                IconMaker.FLUID,
                HydratorEntity::new,
                HydratorHandler::new
        );

        POWERED_FURNACE = new MachineType<>(
                new FurnaceTypeMachineBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f).luminance(b -> b.get(MachineBlock.ACTIVATED) ? 11 : 0), PoweredFurnaceEntity::new),
                "furnace",
                IconMaker.ENERGY,
                PoweredFurnaceEntity::new,
                PoweredFurnaceHandler::new);

        GENERATOR = new MachineType<>(
                new FurnaceTypeMachineBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f).luminance(b -> b.get(MachineBlock.ACTIVATED) ? 11 : 0), GeneratorEntity::new),
                "generator",
                IconMaker.ENERGY,
                GeneratorEntity::new,
                GeneratorHandler::new);


        SAWMILL = new MachineType<>(
                new MachineBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f).luminance(b -> b.get(MachineBlock.ACTIVATED) ? 11 : 0), SawmillEntity::new),
                "sawmill",
                IconMaker.ENERGY,
                SawmillEntity::new,
                SawmillHandler::new
        );

        LIQUIFIER = new MachineType<>(
                new MachineBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f).luminance(b -> b.get(MachineBlock.ACTIVATED) ? 11 : 0), LiquifierEntity::new),
                "liquifier",
                IconMaker.ENERGY,
                LiquifierEntity::new,
                LiquifierHandler::new
        );
        FOUNDRY = new MachineType<>(
                new MachineBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f).luminance(b -> b.get(MachineBlock.ACTIVATED) ? 11 : 0), FoundryEntity::new),
                "foundry",
                IconMaker.ENERGY,
                FoundryEntity::new,
                FoundryHandler::new
        );

        DISTILLER = new MachineType<>(
                new MachineBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f).luminance(b -> b.get(MachineBlock.ACTIVATED) ? 11 : 0), DistillerEntity::new),
                "distiller",
                IconMaker.ENERGY,
                DistillerEntity::new,
                DistillerHandler::new
        );

        ENCHANTER = new MachineType<>(
                new MachineBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f).luminance(b -> b.get(MachineBlock.ACTIVATED) ? 11 : 0), EnchanterEntity::new),
                "enchanter",
                IconMaker.ENERGY,
                EnchanterEntity::new,
                EnchanterHandler::new
        );

        DISENCHANTER = new MachineType<>(
                new MachineBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f).luminance(b -> b.get(MachineBlock.ACTIVATED) ? 11 : 0), DisenchanterEntity::new),
                "disenchanter",
                IconMaker.ENERGY,
                DisenchanterEntity::new,
                DisenchanterHandler::new
        );

        PRESSURIZER = new MachineType<>(
                new MachineBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f).luminance(b -> b.get(MachineBlock.ACTIVATED) ? 11 : 0), PressurizerEntity::new),
                "pressurizer",
                IconMaker.ENERGY,
                PressurizerEntity::new,
                PressurizerHandler::new
        );

        CENTRIFUGE = new MachineType<>(
                new MachineBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f).luminance(b -> b.get(MachineBlock.ACTIVATED) ? 11 : 0), CentrifugeEntity::new),
                "centrifuge",
                IconMaker.ENERGY,
                CentrifugeEntity::new,
                CentrifugeHandler::new
        );
        HYDROPONATOR = new MachineType<>(
                new MachineBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f).luminance(b -> b.get(MachineBlock.ACTIVATED) ? 11 : 0), HydroponatorEntity::new),
                "hydroponator",
                IconMaker.ENERGY,
                HydroponatorEntity::new,
                HydroponatorHandler::new
        );

        CRAFTER = new MachineType<>(
                new MachineBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f).luminance(b -> b.get(MachineBlock.ACTIVATED) ? 11 : 0), CrafterEntity::new),
                "crafter",
                IconMaker.ENERGY,
                CrafterEntity::new,
                CrafterHandler::new
        );

        ITEM_COLLECTOR = new MachineType<>(
                new MachineBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f), ItemCollectorEntity::new) {
                    @Override
                    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
                        return Block.createCuboidShape(3, 0, 3, 13, 14, 13);
                    }
                },
                "item_collector",
                IconMaker.ENERGY,
                ItemCollectorEntity::new,
                ItemCollectorHandler::new
        );

        QUARRY = new MachineType<>(
                new MachineBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f), QuarryEntity::new),
                "quarry",
                IconMaker.ENERGY,
                QuarryEntity::new,
                QuarryHandler::new
        );

        XP_BANK = new MachineType<>(
                new MachineBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f), BankEntity::new),
                "xp_bank",
                IconMaker.ENERGY,
                BankEntity::new,
                BankHandler::new
        );

        TRANSMUTER = new MachineType<>(
                new MachineBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f), TransmuterEntity::new),
                "transmuter",
                IconMaker.ENERGY,
                TransmuterEntity::new,
                TransmuterHandler::new
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
