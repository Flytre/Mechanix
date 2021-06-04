package net.flytre.mechanix.api.inventory;

import net.flytre.flytre_lib.common.inventory.EasyInventory;
import net.flytre.mechanix.api.fluid.FluidInventory;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Use this when your machine needs to store both fluids AND items as it takes care of a few errors
 * and reduces the wordiness of your class file. Not strictly necessary but very helpful!
 */
public interface DoubleInventory extends EasyInventory, FluidInventory {
    @Override
    default boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    default void onOpen(PlayerEntity player) {
        EasyInventory.super.onOpen(player);
        FluidInventory.super.onOpen(player);
    }

    @Override
    default void onClose(PlayerEntity player) {
        EasyInventory.super.onClose(player);
        FluidInventory.super.onClose(player);
    }

    @Override
    default void clear() {
        EasyInventory.super.clear();
        FluidInventory.super.clear();
    }

}
