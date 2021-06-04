package net.flytre.mechanix.util;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.flytre.flytre_lib.common.inventory.EasyInventory;
import net.flytre.flytre_lib.common.inventory.IOType;
import net.flytre.mechanix.api.energy.compat.EnergyEntity;
import net.flytre.mechanix.api.fluid.FluidFilterInventory;
import net.flytre.mechanix.api.fluid.FluidInventory;
import net.flytre.mechanix.block.cell.EnergyCellEntity;
import net.flytre.mechanix.block.fluid_pipe.FluidPipeEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Map;


//TODO: INCOMPLETE PORT OF CLASS
public class Packets {
    public static final Identifier ENERGY_IO_CHANGE = new Identifier("mechanix", "io_change");
    public static final Identifier ITEM_IO_CHANGE = new Identifier("mechanix", "item_io_change");
    public static final Identifier FLUID_IO_CHANGE = new Identifier("mechanix", "fluid_io_change");
    public static final Identifier FILTER_TYPE = new Identifier("mechanix", "filter_type");
    public static final Identifier XP_TRANSFER = new Identifier("mechanix", "xp_transfer");
    public static final Identifier MOD_MATCH = new Identifier("mechanix", "mod_match");

    public static void serverPacketReceived() {

        ServerPlayNetworking.registerGlobalReceiver(ENERGY_IO_CHANGE, (server, player, handler, attachedData, responseSender) -> {
            BlockPos pos = attachedData.readBlockPos();
            int side = attachedData.readInt();
            int state = attachedData.readInt();
            World world = player.getEntityWorld();
            server.execute(() -> {
                BlockEntity entity = world.getBlockEntity(pos);
                if (!(entity instanceof EnergyEntity))
                    return;
                EnergyEntity cell = (EnergyEntity) entity;
                Map<Direction, Boolean> map = cell.getEnergyIO();
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
                if (entity instanceof EnergyCellEntity) {
                    ((EnergyCellEntity) entity).updateBlockStates();
                    entity.markDirty();
                }
                if (entity instanceof BlockEntityClientSerializable)
                    ((BlockEntityClientSerializable) entity).sync();
            });
        });


        ServerPlayNetworking.registerGlobalReceiver(ITEM_IO_CHANGE, (server, player, handler, attachedData, responseSender) -> {
            BlockPos pos = attachedData.readBlockPos();
            int side = attachedData.readInt();
            int state = attachedData.readInt();
            World world = player.getEntityWorld();
            server.execute(() -> {
                BlockEntity entity = world.getBlockEntity(pos);
                if (!(entity instanceof EasyInventory))
                    return;
                EasyInventory inv = (EasyInventory) entity;
                Map<Direction, IOType> map = inv.getItemIO();
                packetHelper(side, state, entity, map);
                if (entity instanceof BlockEntityClientSerializable)
                    ((BlockEntityClientSerializable) entity).sync();
            });
        });


        ServerPlayNetworking.registerGlobalReceiver(FLUID_IO_CHANGE, (server, player, handler, attachedData, responseSender) -> {
            BlockPos pos = attachedData.readBlockPos();
            int side = attachedData.readInt();
            int state = attachedData.readInt();
            World world = player.getEntityWorld();
            server.execute(() -> {
                BlockEntity entity = world.getBlockEntity(pos);
                if (!(entity instanceof FluidInventory))
                    return;
                FluidInventory inv = (FluidInventory) entity;
                Map<Direction, IOType> map = inv.getFluidIO();
                packetHelper(side, state, entity, map);
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(FILTER_TYPE, (server, player, handler, attachedData, responseSender) -> {

            BlockPos pos = attachedData.readBlockPos();
            int newMode = attachedData.readInt();
            World world = player.getEntityWorld();
            server.execute(() -> {
                BlockEntity entity = world.getBlockEntity(pos);
                if (entity instanceof FluidPipeEntity) {
                    FluidPipeEntity ip = (FluidPipeEntity) entity;
                    FluidFilterInventory iv = ip.getFilter();
                    iv.setFilterType(newMode);
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(MOD_MATCH, (server, player, handler, attachedData, responseSender) -> {

            BlockPos pos = attachedData.readBlockPos();
            int newMode = attachedData.readInt();
            World world = player.getEntityWorld();
            server.execute(() -> {
                BlockEntity entity = world.getBlockEntity(pos);
                if (entity instanceof FluidPipeEntity) {
                    FluidPipeEntity ip = (FluidPipeEntity) entity;
                    FluidFilterInventory iv = ip.getFilter();
                    iv.setMatchMod(newMode == 1);
                }
            });
        });

    }

    private static void packetHelper(int side, int state, BlockEntity entity, Map<Direction, IOType> map) {
        switch (side) {
            case 0:
                map.put(Direction.NORTH, IOType.byId(state));
                break;
            case 1:
                map.put(Direction.WEST, IOType.byId(state));
                break;
            case 2:
                map.put(Direction.EAST, IOType.byId(state));
                break;
            case 3:
                map.put(Direction.UP, IOType.byId(state));
                break;
            case 4:
                map.put(Direction.DOWN, IOType.byId(state));
                break;
            case 5:
                map.put(Direction.SOUTH, IOType.byId(state));
                break;
        }
        entity.markDirty();
    }
}
