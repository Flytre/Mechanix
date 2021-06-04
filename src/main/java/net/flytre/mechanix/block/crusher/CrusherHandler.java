package net.flytre.mechanix.block.crusher;

import net.flytre.flytre_lib.common.inventory.OutputSlot;
import net.flytre.mechanix.api.energy.ItemEnergyScreenHandler;
import net.flytre.mechanix.block.alloyer.AlloyerEntity;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;

public class CrusherHandler extends ItemEnergyScreenHandler<CrusherEntity> {


    public CrusherHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        super(MachineRegistry.CRUSHER.getHandlerType(), syncId, playerInventory, CrusherEntity::new, buf);
    }

    public CrusherHandler(int syncId, PlayerInventory playerInventory, CrusherEntity entity, PropertyDelegate propertyDelegate) {
        super(MachineRegistry.CRUSHER.getHandlerType(), syncId, playerInventory, entity, propertyDelegate);
    }

    @Override
    protected void constCommon(PlayerInventory playerInventory, CrusherEntity entity) {
        this.addSlot(new Slot(entity, 0, 66, 35));
        this.addSlot(new OutputSlot(entity, 1, 135, 35));
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
            if(index <= 1) {
                if(!this.insertItem(stack,4,38,false))
                    return ItemStack.EMPTY;
            } else {
                if(!this.insertItem(stack,0,1,false))
                    return ItemStack.EMPTY;
            }
        }
        return stack;
    }


}
