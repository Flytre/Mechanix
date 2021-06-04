package net.flytre.mechanix.block.hydroponator;

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

public class HydroponatorHandler extends ItemEnergyScreenHandler<HydroponatorEntity> {


    public HydroponatorHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        super(MachineRegistry.HYDROPONATOR.getHandlerType(), syncId, playerInventory, HydroponatorEntity::new, buf);
    }

    public HydroponatorHandler(int syncId, PlayerInventory playerInventory, HydroponatorEntity entity, PropertyDelegate propertyDelegate) {
        super(MachineRegistry.HYDROPONATOR.getHandlerType(), syncId, playerInventory, entity, propertyDelegate);
    }


    @Override
    protected void constCommon(PlayerInventory playerInventory, HydroponatorEntity entity) {
        this.addSlot(new Slot(entity, 0, 79, 23));
        this.addSlot(new Slot(entity, 1, 79, 46));
        this.addSlot(new OutputSlot(entity, 2, 135, 23));
        this.addSlot(new OutputSlot(entity, 3, 135, 46));
        this.addSlot(new FluidSlot(entity, 0, 42, 13, false));
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
