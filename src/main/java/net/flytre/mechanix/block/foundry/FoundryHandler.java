package net.flytre.mechanix.block.foundry;

import net.flytre.flytre_lib.common.inventory.OutputSlot;
import net.flytre.mechanix.api.energy.ItemEnergyScreenHandler;
import net.flytre.mechanix.api.fluid.screen.FluidSlot;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;

public class FoundryHandler extends ItemEnergyScreenHandler<FoundryEntity> {

    public FoundryHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        super(MachineRegistry.FOUNDRY.getHandlerType(), syncId, playerInventory, FoundryEntity::new, buf);
    }

    public FoundryHandler(int syncId, PlayerInventory playerInventory, FoundryEntity entity, PropertyDelegate propertyDelegate) {
        super(MachineRegistry.FOUNDRY.getHandlerType(), syncId, playerInventory, entity, propertyDelegate);
    }

    @Override
    protected void constCommon(PlayerInventory playerInventory, FoundryEntity entity) {
        this.addSlot(new OutputSlot(entity, 0, 135, 35));
        this.addSlot(new FluidSlot(entity, 0, 56, 13, false));
        super.constCommon(playerInventory, entity);
    }

    public double operationProgress() {
        return getPropertyDelegate().get(5) == 0 ? 0 : getPropertyDelegate().get(4) / (double) getPropertyDelegate().get(5);
    }


    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (index == 0) {
                if (!this.insertItem(itemStack2, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onStackChanged(itemStack2, itemStack);

            } else if (index < 28) {
                if (!this.insertItem(itemStack2, 29, 37, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < 37 && !this.insertItem(itemStack2, 1, 28, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, itemStack2);
        }

        return itemStack;
    }



}
