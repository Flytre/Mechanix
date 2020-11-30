package net.flytre.mechanix.base;

import net.flytre.mechanix.base.fluid.FluidInventory;
import net.minecraft.entity.player.PlayerEntity;

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
