package net.flytre.mechanix.util;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.flytre.mechanix.api.energy.compat.EnergyEntity;
import net.flytre.mechanix.api.energy.TREnergyStorageImpl;
import net.flytre.mechanix.recipe.MechanixReloader;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.condition.RandomChanceWithLootingLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import team.reborn.energy.Energy;


public class MiscRegistry {

    public static final Identifier WITHER_SKELETON_TABLE = new Identifier("minecraft", "entities/wither_skeleton");

    public static final ItemGroup TAB = FabricItemGroupBuilder.build(
            new Identifier("mechanix", "all"),
            () -> new ItemStack(MachineRegistry.CABLES.getStandard()));

    public static void init() {
        Energy.registerHolder(i -> i instanceof EnergyEntity, t -> new TREnergyStorageImpl((EnergyEntity) t));
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(MechanixReloader.INSTANCE);

        LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, supplier, setter) -> {
            if (WITHER_SKELETON_TABLE.equals(id)) {
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder()
                        .rolls(ConstantLootTableRange.create(1))
                        .withEntry(ItemEntry.builder(ItemRegistry.WITHER_BONE)
                                .conditionally(
                                        RandomChanceWithLootingLootCondition.builder(0.3f, 0.1f)
                                )
                                .build());

                supplier.withPool(poolBuilder.build());
            }
        });
    }
}
