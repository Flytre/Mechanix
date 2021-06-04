package net.flytre.mechanix.block.disenchanter;

import net.flytre.flytre_lib.common.inventory.OutputSlot;
import net.flytre.mechanix.api.energy.ItemEnergyScreenHandler;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;

public class DisenchanterHandler extends ItemEnergyScreenHandler<DisenchanterEntity> {


    public DisenchanterHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        super(MachineRegistry.DISENCHANTER.getHandlerType(), syncId, playerInventory, DisenchanterEntity::new, buf);
    }

    public DisenchanterHandler(int syncId, PlayerInventory playerInventory, DisenchanterEntity entity, PropertyDelegate propertyDelegate) {
        super(MachineRegistry.DISENCHANTER.getHandlerType(), syncId, playerInventory, entity, propertyDelegate);
    }

    @Override
    protected void constCommon(PlayerInventory playerInventory, DisenchanterEntity entity) {
        this.addSlot(new Slot(entity, 0, 66, 23));
        this.addSlot(new Slot(entity, 1, 66, 46));
        this.addSlot(new OutputSlot(entity, 2, 135, 23));
        this.addSlot(new OutputSlot(entity, 3, 135, 46));
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
                if (!this.insertItem(stack, 0, 2, false))
                    return ItemStack.EMPTY;
            }
        }
        return stack;
    }


}
