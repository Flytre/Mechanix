package net.flytre.mechanix.api.upgrade;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class UpgradeSlot extends Slot {
    public final UpgradeInventory upgradeInventory;
    private final int index;

    public UpgradeSlot(UpgradeInventory upgradeInventory, int index, int x, int y) {
        super(new SimpleInventory(9), index, x, y);
        this.upgradeInventory = upgradeInventory;
        this.index = index;
    }

    public boolean canInsert(ItemStack stack) {
        return canInsert(stack, stack.getCount());
    }

    public boolean canInsert(ItemStack stack, int qty) {
        boolean bl = upgradeInventory.isValidUpgrade(stack);
        boolean bl2 = stack.getItem() instanceof UpgradeItem;
        ItemStack copy = stack.copy();
        copy.setCount(qty);
        if (bl && bl2) {
            return ((UpgradeItem) stack.getItem()).isValid(upgradeInventory, copy, index);
        }
        return false;
    }


    @Override
    public ItemStack getStack() {
        return this.upgradeInventory.getUpgrade(this.index);
    }

    @Override
    public void setStack(ItemStack stack) {
        this.upgradeInventory.setUpgrade(this.index, stack);
        this.markDirty();
    }

    @Override
    public void markDirty() {
        this.upgradeInventory.markUpgradesDirty();
    }

    @Override
    public int getMaxItemCount() {
        return this.upgradeInventory.getMaxUpgradeSlotCount();
    }

    @Override
    public int getMaxItemCount(ItemStack stack) {
        return this.getMaxItemCount();
    }

    @Override
    public ItemStack takeStack(int amount) {
        return this.upgradeInventory.removeUpgrade(this.index, amount);
    }

}
