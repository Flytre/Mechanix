package net.flytre.mechanix.block.quarry;

import net.flytre.mechanix.api.energy.ItemEnergyScreenHandler;
import net.flytre.mechanix.block.pressurizer.PressurizerEntity;
import net.flytre.mechanix.block.pressurizer.PressurizerHandler;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

public class QuarryHandler extends ItemEnergyScreenHandler<QuarryEntity> {


    public QuarryHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        super(MachineRegistry.QUARRY.getHandlerType(), syncId, playerInventory, QuarryEntity::new, buf);
    }

    public QuarryHandler(int syncId, PlayerInventory playerInventory, QuarryEntity entity, PropertyDelegate propertyDelegate) {
        super(MachineRegistry.QUARRY.getHandlerType(), syncId, playerInventory, entity, propertyDelegate);
    }

    @Override
    protected void constCommon(PlayerInventory playerInventory, QuarryEntity entity) {

        this.addSlot(new Slot(entity, 0, 51, 36));

        for (int o = 0; o < 4; o++)
            for (int n = 0; n < 5; n++)
                this.addSlot(new Slot(entity, 1 + n + o * 5, 80 + n * 18, 9 + o * 18));
        super.constCommon(playerInventory, entity);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            stack = slot.getStack();
            if (index <= 20) {
                if (!this.insertItem(stack, 21, 57, false))
                    return ItemStack.EMPTY;
            } else {
                if (!this.insertItem(stack, 1, 21, false))
                    return ItemStack.EMPTY;
            }
        }
        return stack;
    }


}
