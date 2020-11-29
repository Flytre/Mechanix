package net.flytre.mechanix.util;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.flytre.mechanix.item.WrenchItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemRegistery {

    public static final Item SERVO = new Item(new FabricItemSettings().group(MiscRegistry.TAB));
    public static final Item WRENCH = new WrenchItem(new FabricItemSettings().group(MiscRegistry.TAB).maxCount(1));
    public static final Item COMPRESSED_DIAMOND = new Item(new FabricItemSettings().group(MiscRegistry.TAB));
    public static final Item ENDALUM_INGOT = new Item(new FabricItemSettings().group(MiscRegistry.TAB));
    public static final Item NEPTUNIUM_INGOT = new Item(new FabricItemSettings().group(MiscRegistry.TAB));
    public static final Item PERLIUM_INGOT = new Item(new FabricItemSettings().group(MiscRegistry.TAB));
    public static final Item REINFORCED_IRON_INGOT = new Item(new FabricItemSettings().group(MiscRegistry.TAB));
    public static final Item VYSTERIUM_INGOT = new Item(new FabricItemSettings().group(MiscRegistry.TAB));


    public static void init() {
        Registry.register(Registry.ITEM, new Identifier("mechanix", "servo"), SERVO);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "wrench"), WRENCH);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "compressed_diamond"), COMPRESSED_DIAMOND);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "endalum_ingot"), ENDALUM_INGOT);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "neptunium_ingot"), NEPTUNIUM_INGOT);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "perlium_ingot"), PERLIUM_INGOT);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "reinforced_iron_ingot"), REINFORCED_IRON_INGOT);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "vysterium_ingot"), VYSTERIUM_INGOT);

    }
}
