package net.flytre.mechanix.util;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class MiscRegistry {

    public static final ItemGroup TAB = FabricItemGroupBuilder.build(
            new Identifier("mechanix", "all"),
            () -> new ItemStack(MachineRegistry.CABLE));

    public static void init() {
    }
}
