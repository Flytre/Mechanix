package net.flytre.mechanix.util;

import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.flytre.mechanix.api.energy.EnergyEntity;
import net.flytre.mechanix.api.fluid.FluidInventory;
import net.flytre.mechanix.block.cell.EnergyCellEntity;
import net.flytre.mechanix.block.item_pipe.FilterInventory;
import net.flytre.mechanix.block.item_pipe.ItemPipeBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.HashMap;

public class Packets {
    public static final Identifier IO_CHANGE = new Identifier("mechanix", "io_change");
    public static final Identifier FLUID_IO_CHANGE = new Identifier("mechanix", "fluid_io_change");
    public static final Identifier FILTER_TYPE = new Identifier("mechanix", "filter_type");
    public static final Identifier PIPE_MODE = new Identifier("mechanix", "pipe_mode");


    public static void serverPacketRecieved() {
        ServerSidePacketRegistry.INSTANCE.register(IO_CHANGE, (packetContext, attachedData) -> {
            BlockPos pos = attachedData.readBlockPos();
            int side = attachedData.readInt();
            int state = attachedData.readInt();
            int panelMode = attachedData.readInt();
            World world = packetContext.getPlayer().getEntityWorld();
            packetContext.getTaskQueue().execute(() -> {
                BlockEntity entity = world.getBlockEntity(pos);
                if (!(entity instanceof EnergyEntity))
                    return;
                EnergyEntity cell = (EnergyEntity) entity;
                ((EnergyEntity) entity).sync();
                HashMap<Direction,Boolean> map = panelMode == 0 ? cell.energyMode : cell.ioMode;
                switch (side) {
                    case 0:
                        map.put(Direction.NORTH, state == 0);
                        break;
                    case 1:
                        map.put(Direction.WEST, state == 0);
                        break;
                    case 2:
                        map.put(Direction.EAST, state == 0);
                        break;
                    case 3:
                        map.put(Direction.UP, state == 0);
                        break;
                    case 4:
                        map.put(Direction.DOWN, state == 0);
                        break;
                    case 5:
                        map.put(Direction.SOUTH, state == 0);
                        break;
                }
                if(entity instanceof EnergyCellEntity)
                    ((EnergyCellEntity) entity).updateBlockStates();
                entity.markDirty();
            });
        });


        ServerSidePacketRegistry.INSTANCE.register(FLUID_IO_CHANGE, (packetContext, attachedData) -> {
            BlockPos pos = attachedData.readBlockPos();
            int side = attachedData.readInt();
            int state = attachedData.readInt();
            World world = packetContext.getPlayer().getEntityWorld();
            packetContext.getTaskQueue().execute(() -> {
                BlockEntity entity = world.getBlockEntity(pos);
                if (!(entity instanceof FluidInventory))
                    return;
                FluidInventory inv = (FluidInventory) entity;
                HashMap<Direction,Boolean> map = inv.getFluidIO();
                switch (side) {
                    case 0:
                        map.put(Direction.NORTH, state == 0);
                        break;
                    case 1:
                        map.put(Direction.WEST, state == 0);
                        break;
                    case 2:
                        map.put(Direction.EAST, state == 0);
                        break;
                    case 3:
                        map.put(Direction.UP, state == 0);
                        break;
                    case 4:
                        map.put(Direction.DOWN, state == 0);
                        break;
                    case 5:
                        map.put(Direction.SOUTH, state == 0);
                        break;
                }
                entity.markDirty();
            });
        });

        ServerSidePacketRegistry.INSTANCE.register(FILTER_TYPE, (packetContext, attachedData) -> {
            BlockPos pos = attachedData.readBlockPos();
            int newMode = attachedData.readInt();
            World world = packetContext.getPlayer().getEntityWorld();
            packetContext.getTaskQueue().execute(() -> {
                BlockEntity entity = world.getBlockEntity(pos);
                if (!(entity instanceof ItemPipeBlockEntity))
                    return;
                ItemPipeBlockEntity ip = (ItemPipeBlockEntity)entity;
                FilterInventory iv = ip.getFilter();
                iv.setFilterType(newMode);
            });
        });

        ServerSidePacketRegistry.INSTANCE.register(PIPE_MODE, (packetContext, attachedData) -> {
            BlockPos pos = attachedData.readBlockPos();
            int newMode = attachedData.readInt();
            World world = packetContext.getPlayer().getEntityWorld();
            packetContext.getTaskQueue().execute(() -> {
                BlockEntity entity = world.getBlockEntity(pos);
                if (!(entity instanceof ItemPipeBlockEntity))
                    return;
                ItemPipeBlockEntity ip = (ItemPipeBlockEntity)entity;
                ip.setRoundRobinMode(newMode != 0);
            });
        });



    }
}
