package net.flytre.mechanix.util;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.flytre.mechanix.api.energy.EnergyEntity;
import net.flytre.mechanix.api.energy.TREnergyStorageImpl;
import net.flytre.mechanix.recipe.MechanixReloader;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import team.reborn.energy.Energy;

public class MiscRegistry {

    public static final ItemGroup TAB = FabricItemGroupBuilder.build(
            new Identifier("mechanix", "all"),
            () -> new ItemStack(MachineRegistry.CABLES.getStandard()));

    public static void init() {
        Energy.registerHolder(i -> i instanceof EnergyEntity, t -> new TREnergyStorageImpl((EnergyEntity) t));
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(MechanixReloader.INSTANCE);
    }
}
