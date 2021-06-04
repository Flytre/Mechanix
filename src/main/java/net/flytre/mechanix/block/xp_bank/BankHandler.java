package net.flytre.mechanix.block.xp_bank;


import net.flytre.mechanix.api.fluid.screen.FluidSlot;
import net.flytre.mechanix.block.tank.FluidTankScreenHandler;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class BankHandler extends FluidTankScreenHandler {


    public BankHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, new BankEntity(), playerInventory, buf);

    }

    private BankHandler(int syncId, BankEntity entity, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, entity);
        pos = buf.readBlockPos();
        entity.setCapacity(buf.readLong());
        states = buf.readInt();
        synced = true;
    }


    public BankHandler(int syncId, PlayerInventory playerInventory, BankEntity entity) {
        super(MachineRegistry.XP_BANK.getHandlerType(), syncId);
        pos = BlockPos.ORIGIN;
        states = 0;

        this.addSlot(new FluidSlot(entity, 0, 73, 15, false));
        addInventorySlots(playerInventory);
        addStandardUpgradeSlots(entity);
    }
}
