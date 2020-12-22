package net.flytre.mechanix.block.quarry;

import net.flytre.mechanix.api.energy.EnergyScreenHandler;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

public class QuarryScreenHandler extends EnergyScreenHandler {

    private final Inventory inventory;

    public QuarryScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, new QuarryEntity(), new ArrayPropertyDelegate(24));
        this.pos = buf.readBlockPos();
    }

    public QuarryScreenHandler(int syncId, PlayerInventory playerInventory, QuarryEntity entity, PropertyDelegate propertyDelegate) {
        super(MachineRegistry.QUARRY.getHandlerType(), syncId, playerInventory, entity, propertyDelegate);

        this.pos = BlockPos.ORIGIN;
        this.addSlot(new Slot(entity, 0, 51, 36));


        for (int o = 0; o < 4; o++) {
            for (int n = 0; n < 5; n++) {
                this.addSlot(new Slot(entity, 1 + n + o * 5, 80 + n * 18, 9 + o * 18));
            }
        }

        int o;
        int n;
        for (o = 0; o < 3; ++o) {
            for (n = 0; n < 9; ++n) {
                this.addSlot(new Slot(playerInventory, n + o * 9 + 9, 8 + n * 18, 84 + o * 18));
            }
        }

        for (o = 0; o < 9; ++o) {
            this.addSlot(new Slot(playerInventory, o, 8 + o * 18, 142));
        }

        this.inventory = entity;
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


    @Override
    public boolean canUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }


}
