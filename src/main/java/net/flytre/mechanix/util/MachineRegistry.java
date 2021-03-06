package net.flytre.mechanix.util;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.flytre.mechanix.api.energy.EnergyItem;
import net.flytre.mechanix.api.fluid.FluidInventory;
import net.flytre.mechanix.api.machine.MachineBlock;
import net.flytre.mechanix.api.machine.MachineList;
import net.flytre.mechanix.api.machine.MachineType;
import net.flytre.mechanix.block.alloyer.AlloyerBlock;
import net.flytre.mechanix.block.alloyer.AlloyerBlockEntity;
import net.flytre.mechanix.block.alloyer.AlloyerScreenHandler;
import net.flytre.mechanix.block.cable.Cable;
import net.flytre.mechanix.block.cell.EnergyCell;
import net.flytre.mechanix.block.cell.EnergyCellEntity;
import net.flytre.mechanix.block.cell.EnergyCellScreenHandler;
import net.flytre.mechanix.block.centrifuge.CentrifugeBlock;
import net.flytre.mechanix.block.centrifuge.CentrifugeBlockEntity;
import net.flytre.mechanix.block.centrifuge.CentrifugeScreenHandler;
import net.flytre.mechanix.block.crafter.CrafterBlock;
import net.flytre.mechanix.block.crafter.CrafterBlockEntity;
import net.flytre.mechanix.block.crafter.CrafterScreenHandler;
import net.flytre.mechanix.block.crusher.CrusherBlock;
import net.flytre.mechanix.block.crusher.CrusherBlockEntity;
import net.flytre.mechanix.block.crusher.CrusherScreenHandler;
import net.flytre.mechanix.block.disenchanter.DisenchanterBlock;
import net.flytre.mechanix.block.disenchanter.DisenchanterEntity;
import net.flytre.mechanix.block.disenchanter.DisenchanterScreenHandler;
import net.flytre.mechanix.block.distiller.DistillerBlock;
import net.flytre.mechanix.block.distiller.DistillerEntity;
import net.flytre.mechanix.block.distiller.DistillerScreenHandler;
import net.flytre.mechanix.block.enchanter.EnchanterBlock;
import net.flytre.mechanix.block.enchanter.EnchanterEntity;
import net.flytre.mechanix.block.enchanter.EnchanterScreenHandler;
import net.flytre.mechanix.block.fluid_pipe.FluidPipe;
import net.flytre.mechanix.block.fluid_pipe.FluidPipeBlockEntity;
import net.flytre.mechanix.block.foundry.FoundryBlock;
import net.flytre.mechanix.block.foundry.FoundryBlockEntity;
import net.flytre.mechanix.block.foundry.FoundryScreenHandler;
import net.flytre.mechanix.block.furnace.PoweredFurnaceBlock;
import net.flytre.mechanix.block.furnace.PoweredFurnaceBlockEntity;
import net.flytre.mechanix.block.furnace.PoweredFurnaceScreenHandler;
import net.flytre.mechanix.block.generator.GeneratorBlock;
import net.flytre.mechanix.block.generator.GeneratorBlockEntity;
import net.flytre.mechanix.block.generator.GeneratorScreenHandler;
import net.flytre.mechanix.block.hydrator.HydratorBlock;
import net.flytre.mechanix.block.hydrator.HydratorBlockEntity;
import net.flytre.mechanix.block.hydrator.HydratorScreenHandler;
import net.flytre.mechanix.block.hydroponator.HydroponatorBlock;
import net.flytre.mechanix.block.hydroponator.HydroponatorEntity;
import net.flytre.mechanix.block.hydroponator.HydroponatorScreenHandler;
import net.flytre.mechanix.block.item_collector.ItemCollectorBlock;
import net.flytre.mechanix.block.item_collector.ItemCollectorEntity;
import net.flytre.mechanix.block.item_collector.ItemCollectorScreenHandler;
import net.flytre.mechanix.block.item_pipe.ItemPipe;
import net.flytre.mechanix.block.item_pipe.ItemPipeBlockEntity;
import net.flytre.mechanix.block.item_pipe.ItemPipeScreenHandler;
import net.flytre.mechanix.block.liquifier.LiquifierBlock;
import net.flytre.mechanix.block.liquifier.LiquifierBlockEntity;
import net.flytre.mechanix.block.liquifier.LiquifierScreenHandler;
import net.flytre.mechanix.block.pressurizer.PressurizerBlock;
import net.flytre.mechanix.block.pressurizer.PressurizerBlockEntity;
import net.flytre.mechanix.block.pressurizer.PressurizerScreenHandler;
import net.flytre.mechanix.block.quarry.QuarryBlock;
import net.flytre.mechanix.block.quarry.QuarryEntity;
import net.flytre.mechanix.block.quarry.QuarryScreenHandler;
import net.flytre.mechanix.block.sawmill.SawmillBlock;
import net.flytre.mechanix.block.sawmill.SawmillEntity;
import net.flytre.mechanix.block.sawmill.SawmillScreenHandler;
import net.flytre.mechanix.block.solar_panel.SolarPanelBlock;
import net.flytre.mechanix.block.solar_panel.SolarPanelEntity;
import net.flytre.mechanix.block.solar_panel.SolarPanelScreenHandler;
import net.flytre.mechanix.block.tank.FluidTank;
import net.flytre.mechanix.block.tank.FluidTankBlockEntity;
import net.flytre.mechanix.block.tank.FluidTankScreenHandler;
import net.flytre.mechanix.block.thermal_generator.ThermalGenBlock;
import net.flytre.mechanix.block.thermal_generator.ThermalGenEntity;
import net.flytre.mechanix.block.thermal_generator.ThermalGenScreenHandler;
import net.flytre.mechanix.block.xp_bank.XpBankBlock;
import net.flytre.mechanix.block.xp_bank.XpBankBlockEntity;
import net.flytre.mechanix.block.xp_bank.XpBankScreenHandler;
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

    public static final Block ITEM_PIPE = new ItemPipe(FabricBlockSettings.of(Material.METAL).hardness(0.9f));
    public static MachineList<Cable> CABLES;
    public static BlockEntityType<ItemPipeBlockEntity> ITEM_PIPE_ENTITY;
    public static ScreenHandlerType<ItemPipeScreenHandler> ITEM_PIPE_SCREEN_HANDLER;

    public static MachineList<FluidPipe> FLUID_PIPES;
    public static BlockEntityType<FluidPipeBlockEntity> FLUID_PIPE_ENTITY;

    public static MachineList<FluidTank> FLUID_TANKS;
    public static BlockEntityType<FluidTankBlockEntity> FLUID_TANK_ENTITY;
    public static ScreenHandlerType<FluidTankScreenHandler> FLUID_TANK_SCREEN_HANDLER;


    public static MachineList<EnergyCell> ENERGY_CELLS;
    public static BlockEntityType<EnergyCellEntity> ENERGY_CELL_ENTITY;
    public static ScreenHandlerType<EnergyCellScreenHandler> ENERGY_CELL_SCREEN_HANDLER;

    public static MachineType<GeneratorBlock, GeneratorBlockEntity, GeneratorScreenHandler> GENERATOR;
    public static MachineType<PoweredFurnaceBlock, PoweredFurnaceBlockEntity, PoweredFurnaceScreenHandler> POWERED_FURNACE;
    public static MachineType<HydratorBlock, HydratorBlockEntity, HydratorScreenHandler> HYDRATOR;
    public static MachineType<FoundryBlock, FoundryBlockEntity, FoundryScreenHandler> FOUNDRY;
    public static MachineType<AlloyerBlock, AlloyerBlockEntity, AlloyerScreenHandler> ALLOYER;
    public static MachineType<LiquifierBlock, LiquifierBlockEntity, LiquifierScreenHandler> LIQUIFIER;
    public static MachineType<PressurizerBlock, PressurizerBlockEntity, PressurizerScreenHandler> PRESSURIZER;
    public static MachineType<CrusherBlock, CrusherBlockEntity, CrusherScreenHandler> CRUSHER;
    public static MachineType<MachineBlock, ThermalGenEntity, ThermalGenScreenHandler> THERMAL_GENERATOR;

    public static MachineList<SolarPanelBlock> SOLAR_PANELS;
    public static BlockEntityType<SolarPanelEntity> SOLAR_PANEL_ENTITY;
    public static ScreenHandlerType<SolarPanelScreenHandler> SOLAR_PANEL_HANDLER;

    public static MachineType<DistillerBlock, DistillerEntity, DistillerScreenHandler> DISTILLER;
    public static MachineType<SawmillBlock, SawmillEntity, SawmillScreenHandler> SAWMILL;
    public static MachineType<CentrifugeBlock, CentrifugeBlockEntity, CentrifugeScreenHandler> CENTRIFUGE;
    public static MachineType<CrafterBlock, CrafterBlockEntity, CrafterScreenHandler> CRAFTER;
    public static MachineType<HydroponatorBlock, HydroponatorEntity, HydroponatorScreenHandler> HYDROPONATOR;
    public static MachineType<QuarryBlock, QuarryEntity, QuarryScreenHandler> QUARRY;
    public static MachineType<XpBankBlock, XpBankBlockEntity, XpBankScreenHandler> XP_BANK;
    public static MachineType<EnchanterBlock, EnchanterEntity, EnchanterScreenHandler> ENCHANTER;
    public static MachineType<DisenchanterBlock, DisenchanterEntity, DisenchanterScreenHandler> DISENCHANTER;
    public static MachineType<ItemCollectorBlock, ItemCollectorEntity, ItemCollectorScreenHandler> ITEM_COLLECTOR;

    public static void init() {

        CABLES = registerTier(
                () -> new Cable(FabricBlockSettings.of(Material.METAL).hardness(0.9f)),
                "cable",
                (cable) -> new BlockItem(cable, new Item.Settings().group(MiscRegistry.TAB))
        );

        registerBlock(ITEM_PIPE, "item_pipe", IconMaker.STANDARD);
        ITEM_PIPE_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "mechanix:item_pipe", BlockEntityType.Builder.create(ItemPipeBlockEntity::new, ITEM_PIPE).build(null));
        ITEM_PIPE_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(new Identifier("mechanix:item_pipe"), ItemPipeScreenHandler::new);


        FLUID_TANKS = registerTier(
                () -> new FluidTank(FabricBlockSettings.of(Material.METAL).nonOpaque().hardness(4.5f).luminance((state) -> state.get(FluidTank.LIGHT_LEVEL))),
                "tank",
                (tank) -> new BlockItem(tank, new Item.Settings().group(MiscRegistry.TAB)) {
                    @Override
                    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
                        FluidInventory.toToolTip(stack, tooltip);
                        super.appendTooltip(stack, world, tooltip, context);
                    }
                });
        FLUID_TANK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "mechanix:tank", BlockEntityType.Builder.create(FluidTankBlockEntity::new, FLUID_TANKS.toArray(new FluidTank[4])).build(null));
        FLUID_TANK_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(new Identifier("mechanix:tank"), FluidTankScreenHandler::new);

        FLUID_PIPES = registerTier(
                () -> new FluidPipe(FabricBlockSettings.of(Material.METAL).nonOpaque().hardness(0.9f)),
                "fluid_pipe",
                (pipe) -> new BlockItem(pipe, new Item.Settings().group(MiscRegistry.TAB))
        );
        FLUID_PIPE_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "mechanix:fluid_pipe", BlockEntityType.Builder.create(FluidPipeBlockEntity::new, FLUID_PIPES.toArray(new FluidPipe[4])).build(null));


        ENERGY_CELLS = registerTier(
                () -> new EnergyCell(FabricBlockSettings.of(Material.METAL).hardness(4.5f)),
                "energy_cell",
                (cell) -> new EnergyItem(cell, new Item.Settings().group(MiscRegistry.TAB))
        );
        ENERGY_CELL_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "mechanix:energy_cell", BlockEntityType.Builder.create(EnergyCellEntity::new, ENERGY_CELLS.toArray(new EnergyCell[4])).build(null));
        ENERGY_CELL_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(new Identifier("mechanix:energy_cell"), EnergyCellScreenHandler::new);

        SOLAR_PANELS = registerTier(() -> new SolarPanelBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f)),
                "solar_panel",
                (panel) -> new BlockItem(panel, new Item.Settings().group(MiscRegistry.TAB)));
        SOLAR_PANEL_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "mechanix:solar_panel", BlockEntityType.Builder.create(SolarPanelEntity::new, SOLAR_PANELS.toArray(new SolarPanelBlock[4])).build(null));
        SOLAR_PANEL_HANDLER = ScreenHandlerRegistry.registerExtended(new Identifier("mechanix:solar_panel"), SolarPanelScreenHandler::new);


        GENERATOR = new MachineType<>(
                new GeneratorBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f)),
                "generator",
                (block) -> new EnergyItem(block, new Item.Settings().group(MiscRegistry.TAB)),
                GeneratorBlockEntity::new,
                GeneratorScreenHandler::new);

        POWERED_FURNACE = new MachineType<>(
                new PoweredFurnaceBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f)),
                "furnace",
                (block) -> new EnergyItem(block, new Item.Settings().group(MiscRegistry.TAB)),
                PoweredFurnaceBlockEntity::new,
                PoweredFurnaceScreenHandler::new);

        HYDRATOR = new MachineType<>(
                new HydratorBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f)),
                "hydrator",
                IconMaker.STANDARD,
                HydratorBlockEntity::new,
                HydratorScreenHandler::new);

        FOUNDRY = new MachineType<>(
                new FoundryBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f)),
                "foundry",
                IconMaker.STANDARD,
                FoundryBlockEntity::new,
                FoundryScreenHandler::new
        );

        LIQUIFIER = new MachineType<>(
                new LiquifierBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f)),
                "liquifier",
                IconMaker.STANDARD,
                LiquifierBlockEntity::new,
                LiquifierScreenHandler::new
        );
        ALLOYER = new MachineType<>(
                new AlloyerBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f)),
                "alloyer",
                IconMaker.STANDARD,
                AlloyerBlockEntity::new,
                AlloyerScreenHandler::new
        );

        PRESSURIZER = new MachineType<>(
                new PressurizerBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f)),
                "pressurizer",
                IconMaker.STANDARD,
                PressurizerBlockEntity::new,
                PressurizerScreenHandler::new
        );

        CRUSHER = new MachineType<>(
                new CrusherBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f)),
                "crusher",
                IconMaker.STANDARD,
                CrusherBlockEntity::new,
                CrusherScreenHandler::new
        );

        THERMAL_GENERATOR = new MachineType<>(
                new ThermalGenBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f)),
                "thermal_generator",
                IconMaker.STANDARD,
                ThermalGenEntity::new,
                ThermalGenScreenHandler::new
        );

        DISTILLER = new MachineType<>(
                new DistillerBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f)),
                "distiller",
                IconMaker.STANDARD,
                DistillerEntity::new,
                DistillerScreenHandler::new
        );

        SAWMILL = new MachineType<>(
                new SawmillBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f)),
                "sawmill",
                IconMaker.STANDARD,
                SawmillEntity::new,
                SawmillScreenHandler::new
        );

        CENTRIFUGE = new MachineType<>(
                new CentrifugeBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f)),
                "centrifuge",
                IconMaker.STANDARD,
                CentrifugeBlockEntity::new,
                CentrifugeScreenHandler::new
        );
        CRAFTER = new MachineType<>(
                new CrafterBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f)),
                "crafter",
                IconMaker.STANDARD,
                CrafterBlockEntity::new,
                CrafterScreenHandler::new
        );
        HYDROPONATOR = new MachineType<>(
                new HydroponatorBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f)),
                "hydroponator",
                IconMaker.STANDARD,
                HydroponatorEntity::new,
                HydroponatorScreenHandler::new
        )
        ;
        QUARRY = new MachineType<>(
                new QuarryBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f)),
                "quarry",
                IconMaker.STANDARD,
                QuarryEntity::new,
                QuarryScreenHandler::new
        );

        XP_BANK = new MachineType<>(
                new XpBankBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f).luminance((state) -> state.get(XpBankBlock.ACTIVATED) ? 12 : 0)),
                "xp_bank",
                IconMaker.STANDARD,
                XpBankBlockEntity::new,
                XpBankScreenHandler::new
        );

        ENCHANTER = new MachineType<>(
                new EnchanterBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f).luminance((state) -> state.get(EnchanterBlock.ACTIVATED) ? 12 : 0)),
                "enchanter",
                IconMaker.STANDARD,
                EnchanterEntity::new,
                EnchanterScreenHandler::new
        );
        DISENCHANTER = new MachineType<>(
                new DisenchanterBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f).luminance((state) -> state.get(DisenchanterBlock.ACTIVATED) ? 12 : 0)),
                "disenchanter",
                IconMaker.STANDARD,
                DisenchanterEntity::new,
                DisenchanterScreenHandler::new
        );

        ITEM_COLLECTOR = new MachineType<>(
                new ItemCollectorBlock(FabricBlockSettings.of(Material.METAL).hardness(4.5f)),
                "item_collector",
                IconMaker.STANDARD,
                ItemCollectorEntity::new,
                ItemCollectorScreenHandler::new
        );
    }

    private static <T extends Block> MachineList<T> registerTier(BlockMaker<T> maker, String id, IconMaker<T> creator) {
        MachineList<T> result = new MachineList<>();

        String[] tiers = new String[]{"", "gilded", "vysterium", "neptunium"};

        for (String tier : tiers) {
            T blk = maker.create();
            result.add(blk);
            registerBlock(blk, tier + (tier.length() > 0 ? "_" : "") + id, creator);
        }
        return result;
    }

    public static <T extends Block> void registerBlock(T block, String id, IconMaker<T> creator) {
        Registry.register(Registry.BLOCK, new Identifier("mechanix", id), block);
        Registry.register(Registry.ITEM, new Identifier("mechanix", id), creator.create(block));
    }
}
