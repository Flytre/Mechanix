package net.flytre.mechanix.block.cell;

import net.flytre.mechanix.api.energy.EnergyEntity;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;


public class EnergyCellEntity extends EnergyEntity implements Tickable {


    private boolean corrected = false;

    public EnergyCellEntity() {
        super(MachineRegistry.ENERGY_CELL_ENTITY);
        setEnergyMode(true, true, true, true, true, true);
    }


    @Override
    public Text getDisplayName() {
        return new TranslatableText("block.mechanix.energy_cell");
    }


    public void updateBlockStates() {
        BlockState block = world.getBlockState(this.getPos());
        if (!(block.getBlock() instanceof EnergyCell))
            return;

        if (block.get(EnergyCell.UP) != energyMode.get(Direction.UP))
            world.setBlockState(getPos(), block.with(EnergyCell.UP, energyMode.get(Direction.UP)));

        if (block.get(EnergyCell.DOWN) != energyMode.get(Direction.DOWN))
            world.setBlockState(getPos(), block.with(EnergyCell.DOWN, energyMode.get(Direction.DOWN)));

        if (block.get(EnergyCell.NORTH) != energyMode.get(Direction.NORTH))
            world.setBlockState(getPos(), block.with(EnergyCell.NORTH, energyMode.get(Direction.NORTH)));

        if (block.get(EnergyCell.WEST) != energyMode.get(Direction.WEST))
            world.setBlockState(getPos(), block.with(EnergyCell.WEST, energyMode.get(Direction.WEST)));

        if (block.get(EnergyCell.EAST) != energyMode.get(Direction.EAST))
            world.setBlockState(getPos(), block.with(EnergyCell.EAST, energyMode.get(Direction.EAST)));

        if (block.get(EnergyCell.SOUTH) != energyMode.get(Direction.SOUTH))
            world.setBlockState(getPos(), block.with(EnergyCell.SOUTH, energyMode.get(Direction.SOUTH)));

    }


    @Override
    public void repeatTick() {
        if (world != null && !world.isClient && !isFull()) {
            double amount = Math.min(this.getMaxTransferRate(), this.getMaxEnergy() - this.getEnergy());
            requestEnergy(amount);
        }
    }

    @Override
    public void onceTick() {
        if(!corrected && world !=null && !world.isClient) {
            Block block = world.getBlockState(pos).getBlock();
            if(block == MachineRegistry.ENERGY_CELLS.getStandard()) {
                setMaxEnergy(500000);
                setMaxTransferRate(100);
            }
            if(block == MachineRegistry.ENERGY_CELLS.getGilded()) {
                setMaxEnergy(1500000);
                setMaxTransferRate(200);
            }
            if(block == MachineRegistry.ENERGY_CELLS.getVysterium()) {
                setMaxEnergy(10000000);
                setMaxTransferRate(400);
            }
            if(block == MachineRegistry.ENERGY_CELLS.getNeptunium()) {
                setMaxEnergy(50000000);
                setMaxTransferRate(1250);
            }
            corrected = true;
        }
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        corrected = tag.getBoolean("init");
        super.fromTag(state, tag);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putBoolean("init",corrected);
        return super.toTag(tag);
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new EnergyCellScreenHandler(syncId, inv, this, this.getProperties());
    }

}
