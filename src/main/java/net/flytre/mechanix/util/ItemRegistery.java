package net.flytre.mechanix.util;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.flytre.mechanix.item.ServoItem;
import net.flytre.mechanix.item.UpgradeItem;
import net.flytre.mechanix.item.WrenchItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

public class ItemRegistery {

    public static final Supplier<Item> ITEM_MAKER = () -> new Item(new FabricItemSettings().group(MiscRegistry.TAB));
    public static final Item SERVO = new ServoItem(new FabricItemSettings().group(MiscRegistry.TAB));
    public static final Item WRENCH = new WrenchItem(new FabricItemSettings().group(MiscRegistry.TAB).maxCount(1));
    public static final Item COMPRESSED_DIAMOND = ITEM_MAKER.get();
    public static final Item ENDALUM_INGOT = ITEM_MAKER.get();
    public static final Item NEPTUNIUM_INGOT = ITEM_MAKER.get();
    public static final Item PERLIUM_INGOT = ITEM_MAKER.get();
    public static final Item IRON_PLATING = ITEM_MAKER.get();
    public static final Item VYSTERIUM_INGOT = ITEM_MAKER.get();
    public static final Item PERLIUM_DUST = ITEM_MAKER.get();
    public static final Item MACHINE_FRAME = ITEM_MAKER.get();
    public static final Item GILDED_UPGRADE = new UpgradeItem(new FabricItemSettings().group(MiscRegistry.TAB));
    public static final Item VYSTERIUM_UPGRADE = new UpgradeItem(new FabricItemSettings().group(MiscRegistry.TAB));
    public static final Item NEPTUNIUM_UPGRADE = new UpgradeItem(new FabricItemSettings().group(MiscRegistry.TAB));
    public static final Item IRON_DUST = ITEM_MAKER.get();
    public static final Item GOLD_DUST = ITEM_MAKER.get();
    public static final Item ENDALUM_DUST = ITEM_MAKER.get();
    public static final Item NEPTUNIUM_DUST = ITEM_MAKER.get();
    public static final Item VYSTERIUM_DUST = ITEM_MAKER.get();
    public static final Item PANEL = ITEM_MAKER.get();
    public static final Item SAWDUST = ITEM_MAKER.get();

    public static void init() {
        Registry.register(Registry.ITEM, new Identifier("mechanix", "servo"), SERVO);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "wrench"), WRENCH);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "compressed_diamond"), COMPRESSED_DIAMOND);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "endalum_ingot"), ENDALUM_INGOT);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "neptunium_ingot"), NEPTUNIUM_INGOT);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "perlium_ingot"), PERLIUM_INGOT);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "iron_plating"), IRON_PLATING);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "vysterium_ingot"), VYSTERIUM_INGOT);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "perlium_dust"), PERLIUM_DUST);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "machine"), MACHINE_FRAME);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "gilded_upgrade"), GILDED_UPGRADE);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "vysterium_upgrade"), VYSTERIUM_UPGRADE);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "neptunium_upgrade"), NEPTUNIUM_UPGRADE);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "iron_dust"), IRON_DUST);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "gold_dust"), GOLD_DUST);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "endalum_dust"), ENDALUM_DUST);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "neptunium_dust"), NEPTUNIUM_DUST);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "vysterium_dust"), VYSTERIUM_DUST);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "panel"), PANEL);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "sawdust"), SAWDUST);
    }
}
