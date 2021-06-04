package net.flytre.mechanix.block.centrifuge;

import net.flytre.flytre_lib.common.inventory.OutputSlot;
import net.flytre.mechanix.api.energy.ItemEnergyScreenHandler;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;

public class CentrifugeHandler extends ItemEnergyScreenHandler<CentrifugeEntity> {


    public CentrifugeHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        super(MachineRegistry.CENTRIFUGE.getHandlerType(), syncId, playerInventory, CentrifugeEntity::new, buf);
    }

    public CentrifugeHandler(int syncId, PlayerInventory playerInventory, CentrifugeEntity entity, PropertyDelegate propertyDelegate) {
        super(MachineRegistry.CENTRIFUGE.getHandlerType(), syncId, playerInventory, entity, propertyDelegate);
    }

    @Override
    protected void constCommon(PlayerInventory playerInventory, CentrifugeEntity entity) {
        this.addSlot(new Slot(entity, 0, 66, 35));
        this.addSlot(new OutputSlot(entity, 1, 129, 26));
        this.addSlot(new OutputSlot(entity, 2, 147, 26));
        this.addSlot(new OutputSlot(entity, 3, 129, 44));
        this.addSlot(new OutputSlot(entity, 4, 147, 44));
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
            if(index < 5) {
                if (!this.insertItem(stack, 5, 40, false))
                    return ItemStack.EMPTY;
            } else {
                if(!this.insertItem(stack,0,1,false))
                    return ItemStack.EMPTY;
            }
        }
        return stack;
    }


}
