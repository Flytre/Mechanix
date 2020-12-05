package net.flytre.mechanix.block.alloyer;

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
import net.minecraft.world.World;

public class AlloyerScreenHandler extends EnergyScreenHandler {
    private final Inventory inventory;
    private final World world;

    public AlloyerScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, new AlloyerBlockEntity(), new ArrayPropertyDelegate(24));
        this.pos = buf.readBlockPos();
    }

    public AlloyerScreenHandler(int syncId, PlayerInventory playerInventory, AlloyerBlockEntity entity, PropertyDelegate propertyDelegate) {
        super(MachineRegistry.ALLOYER.getHandlerType(), syncId, playerInventory, entity, propertyDelegate);

        this.pos = BlockPos.ORIGIN;
        this.addSlot(new Slot(entity, 0, 61, 21));
        this.addSlot(new Slot(entity, 1, 98, 21));
        this.addSlot(new Slot(entity, 2, 136, 21));
        this.addSlot(new Slot(entity, 3, 98, 58));

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
        this.world = playerInventory.player.world;
    }


    public double operationProgress() {
        return getPropertyDelegate().get(8) / 120.0;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            stack = slot.getStack();
            if(index <= 3) {
                if(!this.insertItem(stack,4,40,false))
                    return ItemStack.EMPTY;
            } else {
                if(!this.insertItem(stack,0,3,false))
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
