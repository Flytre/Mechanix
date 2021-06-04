package net.flytre.mechanix.api.energy;

import net.flytre.flytre_lib.common.inventory.IOType;
import net.flytre.mechanix.api.energy.compat.EnergyEntityWithItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Direction;

import java.util.HashMap;
import java.util.Map;

public abstract class StandardEnergyEntityWithItems extends StandardEnergyEntity implements EnergyEntityWithItems {

    private Map<Direction, IOType> itemMode;

    public StandardEnergyEntityWithItems(BlockEntityType<?> type) {
        super(type);
        itemMode = new HashMap<>();
    }


    @Override
    public Map<Direction, IOType> getItemIO() {
        return itemMode;
    }

    @Override
    public void setItemIO(Map<Direction, IOType> itemIO) {
        this.itemMode = itemIO;
        if (world != null && !world.isClient && getPanelType() == 1)
            sync();
    }


    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        EnergyEntityWithItems.fromTag(this, tag);
        super.fromTag(state, tag);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        EnergyEntityWithItems.toTag(this, tag);
        return super.toTag(tag);
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        if (getPanelType() == 1)
            setItemIO(IOType.intToMap(tag.getInt("IOMode")));
        else
            super.fromClientTag(tag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        if (getPanelType() == 1) {
            tag.putInt("IOMode", IOType.mapToInt(getItemIO()));
            return tag;
        } else
            return super.toClientTag(tag);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        super.writeScreenOpeningData(serverPlayerEntity, packetByteBuf);
        packetByteBuf.writeInt(IOType.mapToInt(this.getItemIO()));
    }
}
