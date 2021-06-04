package net.flytre.mechanix.util;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.flytre.mechanix.api.energy.compat.EnergyEntity;
import net.flytre.mechanix.api.energy.TREnergyStorageImpl;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import team.reborn.energy.Energy;


//TODO: INCOMPLETE PORT OF CLASS
public class MiscRegistry {

    public static final ItemGroup TAB = FabricItemGroupBuilder.build(
            new Identifier("mechanix", "all"),
            () -> new ItemStack(MachineRegistry.CABLES.getStandard()));

    public static void init() {
        Energy.registerHolder(i -> i instanceof EnergyEntity, t -> new TREnergyStorageImpl((EnergyEntity) t));
    }
}
