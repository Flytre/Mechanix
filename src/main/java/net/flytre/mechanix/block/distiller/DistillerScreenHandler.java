package net.flytre.mechanix.block.distiller;

import net.flytre.mechanix.api.energy.EnergyEntity;
import net.flytre.mechanix.api.energy.EnergyScreenHandler;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DistillerScreenHandler extends EnergyScreenHandler {
    private final EnergyEntity entity;
    private final World world;

    public DistillerScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, new DistillerEntity(), new ArrayPropertyDelegate(24));
        this.pos = buf.readBlockPos();
    }

    public DistillerScreenHandler(int syncId, PlayerInventory playerInventory, EnergyEntity entity, PropertyDelegate propertyDelegate) {
        super(MachineRegistry.DISTILLER_HANDLER, syncId, playerInventory, entity, propertyDelegate);

        pos = BlockPos.ORIGIN;

        int o;
        int n;
        for(o = 0; o < 3; ++o) {
            for(n = 0; n < 9; ++n) {
                this.addSlot(new Slot(playerInventory, n + o * 9 + 9, 8 + n * 18, 84 + o * 18));
            }
        }

        for(o = 0; o < 9; ++o) {
            this.addSlot(new Slot(playerInventory, o, 8 + o * 18, 142));
        }
        this.entity = entity;
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
            if(index <= 26) {
                if(!this.insertItem(stack,27,36,false) )
                    return ItemStack.EMPTY;
            } else {
                if(!this.insertItem(stack,0,27,false))
                    return ItemStack.EMPTY;
            }
        }
        return stack;
    }


    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
