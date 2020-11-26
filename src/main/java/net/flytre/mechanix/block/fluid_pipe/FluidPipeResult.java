package net.flytre.mechanix.block.fluid_pipe;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class FluidPipeResult {
    private final BlockPos pos;
    private final double max;
    private final Direction direction;

    public FluidPipeResult(BlockPos pos, double max, Direction direction) {
        this.pos = pos;
        this.max = max;
        this.direction = direction;
    }

    public BlockPos getDestination() {
        return pos;
    }

    public double getAmount() {
        return max;
    }

    public Direction getDirection() {
        return direction;
    }
}
