package net.flytre.mechanix.compat.waila;

import mcp.mobius.waila.api.IServerDataProvider;
import net.flytre.mechanix.api.energy.compat.EnergyEntity;
import net.flytre.mechanix.api.fluid.FluidInventory;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class EnergyServerDataProvider implements IServerDataProvider<BlockEntity> {

    public static EnergyServerDataProvider INSTANCE = new EnergyServerDataProvider();

    @Override
    public void appendServerData(CompoundTag data, ServerPlayerEntity player, World world, BlockEntity entity) {

        if (!(entity instanceof EnergyEntity))
            return;


        EnergyEntity energyEntity = (EnergyEntity) entity;
        data.putDouble("energy", energyEntity.getEnergy());
        data.putDouble("maxEnergy", energyEntity.getMaxEnergy());
    }
}
