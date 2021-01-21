package net.flytre.mechanix.util;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.flytre.mechanix.api.energy.EnergyEntity;
import net.flytre.mechanix.api.fluid.FluidInventory;
import net.flytre.mechanix.api.fluid.FluidStack;
import net.flytre.mechanix.block.cell.EnergyCellEntity;
import net.flytre.mechanix.block.item_collector.ItemCollectorEntity;
import net.flytre.mechanix.block.item_pipe.FilterInventory;
import net.flytre.mechanix.block.item_pipe.ItemPipeBlockEntity;
import net.flytre.mechanix.block.xp_bank.XpBankBlockEntity;
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
    public static final Identifier XP_TRANSFER = new Identifier("mechanix", "xp_transfer");


    public static void serverPacketRecieved() {

        ServerPlayNetworking.registerGlobalReceiver(IO_CHANGE, (server, player, handler, attachedData, responseSender) -> {
            BlockPos pos = attachedData.readBlockPos();
            int side = attachedData.readInt();
            int state = attachedData.readInt();
            int panelMode = attachedData.readInt();
            World world = player.getEntityWorld();
            server.execute(() -> {
                BlockEntity entity = world.getBlockEntity(pos);
                if (!(entity instanceof EnergyEntity))
                    return;
                EnergyEntity cell = (EnergyEntity) entity;
                ((EnergyEntity) entity).sync();
                HashMap<Direction, Boolean> map = panelMode == 0 ? cell.energyMode : cell.ioMode;
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
                if (entity instanceof EnergyCellEntity)
                    ((EnergyCellEntity) entity).updateBlockStates();
                entity.markDirty();
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
                HashMap<Direction, Boolean> map = inv.getFluidIO();
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

        ServerPlayNetworking.registerGlobalReceiver(FILTER_TYPE, (server, player, handler, attachedData, responseSender) -> {

            BlockPos pos = attachedData.readBlockPos();
            int newMode = attachedData.readInt();
            World world = player.getEntityWorld();
            server.execute(() -> {
                BlockEntity entity = world.getBlockEntity(pos);
                if (entity instanceof ItemPipeBlockEntity) {
                    ItemPipeBlockEntity ip = (ItemPipeBlockEntity) entity;
                    FilterInventory iv = ip.getFilter();
                    iv.setFilterType(newMode);
                } else if (entity instanceof ItemCollectorEntity) {
                    ItemCollectorEntity ip = (ItemCollectorEntity) entity;
                    FilterInventory iv = ip.getFilter();
                    iv.setFilterType(newMode);
                }
            });
        });


        ServerPlayNetworking.registerGlobalReceiver(PIPE_MODE, (server, player, handler, attachedData, responseSender) -> {
            BlockPos pos = attachedData.readBlockPos();
            int newMode = attachedData.readInt();
            World world = player.getEntityWorld();
            server.execute(() -> {
                BlockEntity entity = world.getBlockEntity(pos);
                if (!(entity instanceof ItemPipeBlockEntity))
                    return;
                ItemPipeBlockEntity ip = (ItemPipeBlockEntity) entity;
                ip.setRoundRobinMode(newMode != 0);
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(XP_TRANSFER, (server, player, handler, attachedData, responseSender) -> {
            BlockPos pos = attachedData.readBlockPos();
            int amount = attachedData.readInt();
            World world = player.getEntityWorld();
            server.execute(() -> {
                BlockEntity entity = world.getBlockEntity(pos);
                if (!(entity instanceof XpBankBlockEntity))
                    return;
                XpBankBlockEntity bank = (XpBankBlockEntity) entity;
                if (amount > 0) {
                    if (bank.getAmount() >= amount * 4) {
                        bank.getFluidStack(0).decrement(amount * 4);
                        player.addExperience(amount);
                    }
                } else {
                    if (-amount <= player.totalExperience && bank.canAdd(new FluidStack(FluidRegistry.LIQUID_XP.getStill(), -amount * 4))) {
                        bank.getFluidStack(0).increment(-amount * 4);
                        player.totalExperience += amount;
                        player.setExperiencePoints(player.totalExperience);
                    }
                }
            });
        });


    }
}
