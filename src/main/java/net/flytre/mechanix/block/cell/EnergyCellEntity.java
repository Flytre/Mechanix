package net.flytre.mechanix.block.cell;

import net.flytre.mechanix.base.EnergyEntity;
import net.flytre.mechanix.util.MachineRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;


public class EnergyCellEntity extends EnergyEntity implements Tickable {


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
    public void tick() {
        if (world != null && !world.isClient && !isFull()) {
            double amount = Math.min(this.getMaxTransferRate(), this.getMaxEnergy() - this.getEnergy());
            requestEnergy(amount);
        }
        super.tick();
    }


    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new EnergyCellScreenHandler(syncId,inv,this,this.getProperties());
    }

}
