package net.flytre.mechanix.block.hydrator;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.flytre_lib.common.inventory.IOType;
import net.flytre.mechanix.api.fluid.screen.FluidHandler;
import net.flytre.mechanix.api.fluid.screen.FluidSlot;
import net.flytre.mechanix.api.upgrade.UpgradeHandler;
import net.flytre.mechanix.block.tank.FluidTankEntity;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Map;

public class HydratorHandler extends UpgradeHandler {

    private BlockPos pos;
    private int states;
    private boolean synced = false;

    public HydratorHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, new HydratorEntity());
        pos = buf.readBlockPos();
        states = buf.readInt();
        synced = true;
    }

    public HydratorHandler(int syncId, PlayerInventory playerInventory, HydratorEntity entity) {
        super(MachineRegistry.HYDRATOR.getHandlerType(), syncId);
        pos = BlockPos.ORIGIN;
        states = 0;

        this.addSlot(new FluidSlot(entity, 0, 73, 15, false));
        addInventorySlots(playerInventory);
        addStandardUpgradeSlots(entity);
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
