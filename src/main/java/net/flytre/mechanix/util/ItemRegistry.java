package net.flytre.mechanix.util;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.flytre.flytre_lib.common.compat.wrench.WrenchItem;
import net.flytre.mechanix.api.upgrade.UpgradeInventory;
import net.flytre.mechanix.api.upgrade.UpgradeItem;
import net.flytre.mechanix.block.cable.Cable;
import net.flytre.mechanix.block.cable.CableSide;
import net.flytre.mechanix.block.fluid_pipe.FluidPipe;
import net.flytre.mechanix.block.fluid_pipe.FluidPipeEntity;
import net.flytre.mechanix.item.ServoItem;
import net.flytre.mechanix.item.TierUpgradeItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.function.Supplier;


public class ItemRegistry {
    public static final Supplier<Item> ITEM_MAKER = () -> new Item(new FabricItemSettings().group(MiscRegistry.TAB));

    public static final Item BLUESTONE = ITEM_MAKER.get();
    public static final Item BLUESTONE_CIRCUIT = ITEM_MAKER.get();
    public static final Item CIRCUIT = ITEM_MAKER.get();
    public static final Item COKE = ITEM_MAKER.get();
    public static final Item COPPER_COIL = ITEM_MAKER.get();
    public static final Item COPPER_DUST = ITEM_MAKER.get();
    public static final Item COPPER_GEAR = ITEM_MAKER.get();
    public static final Item COPPER_NUGGET = ITEM_MAKER.get();
    public static final Item COPPER_PLATE = ITEM_MAKER.get();
    public static final Item DIAMOND_DUST = ITEM_MAKER.get();
    public static final Item DIAMOND_GEAR = ITEM_MAKER.get();
    public static final Item DIAMOND_PLATE = ITEM_MAKER.get();
    public static final Item ENDALUM_DUST = ITEM_MAKER.get();
    public static final Item ENDALUM_INGOT = ITEM_MAKER.get();
    public static final Item ENDALUM_NUGGET = ITEM_MAKER.get();
    public static final Item GILDED_UPGRADE = new TierUpgradeItem(new FabricItemSettings().group(MiscRegistry.TAB));
    public static final Item GOLD_COIL = ITEM_MAKER.get();
    public static final Item GOLD_DUST = ITEM_MAKER.get();
    public static final Item GOLD_GEAR = ITEM_MAKER.get();
    public static final Item GOLD_PLATE = ITEM_MAKER.get();
    public static final Item INFERNUM_POWDER = ITEM_MAKER.get();
    public static final Item IRON_DUST = ITEM_MAKER.get();
    public static final Item IRON_GEAR = ITEM_MAKER.get();
    public static final Item IRON_PLATE = ITEM_MAKER.get();
    public static final Item MACHINE_FRAME = ITEM_MAKER.get();
    public static final Item MASTERWORK_CIRCUIT = ITEM_MAKER.get();
    public static final Item NEPTUNIUM_DUST = ITEM_MAKER.get();
    public static final Item NEPTUNIUM_INGOT = ITEM_MAKER.get();
    public static final Item NEPTUNIUM_NUGGET = ITEM_MAKER.get();
    public static final Item NEPTUNIUM_UPGRADE = new TierUpgradeItem(new FabricItemSettings().group(MiscRegistry.TAB));
    public static final Item OBSIDIAN_DUST = ITEM_MAKER.get();
    public static final Item PANEL = ITEM_MAKER.get();
    public static final Item PERLIUM_DUST = ITEM_MAKER.get();
    public static final Item PERLIUM_INGOT = ITEM_MAKER.get();
    public static final Item PERLIUM_NUGGET = ITEM_MAKER.get();
    public static final Item SAWDUST = ITEM_MAKER.get();
    public static final Item SERVO = new ServoItem(new FabricItemSettings().group(MiscRegistry.TAB));
    public static final Item STEEL_DUST = ITEM_MAKER.get();
    public static final Item STEEL_GEAR = ITEM_MAKER.get();
    public static final Item STEEL_INGOT = ITEM_MAKER.get();
    public static final Item STEEL_NUGGET = ITEM_MAKER.get();
    public static final Item STEEL_PLATE = ITEM_MAKER.get();
    public static final Item VYSTERIUM_DUST = ITEM_MAKER.get();
    public static final Item VYSTERIUM_INGOT = ITEM_MAKER.get();
    public static final Item VYSTERIUM_NUGGET = ITEM_MAKER.get();
    public static final Item VYSTERIUM_UPGRADE = new TierUpgradeItem(new FabricItemSettings().group(MiscRegistry.TAB));
    public static final Item WITHER_ASH = ITEM_MAKER.get();
    public static final Item WITHER_BONE = ITEM_MAKER.get();
    public static final Item WRENCH = new WrenchItem(new FabricItemSettings().group(MiscRegistry.TAB).maxCount(1));

