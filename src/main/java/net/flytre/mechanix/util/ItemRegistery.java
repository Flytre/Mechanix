package net.flytre.mechanix.util;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.flytre.mechanix.item.WrenchItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemRegistery {

    public static final Item SERVO = new Item(new FabricItemSettings().group(MiscRegistry.TAB));
    public static final Item WRENCH = new WrenchItem(new FabricItemSettings().group(MiscRegistry.TAB).maxCount(1));


    public static void init() {
        Registry.register(Registry.ITEM, new Identifier("mechanix", "servo"), SERVO);
        Registry.register(Registry.ITEM, new Identifier("mechanix", "wrench"), WRENCH);
    }
}
