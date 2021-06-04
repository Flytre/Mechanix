package net.flytre.mechanix.block.crafter;


import net.flytre.flytre_lib.common.inventory.OutputSlot;
import net.flytre.mechanix.api.energy.ItemEnergyScreenHandler;
import net.flytre.mechanix.block.centrifuge.CentrifugeEntity;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;

public class CrafterHandler extends ItemEnergyScreenHandler<CrafterEntity> {

    public CrafterHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        super(MachineRegistry.CRAFTER.getHandlerType(), syncId, playerInventory, CrafterEntity::new, buf);
    }

    public CrafterHandler(int syncId, PlayerInventory playerInventory, CrafterEntity entity, PropertyDelegate propertyDelegate) {
        super(MachineRegistry.CRAFTER.getHandlerType(), syncId, playerInventory, entity, propertyDelegate);
    }

    @Override
    protected void constCommon(PlayerInventory playerInventory, CrafterEntity entity) {
        for (int o = 0; o < 3; ++o) {
            for (int n = 0; n < 3; ++n) {
                this.addSlot(new Slot(entity, n + o * 3, 46 + n * 18, 17 + o * 18));
            }
        }
        this.addSlot(new Slot(entity, 9, 140, 49));
        this.addSlot(new OutputSlot(entity, 10, 140, 20));
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
                if (!this.insertItem(stack, 11, 46, false))
                    return ItemStack.EMPTY;
            } else {
                if(!this.insertItem(stack,0,9,false))
                    return ItemStack.EMPTY;
            }
        }
        return stack;
    }


}