    public static final Item WATER_FABRICATOR = new UpgradeItem(1) {
        @Override
        public boolean isValid(UpgradeInventory inv, ItemStack stack, int slot) {
            return true;
        }
    };
    public static final Item OVERCLOCKER = new UpgradeItem(16) {

        @Override
        public boolean isValid(UpgradeInventory inv, ItemStack stack, int slot) {
            return inv.upgradeQuantity(OVERCLOCKER) + stack.getCount() <= 5;
        }
    };

    public static final Item ENERGY_SAVER = new UpgradeItem(16) {

        @Override
        public boolean isValid(UpgradeInventory inv, ItemStack stack, int slot) {
            return inv.upgradeQuantity(ENERGY_SAVER) + stack.getCount() <= 5;
        }
    };

    public static final Item ADVANCED_SUCTION = new UpgradeItem(16) {

        @Override
        public boolean isValid(UpgradeInventory inv, ItemStack stack, int slot) {
            return !inv.hasUpgrade(ADVANCED_SUCTION) && stack.getCount() == 1;
        }
    };

    public static final Item COILER = new UpgradeItem(1) {

        @Override
        public boolean isValid(UpgradeInventory inv, ItemStack stack, int slot) {
            return !inv.hasUpgrade(COILER) && stack.getCount() == 1;
        }
    };


    public static void init() {

        WrenchItem.NO_SHIFT_TICK.add((World world, BlockHitResult hitResult, Block block, PlayerEntity player, BlockState state, BlockEntity blockEntity) -> {
            Boolean wrenched = null;
            if (block instanceof Cable)
                wrenched = state.get(Cable.getProperty(hitResult.getSide())) == CableSide.WRENCHED;
            if (block instanceof FluidPipe && blockEntity instanceof FluidPipeEntity)
                wrenched = ((FluidPipeEntity) blockEntity).wrenched.get(hitResult.getSide());
            if (wrenched != null)
                player.sendMessage(new TranslatableText("item.mechanix.wrench.1").append(" (" + hitResult.getSide().name() + "): " + wrenched), true);

        });

        Registry.register(Registry.ITEM, new Identifier("mechanix", "advanced_suction"), ADVANCED_SUCTION);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "bluestone"), BLUESTONE);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "bluestone_circuit"), BLUESTONE_CIRCUIT);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "circuit"), CIRCUIT);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "coiler"), COILER);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "coke"), COKE);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "copper_coil"), COPPER_COIL);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "copper_dust"), COPPER_DUST);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "copper_gear"), COPPER_GEAR);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "copper_nugget"), COPPER_NUGGET);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "copper_plate"), COPPER_PLATE);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "diamond_dust"), DIAMOND_DUST);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "diamond_gear"), DIAMOND_GEAR);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "diamond_plate"), DIAMOND_PLATE);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "endalum_dust"), ENDALUM_DUST);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "endalum_ingot"), ENDALUM_INGOT);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "endalum_nugget"), ENDALUM_NUGGET);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "energy_saver"), ENERGY_SAVER);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "gilded_upgrade"), GILDED_UPGRADE);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "gold_coil"), GOLD_COIL);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "gold_dust"), GOLD_DUST);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "gold_gear"), GOLD_GEAR);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "gold_plate"), GOLD_PLATE);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "infernum_powder"), INFERNUM_POWDER);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "iron_dust"), IRON_DUST);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "iron_gear"), IRON_GEAR);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "iron_plate"), IRON_PLATE);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "machine"), MACHINE_FRAME);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "masterwork_circuit"), MASTERWORK_CIRCUIT);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "neptunium_dust"), NEPTUNIUM_DUST);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "neptunium_ingot"), NEPTUNIUM_INGOT);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "neptunium_nugget"), NEPTUNIUM_NUGGET);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "neptunium_upgrade"), NEPTUNIUM_UPGRADE);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "obsidian_dust"), OBSIDIAN_DUST);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "overclocker"), OVERCLOCKER);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "panel"), PANEL);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "perlium_dust"), PERLIUM_DUST);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "perlium_ingot"), PERLIUM_INGOT);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "perlium_nugget"), PERLIUM_NUGGET);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "sawdust"), SAWDUST);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "servo"), SERVO);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "steel_dust"), STEEL_DUST);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "steel_gear"), STEEL_GEAR);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "steel_ingot"), STEEL_INGOT);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "steel_nugget"), STEEL_NUGGET);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "steel_plate"), STEEL_PLATE);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "vysterium_dust"), VYSTERIUM_DUST);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "vysterium_ingot"), VYSTERIUM_INGOT);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "vysterium_nugget"), VYSTERIUM_NUGGET);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "vysterium_upgrade"), VYSTERIUM_UPGRADE);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "water_fabricator"), WATER_FABRICATOR);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "wither_ash"), WITHER_ASH);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "wither_bone"), WITHER_BONE);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "wrench"), WRENCH);
    }
}
