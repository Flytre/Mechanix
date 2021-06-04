package net.flytre.mechanix.block.alloyer;

import net.flytre.flytre_lib.common.inventory.OutputSlot;
import net.flytre.mechanix.api.energy.ItemEnergyScreenHandler;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;

public class AlloyerHandler extends ItemEnergyScreenHandler<AlloyerEntity> {


    public AlloyerHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        super(MachineRegistry.ALLOYER.getHandlerType(), syncId, playerInventory, AlloyerEntity::new, buf);
    }

    public AlloyerHandler(int syncId, PlayerInventory playerInventory, AlloyerEntity entity, PropertyDelegate propertyDelegate) {
        super(MachineRegistry.ALLOYER.getHandlerType(), syncId, playerInventory, entity, propertyDelegate);
    }

    @Override
    protected void constCommon(PlayerInventory playerInventory, AlloyerEntity entity) {
        this.addSlot(new Slot(entity, 0, 61, 21));
        this.addSlot(new Slot(entity, 1, 98, 21));
        this.addSlot(new Slot(entity, 2, 136, 21));
        this.addSlot(new OutputSlot(entity, 3, 98, 58));
        super.constCommon(playerInventory, entity);
    }

    public double operationProgress() {
        return getPropertyDelegate().get(5) == 0 ? 0 : getPropertyDelegate().get(4) / (double) getPropertyDelegate().get(5);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            stack = slot.getStack();
            if (index <= 3) {
                if (!this.insertItem(stack, 4, 40, false))
                    return ItemStack.EMPTY;
            } else {
                if (!this.insertItem(stack, 0, 3, false))
                    return ItemStack.EMPTY;
            }
        }
        return stack;
    }
}
