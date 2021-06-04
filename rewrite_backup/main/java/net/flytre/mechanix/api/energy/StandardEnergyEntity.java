package net.flytre.mechanix.api.energy;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.flytre.flytre_lib.common.util.Formatter;
import net.flytre.mechanix.api.energy.compat.EnergyEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

//TODO: REMOVED A BUNCH OF SYNC CALLS, MAKE SURE THAT DOESN'T FUCK SHIT UP!!!!!
public abstract class StandardEnergyEntity extends BlockEntity implements EnergyEntity, Tickable, ExtendedScreenHandlerFactory, BlockEntityClientSerializable {

    private final PropertyDelegate properties;
    private Map<Direction, Boolean> energyMode;
    private double energy;
    private double maxEnergy;
    private double maxTransferRate;

    public StandardEnergyEntity(BlockEntityType<?> type) {
        super(type);
        energyMode = new HashMap<>();
        properties = new ArrayPropertyDelegate(24); //the rest unused for subclasses

        //defaults - PLEASE OVERRIDE
        maxEnergy = 300000;
        maxTransferRate = 300;
        setEnergyMode(true, true, true, true, true, true);
    }


    @Override
    public PropertyDelegate getDelegate() {
        return properties;
    }

    @Override
    public Map<Direction, Boolean> getEnergyIO() {
        return energyMode;
    }

    @Override
    public void setEnergyIO(Map<Direction, Boolean> energyIO) {
        this.energyMode = energyIO;
    }

    @Override
    public int getPanelType() {
        return 0;
    }

    @Override
    public double getEnergy() {
        return energy;
    }

    @Override
    public void setEnergy(double energy) {
        this.energy = energy;
    }

    @Override
    public double getMaxEnergy() {
        return maxEnergy;
    }

    @Override
    public void setMaxEnergy(double energy) {
        this.maxEnergy = energy;
    }

    @Override
    public double getMaxTransferRate() {
        return maxTransferRate;
    }

    @Override
    public void setMaxTransferRate(double amount) {
        this.maxTransferRate = amount;
    }

    @Override
    public BlockPos getPos() {
        return pos;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        EnergyEntity.fromTag(this, tag);
        super.fromTag(state, tag);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        EnergyEntity.toTag(this, tag);
        return super.toTag(tag);
    }


    @Override
    public void tick() {
        techRebornPush();
        updateDelegate();
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(pos);
        packetByteBuf.writeInt(Formatter.mapToInt(this.getEnergyIO()));
        packetByteBuf.writeInt(getPanelType());
    }


    @Override
    public void fromClientTag(CompoundTag tag) {
        setEnergyIO(Formatter.intToMap(tag.getInt("EnergyMode")));
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        tag.putInt("EnergyMode", Formatter.mapToInt(getEnergyIO()));
        return tag;
    }

    @Override
    public void setEnergyMode(boolean up, boolean down, boolean north, boolean east, boolean south, boolean west) {
        EnergyEntity.super.setEnergyMode(up, down, north, east, south, west);
        if (world != null && !world.isClient)
            sync();
    }
}
