package net.flytre.mechanix.block.cell;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.flytre.mechanix.api.energy.SimpleEnergyScreenHandler;
import net.flytre.mechanix.api.energy.StandardEnergyEntity;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class EnergyCellEntity extends StandardEnergyEntity implements BlockEntityClientSerializable {

    private boolean corrected = false;
    private boolean sync = false;

    public EnergyCellEntity() {
        super(MachineRegistry.ENERGY_CELL_ENTITY);
        setMaxEnergy(50000000);
        setEnergyMode(true, true, true, true, true, true);
    }

    @Override
    public void setEnergy(double energy) {
        sync = true;
        super.setEnergy(energy);
    }

    public void correct() {
        assert world != null;
        Block block = world.getBlockState(pos).getBlock();
        if (block == MachineRegistry.ENERGY_CELLS.getStandard()) {
            setMaxEnergy(500000);
            setMaxTransferRate(100);
        }
        if (block == MachineRegistry.ENERGY_CELLS.getGilded()) {
            setMaxEnergy(1500000);
            setMaxTransferRate(200);
        }
        if (block == MachineRegistry.ENERGY_CELLS.getVysterium()) {
            setMaxEnergy(10000000);
            setMaxTransferRate(400);
        }
        if (block == MachineRegistry.ENERGY_CELLS.getNeptunium()) {
            setMaxEnergy(50000000);
            setMaxTransferRate(1250);
        }
        corrected = true;
    }

    @Override
    public void tick() {
        super.tick();

        if (!corrected && world != null && !world.isClient) {
            correct();
        }

        if (world != null && !world.isClient && !isFull()) {
            double amount = Math.min(this.getMaxTransferRate(), this.getMaxEnergy() - this.getEnergy());
            requestEnergy(amount);
        }

        if (sync) {
            if (world != null && !world.isClient)
                sync();
            sync = false;
        }
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        corrected = tag.getBoolean("init");
        super.fromTag(state, tag);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putBoolean("init", corrected);
        return super.toTag(tag);
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("block.mechanix.energy_cell");
    }


    public void updateBlockStates() {
        assert world != null;
        BlockState block = world.getBlockState(this.getPos());
        if (!(block.getBlock() instanceof EnergyCell))
            return;

        if (block.get(EnergyCell.UP) != getEnergyIO().get(Direction.UP))
            world.setBlockState(getPos(), block.with(EnergyCell.UP, getEnergyIO().get(Direction.UP)));

        if (block.get(EnergyCell.DOWN) != getEnergyIO().get(Direction.DOWN))
            world.setBlockState(getPos(), block.with(EnergyCell.DOWN, getEnergyIO().get(Direction.DOWN)));

        if (block.get(EnergyCell.NORTH) != getEnergyIO().get(Direction.NORTH))
            world.setBlockState(getPos(), block.with(EnergyCell.NORTH, getEnergyIO().get(Direction.NORTH)));

        if (block.get(EnergyCell.WEST) != getEnergyIO().get(Direction.WEST))
            world.setBlockState(getPos(), block.with(EnergyCell.WEST, getEnergyIO().get(Direction.WEST)));

        if (block.get(EnergyCell.EAST) != getEnergyIO().get(Direction.EAST))
            world.setBlockState(getPos(), block.with(EnergyCell.EAST, getEnergyIO().get(Direction.EAST)));

        if (block.get(EnergyCell.SOUTH) != getEnergyIO().get(Direction.SOUTH))
            world.setBlockState(getPos(), block.with(EnergyCell.SOUTH, getEnergyIO().get(Direction.SOUTH)));

    }


    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new SimpleEnergyScreenHandler(syncId, inv, this, this.getDelegate());
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        tag.putDouble("energy", getEnergy());
        tag.putDouble("maxEnergy", getMaxEnergy());
        return tag;
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        setEnergy(tag.getDouble("energy"));
        if (tag.contains("maxEnergy"))
            setMaxEnergy(tag.getDouble("maxEnergy"));
    }
}
