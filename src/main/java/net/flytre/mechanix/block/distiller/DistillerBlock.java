package net.flytre.mechanix.block.distiller;

import net.flytre.mechanix.api.connectable.CableConnectable;
import net.flytre.mechanix.api.connectable.FluidPipeConnectable;
import net.flytre.mechanix.api.machine.MachineBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class DistillerBlock extends MachineBlock implements CableConnectable, FluidPipeConnectable {
    public DistillerBlock(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockView world) {
        return new DistillerEntity();
    }
}
