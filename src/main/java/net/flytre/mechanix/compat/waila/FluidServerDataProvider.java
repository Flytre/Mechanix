package net.flytre.mechanix.compat.waila;

import mcp.mobius.waila.api.IServerDataProvider;
import net.flytre.mechanix.api.fluid.FluidInventory;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class FluidServerDataProvider implements IServerDataProvider<BlockEntity> {

    public static FluidServerDataProvider INSTANCE = new FluidServerDataProvider();

    @Override
    public void appendServerData(CompoundTag data, ServerPlayerEntity player, World world, BlockEntity entity) {

        if (!(entity instanceof FluidInventory))
            return;


        FluidInventory fluidInventory = (FluidInventory) entity;
        if (fluidInventory.getFluids().size() <= 3) {
            FluidInventory.toTag(data, fluidInventory.getFluids());
            data.putInt("fluid_size", ((FluidInventory) entity).getFluids().size());
        }
    }
}
