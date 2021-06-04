package net.flytre.mechanix.block.tank;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.flytre_lib.common.inventory.IOType;
import net.flytre.mechanix.api.fluid.screen.FluidHandler;
import net.flytre.mechanix.api.fluid.screen.FluidSlot;
import net.flytre.mechanix.api.upgrade.UpgradeHandler;
import net.flytre.mechanix.block.xp_bank.BankHandler;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Map;

public class FluidTankScreenHandler extends UpgradeHandler {
    protected BlockPos pos;
    protected int states;
    protected boolean synced = false;

    public FluidTankScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, new FluidTankEntity(), playerInventory, buf);

    }

    private FluidTankScreenHandler(int syncId, FluidTankEntity entity, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, entity);
        pos = buf.readBlockPos();
        entity.setCapacity(buf.readLong());
        states = buf.readInt();
        synced = true;

    }


    public FluidTankScreenHandler(int syncId, PlayerInventory playerInventory, FluidTankEntity entity) {
        super(MachineRegistry.FLUID_TANK_SCREEN_HANDLER, syncId);
        pos = BlockPos.ORIGIN;
        states = 0;

        this.addSlot(new FluidSlot(entity, 0, 73, 15, false));
        addInventorySlots(playerInventory);
    }

    protected FluidTankScreenHandler(ScreenHandlerType<BankHandler> handlerType, int syncId) {
        super(handlerType, syncId);
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
        return simpleTransferSlot(player, index);
    }

}
