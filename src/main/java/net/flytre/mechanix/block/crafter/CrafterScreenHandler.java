package net.flytre.mechanix.block.crafter;

import net.flytre.mechanix.api.energy.EnergyScreenHandler;
import net.flytre.mechanix.api.inventory.OutputSlot;
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

public class CrafterScreenHandler extends EnergyScreenHandler {

    private final Inventory inventory;

    public CrafterScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, new CrafterBlockEntity(), new ArrayPropertyDelegate(24));
        this.pos = buf.readBlockPos();
    }

    public CrafterScreenHandler(int syncId, PlayerInventory playerInventory, CrafterBlockEntity entity, PropertyDelegate propertyDelegate) {
        super(MachineRegistry.CRAFTER.getHandlerType(), syncId, playerInventory, entity, propertyDelegate);

        this.pos = BlockPos.ORIGIN;
        for (int o = 0; o < 3; ++o) {
            for (int n = 0; n < 3; ++n) {
                this.addSlot(new Slot(entity, n + o * 3, 46 + n * 18, 17 + o * 18));
            }
        }
        this.addSlot(new Slot(entity,9,140,49));
        this.addSlot(new OutputSlot(entity,10,140,20));

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

    public double operationProgress() {
        return getPropertyDelegate().get(8) / (double)120;
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

    @Override
    public boolean canUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }


}
