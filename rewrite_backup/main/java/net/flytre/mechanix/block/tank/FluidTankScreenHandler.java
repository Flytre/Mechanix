package net.flytre.mechanix.block.tank;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.flytre_lib.common.inventory.IOType;
import net.flytre.mechanix.api.fluid.FluidHandler;
import net.flytre.mechanix.api.fluid.FluidSlot;
import net.flytre.mechanix.api.fluid.Fraction;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Map;

public class FluidTankScreenHandler extends FluidHandler {
    private BlockPos pos;
    private int states;
    private boolean synced = false;

    public FluidTankScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, new FluidTankEntity(), playerInventory, buf);

    }

    private FluidTankScreenHandler(int syncId, FluidTankEntity entity, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, entity);
        pos = buf.readBlockPos();
        entity.setCapacity(Fraction.fromPacket(buf));
        states = buf.readInt();
        synced = true;

    }


    public FluidTankScreenHandler(int syncId, PlayerInventory playerInventory, FluidTankEntity entity) {
        super(MachineRegistry.FLUID_TANK_SCREEN_HANDLER, syncId);
        pos = BlockPos.ORIGIN;
        states = 0;

        this.addSlot(new FluidSlot(entity, 0, 73, 15, false));

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
    }

    @Environment(EnvType.CLIENT)
    public FluidTankEntity getEntity() {
        assert MinecraftClient.getInstance().world != null;
        return (FluidTankEntity) MinecraftClient.getInstance().world.getBlockEntity(pos);
    }

    public BlockPos getPos() {
        return pos;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }


    public IOType getIOType(Direction direction) {
        Map<Direction, IOType> itemMap = IOType.intToMap(states);
        return itemMap.get(direction);
    }

    public int fluidButtonState(Direction direction) {
        return getIOType(direction).getIndex();
    }

    public boolean isSynced() {
        return synced;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            stack = slot.getStack();
            if (index <= 26) {
                if (!this.insertItem(stack, 27, 36, false))
                    return ItemStack.EMPTY;
            } else {
                if (!this.insertItem(stack, 0, 27, false))
                    return ItemStack.EMPTY;
            }
        }
        return stack;
    }

}
