package net.flytre.mechanix.api.upgrade;

import net.flytre.flytre_lib.common.util.InventoryUtils;
import net.flytre.mechanix.api.fluid.FluidInventory;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.api.machine.Tiered;
import net.flytre.mechanix.util.ItemRegistry;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.collection.DefaultedList;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

public interface UpgradeInventory {

    static CompoundTag toTag(CompoundTag tag, DefaultedList<ItemStack> stacks) {
        return InventoryUtils.toTag(tag, stacks, "Upgrades");
    }

    static CompoundTag fromTag(CompoundTag tag, DefaultedList<ItemStack> stacks) {
        return InventoryUtils.fromTag(tag, stacks, "Upgrades");
    }

    static <T extends FluidInventory & UpgradeInventory> boolean waterFabricatorEffect(T entity, long perUpgrade, int slot) {
        FluidStack stack = new FluidStack(Fluids.WATER, perUpgrade * entity.upgradeQuantity(ItemRegistry.WATER_FABRICATOR));

        if (entity.hasUpgrade(ItemRegistry.WATER_FABRICATOR))
            entity.addInternal(slot, stack);
        return false;
    }

    DefaultedList<ItemStack> getUpgrades();

    default int upgradeSlots() {
        return getUpgrades().size();
    }

    default boolean hasNoUpgrades() {
        for (int i = 0; i < upgradeSlots(); i++) {
            ItemStack stack = getUpgrade(i);
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    default ItemStack getUpgrade(int slot) {
        return getUpgrades().get(slot);
    }

    default ItemStack removeUpgrade(int slot, int amount) {
        ItemStack result = Inventories.splitStack(getUpgrades(), slot, amount);
        if (!result.isEmpty()) {
            markUpgradesDirty();
        }
        return result;
    }

    default ItemStack removeUpgrade(int slot) {
        return Inventories.removeStack(getUpgrades(), slot);
    }

    default void setUpgrade(int slot, ItemStack stack) {
        getUpgrades().set(slot, stack);
        if (stack.getCount() > getMaxUpgradeSlotCount()) {
            stack.setCount(getMaxUpgradeSlotCount());
        }
        markUpgradesDirty();
    }

    default void clearUpgrades() {
        getUpgrades().clear();
    }

    default int[] getAvailableUpgradeSlots() {
        return IntStream.range(0, upgradeSlots()).toArray();
    }

    default int upgradeQuantity(Item item) {
        int i = 0;

        for (int j = 0; j < this.upgradeSlots(); ++j) {
            ItemStack itemStack = this.getUpgrade(j);
            if (itemStack.getItem().equals(item)) {
                i += itemStack.getCount();
            }
        }

        return i;
    }

    default boolean hasUpgrade(Item upgrade) {
        return getUpgrades().stream().anyMatch(i -> i.getItem() == upgrade);
    }

    default int getMaxUpgradeSlotCount() {
        return 64;
    }

    default void markUpgradesDirty() {
    }

    default Set<Item> validUpgrades() {
        return new HashSet<>();
    }

    default boolean isValidUpgrade(ItemStack stack) {
        return validUpgrades().contains(stack.getItem());
    }
}
